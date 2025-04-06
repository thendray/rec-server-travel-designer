package service

import model.{PlaceRecommendationResponse, RouteRecommendationResponse}
import zio.{IO, ZLayer}

class RecommendationServiceImpl() extends RecommendationService {

  override def getPlaces(userId: Long): IO[String, PlaceRecommendationResponse] = ???

  override def getRoutes(userId: Long): IO[String, RouteRecommendationResponse] = ???
}

object RecommendationServiceImpl {
  val live = ZLayer.fromFunction(() => new RecommendationServiceImpl())
}
