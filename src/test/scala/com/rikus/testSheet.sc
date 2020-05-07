
import scala.collection.mutable.ListBuffer
import com.rikus.JsonFormats._
import com.rikus.dao.Direction
import com.rikus.dao.Direction.Direction
import spray.json._

val destinations: scala.collection.mutable.ListBuffer[(Int, Option[Direction])] = ListBuffer.empty[(Int, Option[Direction])] //todo : Fix to incorporate direction into destinations
destinations += ((1, Some(Direction.UP)))

println(destinations)

val addDestination = (floor: Int, direction: Option[Direction]) =>
  if (!(destinations.contains((floor, None)) || destinations.contains((floor, Some(_:Direction)))))
    destinations += ((floor, direction))


addDestination(2,Some(Direction.DOWN))
println(destinations)


addDestination(3,None)
println(destinations)