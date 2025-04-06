package repository.point_card

import repository.point_card.RoutePointCardDao.RoutePointCardDb
import zio.Task

trait RoutePointCardDao {

  def getById(id: Long): Task[Option[RoutePointCardDb]]
  def getByIds(ids: Seq[Long]): Task[List[RoutePointCardDb]]
  def getAllByAuthorId(authorId: Long): Task[List[RoutePointCardDb]]

}

object RoutePointCardDao {

  case class RoutePointCardDb(
      id: Long,
      name: String,
      routePointId: Long,
      authorId: Long,
      description: String,
      photo: String,
      category: String)
}
