# Mantleplace



## Local development setup

- Install docker on your machine

  - Macos: get it from [here https://docs.docker.com/desktop/mac/install/](https://docs.docker.com/desktop/mac/install/)

  - Linux

  ```shell
  curl -sL get.docker.com | sudo bash
  docker buildx install
  docker version
  docker buildx version
  ```
  

- Install `docker-compose` on your machine

  - Macos: get it from [here https://docs.docker.com/compose/install/](https://docs.docker.com/compose/install/)

  - Linux

  ```shell
  sudo curl -L "https://github.com/docker/compose/releases/download/$(curl -s https://api.github.com/repos/docker/compose/releases/latest | grep tag_name | cut -d : -f2 | cut -d , -f1 | xargs)/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
  sudo chmod +x /usr/local/bin/docker-compose
  sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
  docker-compose version
  ```
  
 ## Note 
 
Before running the mantleplace on your local, make sure that your postgresql database is enabled.


## Running Mantleplace directly from Intellij Idea

- Install Intellij Ultimate version from there [website https://www.jetbrains.com/idea/](https://www.jetbrains.com/idea/)
- Follow the steps given in client repo from [here https://github.com/AssetMantle/client](https://github.com/AssetMantle/client)
- To setup the database properly follow the steps [here https://github.com/AssetMantle/client#starting-client-in-intellij](https://github.com/AssetMantle/client#starting-client-in-intellij)


  
 

## Run mantleplace

```shell
docker-compose up
```
