package handler

import model.{PlaceRecommendationResponse, RouteRecommendationResponse}
import io.circe.generic.auto._
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._


object BaseEndpoints {

  private val baseRecEndpoint = endpoint.in("api" / "recommendation")

  val getPlaceRecEndpoint =
    baseRecEndpoint
      .in("place" / path[Long]("userId"))
      .post
      .out(jsonBody[PlaceRecommendationResponse])
      .errorOut(jsonBody[String])

  val getRouteRecEndpoint =
    baseRecEndpoint
      .in("route" / path[Long]("userId"))
      .post
      .out(jsonBody[RouteRecommendationResponse])
      .errorOut(jsonBody[String])

}
