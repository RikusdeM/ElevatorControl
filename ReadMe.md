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
`$ curl --location --request GET 'localhost:8080/ElevatorControl/createElevator/1'`

### Query the status of all the elevators
`$ curl --location --request GET 'localhost:8080/ElevatorControl/status'`

### Request an elevator to do a pickup at specified floor with intended direction of travel
`curl --location --request GET 'localhost:8080/ElevatorControl/pickup?id=<elevatorId>&floor=<floorNumber>&direction=<up/down>'`

where the query parameters are : 

`<elevatorId>:Int` [eg: 1] 

`<floorNumber>:Int` [eg: 1]

`<direction>:String (up / down)` [eg: up]

### Request an elevator to do a drop off at specified floor
`curl --location --request GET 'localhost:8080/ElevatorControl/dropOff?id=<elevatorId>&floor=<floorNumber>'`

where the query parameters are : 

`<elevatorId>:Int` [eg: 1] 

`<floorNumber>:Int` [eg: 1]

### Step through to the next state
`curl --location --request GET 'localhost:8080/ElevatorControl/step'`

## [Fourth Example](http://www.fourthexample.com) 
