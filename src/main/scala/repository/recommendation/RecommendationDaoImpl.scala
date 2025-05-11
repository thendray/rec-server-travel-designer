package repository.recommendation

import cats.data.NonEmptyList
import cats.implicits.catsSyntaxApplicativeId
import doobie._
import doobie.implicits._
import model.RouteRecommendationResponse
import model.RouteRecommendationResponse.RouteRecommendation
import repository.recommendation.RecommendationDao.{RoutePointCard, RoutePointCardDb, RouteSpecialSettingsDb}
import utils.CustomTransactor
import zio._
import zio.interop.catz._

class RecommendationDaoImpl(xa: Transactor[Task]) extends RecommendationDao {

  override def getRecommendationCardIds(userId: Long): Task[List[Long]] = {
    sql"""
      SELECT card_id
      FROM recommendation_cards
      WHERE user_id = $userId
    """
      .query[Long]
      .to[List]
      .transact(xa)
  }

  override def getRecommendationRouteIds(userId: Long): Task[List[Long]] = {
    sql"""
      SELECT id
      FROM route
      WHERE id NOT IN (
        SELECT route_id FROM route_member WHERE user_id = $userId
      )
    """
      .query[Long]
      .to[List]
      .transact(xa)
  }

  override def putRecommendationCardIds(userId: Long, cardIds: List[Long]): Task[Int] = {
    val deleteQuery =
      sql"""
        DELETE FROM recommendation_cards WHERE user_id = $userId
      """.update.run

    val insertBatch = if (cardIds.nonEmpty) {
      Update[(Long, Long)](
        """
        INSERT INTO recommendation_cards (user_id, card_id)
        VALUES (?, ?)
      """
      ).updateMany(cardIds.map(cardId => (userId, cardId)))
    } else {
      0.pure[ConnectionIO]
    }

    (for {
      _ <- deleteQuery
      count <- insertBatch
    } yield count).transact(xa)
  }

  override def putRecommendationRouteIds(userId: Long, routeIds: List[Long]): Task[Int] = {
    val deleteQuery =
      sql"""
        DELETE FROM recommendation_routes WHERE user_id = $userId
      """.update.run

    val insertStatements = routeIds.map { routeId =>
      sql"""
        INSERT INTO recommendation_routes (user_id, route_id)
        VALUES ($userId, $routeId)
      """.update.run
    }

    val insertBatch = insertStatements.foldLeft(0.pure[ConnectionIO]) { (acc, query) =>
      acc.flatMap(sum => query.map(_ + sum))
    }

    (for {
      _ <- deleteQuery
      count <- insertBatch
    } yield count).transact(xa)
  }

  override def getUserNames(userId: Long): Task[String] =
    sql"""
        SELECT username FROM users WHERE id = $userId
      """
      .query[String]
      .option
      .transact(xa)
      .map(_.get)

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

  override def getByRoute(id: Long): Task[List[RoutePointCard]] = {
    val query = fr"""
      SELECT rpc.id, name, address, users.username, description, photo, category
      FROM room_card as rm
      join route_point_card rpc on rpc.id = rm.point_card_id
      JOIN users on rpc.author_id = users.id
      JOIN route_point p ON p.id = rpc.route_point_id
      WHERE rm.route_id = $id
    """
    query
      .query[RoutePointCard]
      .to[List]
      .transact(xa)
  }

  override def getRoutes(ids: Seq[Long]): Task[List[RouteRecommendation]] = {
    sql"""
         SELECT id, name, days FROM route
         WHERE ${Fragments.in(fr"id", NonEmptyList.fromListUnsafe(ids.toList))}
       """
      .query[(Long, String, Int)]
      .map { case (id, name, days) =>
        RouteRecommendation(id, name, List.empty, days, "", "", List.empty)
      }.to[List]
      .transact(xa)
  }

  override def getUsers: Task[List[Long]] =
    sql"SELECT id FROM users".query[Long].to[List].transact(xa)

  override def notAuthorIds(userId: Long): Task[List[Long]] =
    sql"SELECT id FROM route_point_card WHERE author_id <> $userId".query[Long].to[List].transact(xa)

  override def getExtra(routeId: Long, isBegin: Boolean): Task[Option[RecommendationDao.RouteSpecialSettingsDb]] = {
    sql"""
      SELECT id, route_id, address, x, y, is_begin
      FROM route_special_settings
      WHERE route_id = $routeId AND is_begin = $isBegin
    """
      .query[RouteSpecialSettingsDb]
      .option
      .transact(xa)
  }
}

object RecommendationDaoImpl {
  val live: ULayer[RecommendationDao] = ZLayer.succeed(new RecommendationDaoImpl(CustomTransactor.transactor))
}
