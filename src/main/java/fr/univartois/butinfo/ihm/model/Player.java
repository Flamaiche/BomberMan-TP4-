package fr.univartois.butinfo.ihm.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.Random;

public class Player extends AbstractCharacter {

    private final MaFacadeBomberman game;
    private AbstractBomb[] BOMB_EXITANTE;
    private Random rand = new Random();
    private int MAXBOMB = 20;
    private ArrayList<AbstractBomb> inventaireBomb = new ArrayList<>();
    private IntegerProperty nbBomb = new SimpleIntegerProperty();

    public Player(MaFacadeBomberman game) {
        super(3);
        this.game = game;

        BOMB_EXITANTE = new AbstractBomb[] {new Bomb(game), new ColumnBomb(game), new RowBomb(game), new LargeBomb(game)};

        remplissageBomb();
        nbBomb.set(inventaireBomb.size());
    }

    @Override
    public String getName() {
        return "guy";
    }

    public ArrayList<AbstractBomb> getInventaireBomb() {
        return inventaireBomb;
    }

    public void remplissageBomb() {
        // aleatoire
        if (inventaireBomb.size() == MAXBOMB) return;
        for (int i = inventaireBomb.size(); i < MAXBOMB; i++) {
            inventaireBomb.add(creatNewBombByModel(BOMB_EXITANTE[rand.nextInt(BOMB_EXITANTE.length)]));
        }
    }

    public void remplissageBomb(AbstractBomb bomb) {
        if (inventaireBomb.size() == MAXBOMB) return;
        for (int i = inventaireBomb.size(); i < MAXBOMB; i++) {
            inventaireBomb.add(creatNewBombByModel(bomb));
        }
    }

    public AbstractBomb creatNewBombByModel(AbstractBomb model) {
        if (model instanceof Bomb) return new Bomb(game);
        if (model instanceof ColumnBomb) return new ColumnBomb(game);
        if (model instanceof RowBomb) return new RowBomb(game);
        if (model instanceof LargeBomb) return new LargeBomb(game);
        throw new IllegalArgumentException("Type de bombe inconnu");
    }

    public AbstractBomb getSelectedBomb(int index) {
        if (inventaireBomb.isEmpty()) return null;
        return inventaireBomb.get(index);
    }

    public AbstractBomb getSelectedBomb() {
        return getSelectedBomb(0);
    }

    public void delSelectedBomb() {
        delSelectedBomb(inventaireBomb.getFirst());
    }

    public void delSelectedBomb(AbstractBomb bomb) {
        if (inventaireBomb.isEmpty()) return;
        inventaireBomb.remove(bomb);
        nbBomb.set(inventaireBomb.size());
    }


    public  int getNbBomb() {
        return nbBomb.get();
    }

    public IntegerProperty nbBombProperty() {
        return nbBomb;
    }

    public int getMaxBomb() {
        return MAXBOMB;
    }

    public void setMaxBomb(int maxBomb) {
        this.MAXBOMB = maxBomb;
        inventaireBomb.clear();
        remplissageBomb();
    }
}
