# FloodPath

Real-time waterlogging and flood-aware safe route system for a Web Technologies project.

## Stack
- HTML, CSS, JavaScript, JSP
- Java 26
- Servlets 4.0 (`javax.*`)
- WebSocket 1.1
- MySQL + JDBC
- Maven WAR
- Apache Tomcat 9.0.120

## Features
- User registration/login
- Predefined admin login
- User flood reports with optional photo evidence
- Separate User and Admin dashboard structure
- Live road-status map
- Dijkstra-style flood-aware routing
- Admin rainfall/water simulation
- WebSocket live updates
- Report verification/rejection

## Build
`mvn clean package`

Deploy `target/FloodPath.war` to Tomcat 9 `webapps/`.

Default admin: `admin@floodpath.com` / `Admin@123`


## Demo login accounts
- Admin: admin@floodpath.local / Admin@123
- User: user1@gmail.com / user123

## Password storage
This academic/demo version intentionally stores passwords as plain text in the `users.password` column because the project requirement is to demonstrate direct password storage. Do not use this authentication approach in production.
