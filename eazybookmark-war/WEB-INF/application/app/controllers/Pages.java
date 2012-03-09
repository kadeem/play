package controllers;

import play.*;
import play.mvc.*;



public class Pages extends Application {

    @Before
    static void checkConnected() {
        if(Auth.getUser() != null) {
            renderArgs.put("user", Auth.getEmail());
        }
    }

    public static void page(String page) {
        render("Pages/" + page + ".html");
    }
}

