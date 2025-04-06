package handler

import model._
import service.RecommendationService
import sttp.tapir.ztapir.{RichZEndpoint, ZServerEndpoint}
import zio.{URLayer, ZIO, ZLayer}

class BaseRouteRouter(recService: RecommendationService) {

  private val getRoutesHandler: Long => ZIO[Any, String, RouteRecommendationResponse] =
    userId => recService.getRoutes(userId)

  val getRoutes: ZServerEndpoint[Any, Any] =
    BaseEndpoints.getRouteRecEndpoint.zServerLogic(getRoutesHandler)

  private val getPlacesHandler: Long => ZIO[Any, String, PlaceRecommendationResponse] =
    userId => recService.getPlaces(userId)

  val getPlaces: ZServerEndpoint[Any, Any] =
    BaseEndpoints.getPlaceRecEndpoint.zServerLogic(getPlacesHandler)

}

object BaseRouteRouter {

  val live: URLayer[RecommendationService, BaseRouteRouter] =
    ZLayer.fromFunction((r: RecommendationService) => new BaseRouteRouter(r))
}
