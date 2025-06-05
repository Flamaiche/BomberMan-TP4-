package fr.univartois.butinfo.ihm.controller;

import fr.univartois.butinfo.ihm.model.*;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class ControleEntree implements IControlerFacade {

    public static final int taille = 50;
    private Facade facade;

    @FXML
    private GridPane grid;

    @FXML private GridPane info;

    private Label affNbBomb;
    private final String txtNbBomb = "nb Bomb : ";
    private Label affVie;
    private final String txtVie = "vie : ";

    private final List<ImageView> bombViews = new ArrayList<>();

    @FXML
    public void initialize() {
        grid.setPrefSize(taille * 10, taille * 10);
        for (int i = 0; i < 10; i++) {
            grid.getColumnConstraints().add(new ColumnConstraints(taille));
            grid.getRowConstraints().add(new RowConstraints(taille));
        }
        // Focus
        grid.setFocusTraversable(true);
        grid.requestFocus();
        // Gestion du clavier
        grid.setOnKeyPressed(event -> {
            if (facade.getStatusFinishPartiee() != null) return;
            switch (event.getCode()) {
                case UP -> facade.movePlayer(-1, 0);
                case DOWN -> facade.movePlayer(1, 0);
                case LEFT -> facade.movePlayer(0, -1);
                case RIGHT -> facade.movePlayer(0, 1);
            }
        });
        grid.setOnKeyReleased(event -> {
            if (facade.getStatusFinishPartiee() != null) return;
            switch (event.getCode()) {
                case SPACE -> facade.dropBomb();
            }
        });
    }

    public void setFacade(Facade facade) {
        this.facade = facade;
    }

    @Override
    public void updateGame() {
        // Supprimer uniquement les images du fond
        grid.getChildren().removeIf(node ->
                node.getUserData() != null && node.getUserData().equals("tile"));

        GameMap map = facade.getMap();
        for (int i = 0; i < map.getHeight(); i++) {
            for (int j = 0; j < map.getWidth(); j++) {
                ImageView image = new ImageView(Uploader.getImage(map.get(i, j).getContent().getName() + ".png"));
                image.setFitWidth(taille);
                image.setFitHeight(taille);
                image.setUserData("tile"); // identifie comme tuile
                grid.add(image, j, i);
            }
        }
        List<Node> toReadd = new ArrayList<>();
        for (Node node : new ArrayList<>(grid.getChildren())) {
            if (node.getUserData() != null && !node.getUserData().equals("tile")) {
                toReadd.add(node);
            }
        }

        // Les réajouter à la fin (ils seront dessinés au-dessus)
        grid.getChildren().removeAll(toReadd);
        grid.getChildren().addAll(toReadd);
    }

    @Override
    public void bindCharacter(AbstractCharacter character) {
        if (character instanceof Player player) {
            player.nbBombProperty().addListener((obs, oldVal, newVal) ->
                    affNbBomb.setText(txtNbBomb + newVal));
        }
        ImageView view = new ImageView(Uploader.getImage(character.getName() + ".png"));
        view.setFitWidth(taille);
        view.setFitHeight(taille);
        grid.add(view, character.getColumn(), character.getRow());
        character.rowProperty().addListener((obs, oldVal, newVal) ->
                GridPane.setRowIndex(view, newVal.intValue()));
        character.columnProperty().addListener((obs, oldVal, newVal) ->
                GridPane.setColumnIndex(view, newVal.intValue()));
    }

    @Override
    public void showBomb(AbstractBomb bomb) {
        ImageView view = new ImageView(Uploader.getImage(bomb.getName() + ".png"));
        view.setFitWidth(taille);
        view.setFitHeight(taille);
        view.setUserData("bomb");
        GridPane.setRowIndex(view, bomb.getRow());
        GridPane.setColumnIndex(view, bomb.getColumn());
        grid.add(view, bomb.getColumn(), bomb.getRow());
        bombViews.add(view);
    }

    @Override
    public void removeBomb(AbstractBomb bomb) {
        bombViews.removeIf(view -> {
            boolean toRemove = GridPane.getRowIndex(view) == bomb.getRow()
                    && GridPane.getColumnIndex(view) == bomb.getColumn();
            if (toRemove) {
                grid.getChildren().remove(view);
            }
            return toRemove;
        });
    }

    @Override
    public void showExplosion(int row, int column) {
        ImageView view = new ImageView(Uploader.getImage("explosion.png"));
        view.setFitWidth(taille);
        view.setFitHeight(taille);
        view.setUserData("explosion");
        GridPane.setRowIndex(view, row);
        GridPane.setColumnIndex(view, column);
        grid.getChildren().add(view);

        // Retire l’explosion après 0.5 seconde
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e -> grid.getChildren().remove(view));
        pause.play();
    }

    public void initMenu(int nbBombMax) {
        GameMap map = facade.getMap();
        int menuNbCase = 2;
        GridPane.clearConstraints(info);
        for (int i = 0; i < menuNbCase; i++) {
            info.getColumnConstraints().add(new ColumnConstraints((map.getWidth()*1.0)*taille/menuNbCase));
        }
         // 1ere cellule / nbBomb
        affNbBomb = new Label(txtNbBomb + nbBombMax);
        info.add(affNbBomb, 0, 0);

        //2eme cellule / vie
        affVie = new Label(txtVie);
        info.add(affVie, 1, 0);
    }

}
