import org.http4s._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir._
import zio.interop.catz._
import cats.syntax.all._
import handler.BaseRouteRouter
import pureconfig.generic.auto._
import service.RecommendationServiceImpl
import task.RecommendationTask
import utils.CustomTransactor
import zio.{Clock, Schedule, Scope, Task, UIO, ULayer, URLayer, ZIO, ZLayer, durationInt}

import java.util.concurrent.TimeUnit

object Main extends zio.ZIOAppDefault {

  type EnvIn = BaseRouteRouter

  def swaggerRoutes(routes: List[ZServerEndpoint[Any, Any]]): HttpRoutes[Task] =
    ZHttp4sServerInterpreter()
      .from(
        SwaggerInterpreter()
          .fromServerEndpoints(routes, "Travel Designer", "1.1")
      )
      .toRoutes

  def makeLayer: ULayer[EnvIn] =
    ZLayer.make[EnvIn](
      RecommendationServiceImpl.live,
      BaseRouteRouter.live,
      ZLayer.succeed(CustomTransactor.transactor)
    )

  def getEndpoints(router: BaseRouteRouter): List[ZServerEndpoint[Any, Any]] =
    List(
      router.getPlaces,
      router.getRoutes
    ).map(_.tag("Recommendation"))

  val task =
    ZIO.service[RecommendationTask].map(_.task)

  val scheduledTask: ZIO[RecommendationTask, Throwable, Unit] =
    task.repeat(Schedule.spaced(1.minutes)).forever

  def run: ZIO[Environment with Scope, Any, Any] = {
    val server = (for {
      mainRouter <- ZIO.service[BaseRouteRouter]
      endpoints = getEndpoints(mainRouter)
      routes: HttpRoutes[Task] = ZHttp4sServerInterpreter()
        .from(endpoints)
        .toRoutes
      _ <-
        ZIO.executor.flatMap(executor =>
          BlazeServerBuilder[Task]
            .withExecutionContext(executor.asExecutionContext)
            .bindHttp(8084, "0.0.0.0")
            .withHttpApp(Router("/" -> (routes <+> swaggerRoutes(endpoints))).orNotFound)
            .serve
            .compile
            .drain
        )
    } yield ())
      .provideLayer(makeLayer)

    (for {
      _ <- scheduledTask.forkDaemon
      _ <- server
    } yield ()).provide(RecommendationTask.live)
  }
}
