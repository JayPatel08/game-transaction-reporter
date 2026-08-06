# Game Transaction Reporter

A Spring Web MVC application for querying, filtering, and reporting game transaction data (`bet` and `win` operations) with paginated results and aggregate summary statistics.

---

## Key Features

* **Date-bounded Reporting**: Query game transaction records within mandatory start and end timestamps.
* **Dynamic Multi-Field Filtering**: Filter records by Account ID, Platform Transaction ID, Game Transaction ID, Game ID, and Transaction Type (`bet` / `win`).
* **Server-Side Pagination & Sorting**: Paginate results (25 or 50 records per page) and sort across table columns in ascending or descending order.
* **Aggregate Financial Summaries**: Compute real-time totals for Total Bets, Total Wins, and Net Balance (Wins − Bets) for queried records.
* **CSV Export Generation**: Export filtered transaction datasets directly to a downloadable, formatted `.csv` file preserving applied criteria and sorting order.
* **Request Correlation & MDC Logging**: Trace HTTP requests end-to-end using `X-Request-ID` header injection and SLF4J MDC context.
* **Performance & Audit Execution Tracking**: AspectJ AOP interceptor monitoring method runtimes and flagging slow database/service calls (> 300 ms).
* **Automated Data Seeding**: Container initialization scripts automatically seed MySQL database tables with sample transaction records on first launch.

---

## Tech Stack

* **JDK**: Java 17
* **Framework**: Spring Web MVC 6.1, Spring Data JPA 3.2, Hibernate 6.4 (Jakarta EE 10 deployment target)
* **Database**: MySQL 8.0
* **View Layer**: Jakarta JSTL 3.0, JSP, Vanilla CSS
* **Build System**: Apache Maven 3.8+
* **Runtime / Container**: Apache Tomcat 10.1, Docker Compose

---

## Getting Started

### Prerequisites

* Docker Desktop (or Docker Engine + Docker Compose)
* Java 17 and Maven 3.8+ (for local CLI development without Docker)

---

### Running with Docker Compose (Recommended)

1. Start the service and database:
   ```bash
   docker compose up --build
   ```
2. Access the application in your browser at:
   ```
   http://localhost:8080/report
   ```

* **Database Initialization**: The MySQL container automatically executes `database/init/transaction_table_creation_with_data.sql` on first boot.
* **Sample Data Notice**: Sample records are dated between **July 2025** and **December 2025** (e.g., `2025-07-31` to `2025-12-24`). Queries outside this range will return empty results.
* **Live File Watch**: Docker Compose is configured to sync JSP and CSS static file changes automatically without container restarts, while Java/POM modifications trigger container rebuilds.

---

### Local Build & Testing

Build the WAR package and execute unit tests locally:

```bash
cd backend

# Run test suite
mvn test

# Package WAR file
mvn clean package
```

---

## System Design & Architecture

### Backend (Server)

* **Configuration**: Pure Java-based Spring configuration (`AppInitializer`, `WebMvcConfig`, `PersistenceJPAConfig`) replacing traditional `web.xml`.
* **Controller Layer**: `TransactionController` validates mandatory inputs (start/end date bounds, enforcing page size limits of 25 or 50), delegates queries, and computes total bet/win/net summary totals for the active page.
* **Service & Persistence**: `TransactionServiceImpl` interacts with `TransactionRepository`. Dynamic filtering across optional fields (`accountId`, `platformTranId`, `gameTranId`, `gameId`, `tranType`) is executed via JPA Criteria API in `TransactionSpecification`.
* **Correlation & Audit Logging**:
  * `RequestCorrelationFilter` attaches or generates `X-Request-ID` headers and populates SLF4J `MDC` context per HTTP request.
  * `LoggingAspect` provides AOP entry/exit logging and flags slow execution paths exceeding 300 ms.

---

### Frontend (Client)

* **View Engine**: Server-side rendered JSP templates located in `backend/src/main/webapp/WEB-INF/views/report.jsp` using Jakarta JSTL core (`c:`) and format (`fmt:`) tags.
* **State Management**: Search filters, pagination index, page size, and column sorting directives (`sortCol`, `sortDir`) are persisted across requests via GET query parameters and hidden form controls.
* **Static Assets**: Custom responsive layout stylesheet defined in `backend/src/main/webapp/resources/css/style.css`, exposed through Spring `ResourceHandlerRegistry`.

---

## Project Structure

```
game-transaction-reporter/
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/bet99/reporter/
│       │   │   ├── aspect/          # AspectJ logging & performance tracing
│       │   │   ├── config/          # Spring MVC & JPA Java configuration
│       │   │   ├── controller/      # Web Controllers
│       │   │   ├── entity/          # JPA Domain Entities
│       │   │   ├── filter/          # MDC Correlation ID servlet filter
│       │   │   ├── repository/      # Spring Data Repositories & Specifications
│       │   │   └── service/         # Business logic layer
│       │   ├── resources/           # Application properties & logback config
│       │   └── webapp/              # JSP views & static web assets (CSS/JS)
│       └── test/                    # JUnit 5 & Mockito test cases
├── database/
│   └── init/                        # MySQL initialization SQL scripts
├── docker-compose.yml
└── README.md
```

---

## Unit Test Suite

### Service Layer (`TransactionServiceImplTest`)
* **Valid Query Execution**: Verifies `getReport` returns a populated `Page<Transaction>` when valid criteria are provided.
* **Null Start Date Validation**: Ensures `IllegalArgumentException` is thrown when `startDate` is missing.
* **Null End Date Validation**: Ensures `IllegalArgumentException` is thrown when `endDate` is missing.
* **Date Sequence Validation**: Ensures `IllegalArgumentException` is thrown when `startDate` is after `endDate`.
* **Descending Sort Configuration**: Validates that sort direction and page size properties are correctly configured on the Spring Data `Pageable` instance.
* **CSV Export Dataset Fetching**: Verifies `getAllTransactionsForExport` fetches unpaginated matching transaction records.

### Repository & Specification (`TransactionSpecificationTest`)
* **Date Range Predicates**: Verifies JPA `CriteriaBuilder` builds `greaterThanOrEqualTo` and `lessThanOrEqualTo` predicates for valid date ranges.
* **Numeric Account ID Guard**: Verifies `IllegalArgumentException` is thrown when non-numeric account IDs are supplied to the specification.
* **Optional Field Predicates**: Verifies `CriteriaBuilder` constructs equality predicates for optional parameters (`accountId`, `platformTranId`, `gameTranId`, `gameId`, `tranType`).
