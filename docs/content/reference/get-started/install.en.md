---
title: Deploying in Self Host
---

DataCap enables users to deploy services to their own machines. This document allows users to learn how to deploy DataCap in an autonomous machine.

### System Requirements

---

!!! warning "Warning"

    The binary package of the software is compiled and tested based on the following system. It has not been tested on other versions and is theoretically supported.

    If you have an unsupported system, use the source code compilation method to actively compile the binary file.

!!!

| System | Version    |
|--------|------------|
| JDK    | `\>=11`    |
| MySQL  | `\>=5.6.x` |

### Prepare the installation package

---

!!! info "Info"

    Download the binary package of the corresponding system from the following address and install it. If you need to install using source code, go to the Developer Documentation module.

!!!

1. [Download the latest release version](../../download.md)

2. Run the following command after downloading the binaries locally
```bash
tar -xvzf datacap-release.tar.gz
```
3. Go to the root directory of the software
```bash
cd datacap
```

### Software configuration

---

For the first installation of the software, you need to import the sql script from the `schema/datacap.sql` file into the MySQL server. Note: The scripts that need to be imported are matched based on the downloaded package

!!! danger "Warning"

    If you are upgrading from another version, run `schema/VERSION/schema.sql`

!!!

All configurations in the DataCap software are in the `configure/application.properties` file.

#### Basic configuration

After importing the `SQL` script, modify the `configure/application.properties` configuration file to modify the configuration information of the MySQL server

```properties
server.port=9096
server.address=localhost
# Fixed serialized data missing for 8 hours
spring.jackson.time-zone=GMT+8
spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
# datacap security management configuration
datacap.security.secret=DataCapSecretKey
datacap.security.expiration=86400000
# datacap editor configuration
datacap.editor.sugs.maxSize=1000
```

- `server.port`: The port on which the service initiates the listener on the server
- `server.address`: Configure the local listening address of the service
- `spring.jackson.time-zone`: Used to configure the time zone
- `spring.jackson.date-format`: Lets you configure the date format
- `datacap.security.secret`: The key used to configure data security management
- `datacap.security.expiration`: The expiration time for configuring data security management
- `datacap.editor.sugs.maxSize`: Lets you configure the maximum number of rows in the data editor

#### Web service configuration

```properties
spring.mvc.throw-exception-if-no-handler-found=true
spring.resources.add-mappings=false
spring.web.resources.add-mappings=true
```

- `spring.mvc.throw-exception-if-no-handler-found`: Used to configure whether an exception is thrown
- `spring.resources.add-mappings`: Lets you configure whether to enable static resource mapping
- `spring.web.resources.add-mappings`: Lets you configure whether to enable static resource mapping

#### Database configuration

!!! danger "Warning"

    If version `>=8.x`, set `allowPublicKeyRetrieval=true`

