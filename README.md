# Project Name

## Description
This project interacts with a PostgreSQL database, and it requires the proper configuration of the `config.properties` file before it can be executed. The configuration file contains essential database connection details and paths to `pg_dump` and `pg_restore` utilities.

Please follow the steps below to set up the configuration file and make sure the program runs smoothly.

## Prerequisites

Before running the program, make sure to configure the `config.properties` file located in the `src/main/resources/` directory.

The file must include the following key-value pairs:

- `db.password`: The password to access the PostgreSQL database.
- `db.url`: The JDBC URL for connecting to the PostgreSQL database.
- `db.user`: The database username.
- `pgDumpPath`: The path to the `pg_dump` utility for backing up the database.
- `pgRestorePath`: The path to the `pg_restore` utility for restoring the database.
- `db.host`: The host address of the PostgreSQL database (usually `127.0.0.1` for local).
- `db.port`: The port number of the PostgreSQL database (default is `5432`).
- `db.name`: The name of the PostgreSQL database.
- `psql`: The path to the `psql` command-line utility (optional but may be needed for interactions with the database).


### Key Points:
- **Ensure the `config.properties` file is configured correctly** before running the program.
- The program uses these configuration values to connect to the PostgreSQL database and interact with the system.
- **Paths for `pg_dump` and `pg_restore`** need to be correctly set to ensure backups and restores work.

This `README.md` will help guide others through the process of setting up the necessary configuration file before running the program.


### Example `config.properties` File

```properties
# Database Configuration
db.password=pwd
db.url=jdbc:postgresql://localhost:5432/product_demo
db.user=root

# PostgreSQL Utility Paths
pgDumpPath=/path/to/pg_dump
pgRestorePath=/path/to/pg_restore

# Database Connection Details
db.host=127.0.0.1
db.port=5432
db.name=product_demo

# Optional: psql command path (if needed for database interaction)
psql=/path/to/psql