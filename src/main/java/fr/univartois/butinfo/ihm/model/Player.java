package fr.univartois.butinfo.ihm.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.Random;

public class Player extends AbstractCharacter {

    public final AbstractBomb[] BOMB_EXITANTE = {new Bomb(game), new ColumnBomb(game), new RowBomb(game), new LargeBomb(game)};
    private Random rand = new Random();
    public int MAXBOMB = 20;
    private ArrayList<AbstractBomb> inventaireBomb = new ArrayList<>();
    private IntegerProperty nbBomb = new SimpleIntegerProperty();

    public Player(MaFacadeBomberman game) {
        super(game, 3);
        remplissageBomb(BOMB_EXITANTE[0]);
    }

    @Override
    public String getName() {
        return "guy";
    }

    public void remplissageBomb() {
        // aleatoire
        if (inventaireBomb.size() == MAXBOMB) return;
        for (int i = inventaireBomb.size(); i < MAXBOMB; i++) {
            inventaireBomb.add(BOMB_EXITANTE[rand.nextInt(BOMB_EXITANTE.length)]);
        }
    }

    public void remplissageBomb(AbstractBomb bomb) {
        if (inventaireBomb.size() == MAXBOMB) return;
        for (int i = inventaireBomb.size(); i < MAXBOMB; i++) {
            inventaireBomb.add(bomb);
        }
    }

    public AbstractBomb getSelectedBomb() {
        if (inventaireBomb.isEmpty()) return null;
        return inventaireBomb.getFirst();
    }

    public void delSelectedBomb() {
        if (inventaireBomb.isEmpty()) return;
        inventaireBomb.removeFirst();
        nbBomb.set(inventaireBomb.size());
    }

    public AbstractBomb dropBomb() {
        AbstractBomb bomb = getSelectedBomb();
        delSelectedBomb();
        return bomb;
    }

    public  int getNbBomb() {
        return nbBomb.get();
    }

    public IntegerProperty nbBombProperty() {
        return nbBomb;
    }
}
