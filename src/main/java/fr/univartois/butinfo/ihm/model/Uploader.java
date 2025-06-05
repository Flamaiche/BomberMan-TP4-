package fr.univartois.butinfo.ihm.model;

import fr.univartois.butinfo.ihm.BombermanApplication;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.NoSuchElementException;

public class Uploader {
    public static final String[] IMAGES = {
            "agent.png", "bomb.png", "bricks.png", "chest.png", "column-bomb.png",
            "explosion.png", "goblin.png", "guy.png", "heart.png", "large-bomb.png",
            "lawn.png", "minotaur.png", "punker.png", "rourke.png", "row-bomb.png", "wall.png"
    };
    public static final int DEFAULT = 10;
    public static HashMap<String, Image> images = new HashMap<>();

    public static Image getImage(String name) {
        if (images.containsKey(name)) {
            return images.get(name);
        }
        try {
            String path = "/" + BombermanApplication.class.getPackageName().replace(".", "/") + "/images/" + name;
            Image image =  new Image(Uploader.class.getResource(path).toExternalForm(), 50, 50, false, false);
            images.put(name, image);
            return image;
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new NoSuchElementException("Could not load image", e);
        }
    }
}