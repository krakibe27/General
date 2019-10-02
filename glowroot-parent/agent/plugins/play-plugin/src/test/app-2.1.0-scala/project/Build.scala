import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "helloworld"
    val appVersion      = "1.0"

    val appDependencies = Seq()

    val main = play.Project(appName, appVersion, appDependencies)
}
