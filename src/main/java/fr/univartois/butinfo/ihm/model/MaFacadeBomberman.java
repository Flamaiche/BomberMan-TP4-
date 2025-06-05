package fr.univartois.butinfo.ihm.model;

public interface MaFacadeBomberman {
    void updateGame();

    void explode(int row, int column);

    void showExplosion(int row, int column);

    void removeBomb(AbstractBomb bomb);
}
