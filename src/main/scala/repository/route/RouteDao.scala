package repository.route

import repository.route.RouteDao.Route
import zio.Task

trait RouteDao {

  def getById(id: Long): Task[Option[Route]]
  def getByIds(ids: Seq[Long]): Task[List[Route]]
  def getByLogin(login: String): Task[Option[Route]]

  def getAllByCreatorId(creatorId: Long): Task[List[Route]]

}

object RouteDao {

  case class Route(
      id: Long,
      creatorId: Long,
      name: String,
      login: String,
      password: String,
      cardLimit: Option[Int],
      days: Int)

}
