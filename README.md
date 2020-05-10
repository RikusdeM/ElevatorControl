# Elevator Control System [Scala Coding Challenge]

## Solution
Originally I started with the First Come First Serve approach to simulate my solution.
After thinking about all the intricacies of such an elevator system I came up with a better more efficient solution.

This solution can be summarized as follows.
Surely if you are the first person at an elevator terminal you would want to be helped first, hence there is some
merit to the FCFS solution.  The way we overcome the inefficiency of such a solution is by respecting your first request 
on the queue as the most important future destination, whilst dropping off and picking up passengers on the way,
only if their intended direction is in the direction of the original move. This way the person that has been waiting 
the longest gets to go where they want, whilst satisfying some of the intermediate requests on the way there.

As a good simulation of such a scenario would be the following situation :  
Lets say we have the following events : 
 * Initial default position of the elevator (1:CurrentFloor,1:DestinationFloor)
 * Drop-off #4
 * Pickup #3 (DOWN)
 * Pickup #2 (DOWN)
 * Drop-off #6
 * ===================================STEP=================================== (assume that here has to have been some step already)
 * Drop-off #1
 * Pickup #3 (UP)
 * Drop-off #8
 * Pickup #7 (DOWN)
 
The simulated movement of the Elevator is as follows :
 

    

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
```bash
Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-1229578759] has been created
```

### Query the status of all the elevators

#### Request :
`$ curl --location --request GET 'localhost:8080/ElevatorControl/status'`
#### Response :
`getting status ...`
#### Logs :
```bash
[2020-05-10 09:41:54,760] [INFO] [com.rikus.dao.ElevatorSupervisor] [ElevatorControlServer-akka.actor.default-dispatcher-20] [akka://ElevatorControlServer/user/ElevatorSupervisor] - Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-1229578759] : {"currentFloor":1,"destinationFloor":1}
```

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
```bash
Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-1229578759] now include : List((1,Some(UP)))
```

### Request an elevator to do a drop off at specified floor

#### Request :
`$ curl --location --request GET 'localhost:8080/ElevatorControl/dropOff?id=<elevatorId>&floor=<floorNumber>'`

where the query parameters are :

`<elevatorId>:Int` [eg: 1]

`<floorNumber>:Int` [eg: 2]

#### Response :
`new dropOff`
#### Logs :
```bash
Destinations for Actor[akka://ElevatorControlServer/user/ElevatorSupervisor/elevator-1#-1229578759] now include : List((2,None))
```

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
