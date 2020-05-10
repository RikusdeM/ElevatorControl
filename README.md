# Elevator Control System [Scala Coding Challenge]

# Table of Contents
1. [Solution](#solution)
2. [Data Structures](#data-structures)
3. [Build & Run](#build--run)

## Solution
Originally I started with the First Come First Serve approach to simulate my solution.
After thinking about all the intricacies of such an elevator system I came up with a better more efficient solution.

This solution can be summarized as follows.
Surely if you are the first person at an elevator terminal you would want to be helped first, hence there is some
merit to the FCFS solution.  The way we overcome the inefficiency of such a solution is by respecting your first request 
on the queue as the most important future destination, whilst dropping off and picking up passengers on the way,
only if their intended direction is in the direction of the original move. This way the person that has been waiting 
the longest gets to go where they want, whilst satisfying some of the intermediate requests on the way there.

A good simulation of such a scenario would be the following situation :  
Lets say we have the following events : 
 * Initial default position of the elevator (1:CurrentFloor,1:DestinationFloor)
 * Drop-off #4
 * Pickup #3 (DOWN)
 * Pickup #2 (DOWN)
 * Drop-off #6
 * ===================================STEP=================================== (assume that here has to have been some step already)
 * Status
 * Drop-off #8
 * Pickup #7 (DOWN)
 * Pickup #5 (UP)
 * ===================================STEP=================================== (assume that here has to have been some step already)
 * Status
 * ===================================STEP=================================== (assume that here has to have been some step already)
 * Status
 * ===================================STEP=================================== (assume that here has to have been some step already)
 * Status
 * ===================================STEP=================================== (assume that here has to have been some step already)
 * Status
 * ===================================STEP=================================== (assume that here has to have been some step already)
 * Status
 * ===================================STEP=================================== (assume that here has to have been some step already)
 * Status
 * ===================================STEP=================================== (assume that here has to have been some step already)
 * Status
 
 
The simulated movement of the Elevator is as follows :
 * Create the elevator  [localhost:8080/ElevatorControl/createElevator/1](http://localhost:8080/ElevatorControl/createElevator/1)
```
[2020-05-10 11:40:17,678] [INFO] [com.rikus.dao.ElevatorSupervisor] [ElevatorControlServer-akka.actor.default-dispatcher-8] [akka://ElevatorControlServer/user/ElevatorSupervisor] - Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-62513659] has been created
```
 * Drop-off #4 [localhost:8080/ElevatorControl/dropOff?id=1&floor=4](http://localhost:8080/ElevatorControl/dropOff?id=1&floor=4)
```
[2020-05-10 11:43:43,305] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-16] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-62513659] now include : List((4,None))
```   
 * Pickup #3 (DOWN) [localhost:8080/ElevatorControl/pickup?id=1&floor=3&direction=down](http://localhost:8080/ElevatorControl/pickup?id=1&floor=3&direction=down)
```
[2020-05-10 11:45:27,462] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-26] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-62513659] now include : List((4,None), (3,Some(DOWN)))
```
 * Pickup #2 (DOWN) [localhost:8080/ElevatorControl/pickup?id=1&floor=2&direction=down](http://localhost:8080/ElevatorControl/pickup?id=1&floor=2&direction=down)
```
[2020-05-10 11:48:00,958] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-36] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-62513659] now include : List((4,None), (3,Some(DOWN)), (2,Some(DOWN)))
```
 * Drop-off #6 [localhost:8080/ElevatorControl/dropOff?id=1&floor=6](http://localhost:8080/ElevatorControl/dropOff?id=1&floor=6) 
```
[2020-05-10 11:49:44,146] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-42] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-62513659] now include : List((4,None), (3,Some(DOWN)), (2,Some(DOWN)), (6,None))
```
 
 * Step [localhost:8080/ElevatorControl/step](http://localhost:8080/ElevatorControl/step)
```
[2020-05-10 11:52:15,273] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-54] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Stepping to next point
[2020-05-10 11:52:15,275] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-54] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Going to (4,None)
[2020-05-10 11:52:15,276] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-54] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-62513659] now include : List((3,Some(DOWN)), (2,Some(DOWN)), (6,None))
```
 As expected the elevator will now make floor #4 the new future destination, as that was the oldest destination in the queue.
 
 * Status [localhost:8080/ElevatorControl/status](http://localhost:8080/ElevatorControl/status)
```
[2020-05-10 12:00:27,533] [INFO] [com.rikus.dao.ElevatorSupervisor] [ElevatorControlServer-akka.actor.default-dispatcher-73] [akka://ElevatorControlServer/user/ElevatorSupervisor] - Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-62513659] : {"currentFloor":1,"destinationFloor":4}
``` 
 * Drop-off #8 [localhost:8080/ElevatorControl/dropOff?id=1&floor=8](http://localhost:8080/ElevatorControl/dropOff?id=1&floor=8)
```
[2020-05-10 12:23:12,551] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-20] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-541721289] now include : List((3,Some(DOWN)), (2,Some(DOWN)), (6,None), (8,None))
``` 
 * Pickup #7 (DOWN) [localhost:8080/ElevatorControl/pickup?id=1&floor=7&direction=down](http://localhost:8080/ElevatorControl/pickup?id=1&floor=7&direction=down)
```
[2020-05-10 12:24:19,121] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-20] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-541721289] now include : List((3,Some(DOWN)), (2,Some(DOWN)), (6,None), (8,None), (7,Some(DOWN)))
```
 * Pickup #5 (UP) [localhost:8080/ElevatorControl/pickup?id=1&floor=5&direction=up](http://localhost:8080/ElevatorControl/pickup?id=1&floor=5&direction=up)
```
[2020-05-10 12:25:25,166] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-24] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-541721289] now include : List((3,Some(DOWN)), (2,Some(DOWN)), (6,None), (8,None), (7,Some(DOWN)), (5,Some(UP)))
```
 * Step [localhost:8080/ElevatorControl/step](http://localhost:8080/ElevatorControl/step) 
```
[2020-05-10 12:27:43,682] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-42] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Stepping to next point
[2020-05-10 12:27:43,683] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-41] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Going to (3,Some(DOWN))
[2020-05-10 12:27:43,683] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-41] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-541721289] now include : List((2,Some(DOWN)), (6,None), (8,None), (7,Some(DOWN)), (5,Some(UP)))
```
 As expected the new future destination is floor #3, as that destination is the oldest closest to previous floor #4.
 
 * Status [localhost:8080/ElevatorControl/status](http://localhost:8080/ElevatorControl/status)
```
[2020-05-10 12:31:05,157] [INFO] [com.rikus.dao.ElevatorSupervisor] [ElevatorControlServer-akka.actor.default-dispatcher-50] [akka://ElevatorControlServer/user/ElevatorSupervisor] - Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-541721289] : {"currentFloor":4,"destinationFloor":3}
``` 

 * Step [localhost:8080/ElevatorControl/step](http://localhost:8080/ElevatorControl/step)
```
2020-05-10 12:33:25,956] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-63] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Stepping to next point
[2020-05-10 12:33:25,969] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-67] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Going to (2,Some(DOWN))
[2020-05-10 12:33:25,969] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-67] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-541721289] now include : List((6,None), (8,None), (7,Some(DOWN)), (5,Some(UP)))
```
 As expected the next destination is floor #2 going down as that was the oldest in the queue and also the closest to previous floor #3.
 
 * Status [localhost:8080/ElevatorControl/status](http://localhost:8080/ElevatorControl/status)
```
[2020-05-10 12:36:26,696] [INFO] [com.rikus.dao.ElevatorSupervisor] [ElevatorControlServer-akka.actor.default-dispatcher-74] [akka://ElevatorControlServer/user/ElevatorSupervisor] - Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-541721289] : {"currentFloor":3,"destinationFloor":2}
```  
 * Step [localhost:8080/ElevatorControl/step](http://localhost:8080/ElevatorControl/step)
```
[2020-05-10 12:41:49,711] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-85] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Stepping to next point
[2020-05-10 12:41:49,726] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-84] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Going to (5,Some(UP))
[2020-05-10 12:41:49,728] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-87] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-541721289] now include : List((6,None), (8,None), (7,Some(DOWN)))
```
 As expected the next destination is floor #5, as the intended direction was chosen as UP and all the other future destinations are above it.
 
 * Status [localhost:8080/ElevatorControl/status](http://localhost:8080/ElevatorControl/status)
```
[2020-05-10 12:45:32,109] [INFO] [com.rikus.dao.ElevatorSupervisor] [ElevatorControlServer-akka.actor.default-dispatcher-98] [akka://ElevatorControlServer/user/ElevatorSupervisor] - Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-541721289] : {"currentFloor":2,"destinationFloor":5}
``` 
 * Step [localhost:8080/ElevatorControl/step](http://localhost:8080/ElevatorControl/step)
```
[2020-05-10 12:46:30,936] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-95] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Stepping to next point
[2020-05-10 12:46:30,936] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-95] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Going to (6,None)
[2020-05-10 12:46:30,938] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-95] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-541721289] now include : List((8,None), (7,Some(DOWN)))
```
 As expected the next destination is floor #6, as it was the oldest in the queue and the closest to the previous floor #5.
 
 * Status [localhost:8080/ElevatorControl/status](http://localhost:8080/ElevatorControl/status)
```
[2020-05-10 12:49:58,331] [INFO] [com.rikus.dao.ElevatorSupervisor] [ElevatorControlServer-akka.actor.default-dispatcher-105] [akka://ElevatorControlServer/user/ElevatorSupervisor] - Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-541721289] : {"currentFloor":5,"destinationFloor":6}
```
 * Step [localhost:8080/ElevatorControl/step](http://localhost:8080/ElevatorControl/step)
```
[2020-05-10 12:50:22,784] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-106] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Stepping to next point
[2020-05-10 12:50:22,785] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-110] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Going to (8,None)
[2020-05-10 12:50:22,787] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-104] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-541721289] now include : List((7,Some(DOWN)))
```
 As expected the next destination is floor #8, because it is the oldest in the queue and agrees with the current movement of the elevator i.e. UP

 * Status [localhost:8080/ElevatorControl/status](http://localhost:8080/ElevatorControl/status)
```
[2020-05-10 12:53:56,467] [INFO] [com.rikus.dao.ElevatorSupervisor] [ElevatorControlServer-akka.actor.default-dispatcher-124] [akka://ElevatorControlServer/user/ElevatorSupervisor] - Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-541721289] : {"currentFloor":6,"destinationFloor":8}
```

 * Step [localhost:8080/ElevatorControl/step](http://localhost:8080/ElevatorControl/step)
```
[2020-05-10 12:54:45,172] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-118] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Stepping to next point
[2020-05-10 12:54:45,173] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-123] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Going to (7,Some(DOWN))
[2020-05-10 12:54:45,173] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-123] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-541721289] now include : List()
```
 Finally the last future destination on the queue is reached i.e. floor #7 going down.
 
 * Status [localhost:8080/ElevatorControl/status](http://localhost:8080/ElevatorControl/status)
```
[2020-05-10 12:56:21,521] [INFO] [com.rikus.dao.ElevatorSupervisor] [ElevatorControlServer-akka.actor.default-dispatcher-121] [akka://ElevatorControlServer/user/ElevatorSupervisor] - Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-541721289] : {"currentFloor":8,"destinationFloor":7}
```

 * Step [localhost:8080/ElevatorControl/step](http://localhost:8080/ElevatorControl/step)
```
[2020-05-10 13:00:31,587] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-144] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Stepping to next point
[2020-05-10 13:00:31,588] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-144] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - No known next destination
``` 
 Queue is empty. Elevator is standing still on last known floor #7
 
 * Status [localhost:8080/ElevatorControl/status](http://localhost:8080/ElevatorControl/status)
```
[2020-05-10 13:01:47,414] [INFO] [com.rikus.dao.ElevatorSupervisor] [ElevatorControlServer-akka.actor.default-dispatcher-140] [akka://ElevatorControlServer/user/ElevatorSupervisor] - Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-541721289] : {"currentFloor":7,"destinationFloor":7}
``` 
 
## Data Structures

The project makes use of [Lightbend Akka](https://akka.io/) Actor framework to construct the Elevator System.
Akka HTTP is used to interface with the actor system.


ElevatorControl uses a `ElevatorSupervisor(numberOfFloors: Int, initialState:(Int,Int))` to construct 
`Elevator(id: Int, initialState: (Int, Int), numberOfFloors: Int)` children. The actor system is instantiated
through the `QuickstartApp` and passed to the `Routes()`.  Each route ["createElevator","pickup","dropOff","step","status"]
sends their respective command to the supervisor actor i.e. `ElevatorSupervisor`.


### ElevatorSupervisor(numberOfFloors: Int, initialState:(Int,Int))
#### Commands:
* `createElevator(id:Int)` 
* `status()`      
* `step()`
* `elevatorStatus(currentFloor: Int, destinationFloor: Int)`
* `pickupReq(id: Int, floor: Int, direction: Direction)`
    * Direction => `UP`/`DOWN`        
* `dropOffReq(id: Int, floor: Int)`
      
### Elevator(id: Int, initialState: (Int, Int), numberOfFloors: Int)
#### Data structures:
```
var currentStatus: elevatorStatus = elevatorStatus(initialState._1, initialState._2)
val destinations: scala.collection.mutable.ListBuffer[(Int, Option[Direction])] = ListBuffer.empty[(Int, Option[Direction])]
```
`currentStatus` is returned when a `status()` request is sent.


`destinations` is a listbuffer that is used to store the future destinations of the elevator. 

    
#### Commands:
* `status()`
* `pickup(floor:Int,direction:Direction)`
* `step()`
* `dropOff(floor:Int)`

## Build & Run
This project utilizes SBT and Java.
Tested versions :

    SBT -> 1.3.3

    Java -> 8

### Building
```bash
$ sbt clean compile
```

### Running
```bash
$ sbt run
```

## Usage Instructions

The following HTTP commands can be used to control the elevator system.

If using postman, import the collection [elevatorControl.postman_collection.json](./elevatorControl.postman_collection.json)

### Create an elevator

#### Request :
```bash
$ curl --location --request GET 'localhost:8080/ElevatorControl/createElevator/<elevatorId>'
```

where 

`<elevatorId>:Int` [eg: 1]

#### Response :
`creating`
#### Logs :
```
Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-1229578759] has been created
```

### Query the status of all the elevators

#### Request :
```bash
$ curl --location --request GET 'localhost:8080/ElevatorControl/status'
```
#### Response :
`getting status ...`
#### Logs :
```
[2020-05-10 09:41:54,760] [INFO] [com.rikus.dao.ElevatorSupervisor] [ElevatorControlServer-akka.actor.default-dispatcher-20] [akka://ElevatorControlServer/user/ElevatorSupervisor] - Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-1229578759] : {"currentFloor":1,"destinationFloor":1}
```

### Request an elevator to do a pickup at specified floor with intended direction of travel

#### Request :
```bash
$ curl --location --request GET 'localhost:8080/ElevatorControl/pickup?id=<elevatorId>&floor=<floorNumber>&direction=<up/down>'
```

where the query parameters are :

`<elevatorId>:Int` [eg: 1]

`<floorNumber>:Int` [eg: 1]

`<direction>:String (up / down)` [eg: up]

#### Response :
`new pickup`
#### Logs :
```
Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-1229578759] now include : List((1,Some(UP)))
```

### Request an elevator to do a drop off at specified floor

#### Request :
```bash
$ curl --location --request GET 'localhost:8080/ElevatorControl/dropOff?id=<elevatorId>&floor=<floorNumber>'
```

where the query parameters are :

`<elevatorId>:Int` [eg: 1]

`<floorNumber>:Int` [eg: 2]

#### Response :
`new dropOff`
#### Logs :
```
Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-1229578759] now include : List((2,None))
```

### Step through to the next state

#### Request :
```bash
$ curl --location --request GET 'localhost:8080/ElevatorControl/step'
```

#### Response :
`step`
#### Logs :
```
[2020-05-10 10:03:43,639] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-18] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Stepping to next point
[2020-05-10 10:03:43,643] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-20] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Going to (2,None)
[2020-05-10 10:03:43,644] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-20] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#530024638] now include : List()
```
