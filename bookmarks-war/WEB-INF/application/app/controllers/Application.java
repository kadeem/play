package controllers;

import play.mvc.*;


import models.*;
import models.User;
import java.util.*;

public class Application extends Controller {

    public static void index() {
        if(Auth.isLoggedIn()) {
            User user = User.findByEmail(Auth.getEmail());
			if(null == user) {
				user = new User();
				user.email = Auth.getEmail();
				user.created = new Date();
				user.insert();
				Profile.edit(user.id);
				return;
			}
            Profile.index(null);
        }
        render();
    }

    public static void login() {
        Auth.login("Application.index");
    }

    public static void logout() {
        Auth.logout("Application.index");
    }

}