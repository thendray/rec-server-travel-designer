package task

import repository.recommendation.RecommendationDao
import zio.{Task, ZIO, ZLayer}

import scala.util.Random

class RecommendationTask(recommendationDao: RecommendationDao) {

  def task: Task[Unit] =
    for {
      users <- recommendationDao.getUsers
      _ = println(s"Run task for users: $users")
      _ <- putPlaces(users)
//      _ <- putRoutes(users)
    } yield ()

  private def putPlaces(users: List[Long]): Task[Unit] =
    ZIO.foreachParDiscard(users) { user =>
      for {
        exist <- recommendationDao.notAuthorIds(user)
        indexes = if (exist.size > 6)
          RecommendationTask.generateRandomNumbers(6, 0, exist.size-1)
        else exist
        _ <-
          recommendationDao.putRecommendationCardIds(user,
            exist.zipWithIndex.collect { case (p, i) if indexes.contains(i) => p }
          )
      } yield ()
    }.withParallelism(2)

  private def putRoutes(users: List[Long]): Task[Unit] = ???

}

object RecommendationTask {
  val live = ZLayer.fromFunction((rec: RecommendationDao) => new RecommendationTask(rec))


  private def generateRandomNumbers(n: Int, k: Int, m: Int): List[Int] = {
    require(k <= m, "Lower bound must be less than or equal to upper bound.")
    require(m - k + 1 >= n, "Range must contain at least n unique numbers.")

    val random = new Random()
    random.shuffle((k to m).toList).take(n)
  }
}
