package fr.univartois.butinfo.ihm.model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Facade implements MaFacadeBomberman {

    private GameMap map;
    private Player player;
    private List<Enemy> enemies;
    private List<AbstractBomb> bombs;
    private IControlerFacade controlerFacade;
    private Boolean statusFinishPartiee;
    private Timeline timelineEnemies;
    private final int HEIGHT = 10;
    private final int WIDTH = 10;
    private final int NBWALL = 15;

    public void setControlerFacade(IControlerFacade controlerFacade) {
        this.controlerFacade = controlerFacade;
    }

    public GameMap getMap() {
        return map;
    }

    public void addEnemy(String name) {
        Enemy enemy = new Enemy(name);
        placeCharacter(enemy);
        enemies.add(enemy);
    }

    public void initGame() {
        if (timelineEnemies != null) {
            timelineEnemies.stop();
        }

        map = GameMapFactory.createMapWithRandomBrickWalls(HEIGHT, WIDTH, NBWALL);
        enemies = new ArrayList<>();
        bombs = new ArrayList<>();
        statusFinishPartiee = null;
        player = new Player(this);
        configureDifficulty();
        placeCharacter(player);

        controlerFacade.initMenu(player.getMaxBomb(), player.getHealth());

        controlerFacade.updateGame();
        generalBind();

        launchEnemyMovement();
    }

    private void configureDifficulty() {
        int totalTiles = HEIGHT * WIDTH;
        int emptySpace = totalTiles - NBWALL;

        // Détermine le nombre d'ennemis (difficulté proportionnelle à l'espace vide)
        int estimatedEnemies = Math.max(2, emptySpace / 20);

        // Calcule le nombre de bombes en fonction du nombre de murs et du nombre d'ennemis
        int estimatedBombs = Math.max(5, (NBWALL * 2 + estimatedEnemies * 5) / 4);
        player.setMaxBomb(estimatedBombs);

        // Ajoute des ennemis en alternant les types
        for (int i = 0; i < estimatedEnemies; i++) {
            Enemy enemy;
            switch (i % 5) {
                case 0 -> enemy = new Goblin();
                case 1 -> enemy = new Minotaur();
                case 2 -> enemy = new Agent();
                case 3 -> enemy = new Punker();
                case 4 -> enemy = new Rourke();
                default -> throw new IllegalStateException("Unexpected enemy index: " + i);
            }
            placeCharacter(enemy);
            enemies.add(enemy);
        }

        System.out.println("Difficulté générée : " +
                estimatedEnemies + " ennemis | " +
                estimatedBombs + " bombes max");
    }


    public Player getPlayer() {
        return player;
    }

    public void placeCharacter(AbstractCharacter character) {
        List<Tile> emptyTiles = map.getEmptyTiles();
        if (!emptyTiles.isEmpty()) {
            Tile tile = emptyTiles.remove(new Random().nextInt(emptyTiles.size()));
            character.setPosition(tile.getRow(), tile.getColumn());
        }
    }

    public void movePlayer(int dRow, int dCol) {
        int newRow = player.getRow() + dRow;
        int newCol = player.getColumn() + dCol;

        // Vérifie les coordonnées
        if (!map.isOnMap(newRow, newCol)) return;

        // Vérifie qu'aucun ennemi n'est présent sur la case cible
        for (Enemy enemy : enemies) {
            if (enemy.getRow() == newRow && enemy.getColumn() == newCol) {
                return; // Ennemi présent => déplacement interdit
            }
        }

        // Vérifie que la case est vide
        if (map.get(newRow, newCol).isEmpty()) {
            player.setPosition(newRow, newCol);
        }
    }


    public void updateGame() {
        controlerFacade.updateGame();
    }

    public void generalBind() {
        // Réaffiche les personnages
        controlerFacade.bindCharacter(player);
        for (Enemy enemy : enemies) {
            controlerFacade.bindCharacter(enemy);
        }
    }

    public void dropBomb() {
        dropBomb(player.getSelectedBomb());
    }

    public void dropBomb(AbstractBomb bomb) {
        if (bomb == null) return;
        if (player.getInventaireBomb().contains(bomb)) {
            player.delSelectedBomb(bomb);
        }

        bomb.setPosition(player.getRow(), player.getColumn());
        bombs.add(bomb);
        controlerFacade.showBomb(bomb);

        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(bomb.getDelay()), e -> {
            if (statusFinishPartiee == null) {
                bomb.explode();
                removeBomb(bomb);
                bombs.remove(bomb);
            }
        }));
        timer.setCycleCount(1);
        timer.play();
    }

    public void explode(int row, int column) {
        if (!map.isOnMap(row, column)) return;

        Tile tile = map.get(row, column);
        if (tile.getContent().isDestroyableByExplosion()) {
            tile.setContent(TileContent.LAWN);
        }

        if (player.getRow() == row && player.getColumn() == column) {
            player.decHealth();  // méthode à implémenter dans Player
            if (player.getHealth() == 0) {
                statusFinishPartiee = false;
                stopGame();
            }
        }

        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            if (enemy.getRow() == row && enemy.getColumn() == column) {
                enemy.decHealth();
                if (enemy.getHealth() == 0) {
                    enemies.remove(i);
                }
            }
        }

        updateGame();

        // Réaffiche les personnages
        controlerFacade.bindCharacter(player);
        for (Enemy enemy : enemies) {
            controlerFacade.bindCharacter(enemy);
        }

        showExplosion(row, column);
    }

    public void removeBomb(AbstractBomb bomb) {
        controlerFacade.removeBomb(bomb);
    }

    public void showExplosion(int row, int column) {
        Tile tile = map.get(row, column);
        if (tile.getContent().isDestroyableByExplosion()) {
            controlerFacade.showExplosion(row, column);
        }
    }

    public void launchEnemyMovement() {
        timelineEnemies = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (enemies.isEmpty()) {
                statusFinishPartiee = true;
                stopGame();
            } else moveEnemies();
        }));
        timelineEnemies.setCycleCount(Timeline.INDEFINITE);
        timelineEnemies.play();
    }

    private void moveEnemies() {
        Random rand = new Random();
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (Enemy enemy : enemies) {
            for (int i = 0; i < enemy.getMoveDistance(); i++) {
                int[] dir = dirs[rand.nextInt(4)];
                int newRow = enemy.getRow() + dir[0];
                int newCol = enemy.getColumn() + dir[1];

                // Empêche le déplacement hors carte ou sur une case occupée
                if (!map.isOnMap(newRow, newCol)) continue;
                if (!map.get(newRow, newCol).isEmpty()) continue;

                // Gestion du combat avec le joueur
                if (player.getRow() == newRow && player.getColumn() == newCol) {
                    player.decHealth();
                    if (player.getHealth() == 0) {
                        statusFinishPartiee = false;
                        stopGame();
                        return; // On arrête le déplacement si le joueur est mort
                    }
                } else {
                    enemy.setPosition(newRow, newCol);
                }
            }
        }
    }

    public Boolean getStatusFinishPartiee() {
        return statusFinishPartiee;
    }

    public void stopGame() {
        if (timelineEnemies != null) {
            timelineEnemies.stop();
        }
        String message = "";
        if (statusFinishPartiee != null) {
            controlerFacade.updateGame();
            if (statusFinishPartiee) {
                message = "Partie terminée : Victoire !";
            } else {
                message = "Partie terminée : Défaite !";
            }
        }
        System.out.println(message);
        controlerFacade.showEndMessage(message);
    }
}
