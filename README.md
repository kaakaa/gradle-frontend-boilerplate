# gradle-frontend-boilerplate

## 1. Hello Worldアプリ

### 概要

[Spark Framework - A tiny Java web framework](http://sparkjava.com/)を利用したHello World.

### 実行

```
git clone https://github.com/kaakaa/gradle-frontend-boilerplate.git
cd gradle-frontend-boilerplate
git checkout sec_1

./gradlew run
```

### ビルドスクリプト

build.gradle
```
plugins {  // (1) - 使用するGradleプラグインを宣言
    id 'java'
    id 'application'
}

group 'org.kaakaa'
version '1.0-SNAPSHOT'

sourceCompatibility = '1.8'
targetCompatibility = '1.8'

mainClassName = 'org.kaakaa.spark.Main'  // (2) - applicationプラグインから実行されるメインクラス名を指定

repositories {
    mavenCentral()  // (3) - dependencies節に書かれた依存関係解決にMaven Centralを使用することを宣言
}

dependencies {
    compile 'com.sparkjava:spark-core:2.3' // (4) - spark-coreへの依存性を宣言
}

task wrapper(type: Wrapper) {
    gradleVersion = '2.12'
}
```

#### (1) 使用するGradleプラグインを宣言

Gradleで使用するプラグインを宣言します。  

[Javaプラグイン](http://gradle.monochromeroad.com/docs/userguide/java_plugin.html)でJavaプロジェクトのビルドに関する情報がひと通り取り込まれます。  
今回使用するWebフレームワークの`spark`は、Javaのmainメソッドにルーティングを定義するため、mainメソッドの実行を楽にできる  
[アプリケーション プラグイン](http://gradle.monochromeroad.com/docs/userguide/application_plugin.html)を使用しています。

#### (2) applicationプラグインから実行されるメインクラス名を指定

`application`プラグインで使用するメインクラスは`mainClassName`としてパッケージ名を含めて指定しておきます。  
`run`タスクを実行すると`mainClassName`で指定したmainメソッドが実行されます。

#### (3) dependencies節に書かれた依存関係解決にMaven Centralを使用することを宣言

GradleではMavenリポジトリを参照して依存関係を解決しますが、参照するリポジトリは`repositories`節で宣言します([51.6. リポジトリ](http://gradle.monochromeroad.com/docs/userguide/dependency_management.html)).  
MavenCentralやjCenterなど、よく使われるMavenリポジトリにはシンタックスが用意されています。

#### (4) spark-coreへの依存性を宣言

実際にJavaプロジェクトで使用するライブラリ(今回では`spark-core`のみ)は`dependencies`節に宣言します。  
ここで`compile`と指定しているのは、`java`プラグインにより追加されるconfigurationで、Javaソースのcompile時に参照されることを宣言しています（runtime時にも参照されますが)。  
`java`プラグインにより追加されるconfigurationについては [8.3. 依存関係のコンフィグレーション](http://gradle.monochromeroad.com/docs/userguide/artifact_dependencies_tutorial.html) を参照ください。  

### アプリケーションコード

org.kaakaa.spark.Main
```
package org.kaakaa.spark;

import static spark.Spark.get;
import static spark.Spark.port;

public class Main {
    public static void main(String[] args) {
        port(8080);  // (1) - 起動ポートの指定

        get("/hello", (rq, rs) -> "Hello World!");  // (2) - Routing
    }
}
```

#### (1) 起動ポートの指定

Sparkアプリを起動した際の起動ポートを指定します。  
デフォルトは`4567`です。

#### (2) Routing

Sparkでのルーティングの宣言方法です。  
この場合、`http://localhost:8080/hello`にアクセスすると、`Hello World!`という文字列を返すことになります。

SparkのRoutingについては、公式サイトを読むとよく分かると思います。  
[Spark Framework - Documentation](http://sparkjava.com/documentation.html)

