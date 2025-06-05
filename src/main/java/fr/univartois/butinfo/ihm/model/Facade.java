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
    private List<Enemy> enemies = new ArrayList<>();
    private List<AbstractBomb> bombs = new ArrayList<>();
    private IControlerFacade controlerFacade;
    private Boolean statusFinishPartiee = null;
    private Timeline timelineEnemies = new Timeline();

    public Facade() {
        map = GameMapFactory.createMapWithRandomBrickWalls(10, 10, 10);
    }

    public void setControlerFacade(IControlerFacade controlerFacade) {
        this.controlerFacade = controlerFacade;
    }

    public GameMap getMap() {
        return map;
    }

    public void addEnemy(String name) {
        Enemy enemy = new Enemy(this, name);
        placeCharacter(enemy);
        enemies.add(enemy);
    }

    public void initGame() {
        player = new Player(this);
        placeCharacter(player);

        controlerFacade.initMenu(player.MAXBOMB);

        addEnemy("goblin");
        addEnemy("minotaur");

        Enemy enemy1 = new Enemy(this, "goblin");
        Enemy enemy2 = new Enemy(this, "minotaur");
        placeCharacter(enemy1);
        placeCharacter(enemy2);
        enemies.add(enemy1);
        enemies.add(enemy2);

        controlerFacade.updateGame();
        generalBind();

        launchEnemyMovement();
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
        if (map.isOnMap(newRow, newCol) && map.get(newRow, newCol).isEmpty()) {
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
        AbstractBomb prototype = player.dropBomb();
        if (prototype == null) return;

        AbstractBomb bomb;
        if (prototype instanceof Bomb) {
            bomb = new Bomb(this);
        } else if (prototype instanceof RowBomb) {
            bomb = new RowBomb(this);
        } else if (prototype instanceof ColumnBomb) {
            bomb = new ColumnBomb(this);
        } else if (prototype instanceof LargeBomb) {
            bomb = new LargeBomb(this);
        } else {
            bomb = new RowBomb(this); // default
        }

        bomb.setPosition(player.getRow(), player.getColumn());
        bombs.add(bomb);
        controlerFacade.showBomb(bomb);

        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(bomb.getDelay()), e -> {
            bomb.explode();
            removeBomb(bomb);
            bombs.remove(bomb);
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
        Timeline timelineEnemies = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
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
            int[] dir = dirs[rand.nextInt(4)];
            int newRow = enemy.getRow() + dir[0];
            int newCol = enemy.getColumn() + dir[1];
            if (map.isOnMap(newRow, newCol) && map.get(newRow, newCol).isEmpty()) {
                enemy.setPosition(newRow, newCol);
            }
        }
    }

    public Boolean getStatusFinishPartiee() {
        return statusFinishPartiee;
    }

    public void stopGame() {
        timelineEnemies.stop();
        System.out.println(statusFinishPartiee);
        System.exit(0);

    }
}
