package repository.route

import cats.data.NonEmptyList
import cats.implicits._
import doobie.implicits._
import doobie.{Fragments, Transactor}
import repository.route.RouteDao.Route
import zio.interop.catz._
import zio.{Task, URLayer, ZIO, ZLayer}

class RouteDaoImpl(xa: Transactor[Task]) extends RouteDao {

  override def getById(id: Long): Task[Option[Route]] = {
    sql"SELECT id, creator_id, name, login, password, card_limit, days FROM route WHERE id = $id"
      .query[Route]
      .option
      .transact(xa)
  }

  override def getByIds(ids: Seq[Long]): Task[List[Route]] = {
    sql"""
          SELECT id, creator_id, name, login, password, card_limit, days FROM route
          WHERE ${Fragments.in(fr"id ", NonEmptyList.fromListUnsafe(ids.toList))}
      """
      .query[Route]
      .to[List]
      .transact(xa)
  }

  override def getByLogin(login: String): Task[Option[Route]] = {
    sql"SELECT id, creator_id, name, login, password, card_limit, days FROM route WHERE login = $login"
      .query[Route]
      .option
      .transact(xa)
  }

  override def getAllByCreatorId(creatorId: Long): Task[List[Route]] = {
    sql"SELECT id, creator_id, name, login, password, card_limit, days FROM route WHERE creator_id = $creatorId"
      .query[Route]
      .to[List]
      .transact(xa)
  }

}

object RouteDaoImpl {

  val live: URLayer[Transactor[Task], RouteDao] =
    ZLayer.fromFunction { (xa: Transactor[Task]) =>
      new RouteDaoImpl(xa)
    }
}
