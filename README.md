# gradle-frontend-boilerplate

## A. (Travis CI)Githubリリースページへのデプロイ

### 概要

GithubへのPushを契機に[Travis CI](https://travis-ci.org/)でビルドを実行し、実行バイナリのzip/tarファイルをGithubページへリリースする。


### 準備

下記を実施しておく。

* TravisCIのアカウントを作成する
  * Githubのアカウントでサインインできます
* [Profile](https://travis-ci.org/profile/)ページから、ビルドを実行するリポジトリを選択しておく
* [travis-ci/travis.rb](https://github.com/travis-ci/travis.rb#readme)をインストールする(要Ruby環境)


### ビルドスクリプト

プロジェクトのルートで下記`travis`コマンドを実行し、`.travis.yml`というTravis CIのビルド設定ファイルを作成する。  

* `travis init`
* `travis setup releases`

対話的にプロジェクトの情報を入力していくだけで、`.travis.yml`がほぼできあがります。  
(自分で`.travis.yml`を書いても問題無いです)


.travis.yml
```
language: java  // (1) - Javaビルドの設定
jdk:
- oraclejdk8
script: "./gradlew build"  // (2) - ビルドコマンド
deploy:  // (3) - デプロイ設定
  provider: releases
  api_key:
    secure: ${GITHUB_TOKEN}
  file:  // (4) - GithubのReleaseページにPushするファイル
    - "./build/distributions/gradle-frontend-boilerplate-1.0-SNAPSHOT.tar"
    - "./build/distributions/gradle-frontend-boilerplate-1.0-SNAPSHOT.zip"
  on:
    repo: kaakaa/gradle-frontend-boilerplate
    tags: true  // (5) - tag付きのコミットのみデプロイが行われるようにする
```

#### (1) Javaビルドの設定

`travis init`コマンドでJavaプロジェクトを選択すると、このように生成される。  
デフォルトではJavaのバーージョンにJDK6,7が指定されているので`oraclejdk8`にしておく。  

#### (2) ビルドコマンド

Travisで実行するビルドタスク指定する。

#### (3) デプロイ設定

`travis setup releases`コマンドで作成される。  

#### (4) GithubのReleaseページにPushするファイル

ここで指定したファイルが、Githubのリリースページにダウンロードできる形で公開される。  
今回は、`gradle assembleDist`の成果物であるzip/tarファイルがリリースされるようにした。  

#### (5) tag付きのコミットのみデプロイが行われるようにする

Pushするたびにリリースされるのも辛いので、タグ付きコミットのみデプロイが行われるようにする
