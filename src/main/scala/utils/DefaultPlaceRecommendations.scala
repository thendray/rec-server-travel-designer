package utils

import repository.recommendation.RecommendationDao.RoutePointCardDb

import scala.util.Random

object DefaultPlaceRecommendations {

  def getNCards(n: Int): List[Long] = {
    val random = new Random()
    random.shuffle(List(40L, 41L, 42L, 43)).take(n)
  }


}
