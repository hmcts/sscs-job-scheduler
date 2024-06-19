# SSCS Job Scheduler
[![Build Status](https://travis-ci.org/hmcts/sscs-job-scheduler.svg?branch=master)](https://travis-ci.org/hmcts/sscs-job-scheduler)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/db1d536343474c40967ab9b236044e1d)](https://www.codacy.com/app/HMCTS/sscs-job-scheduler)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/db1d536343474c40967ab9b236044e1d)](https://www.codacy.com/app/HMCTS/sscs-job-scheduler)

The job scheduler library allows other services to schedule callbacks in the future. For example, scheduler
can call back to a specified handler next week Sunday at 2am.

## Getting started

### Prerequisites
- [JDK 17](https://java.com)

## Developing

### Unit tests
To run all unit tests execute the following command:
```bash
./gradlew test
```

### Code quality checks
We use [checkstyle](http://checkstyle.sourceforge.net/) and [PMD](https://pmd.github.io/).  
To run all checks execute the following command:
```bash
./gradlew check
```

## Job management

The service manages its clients' jobs with [Quartz](http://www.quartz-scheduler.org/).  
Applications importing this project as a JAR will be required to use their own database. 
This could be a PostgreSQL database for persisting those jobs. Also, Quartz can be configured
to run in cluster mode, i.e. the load will be distributed among multiple nodes, each
running different jobs.

## Configuration Example

```

job.scheduler:
  quartzProperties:
    org.quartz:
      scheduler:
        instanceId: AUTO
      dataSource:
        jobscheduler:
          driver: org.postgresql.Driver
          URL: ${spring.datasource.url}
          user: ${spring.datasource.username}
          password: ${spring.datasource.password}
      jobStore:
        isClustered: true
        class: org.quartz.impl.jdbcjobstore.JobStoreTX
        driverDelegateClass: org.quartz.impl.jdbcjobstore.PostgreSQLDelegate
        dataSource: jobscheduler
      threadPool:
        class: org.quartz.simpl.SimpleThreadPool
        threadCount: 8

  retryPolicy:
    maxNumberOfJobExecutions: ${MAX_NUMBER_OF_JOB_EXECUTIONS:5}
    delayBetweenAttemptsInMs: ${DELAY_BETWEEN_JOB_ATTEMPTS_MS:15000}

```

## Data security

As of now, job information is stored in an unencrypted form. This means that clients
of this service must not include any sensitive information (tokens, passwords, personally
identifiable information, etc.) in their requests.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
