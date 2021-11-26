# parts-picker-web

An easy, accessible part management system for makers.

## Features
to do
### Planned

## Usage

Make sure to start a POSTGRESQL 13.3 database on port 5433.  
To create a fitting container and start it, run:
```
docker run -i -t --name local_db -p 5433:5432 -e POSTGRES_PASSWORD=local -e POSTGRES_DB=pick_db postgres:13.3-alpine
```
To stop it, run:
```
docker stop local_db
```
To restart it for later use, run:
```
docker start -i local_db
```
## Versioning
This project makes use of semantic versioning for version numbers.
Commits will be automatically tagged with a new version number.  

See https://semver.org/ for more details about semantic versioning.
## Tech stack  
  
We picked a modern tech stack to ensure a fast, feature-rich application.  

Featuring:

Backend
- Kotlin
    - Spring Boot
    - Testing
      - Kotest 
      - Mockk 
- PostgreSQL


This will be extended along the way.

### Testing

Tests are using the ShouldSpec from Kotest. 

Tests are named by the following schema:
```
should "[behave like] expected behavior" when "test conditions"
```
Example:
```
should return status 200 when endpoint called
```