!!!

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/datacap?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&useSSL=false&useOldAliasMetadataBehavior=true&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=12345678
```

- `spring.datasource.url`: Lets you configure the database connection address
- `spring.datasource.username`: Lets you configure the database username
- `spring.datasource.password`: Used to configure the database password

!!! info "Info"

    All Spring Data configuration parameters are supported

!!!

#### Actuator configuration

```properties
datacap.executor.data=
datacap.executor.way=local
datacap.executor.mode=client
datacap.executor.seatunnel.home=/opt/lib/seatunnel
```

- `datacap.executor.data`: Used to configure the data buffer path of the actuator
- `datacap.executor.way`: It is used to configure the execution mode of the actuator, and different actuators have different execution methods
- `datacap.executor.mode`: It is used to configure the execution mode of the actuator, different actuators have different execution modes
- `datacap.executor.seatunnel.home`: The Apache Seatunnel home directory used to configure the executor

#### Upload the configuration

```properties
datacap.config.data=
datacap.cache.data=
```

- `datacap.config.data`: The path used to configure the upload profile
- `datacap.cache.data`: Lets you configure the path to upload the cache file

#### OpenAi configuration

```properties
datacap.openai.backend=https://api.openai.com
datacap.openai.token=
datacap.openai.model=GPT_35_TURBO_0613
datacap.openai.timeout=30
```

- `datacap.openai.backend`: Used to configure OpenAI's backend address
- `datacap.openai.token`: The token used to configure OpenAI
- `datacap.openai.model`: The model used to configure OpenAI
- `datacap.openai.timeout`: Used to configure the timeout period for OpenAI

#### Primary system configuration

```properties
datacap.registration.enable=
datacap.captcha.enable=
datacap.cache.maximum=100000
datacap.cache.expiration=5
datacap.audit.sql.print=false
```

- `datacap.registration.enable`: Used to configure whether to enable registration
- `datacap.captcha.enable`: This parameter is used to configure whether to enable the verification code
- `datacap.cache.maximum`: Lets you configure the maximum cache value
- `datacap.cache.expiration`: Lets you configure the cache expiration time
- `datacap.audit.sql.print`: This parameter is used to configure whether to print SQL statements

#### Pipeline configuration

```properties
datacap.pipeline.maxRunning=100
datacap.pipeline.maxQueue=200
datacap.pipeline.reset=STOPPED
```

- `datacap.pipeline.maxRunning`: Used to configure the maximum number of runs
- `datacap.pipeline.maxQueue`: Used to configure the maximum queue
- `datacap.pipeline.reset`: Lets you configure a reset policy

#### Storage configuration

---

Storage currently supports

- `Local`: local storage
- `AliOss`: Alibaba Cloud OSS
- `Qiniu`: Qiniu Cloud

##### Local storage configuration

```properties
datacap.fs.type=Local
datacap.fs.access=
datacap.fs.secret=
datacap.fs.endpoint=
datacap.fs.bucket=
```

- `datacap.fs.type`: used to configure the file system type
- `datacap.fs.access`: used to configure file system access, this type can be empty
- `datacap.fs.secret`: used to configure the file system key, this type can be empty
- `datacap.fs.endpoint`: used to configure the file system endpoint. If filled in, it will be appended as a directory **This type can be empty**
- `datacap.fs.bucket`: used to configure the file system bucket, this type can be empty

##### Alibaba Cloud OSS configuration

```properties
datacap.fs.type=AliOss
datacap.fs.access=
datacap.fs.secret=
datacap.fs.endpoint=
datacap.fs.bucket=
```

- `datacap.fs.type`: AliOss

##### Qiniu Cloud Configuration

```properties
datacap.fs.type=Qiniu
datacap.fs.access=
datacap.fs.secret=
datacap.fs.endpoint=
datacap.fs.bucket=
```

- `datacap.fs.type`: Qiniu

#### Experimental features

```properties
datacap.experimental.autoLimit=true
datacap.experimental.data={user.dir}/data
datacap.experimental.avatarPath={username}/avatar/
```

- `datacap.experimental.autoLimit`: This parameter specifies whether to automatically increase the LIMIT
- `datacap.experimental.data`: The data path used to configure the experimental feature
- `datacap.experimental.avatarPath`: Avatar path to configure experimental features

#### Log configuration

!!! warning

    If you need to modify the log configuration, you can simply modify the `configure/logback.xml` configuration file

#### JVM configuration

!!! warning "Warning"

    If you need to customize the JVM configuration, you can simply modify the `configure/jvm.conf` configuration file

!!!

### Software startup

---

> Before starting the service, please install various plug-ins required by the system and execute the command `./bin/install-plugin.sh`

#### Start the service

DataCap service startup is very simple, execute the following script

```bash
./bin/startup.sh
```

#### Discontinuation of service

Stop the service and execute the following script

```bash
./bin/shutdown.sh
```

#### Debug the service

!!! info "Info"

    If you want to debug the system, you can start the service with `./bin/debug.sh`, but it stops when you close the window

!!!