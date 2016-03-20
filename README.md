# gradle-frontend-boilerplate

## 1. Hello Worldアプリ

### 概要

Sparkのテンプレートエンジンとして[neuland/jade4j: a jade implementation written in Java](https://github.com/neuland/jade4j)を使用したサンプル.

### 実行

```
git clone https://github.com/kaakaa/gradle-frontend-boilerplate.git
cd gradle-frontend-boilerplate
git checkout sec_2

./gradlew run
```

### ビルドスクリプト

build.gradle
```
plugins {
    id 'java'
    id 'application'
}

group 'org.kaakaa'
version '1.0-SNAPSHOT'

sourceCompatibility = '1.8'
targetCompatibility = '1.8'

mainClassName = 'org.kaakaa.spark.Main'

repositories {
    mavenCentral()
}

dependencies {
    compile 'com.sparkjava:spark-core:2.3'
    compile 'com.sparkjava:spark-template-jade:2.3'  // (1) - jade4jへの依存を追加
}

task wrapper(type: Wrapper) {
    gradleVersion = '2.12'
}
```

#### (1) jade4jへの依存性を宣言

特に説明の必要はありませんが、sparkで使用するテンプレートエンジンとして`spark-template-jade`を指定しています。  
その他のテンプレートエンジンを使用する場合は [Spark Framework - Documentation](http://sparkjava.com/documentation.html#views-templates) を。

### アプリケーションコード

org.kaakaa.spark.Main
```
package org.kaakaa.spark;

import spark.ModelAndView;
import spark.template.jade.JadeTemplateEngine;

import java.util.Collections;

import static spark.Spark.get;
import static spark.Spark.port;

public class Main {
    public static void main(String[] args) {
        port(8080);

        // (1) - jadeテンプレートを指定
        get("/hello", (rq, rs) -> new ModelAndView(Collections.EMPTY_MAP, "hello"), new JadeTemplateEngine());
    }
}
```

hello.jade
```
doctype html
html(lang="ja")
  head
    title gradle-frontend-boilerplate
  body
    h1 gradle-frontend-boilerplate
    p Hello World!
```

#### (1) jadeテンプレとを指定

`/hello`にアクセスされた場合、`hello.jade`のjadeテンプレートを元に生成されたHTMLを返すよう指定しています。  

`1. Hello Worldアプリ`では`get`メソッドの第２引数のラムダ式は単なるString文字列を返していましたが、テンプレートエンジンを使用する場合`ModelAndView`クラスのインスタンスを返すようになります。  
また、第３引数にテンプレートエンジンクラスのインスタンスを指定します。  

`ModelAndView`クラスのコンストラクタの第１引数にはテンプレート内で使用するオブジェクトをMap形式で指定し（今回は使用しないため空のMap）、第２引数にはテンプレートファイルの拡張子を除いたファイル名を指定します。　　
この時、Sparkは第２引数で指定されたテンプレートファイルを`${projectDir}/src/main/resource/templates`フォルダから探します。  

Sparkがテンプレートファイルを探しに行くフォルダは、使用するテンプレートエンジンによって異なるようです。  
各Spark用テンプレートエンジンのREADMEに書いてあるようなので、確認しましょう。 [spark-template-engines/spark-template-jade at master · perwendel/spark-template-engines](https://github.com/perwendel/spark-template-engines/tree/master/spark-template-jade)


