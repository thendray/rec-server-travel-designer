package service

import model.PlaceRecommendationResponse.PlaceRecommendation
import model.RouteRecommendationResponse.{RouteRecommendation, TopPlace}
import model.{PlaceRecommendationResponse, RouteRecommendationResponse}
import repository.recommendation.RecommendationDao
import utils.DefaultPlaceRecommendations
import zio.{IO, ZIO, ZLayer}

import scala.util.Random

class RecommendationServiceImpl(recommendationDao: RecommendationDao) extends RecommendationService {

  override def getPlaces(userId: Long): IO[String, PlaceRecommendationResponse] =
    for {
      attempt <- recommendationDao.getRecommendationCardIds(userId).mapError(e => e.getMessage)
      placesIds = if (attempt.size >= 3) attempt else attempt ++ DefaultPlaceRecommendations.getNCards(3 - attempt.size)
      _ = println(s"Places ids: $placesIds")
      placeCards <- recommendationDao.getByIds(placesIds).mapError(e => e.getMessage)
      result <- ZIO
        .foreach(placeCards) { card =>
          recommendationDao.getUserNames(card.authorId).map { name =>
            PlaceRecommendation(
              id = card.id,
              name = card.name,
              author = name,
              description = card.description,
              category = card.category,
              photo = card.photo
            )
          }
        }
        .mapError(e => e.getMessage)
    } yield PlaceRecommendationResponse(result)

  override def getRoutes(userId: Long): IO[String, RouteRecommendationResponse] =
    for {
      ids <- recommendationDao.getRecommendationRouteIds(userId).mapError(e => e.getMessage)
      random = new Random()
      resultIds = random.shuffle(ids).take(3)
      rr <- recommendationDao.getRoutes(resultIds).mapError(e => e.getMessage)
      routes <- ZIO.foreachPar(rr) { r =>
        for {
          beginOpt <- recommendationDao.getExtra(r.id, isBegin = true)
          endOpt <- recommendationDao.getExtra(r.id, isBegin = false)
          begin = beginOpt.map(_.address).getOrElse("Не указано")
          end = endOpt.map(_.address).getOrElse("Не указано")
          cards <- recommendationDao.getByRoute(r.id)
          random = new Random()
          top4 = random.shuffle(cards).take(4)
        } yield RouteRecommendation(
          id = r.id,
          name = r.name,
          photos = top4.map(_.photo),
          days = r.days,
          beginAddress = begin,
          endAddress = end,
          topPlaces =
            (top4.map(c => (c.name, c.address)) ++ List(("...", "..."), ("...", "..."), ("...", "...")))
              .take(3)
              .map(r => TopPlace(r._1, r._2))
        )
      }.withParallelism(3)
        .mapError(e => e.getMessage)

    } yield RouteRecommendationResponse(routes)
}

object RecommendationServiceImpl {
  val live = ZLayer.fromFunction((rec: RecommendationDao) => new RecommendationServiceImpl(rec))
}
