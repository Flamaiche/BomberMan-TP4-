package fr.univartois.butinfo.ihm.model;

import java.util.ArrayList;

public interface IControlerFacade {

    void updateGame();

    void bindCharacter(AbstractCharacter character);

    void showBomb(AbstractBomb bomb);

    void removeBomb(AbstractBomb bomb);

    void showExplosion(int row, int column);

    void initMenu(int nbBombMax, int playerHealthInit, int nbEnemy);

    void showEndMessage(String message);

    void setFacade(Facade facade);
}
