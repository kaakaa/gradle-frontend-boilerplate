# gradle-frontend-boilerplate

## B. GradleでDockerイメージを作成する

### 概要

[bmuschko/gradle-docker-plugin](https://github.com/bmuschko/gradle-docker-plugin)を使って、Gradleから`docker build`を実行するタスクを定義する。  

### 準備

下記を実施しておく。

* dockerをインストールする
* `unix:///var/run/docker.sock`でDocker Remote APIにアクセスできるようにする
  * [Remote API](https://docs.docker.com/engine/reference/api/docker_remote_api/)

### 実行

```
git clone https://github.com/kaakaa/gradle-frontend-boilerplate.git
cd gradle-frontend-boilerplate
git checkout sec_B

./gradlew buildDockerImage
```

### ビルドスクリプト

長くなってきたので掻い摘んで。  

build.gradle
```gradle
plugins {
    id 'java'
    id 'application'
    id 'com.moowork.grunt' version '0.11'
    id 'com.moowork.node' version '0.11'
    id 'com.bmuschko.docker-remote-api' version '2.6.6'  // (1) - GradleからDockerを操作するプラグインを追加する
}

...


/** For docker build */
docker {
    url = 'unix:///var/run/docker.sock'  // (2) - Docker Remote APIのアドレスを指定する
}

import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage

task buildDockerImage(type: DockerBuildImage) {  // (3) - docker buildを実行するタスクを定義する
    dependsOn clean, installDist  // (4) - docker buildに必要なファイルを生成するタスクへの依存を宣言する
    inputDir = rootDir
    tag = 'kaakaa/gradle-frontend-boilerplate'
}

/** Other settings */
task wrapper(type: Wrapper) {
    gradleVersion = '2.12'
}
```

#### (1) GradleからDockerを操作するプラグインを追加する

今回は[bmuschko/gradle-docker-plugin: Gradle plugin for managing Docker images and containers.](https://github.com/bmuschko/gradle-docker-plugin)を使う。  

GradleでDocker操作するプラグインは、同じぐらいのスター数でいくつもあるのでデファクトが無い感じ…

* [Transmode/gradle-docker: A Gradle plugin to build Docker images from the build script.](https://github.com/Transmode/gradle-docker)
* [gesellix/gradle-docker-plugin: Gradle Docker plugin](https://github.com/gesellix/gradle-docker-plugin)

#### (2) Docker Remote APIのアドレスを指定する

予め起動していたDocker Remote APIのアドレスを指定する

#### (3) docker buildを実行するタスクを定義する

`bmuschko/gradle-docker-plugin`のお作法通り、`docker build`を実行するタスクを定義する。  
tag名はお好みで。

#### (4) docker buildに必要なファイルを生成するタスクへの依存を宣言する

`installDist`タスクの成果物をDockerイメージに含めるため、`buildDockerImage`タスクが`installDist`タスクのあとに実行されるよう設定している。

* * *

Dockerfile
```docker
FROM java

ADD build/install/gradle-frontend-boilerplate /usr/local/src

ENTRYPOINT ["sh", "-c", "/usr/local/src/bin/gradle-frontend-boilerplate"]
```

`installDist`の成果物をDockerイメージの`/usr/local/src`に追加し、起動スクリプトを実行するだけのDockerfileです。

