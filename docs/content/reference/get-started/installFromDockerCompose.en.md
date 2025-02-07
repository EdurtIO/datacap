---
title: Deploying in Docker Compose
---

The DataCap project provides Docker Compose deployment by downloading the [docker-compose.yml](https://github.com/devlive-community/datacap/blob/dev/docker-compose.yml) file, or using the following code for service deploy.

#### Simplified version

---

> Only some basic functions

```yaml
version: '3.8'

services:
  app-mysql:
    image: mysql:latest
    environment:
      MYSQL_ROOT_PASSWORD: 12345678
      MYSQL_DATABASE: datacap
    ports:
      - "3306:3306"
    volumes:
      - ./configure/schema/datacap.sql:/docker-entrypoint-initdb.d/schema.sql

  app-datacap:
    image: qianmoq/datacap:latest
    restart: always
    ports:
      - "9099:9099"
    depends_on:
      - app-mysql
      - app-clickhouse
    volumes:
      - ./configure/docker/application.properties:/opt/app/datacap/configure/application.properties
```

#### Advanced version

---

> This method includes the `dataset` function

```yaml
version: '3.8'

services:
  app-mysql:
    image: mysql:latest
    environment:
      MYSQL_ROOT_PASSWORD: 12345678
      MYSQL_DATABASE: datacap
    ports:
      - "3306:3306"
    volumes:
      - ./configure/schema/datacap.sql:/docker-entrypoint-initdb.d/schema.sql

  app-clickhouse:
    image: clickhouse/clickhouse-server:latest
    restart: always
    ports:
      - "8123:8123"
    environment:
      - CLICKHOUSE_DB=datacap

  app-datacap:
    image: qianmoq/datacap:latest
    restart: always
    ports:
      - "9099:9099"
    depends_on:
      - app-mysql
      - app-clickhouse
    volumes:
      - ./configure/docker/application.properties:/opt/app/datacap/configure/application.properties
```

!!! warning "Warning"

    You need to download multiple files at the same time:

    - [datacap.sql](https://github.com/devlive-community/datacap/blob/dev/configure/schema/datacap.sql)
    - [application.properties](https://github.com/devlive-community/datacap/blob/dev/configure/docker/application.properties)

    After the download is completed, place them in the specified directory, that is, `./configure/docker/` and `./configure/schema/`. If you need to customize the directory, you can modify the ` mounted in the `docker-compose.yml` file. volumes` can be configured.

!!!

## Start service

---

After the above work is completed, use the following command to start the service. **Must be executed in the directory containing the docker-compose.yml file**

```bash
docker-compose up
```

If you need to start in the background, use the following command

```bash
docker-compose up -d
```
After successful startup, open http://localhost:9096/ in the browser to see the website.

## Out of service

---

To stop the service you need to use the following command

```bash
docker-compose down
```
