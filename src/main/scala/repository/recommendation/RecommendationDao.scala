package repository.recommendation

import cats.data.NonEmptyList
import model.RouteRecommendationResponse.RouteRecommendation
import repository.recommendation.RecommendationDao.{RoutePointCard, RoutePointCardDb, RouteSpecialSettingsDb}
import zio.Task

trait RecommendationDao {

  def getRecommendationCardIds(userId: Long): Task[List[Long]]

  def getRecommendationRouteIds(userId: Long): Task[List[Long]]

  def putRecommendationCardIds(userId: Long, cardIds: List[Long]): Task[Int]

  def putRecommendationRouteIds(userId: Long, cardIds: List[Long]): Task[Int]

  def getUserNames(userId: Long): Task[String]

  def getByIds(ids: Seq[Long]): Task[List[RoutePointCardDb]]
  def getByRoute(id: Long): Task[List[RoutePointCard]]

  def getRoutes(ids: Seq[Long]): Task[List[RouteRecommendation]]

  def getUsers: Task[List[Long]]

  def notAuthorIds(userId: Long): Task[List[Long]]

  def getExtra(routeId: Long, isBegin: Boolean): Task[Option[RouteSpecialSettingsDb]]

}

object RecommendationDao {

  case class RouteSpecialSettingsDb(
      id: Long,
      routeId: Long,
      address: String,
      x: Double,
      y: Double,
      isBegin: Boolean)

  case class RoutePointCardDb(
      id: Long,
      name: String,
      routePointId: Long,
      authorId: Long,
      description: String,
      photo: String,
      category: String)

  case class RoutePointCard(
      id: Long,
      name: String,
      address: String,
      author: String,
      description: String,
      photo: String,
      category: String)
}
