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
        staticFileLocation("/public");

        get("/hello", (rq, rs) -> new ModelAndView(Collections.EMPTY_MAP, "hello"), new JadeTemplateEngine());
    }
}
