
import scala.collection.mutable.ListBuffer
import com.rikus.JsonFormats._
import com.rikus.dao.test
import spray.json._

val destinations = ListBuffer.empty[Int].distinct
destinations += 1

println(destinations)

destinations += 2
destinations += 3
println(destinations)

destinations -= 1

destinations += 3
println(destinations)
