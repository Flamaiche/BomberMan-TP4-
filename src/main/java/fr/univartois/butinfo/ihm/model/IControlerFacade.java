package fr.univartois.butinfo.ihm.model;

public interface IControlerFacade {

    void updateGame();

    void bindCharacter(AbstractCharacter character);

    void showBomb(AbstractBomb bomb);

    void removeBomb(AbstractBomb bomb);

    void showExplosion(int row, int column);

    void initMenu(int nbBombMax, int playerHealthInit);
}
