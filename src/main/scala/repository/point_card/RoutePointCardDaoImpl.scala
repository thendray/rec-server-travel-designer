package repository.point_card

import cats.data.NonEmptyList
import cats.implicits.toFoldableOps
import doobie.implicits._
import doobie.{Fragments, Get, Transactor}
import repository.point_card.RoutePointCardDao.RoutePointCardDb
import zio.interop.catz._
import zio.{Task, URLayer, ZIO, ZLayer}

class RoutePointCardDaoImpl(xa: Transactor[Task]) extends RoutePointCardDao {

  override def getById(id: Long): Task[Option[RoutePointCardDb]] = {
    sql"SELECT id, name, route_point_id, author_id, description, photo, category FROM route_point_card WHERE id = $id"
      .query[RoutePointCardDb]
      .option
      .transact(xa)
  }

  override def getByIds(ids: Seq[Long]): Task[List[RoutePointCardDb]] = {
    val query = fr"""
      SELECT id, name, route_point_id, author_id, description, photo, category
      FROM route_point_card
      WHERE ${Fragments.in(fr"id ", NonEmptyList.fromListUnsafe(ids.toList))}
    """
    query
      .query[RoutePointCardDb]
      .to[List]
      .transact(xa)
  }

  override def getAllByAuthorId(authorId: Long): Task[List[RoutePointCardDb]] = {
    sql"SELECT id, name, route_point_id, author_id, description, photo, category FROM route_point_card WHERE author_id = $authorId"
      .query[RoutePointCardDb]
      .to[List]
      .transact(xa)
  }

}

object RoutePointCardDaoImpl {

  val live: URLayer[Transactor[Task], RoutePointCardDao] =
    ZLayer.fromFunction { (xa: Transactor[Task]) =>
      new RoutePointCardDaoImpl(xa)
    }
}
