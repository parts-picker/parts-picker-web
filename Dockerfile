ARG CACHE_HOME=/home/gradle/cache_home

FROM gradle:8.8-jdk21-alpine AS CACHE
ARG CACHE_HOME
ENV LOCAL_CACHE_HOME=$CACHE_HOME
ENV GRADLE_USER_HOME ${LOCAL_CACHE_HOME}

RUN mkdir -p ${LOCAL_CACHE_HOME}
COPY build.gradle.kts /home/gradle/build/
WORKDIR /home/gradle/build
RUN gradle clean build -i -x bootJar


FROM gradle:8.8-jdk21-alpine AS BUILD
ARG CACHE_HOME
ENV LOCAL_CACHE_HOME=$CACHE_HOME
ENV BUILD_HOME=/home/gradle/src

COPY --from=CACHE $CACHE_HOME /home/gradle/.gradle
COPY --chown=gradle:gradle . $BUILD_HOME
WORKDIR $BUILD_HOME
RUN gradle bootJar -i


FROM eclipse-temurin:21-jdk-alpine
ENV RUN_DIR=/opt/app
RUN mkdir $RUN_DIR
COPY --from=BUILD /home/gradle/src/build/libs/web-docker-ready.jar $RUN_DIR/app.jar
ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]



