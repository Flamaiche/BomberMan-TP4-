package fr.univartois.butinfo.ihm.controller;

import fr.univartois.butinfo.ihm.model.Enemy;
import fr.univartois.butinfo.ihm.model.Facade;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EnemiesController {

    @FXML
    private ListView<Enemy> enemyListView;

    @FXML
    private Label enemyNameLabel;

    @FXML
    private Label enemyHealthLabel;

    @FXML
    private Label enemyMoveDistanceLabel;

    @FXML
    private Label enemyCoordinatesLabel;

    @FXML
    private Button closeButton;

    private Scene scenePrincipale;
    private Facade facade;

    @FXML
    private void initialize() {
        enemyListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                enemyNameLabel.setText(newVal.getName());
                enemyHealthLabel.setText(String.valueOf(newVal.getHealth()));
                enemyMoveDistanceLabel.setText(String.valueOf(newVal.getMoveDistance()));
                enemyCoordinatesLabel.setText("(" + newVal.getRow() + ", " + newVal.getColumn() + ")");
                closeButton.setDisable(false);
            } else {
                enemyNameLabel.setText("");
                enemyHealthLabel.setText("");
                enemyMoveDistanceLabel.setText("");
                enemyCoordinatesLabel.setText("");
                closeButton.setDisable(true);
            }
        });
    }

    public void setScenePrincipale(Scene scenePrincipale) {
        this.scenePrincipale = scenePrincipale;
    }

    public void setFacade(Facade facade) {
        this.facade = facade;
    }

    public void setEnemyList(ObservableList<Enemy> enemyList) {
        enemyListView.setItems(enemyList);

        if (!enemyList.isEmpty()) {
            enemyListView.getSelectionModel().selectFirst();
            enemyListView.requestFocus();
        } else {
            closeButton.setDisable(true);
        }
    }

    @FXML
    private void close() {
        facade.setPaused(false);
        Stage stage = (Stage) enemyListView.getScene().getWindow();
        stage.setScene(scenePrincipale);
    }
}
