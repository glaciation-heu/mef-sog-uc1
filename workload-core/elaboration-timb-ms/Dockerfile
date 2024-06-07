FROM maven:3.8.8-ibmjava-8 as BUILDER
WORKDIR /workspace
COPY /pom.xml pom.xml
COPY /src/ src
RUN mvn --batch-mode -f pom.xml clean package -DskipTests

FROM amazoncorretto:8
ARG DEPENDENCY=/workspace
COPY --from=BUILDER ${DEPENDENCY}/target/elaboration-timb-ms-1.0.0.jar elaboration-timb-ms.jar
ENTRYPOINT ["java","-jar","/elaboration-timb-ms.jar"]
