package task

import zio.{Task, ZLayer}

class RecommendationTask() {

  def task: Task[Unit] = ???

}

object RecommendationTask {
  val live = ZLayer.fromFunction(() => new RecommendationTask())
}
