---
title: Docker Deployment
---

The DataCap project provides [devliveorg/datacap](https://hub.docker.com/r/devliveorg/datacap) Docker images containing the DataCap server and default configurations. The Docker images are published to Docker Hub and can be used with Docker runtime.

### Running the Container

To run DataCap in Docker, you must have Docker Engine installed on your machine. You can get it from the [Docker website](https://hub.docker.com/), or use your operating system's packaging system.

Use the docker command to run a container from the [devliveorg/datacap](https://hub.docker.com/r/devliveorg/datacap) image. Give it the name 'datacap' to make it easier to reference later. Run it in the background and map the default DataCap port (i.e., `9096`) from inside the container to port `9096` on your workstation.

```bash
docker run -d -p 9909:9096 --name datacap devliveorg/datacap
```

If you don't specify a container image tag, it defaults to `latest`, but you can use any of the published DataCap versions, such as `devliveorg/datacap:2024.4`.

!!! danger "Warning"

    External MySQL configuration mounting is required. The service can be started using:

    ```bash
    docker run -d -p 9096:9096 -v /root/application.properties:/opt/app/datacap/configure/application.properties --name datacap devliveorg/datacap
    ```

    Assuming your configuration file is at `/root/application.properties`. For other paths, please specify the absolute path.

!!!

Run `docker ps` to see all containers running in the background.

```bash
-> % docker ps
CONTAINER ID   IMAGE                    COMMAND               CREATED      STATUS          PORTS                    NAMES
2096fba19e2a   devliveorg/datacap:latest           "sh ./bin/debug.sh"   5 days ago   Up 14 seconds   0.0.0.0:9909->9096/tcp   datacap
```

### Cleanup

You can stop and start the container using the `docker stop datacap` and `docker start datacap` commands. To completely remove a stopped container, run `docker rm datacap`.