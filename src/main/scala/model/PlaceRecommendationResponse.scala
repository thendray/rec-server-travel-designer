package model

import model.PlaceRecommendationResponse.PlaceRecommendation

case class PlaceRecommendationResponse(cards: List[PlaceRecommendation])

object PlaceRecommendationResponse {

  case class PlaceRecommendation(
      id: Long,
      name: String,
      author: String,
      description: String,
      category: String,
      photo: String)
}
