package model

import model.RouteRecommendationResponse.RouteRecommendation

case class RouteRecommendationResponse(routes: List[RouteRecommendation])

object RouteRecommendationResponse {

  case class RouteRecommendation(
      id: Long,
      name: String,
      photos: List[String],
      days: Int,
      beginAddress: String,
      endAddress: String,
      topPlaces: List[TopPlace])

  case class TopPlace(name: String, address: String)
}
