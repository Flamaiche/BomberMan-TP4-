package fr.univartois.butinfo.ihm.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public abstract class AbstractCharacter {

    protected MaFacadeBomberman game;
    private final IntegerProperty row = new SimpleIntegerProperty();
    private final IntegerProperty column = new SimpleIntegerProperty();
    private int health;

    protected AbstractCharacter(MaFacadeBomberman game ,int initialHealth) {
        this.game = game;
        this.health = initialHealth;
    }

    public abstract String getName();

    public int getRow() {
        return row.get();
    }

    public int getColumn() {
        return column.get();
    }

    public void setPosition(int row, int column) {
        this.row.set(row);
        this.column.set(column);
    }

    public int getHealth() {
        return health;
    }

    public void incHealth() {
        health++;
    }

    public void decHealth() {
        health--;
    }

    public IntegerProperty rowProperty() {
        return row;
    }

    public IntegerProperty columnProperty() {
        return column;
    }
}
