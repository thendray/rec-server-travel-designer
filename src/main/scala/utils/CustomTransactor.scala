package utils

import doobie.util.transactor.Transactor
import zio.Task
import zio.interop.catz._


object CustomTransactor {

  val transactor: Transactor[Task] = Transactor.fromDriverManager[Task](
    driver = "org.postgresql.Driver",
    url = "jdbc:postgresql://51.250.77.99:5432/all_users",
    user = "user_travel",
    password = "user-travel",
    logHandler = None
  )

}
