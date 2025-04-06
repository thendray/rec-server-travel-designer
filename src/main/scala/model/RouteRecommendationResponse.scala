package model

import model.RouteRecommendationResponse.RouteRecommendation

case class RouteRecommendationResponse(routes: List[RouteRecommendation])

object RouteRecommendationResponse {

  case class RouteRecommendation(
      id: Long,
      name: String,
      protos: List[String],
      days: Int,
      beginAddress: String,
      endAddress: String,
      topPlacesName: List[String])
}
