# gradle-frontend-boilerplate

## 3. Gradleのビルドフローにnpm/grunt/bowerを組み込む

### 概要

Gradleのビルドフローに`npm`,`grunt`,`bower`を組み込む。

厳密には、Gradleプラグイン`com.moowork.grunt`を使って`npm install`とGruntタスクを実行するだけ。  
`bower`はGruntタスクとして操作する。

* * *

Javaプロジェクトからbootstrapなどのフロントエンド資産を利用する場合、[WebJars - Web Libraries in Jars](http://www.webjars.org/)を利用するなどの方法もあるが、今回はフロントエンドツールに慣れるために、なるべくフロントエンド部分はGradleから**実行する**だけにしたかったからである.

### 実行

```
git clone https://github.com/kaakaa/gradle-frontend-boilerplate.git
cd gradle-frontend-boilerplate
git checkout sec_3

./gradlew run
```

### ビルドスクリプト

build.gradle
```Gradle
plugins {
    id 'java'
    id 'application'
    id 'com.moowork.grunt' version '0.11'  // (1) - npm/gruntを操作するGradleプラグインを指定する
    id 'com.moowork.node' version '0.11'
}

group 'org.kaakaa'
version '1.0-SNAPSHOT'

/** For Java build */
sourceCompatibility = '1.8'
targetCompatibility = '1.8'

mainClassName = 'org.kaakaa.spark.Main'

sourceSets {  // (2) - フロントエンドビルドの成果物をJavaビルドのリソースに組み込む
    main {
        resources {
            srcDir 'src/main/resources'
            srcDir 'src/main/web/bower_components'
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compile 'com.sparkjava:spark-core:2.3'
    compile 'com.sparkjava:spark-template-jade:2.3'
}


/** For frontend build */
node {  // (3) - nodeの設定
    version = '4.4.0'
    download = true
    workDir = file("${projectDir}/src/main/web")
    nodeModulesDir = file("${projectDir}/src/main/web")
}

grunt {
    workDir = file("${projectDir}/src/main/web")
}

grunt_build.dependsOn 'installGrunt'  //  (4) - gruntの実行環境を構築する
grunt_build.dependsOn 'npmInstall'
processResources.dependsOn grunt_build

/** Other settings */
task wrapper(type: Wrapper) {
    gradleVersion = '2.12'
}
```

#### (1) npm/gruntを操作するGradleプラグインを指定する

[com.moowork.grunt](https://github.com/srs/gradle-grunt-plugin)プラグインではGruntをインストールする`installGrunt`タスクと、Gruntのタスクを実行する`grunt_<task>`ルールがGradleタスクとして追加される。  
今回は`grunt_build`タスクの実行によりgruntの`build`タスクを実行することを想定している。

また、[com.moowork.node](https://github.com/srs/gradle-node-plugin)プラグインで、node.js/npmのインストールとnode/npmタスクの実行をGradleから行うことができる。  

#### (2) フロントエンドビルドの成果物をJavaビルドのリソースに組み込む

今回、フロントエンド系の資産については`src/main/web`内で扱うようにしていますが、そこで生成されたjs/cssファイルをJavaビルドの成果物に含めるために`src/main/web/bower_components`をresourcesフォルダとして追加しています。

#### (3) nodeの設定

`com.moowork.node`で使用する設定を`node`節に、`com.moowork.grunt`で使用する設定を`grunt`節にそれぞれ記述しています。  
先にも述べたように、node/gruntで扱う資産は`src/main/web`内で扱うため、workDirにそのディレクトリを指定しています。

#### (4) gruntの実行環境を構築する

grunt_buildタスクが実行される前に`installGrunt`,`npmInstall`が実行されるよう設定しています。  
また、`grunt_build`タスクが、Javaビルドのリソースを処理する`processResources`タスクの実行前に実行されるよう設定しています。

### ビルドスクリプト(Grunt)

Gradleの`grunt_build`タスクにより実行されるgruntファイル.  
`src/main/web/Gruntfile.js`に置いてある.  

Gruntfile.js
```JavaScript
module.exports = function(grunt) {
  grunt.initConfig({
    "bower-install-simple": {  // (1) - bower installを実行する
      options: {
        directory: "bower_components/public"  // (2) - bower installの出力先を変更する
      },
      "prod": {
        options: {
          production: true
        }
      },
      "dev": {
        options: {
          production: false
        }
      }
    },
    wiredep: {
      task: {
        src: ['../resources/templates/**/*.jade'],  // (3) - jadeファイルにbowerコンポーネントへの参照をinjectする
        directory: 'bower_components/public',
        ignorePath: '../../web/bower_components/public'  // (4) - bowerコンポーネントへの参照パスから要らない部分を削除する
      }
    }
  });

  grunt.loadNpmTasks('grunt-bower-install-simple');
  grunt.loadNpmTasks('grunt-wiredep');

  grunt.registerTask('default', 'build');
  grunt.registerTask('build', ['bower-install-simple', 'wiredep']);
};
```

#### (1) bower installを実行する

`bower install`を実行する.  
今回、bower.jsonには`bootstrap`への依存のみ宣言してある.

#### (2) bower installの出力先を変更する

`bower install`の出力先が`bower_components`ままだと、bowerのコンポーネントをSparkでStaticファイルとして配信出来ない(ココらへんはGradleの`processResource`タスクとの関係).
とりあえず今回は`bower install`の出力先を変更し、SparkのStaticファイルを`/public`に指定することにしている.

#### (3) jadeファイルへbowerコンポーネントへの参照をinjectする

`bower install`でinstallされたコンポーネントへの参照をjadeファイルへ記述するタスク.  
便利なのだが、`grunt-wiredep`はもうメンテされてないらしい. [stephenplusplus/grunt-wiredep: Inject Bower packages into your source code with Grunt.](https://github.com/stephenplusplus/grunt-wiredep)

#### (4) bowerコンポーネントへの参照パスから要らない部分を削除する

`(3) jadeファイルへbowerコンポーネントへの参照をinjectする`でjadeファイルにinjectされるパスは、相対パスのため`../../web/bower_components/public/bootstrap/dist/css/bootstrap.css`というような形式になる.  
このままではWebサーバとして動作させた時に上手く動かないため、不要な部分を削る.

この削り方は`(2) bower installの出力先を変更する`に依存しており、ここらへんの操作のコンテキストがとても高くなりがちなのが悩みどころ.

### アプリケーションコード

org.kaakaa.spark.Main  
```Java
package org.kaakaa.spark;

import spark.ModelAndView;
import spark.template.jade.JadeTemplateEngine;

import java.util.Collections;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFileLocation;

public class Main {
    public static void main(String[] args) {
        port(8080);
        staticFileLocation("/public");  // (1) - Staticファイルの格納場所として`/public`を指定する

        get("/hello", (rq, rs) -> new ModelAndView(Collections.EMPTY_MAP, "hello"), new JadeTemplateEngine());
    }
}
```

#### (1) Staticファイルの格納場所として`/public`を指定する

ビルドスクリプト(Grunt)の`(2) bower installの出力先を変更する`でも触れたように、SparkのStaticファイルの格納場所として`/public`を指定する.


hello.jade
```Jade
doctype html
html(lang="ja")
  head
    title gradle-frontend-boilerplate
    // bower:css  // (1) - grunt-wiredepタスクによりbowerのmainコンポーネントがinjectされる
    // endbower
    // bower:js
    // endbower
  body
    nav.navbar.navbar-inverse.navbar-fixed-top
      .container
        .navbar-header
          a.navbar-brand Gradle-frontend-boilerplate
    .container(style="margin-top: 50px;")
      h1 gradle-frontend-boilerplate
      p Hello World!
```

#### (1) grunt-wiredepタスクによりbowerのmainコンポーネントがinjectされる

gruntの`grunt-wiredep`タスクが実行されると、bowerのmainコンポーネントとして指定されているjs/cssファイルへの参照がコメントの間にinjectされる。
