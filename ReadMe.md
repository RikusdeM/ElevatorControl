# Elevator Control System [Scala Coding Challenge]

## Solution

## Data Structures

## Build & Run
This project utilizes SBT and Java.
Tested versions :

    SBT -> 1.3.3

    Java -> 8

### Building
` $ sbt clean compile `

### Running
`$ sbt run`

## Usage Instructions

The following HTTP commands can be used to control the elevator system.

### Create an elevator

#### Request :
`$ curl --location --request GET 'localhost:8080/ElevatorControl/createElevator/1'`
#### Response :
`creating`
#### Logs :
`Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-1229578759] has been created`

### Query the status of all the elevators

#### Request :
`$ curl --location --request GET 'localhost:8080/ElevatorControl/status'`
#### Response :
`getting status ...`
#### Logs :
`Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-1229578759] has been created`

### Request an elevator to do a pickup at specified floor with intended direction of travel

#### Request :
`$ curl --location --request GET 'localhost:8080/ElevatorControl/pickup?id=<elevatorId>&floor=<floorNumber>&direction=<up/down>'`

where the query parameters are :

`<elevatorId>:Int` [eg: 1]

`<floorNumber>:Int` [eg: 1]

`<direction>:String (up / down)` [eg: up]

#### Response :
`new pickup`
#### Logs :
`Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-1229578759] now include : List((1,Some(UP)))`

### Request an elevator to do a drop off at specified floor

#### Request :
`$ curl --location --request GET 'localhost:8080/ElevatorControl/dropOff?id=<elevatorId>&floor=<floorNumber>'`

where the query parameters are :

`<elevatorId>:Int` [eg: 1]

`<floorNumber>:Int` [eg: 2]

#### Response :
`new dropOff`
#### Logs :
`Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-1229578759] now include : List((2,None))`

### Step through to the next state

#### Request :
`$ curl --location --request GET 'localhost:8080/ElevatorControl/step'`

#### Response :
`step`
#### Logs :
```bash
[2020-05-10 10:03:43,639] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-18] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Stepping to next point
[2020-05-10 10:03:43,643] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-20] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Going to (2,None)
[2020-05-10 10:03:43,644] [INFO] [com.rikus.dao.Elevator] [ElevatorControlServer-akka.actor.default-dispatcher-20] [akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1] - Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#530024638] now include : List()
```
