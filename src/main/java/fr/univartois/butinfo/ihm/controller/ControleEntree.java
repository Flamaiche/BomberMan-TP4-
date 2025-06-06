package fr.univartois.butinfo.ihm.controller;

import fr.univartois.butinfo.ihm.model.*;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ControleEntree implements IControlerFacade {

    public static final int taille = 50;
    private Facade facade;

    @FXML
    private GridPane grid;

    @FXML private GridPane info;

    private final int textImageTaille = 20;
    private Label affNbBomb;
    private final String txtNbBomb = "NB BOMB : ";
    private HBox affVie;
    private final String txtVie = "VIE : ";
    private Button resetButton;

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
                case I -> {
                            try {
                                afficherInventaire();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
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
                image.setPreserveRatio(true);
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
            player.healthProperty().addListener((obs, oldVal, newVal) ->
                    createAffVie((int)newVal));
        }
        ImageView view = new ImageView(Uploader.getImage(character.getName() + ".png"));
        view.setPreserveRatio(true);
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

    public void initMenu(int nbBombInit, int playerHealthInit) {
        GameMap map = facade.getMap();
        info.setPrefSize(map.getWidth(), taille);
        final int menuNbCase = 3;
        info.getChildren().clear();
        info.getColumnConstraints().clear();
        info.getRowConstraints().clear();
        for (int i = 0; i < menuNbCase; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / menuNbCase); // 33.33% par colonne
            info.getColumnConstraints().add(col);
        }
        info.setPadding(new Insets(10));

        // 1ère cellule : nbBomb
        affNbBomb = new Label(txtNbBomb + nbBombInit);
        GridPane.setHalignment(affNbBomb, HPos.CENTER);
        GridPane.setValignment(affNbBomb, VPos.CENTER);
        info.add(affNbBomb, 0, 0);

        // 2e cellule : vie
        affVie = new HBox();
        GridPane.setHalignment(affVie, HPos.CENTER);
        GridPane.setValignment(affVie, VPos.CENTER);
        createAffVie(playerHealthInit);
        info.add(affVie, 1, 0);

        // 3e cellule : bouton reset
        resetButton = new Button("Reset");
        resetButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        resetButton.setOnAction(event -> {
            System.out.println("Reset button pressed");
            facade.stopGame();
            facade.initGame();
        });
        GridPane.setHalignment(resetButton, HPos.CENTER);
        GridPane.setValignment(resetButton, VPos.CENTER);
        info.add(resetButton, 2, 0);
    }

    public HBox creatViewImageHeartHBox(int nbImage,int textImageTaille) {
        HBox heartsBox = new HBox(5); // espacement de 5px entre les cœurs
        for (int i = 0; i < nbImage; i++) {
            ImageView heart = new ImageView(Uploader.getImage("heart.png"));
            heart.setFitWidth(textImageTaille);
            heart.setFitHeight(textImageTaille);
            heart.setPreserveRatio(true);
            heartsBox.getChildren().add(heart);
        }
        return heartsBox;
    }

    public void createAffVie(int playerHealthInit) {
        affVie.getChildren().clear();
        Label labelVie = new Label(txtVie);
        Label labelVieEnPlus = new Label("");
        affVie.getChildren().add(labelVie);
        affVie.getChildren().add(labelVieEnPlus);
        if (playerHealthInit > 5) {
            labelVieEnPlus.setText("+" + (playerHealthInit-5));
            playerHealthInit = 5;
        }
        labelVie.setContentDisplay(ContentDisplay.RIGHT);
        labelVie.setGraphic(creatViewImageHeartHBox(playerHealthInit, textImageTaille));
    }

    private void afficherInventaire() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univartois/butinfo/ihm/view/inventaire.fxml"));
        Parent inventaireRoot = loader.load();

        InventaireController controllerInventaire = loader.getController();
        controllerInventaire.setFacade(facade);
        controllerInventaire.setScenePrincipale(grid.getScene());
        ObservableList<AbstractBomb> observableBombs = FXCollections.observableArrayList(facade.getPlayer().getInventaireBomb());
        controllerInventaire.setBombList(observableBombs);

        Stage stage = (Stage) grid.getScene().getWindow();
        stage.setScene(new Scene(inventaireRoot));
        stage.show();
    }



}
