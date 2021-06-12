# 2048 Web Application

## GitLab

### Install GitLab runner locally in a Docker container:

1. Create a Docker volume to store runner config:
```shell
docker volume create gitlab-runner-vol
```

2. Register runner with ```docker``` executor:
```shell
docker run --rm -it -v gitlab-runner-vol:/etc/gitlab-runner gitlab/gitlab-runner:latest register
```
 * default image for docker executor: ```maven:3.8.1-jdk-11```

3. Register runner with ```shell``` executor:
```shell
docker run --rm -it -v gitlab-runner-vol:/etc/gitlab-runner gitlab/gitlab-runner:latest register
```

4. Tweak runner config:
```shell
docker run --rm -it -v gitlab-runner-vol:/etc/gitlab-runner debian

apt update
apt install nano
nano /etc/gitlab-runner/config.toml
```
 * add ```pull_policy = "if-not-present"``` to runner config, i.e.:
```yaml
[[runners]]
  ...
  executor = "docker"
  ...
  [runners.docker]
    ...
    pull_policy = "if-not-present"    # pull docker images on demand and not always
```

5. Create and run container (using a WSL2 Linux shell):
```shell
docker run -d --name gitlab-runner --restart always \
           -v gitlab-runner-vol:/etc/gitlab-runner \
           -v /var/run/docker.sock:/var/run/docker.sock \
           -v /usr/bin/docker:/usr/bin/docker \
           -v /usr/bin/com.docker.cli:/usr/bin/com.docker.cli \
           gitlab/gitlab-runner:latest
```
 * ```/var/run/docker.sock``` must be read-/writeable for all, so ```chmod 666 /var/run/docker.sock``` as root on the host if necessary
 * only works this way, if Docker uses WSL2 back-end
 * otherwise, GitLab Runner should be installed locally and not in a container

6. If the runner is not needed anymore, stop and remove the container and volume:
```shell
docker stop gitlab-runner
docker rm gitlab-runner
docker volume rm gitlab-runner-vol
```

## GitHub

### Setup local CI/CD environment for deployment job in GitHub Actions:

1. Create and run a container for hosting the servlet using Tomcat:
```shell
docker run -d --name github-runner -p 8081:8080 tomcat:9.0.46-jdk11
```

2. Open a shell in the container:
```shell
docker exec -it github-runner /bin/bash
```

3. Install Maven in the container:
```shell
apt update
apt install maven
```

4. Change permissions on ```/usr/local/tomcat/webapps``` to world read-/writeable:
```shell
chmod 777 /usr/local/tomcat/webapps
```

5. Create a new user for running the GitHub self-hosted runner and switch to that user:
```shell
adduser github-runner
su -l github-runner
```

6. Install and run self-hosted runner for Linux X64 as explained in the settings of your repository (Settings &rarr; Actions &rarr; Runners)

7. To stop the runner, press ```Ctrl + C```.

8. If the runner is not needed anymore, ```exit``` the container shell and then stop and remove the container:
```shell
docker stop github-runner
docker rm github-runner
```
