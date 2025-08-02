## Build Instructions

#### Install PostgreSQL

- Postgres can be installed from `https://www.postgresql.org/download/`
- PGAdmin or DBeaver for GUI DB managers (or any other DB tool you'd like to use)
  - `https://www.pgadmin.org/download/`
  - `https://dbeaver.io/download/`

#### Set up the DB Connection

- Edit the `application.properties` file for DB connections. This is only for development and should not have any important real prod info.
- Enter the DB name, user, and password.
- Run the `schema.sql` file in a new PostgreSQL DB. The default name is `student_management`.

#### Other options

- The default port can be changed with `server.port`
- CORS is only allowed for the domain specified in `cors.allowed-origins`. This should match the frontend URL.
- OpenAPI/Swagger can be used to test the API without the frontend at `/swagger-ui/index.html`

#### Run and build the app

- Use any IDE that supports running a spring boot app (Eclipse/IntelliJ/VSCode) as normal.
- Alternatively, use `mvn spring-boot:run` to run the app from the command line.

#### Overview

- The backend is broken up into a controller, service, and persistence layer.
- Each of the layers has a base interface that is implemented.
- Custom exceptions for handling resources that already exist or don't exist.
- The persistence layer uses Spring's `NamedParameterJdbcTemplate`.
- The DB uses a junction table to connect students with courses as a many to many relation.
- A set of unit tests are included for the controllers and services.

#### Additional Comments

Some areas that could be revisited with more time:

- Additional logging.
- Add a standard format for responses on inserts, updates, and deletions.
- Further testing for query performance.
- Add some data to lookup tables (like a fixed list of majors).
- Look into caching strategies.
