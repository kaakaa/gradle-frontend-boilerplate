# gradle-frontend-boilerplate

## C. GradleでDockerイメージを作成する

### 概要

GithubへのPushを契機にTravisCIでDockerビルドを行い、DockerイメージをDockerHubへPushする

* * *

TravisCI上ではDocker Remote APIが起動していなさそうなので、先ほど作った`buildDockerImage`タスクは使用せず、普通の`docker build`コマンドでDockerイメージを作成する。


### ビルドスクリプト

.travis.yml
```gradle
sudo: required  // (1) - Dockerを使うことを宣言
services:
- docker
language: java
jdk:
- oraclejdk8
env:  // (2) - Dockerビルド、DockerHubへのPushに使う情報を宣言する
  global:
  - REPO=kaakaa/gradle-frontend-boilerplate
  - COMMIT=${TRAVIS_COMMIT::8}
  - TAG=${COMMIT}
  - secure: <encrypted env>
  - secure: <encrypted env>
  - secure: <encrypted env>

script: "./gradlew build"
deploy:
  provider: releases
  api_key:
    secure: <encrypted repo>
  file:
  - "./build/distributions/gradle-frontend-boilerplate-1.0-SNAPSHOT.tar"
  - "./build/distributions/gradle-frontend-boilerplate-1.0-SNAPSHOT.zip"
  on:
    repo: kaakaa/gradle-frontend-boilerplate
    tags: true

after_deploy:  // (3) - Dockerビルド、DockerHubへのPushをする
- docker login -e $DOCKER_EMAIL -u $DOCKER_USER -p $DOCKER_PASS
- docker build -f Dockerfile -t $REPO:$COMMIT .
- docker tag $REPO:$COMMIT $REPO:$TAG
- docker push $REPO:$COMMIT
```

#### (1) Dockerを使うことを宣言

Travisビルドで`docker`コマンドを使用するには、この３行が必要。  
[Using Docker in Builds - Travis CI](https://docs.travis-ci.com/user/docker/)

#### (2) Dockerビルド、DockerHubへのPushに使う情報を宣言する

Dockerイメージ名やDockerHubへPushする際の名前の情報などを宣言している。  
`${TRAVIS_COMMIT}`はTravisでのビルドが走る際のgitのコミットIDを示しており、`${TRAVIS_COMMIT::8}`で、その先頭８文字を取得できる。  
[Environment Variables - Travis CI](https://docs.travis-ci.com/user/environment-variables/#Default-Environment-Variables)

`secure`文字列はDockerHubへのログイン情報をencryptした文字列。  
下記のコマンドで生成できる。

```
travis encrypt DOCKEREMAIL=hoge@exampl.com --add env.global   
travis encrypt DOCKER_USER=kaakaa_ --add env.global   
travis encrypt DOCKER_PASS=password --add env.global   
```

[Using Docker in Builds - Travis CI](https://docs.travis-ci.com/user/docker/#Pushing-a-Docker-Image-to-a-Registry)  
[Encryption keys - Travis CI](https://docs.travis-ci.com/user/encryption-keys/)  

#### (3) Dockerビルド、DockerHubへのPushをする

dockerコマンドを普通に打ってるだけ。  

TravisではDocker Remote APIが起動してないのでgradleの`buildDockerImage`タスクが使えないのが悲しい。

* * *

build.gradle
```gradle
build.dependsOn installDist
```

Travis上で実行している`build`タスクが、Dockerビルドで必要なファイルを生成するための`installDist`タスクに依存するよう、どこかに上記１行を追加する。  
悲しい。
