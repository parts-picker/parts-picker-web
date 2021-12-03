# parts-picker-web

parts-picker is an easy, accessible part & inventory management system for makers.

*Notice: Please be aware that this is a training project in a really early stage of development* 

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

### Utilities

#### Githooks

This project uses Githooks to manage git hooks inside the repository.
The hooks will automatically run by Githooks.
For more information on how to install & use Githooks, visit its [repository](https://github.com/gabyx/Githooks).  
To install the existing hooks, install Githooks & set up a fresh clone or run the following command inside your repo:
```
git hooks install
```

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

### Code Quality
#### detekt

This project uses detekt for static code analysis. 
The analysis is integrated into gradle. You can find more information about detekt in its [repository](https://github.com/detekt/detekt).


You can run the check by running the following command:

```
./gradlew detekt
```

Reports in different formats can be found at ```./build/reports/detekt```.

#### ktlint

This project uses ktlint as a linter & formatter.
It is integrated in in detekt and will be automatically run along detekt.
If possible, linting errors will be corrected automatically.
More information about ktlint can be found in its [repository](https://github.com/pinterest/ktlint).


