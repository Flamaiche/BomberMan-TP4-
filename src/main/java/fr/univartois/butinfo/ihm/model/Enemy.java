package fr.univartois.butinfo.ihm.model;

public class Enemy extends AbstractCharacter {

    private final String name;
    private final int moveDistance;

    public Enemy(String name, int initialHealth, int moveDistance) {
        super(initialHealth);
        this.name = name;
        this.moveDistance = moveDistance;
    }

    public Enemy(String name) {
        this(name, 1, 1); // Default health and move distance
    }

    @Override
    public String getName() {
        return name;
    }

    public int getMoveDistance() {
        return moveDistance;
    }
}

class Goblin extends Enemy {

    private static final int HEALTH = 2;
    private static final int NB_DEPLACEMENT = 1;

    public Goblin() {
        super("goblin", HEALTH, NB_DEPLACEMENT);
    }
}


class Agent extends Enemy {

    private static final int HEALTH = 4;
    private static final int NB_DEPLACEMENT = 2;

    public Agent() {
        super("agent", HEALTH, NB_DEPLACEMENT);
    }
}


class Minotaur extends Enemy {

    private static final int HEALTH = 6;
    private static final int NB_DEPLACEMENT = 1;

    public Minotaur() {
        super("minotaur", HEALTH, NB_DEPLACEMENT);
    }
}


class Punker extends Enemy {

    private static final int HEALTH = 3;
    private static final int NB_DEPLACEMENT = 2;

    public Punker() {
        super("punker", HEALTH, NB_DEPLACEMENT);
    }
}


class Rourke extends Enemy {

    private static final int HEALTH = 5;
    private static final int NB_DEPLACEMENT = 1;

    public Rourke() {
        super("rourke", HEALTH, NB_DEPLACEMENT);
    }
}
