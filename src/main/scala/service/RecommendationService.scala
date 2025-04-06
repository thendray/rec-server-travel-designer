package service

import model.{PlaceRecommendationResponse, RouteRecommendationResponse}
import zio.{IO, Task}

trait RecommendationService {

  def getPlaces(userId: Long): IO[String, PlaceRecommendationResponse]
  def getRoutes(userId: Long): IO[String, RouteRecommendationResponse]

}
