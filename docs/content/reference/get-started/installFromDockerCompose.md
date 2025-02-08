---
title: Docker Compose 部署
---

DataCap 项目提供 Docker Compose 方式部署，通过下载 [docker-compose.yml](https://github.com/devlive-community/datacap/blob/dev/docker-compose.yml) 文件，或者使用以下代码进行服务部署。

#### 简化版

---

> 只有基础的一些功能

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

#### 进阶版

---

> 该方式包含了 `数据集` 功能

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

!!! warning "注意"

    需要同时下载一下多个文件：

    - [datacap.sql](https://github.com/devlive-community/datacap/blob/dev/configure/schema/datacap.sql)
    - [application.properties](https://github.com/devlive-community/datacap/blob/dev/configure/docker/application.properties)

    下载完成后将他们放置到指定目录，也就是 `./configure/docker/` 和 `./configure/schema/` 如果需要自定义目录，可以修改 `docker-compose.yml` 文件中挂载的 `volumes` 配置即可。

!!!

## 启动服务

---

以上工作完成后，使用以下命令进行启动服务。**必须在包含 docker-compose.yml 文件的目录下执行**

```bash
docker-compose up
```

如果需要后台启动使用以下命令

```bash
docker-compose up -d
```

启动成功后，浏览器打开 http://localhost:9096/ 即可看到网站。

## 停止服务

---

停止服务需要使用以下命令

```bash
docker-compose down
```
