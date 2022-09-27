# Mantleplace

## Running MantlePlace directly from Intellij Idea

- Install Intellij Ultimate version from their [website](https://www.jetbrains.com/idea/)

### Prerequisites:

1. Install `sdkman`:
    ```shell
    curl -s "https://get.sdkman.io" | bash
    source "$HOME/.sdkman/bin/sdkman-init.sh"
    ```
2. Install `Java 11.0.x`:
    1. `sdk install java 11.0.11.hs-adpt`
3. Install `sbt 1.6.2` or the version mentioned in `project/build.properties`:
    1. `sdk install sbt 1.6.2`
4. Install `PostgreSQL 14`:
    1. [MacOS](https://postgresapp.com) (Make default username and password `postgres` and `postgres` respectively)
    2. Ubuntu:
    ```shell
    wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -
    RELEASE=$(lsb_release -cs)
    echo "deb http://apt.postgresql.org/pub/repos/apt/ ${RELEASE}"-pgdg main | sudo tee  /etc/apt/sources.list.d/pgdg.list
    sudo apt update
    sudo apt -y install postgresql-14
    sudo su - postgres or psql -U postgres
    psql -c "alter user postgres with password 'postgres'"
    exit
   ```

### Starting Client in Intellij:

1. Click on `Database` on right then on `+` -> `DataSource` -> `PostgreSQL`
2. Fill the following:
    1. `User`: `postgres`
    2. `Password`: `postgres`
    3. `URL`: `jdbc:postgresql://localhost:5432/postgres`
    4. `Database`: `postgres`
    5. `Save`: `Forever`
    6. Do `Test Connection` this should succeed. Then `Apply` -> `OK`
3. Select `0.sql` contents and execute in `console`
4. Update `MantlePlace.run.xml` with correct values and the run by selecting `MantlePlace configuration` and clicking
   Run icon next to it. Debug is next to it.
5. Click on `Database` on right then on `stack + wrench`, go to `Schema` and select `mantlePlace` and all its tables to
   view.

### Starting Client on Terminal:

1. `sudo su - postgres`
2. `psql`
3. Copy contents of `0.sql` and paste there
4. `\q`
5. `exit`
6. Set all the environment variables in `application.conf` (They are of form `${NAME}`)
7. Create binary for client:
    1. Go to project directory in terminal.
    2. `sbt clean`
    3. `sbt dist`
    4. You get a `assetMantle-1.0.zip` file which contains binary file in `bin` folder.
8. Modify `applicaltion.conf` as per the requirements.
9. Run `./persistenceclient`

## Local development setup

- Install docker on your machine

    - Macos: get it
      from [here https://docs.docker.com/desktop/mac/install/](https://docs.docker.com/desktop/mac/install/)

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

## Run mantleplace

```shell
docker-compose up
```
