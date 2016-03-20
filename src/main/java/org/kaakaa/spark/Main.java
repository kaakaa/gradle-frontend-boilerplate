package org.kaakaa.spark;

import static spark.Spark.get;
import static spark.Spark.port;

public class Main {
    public static void main(String[] args) {
        port(8080);

        get("/hello", (rq, rs) -> "Hello World!");
    }
}
