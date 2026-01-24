# Backend Engineer A – Responsibilities & Contribution

Role Overview

The Backend Engineer A role was responsible for designing and implementing the core backend infrastructure of the application. This role focused on business logic implementation, data model definition, unit testing, and DevOps activities required for continuous integration and containerized deployment, in full compliance with the project specification.

## Backend Development

+ Implemented business logic for selected backend microservices using Spring Boot 4.0.1 and Java 25

+ Defined domain models and database entities using JPA/Hibernate

+ Implemented application rules related to:

+  user authentication and authorization

+   public and private profile visibility

+   follow requests and approval workflows

+   blocking logic and content visibility restrictions

+ Applied a layered backend architecture (Controller – Service – Repository) to ensure clear separation of concerns

## Unit Testing & Code Quality

+ Developed JUnit 5 unit tests covering all implemented business logic

+ Used Mockito for mocking dependencies and isolating service-layer functionality

+ Ensured minimum 70% unit test coverage, as required by the project assignment

+ Integrated JaCoCo for measuring and reporting code coverage

+ Configured CI workflows so that every Pull Request triggers automated unit test execution

## DevOps & CI/CD

+ Implemented CI pipelines using GitHub Actions, including:

+   automatic execution of unit tests on each Pull Request

+   automated build, test execution, and Docker image creation on each commit to the main branch

+ Created and maintained Dockerfile configurations for backend services

+ Designed and managed docker-compose files for orchestrating backend services and database containers

+ Implemented Docker image versioning using timestamp-based tags (yyyyMMdd-HHmmss)

+ Published Docker images to a public Docker repository, in accordance with project requirements

## Technologies Used

+ Java 25 (LTS)

+ Spring Boot 4.0.1

+ Maven

+ Spring Data JPA / Hibernate

+ PostgreSQL

+ JUnit 5, Mockito, JaCoCo

+ Docker & Docker Compose

+ GitHub Actions (CI/CD)
