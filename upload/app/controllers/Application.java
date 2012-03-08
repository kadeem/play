package controllers;

import models.Picture;
import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
        render();
    }

    public static void uploadPicture(Picture picture) {
        picture.save();
        index();
    }
    public static void getPicture(long id) {
        Picture picture = Picture.findById(id);
        response.setContentTypeIfNotSet(picture.image.type());
        renderBinary(picture.image.get());
    }
}