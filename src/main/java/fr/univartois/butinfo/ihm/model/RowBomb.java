package fr.univartois.butinfo.ihm.model;

public class RowBomb extends AbstractBomb {

    public RowBomb(MaFacadeBomberman game) {
        super(game);
    }

    @Override
    public String getName() {
        return "row-bomb";
    }

    @Override
    public String getDescription() {
        return "Cette bombe fait exploser le contenu des tuiles voisines situées sur la même ligne.";
    }

    @Override
    public int getDelay() {
        return 2;
    }

    @Override
    public void explode() {
        exploded.set(true);
        for (int i = -1; i <= 1; i++) {
            game.explode(row, column + i);
        }
    }

}
