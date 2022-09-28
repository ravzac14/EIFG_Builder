package ui.console

import scala.concurrent.{ Await, ExecutionContext, Future, Promise }
import scala.concurrent.duration.Duration
import scala.io.StdIn
import scala.util.{ Failure, Success, Try }

class StdOutConsole(config: ConsoleConfig) extends Console {
  override def writeUntyped(out: String): Try[Unit] = {
    val outForConsole =
      config.writePrefix.getOrElse("") + out + config.writePostfix.getOrElse("")
    Success(println(outForConsole))
  }

  override def readUntyped(): Try[String] = {
    def read: String =
      config.readPrefix.map(StdIn.readLine(_)).getOrElse(StdIn.readLine())
    Try(read)
  }

  override def clear(): Try[Unit] = {
    Success {
      print("\u001b[2J")
      print("\u001b[H")
    }
  }

  override def waitForInterrupt(n: Duration)(implicit
      ec: ExecutionContext): Try[Option[String]] = {
    val p = Promise[Option[String]]()
    val f = p.future

    val resultFuture = Future.fromTry(readUntyped())
    resultFuture.onComplete {
      case Success(result) => p.success(Some(result))
      case Failure(ex)     => p.failure(ex)
    }
    Try(Await.result(f, n)).recoverWith { case _: concurrent.TimeoutException =>
      Success(None)
    }
  }
}
