import scala.collection.mutable.ListBuffer

val destinations = ListBuffer.empty[Int].distinct
destinations += 1

println(destinations)

destinations += 2
destinations += 3
println(destinations)

destinations -= 1

destinations += 3
println(destinations)

