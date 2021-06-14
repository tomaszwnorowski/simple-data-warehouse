# Simple Data Warehouse

Spring Boot application that executes dynamic SQL queries using MyBatis and exposes the results over REST API.

> **_NOTE:_**  This is a recruitment task and therefore efforts were limited to a few hours

## Requirements

Write a simple backend application that exposes data extracted from a csv file via an API:

* API should be generic and efficient
* ETL process for ingesting CSV can be built using any tool

API should operate using following concepts:

- a set of metrics (plus calculated ones) to be aggregated on
- an optional set of dimensions to be grouped by
- an optional set of dimension filters to be filtered on

## Solution

The intention is to built generic and efficient API which depending on the interpretation might mean different things.
On one end of the spectrum we get maximum flexibility by just exposing underlying data store via REST API
(running queries passed via HTTP) or by using tools like [PostgREST](https://postgrest.org/en/stable/). Both of them are
not trying to hide the fact that underlying datastore is a relational database and are fully driven by the functionality
supported by database engine. On the other end of the spectrum we get less flexibility, API is designed to support
specific business use-cases but generic queries are difficult or not possible. For the purpose of this project lets try
to find the middle ground and optimize for a lot of flexibility while still trying to hide the details about the
underlying data store (in case we would like to swap it with time series database).

### ETL

Will be implemented using [Apache Camel](https://camel.apache.org/). Although we are not processing CSV line by line (
which would be desirable in case input size increases), we will perform bulk insert to load data into the warehouse.
When ingestion process completes, we will publish an event that informs rest of the application that we are ready to
process queries. 

> **_NOTE:_** Please note that we read file from `file:src/main/resources/?fileName=analytics.csv` to avoid requesting
it from the internet and risking that build will fail. In the case of packaging application to a jar or docker image
Apache Camel won't be able to read it from classpath and therefore csv location should be changed via `etl.csvLocation`
property (or environment variable).

### Dynamic Queries

In given timeframe it is not possible to write sophisticated validation logic that would prevent malicious users from
attempting SQL injection attacks. We will assume that this is internal tool and likelihood of this kind of problems is
low. Having said that, `Expression` interface was created with this particular use cases in mind (supporting dynamic
expressions and validation).

### API

As already mentioned, we will attempt to hide details about underlying datastore (the best effort approach assuming we have few
hours for whole project). In RESTful spirit we have decided to use POST (since we are not referencing any resource, just
performing computation) instead of GET, but that has few implications. Probably the most important one is that you can't
bookmark (easily store, share) queries. It was not communicated whether this is required and to do it correctly (not exceeding
url length, character encoding, GET with custom DSL in query parameters) would require much more work, and we would probably
end up with something similar to [PostgREST](https://postgrest.org/en/stable/) - even in that case I would argue that
it is easy to make mistakes when having to deal with this type of queries `and=(grade.gte.90,student.is.true,or(age.gte.14,age.is.null))`.

`POST /v1/queries`

```json
{
  "metrics": [
    {
      "metric": "SUM(column_name)",
      "alias": "column_alias"
    }
  ],
  "filters": [
    {
      "key": "column_name",
      "operator": "operator",
      "value": "expected_value"
    }
  ],
  "groups": [
    {
      "group": "column_name"
    }
  ]
}
```

The queries are executed against table with following columns (those are the names that should be used as column names):

* data_source
* campaign
* daily
* clicks
* impressions

Metric can be either static (column_name) or dynamic (expression). Alias can be explicitly provided or derived from the
expression (if alias was not provided). Filter supports standard
SQL [operators](https://www.w3schools.com/sql/sql_operators.asp)
Groups are basically name of columns for group by statement

> **_NOTE:_**  It's clear to see that some abstractions from the database are leaking to the API (e.g. column names,
> operators and functions). In real scenario more attention would be needed to expose only what is needed and can be
> generic enough (relational database agnostic) to be implemented on top of different data store (e.g. time series database)

### Local Run

To be able to bootstrap the project there are only two dependencies:

| Dependency | Comment |
|---|---|
|docker|to run postgresql|
|java|to bootstrap gradle|

Downstream dependencies should be downloaded and configured automatically:

| Dependency | Comment |
|---|---|
|postgres docker image|downloaded by docker|
|gradle distribution|downloaded by gradle wrapper|
|JDK 16|downloaded by gradle toolchain|

### Staring / Stopping Database

Database must be running before starting the application:

```shell
./gradlew postgresUp
./gradlew postgresDown
```

### Running application

```shell
./gradlew bootRun
```

> **_NOTE:_** To both start postgres and application run `./gradlew postgresUp bootRun`

> **_WARNING:_** Apache Camel changes the location of successfully ingested CSV to `.camel` directory (to avoid accidentally
> processing it again). To process it again database should be cleaned or restarted `./gradlew postgresDown postgresUp`.

### Testing

There are few sample unit and integration tests that give an idea how to approach testing for this kind of application.
We want to run fast test first to get immediate feedback whether application logic work and only occasionally run
integration tests. They are definitely not exhaustive and would need more attention. The integration test runs the whole
scenario of ingesting CSV and executing queries. On top of that `api.http` file was included in repository and can be
used to run queries via IntelliJ, it contains all three queries mentioned in the requirements.

|Group|Command|Comment|
|---|---|---|
|unit|`./gradlew test` ||
|integration|`./gradlew integrationTest`|Will automatically start database|

> **_NOTE:_** Make sure the database is not already running when starting integration test or run test with database
> that is already started `./gradlew integrationTest -x postgresUp -x postgresDown`

You can find test results in following directory:
```
build
├── reports
│   └── tests
│       ├── test
│       └── integrationTest
```

> **_IMPORTANT:_** Test classes should be tagged with `@UnitTest` or `@IntegrationTest` annotation

### Code Style
We will be using google java format that is enforced via gradle spotless plugin. To format the code same way via IDE:

**Intellij plugin**

https://plugins.jetbrains.com/plugin/8527-google-java-format

**Intellij style**

https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml

File → Settings → Editor → Code Style

## TODO
* prevent SQL injection (by validating metrics, filter, group expressions)
* increase test coverage
* support sorting of results
* support for paginating of results
* use data format (or remodel existing) that allows streaming of records
* depending on performance requirements usage of some binary protocol might be needed
* fully implement envelope for exceptions - https://datatracker.ietf.org/doc/html/rfc7807
* when metric is specified like this `SUM(column_name)` (implies aggregation) we can be smart about it and implicitly include `column_name` as a group