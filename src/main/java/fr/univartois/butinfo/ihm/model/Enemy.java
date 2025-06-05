package fr.univartois.butinfo.ihm.model;

public class Enemy extends AbstractCharacter {

    private final String name;

    public Enemy(MaFacadeBomberman game ,String name) {
        super(game, 1);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
