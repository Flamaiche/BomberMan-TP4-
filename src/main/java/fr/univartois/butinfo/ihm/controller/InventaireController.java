package fr.univartois.butinfo.ihm.controller;

import fr.univartois.butinfo.ihm.model.AbstractBomb;
import fr.univartois.butinfo.ihm.model.Facade;
import fr.univartois.butinfo.ihm.model.Uploader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.Map;

public class InventaireController {

    @FXML
    private ListView<BombGroup> bombListView;

    @FXML
    private Label bombNameLabel;

    @FXML
    private Label bombQuantityLabel;

    @FXML
    private TextArea bombDescriptionArea;

    @FXML
    private ImageView bombImageView;

    @FXML
    private Button selectionButton;

    private Scene scenePrincipale;
    private Facade facade;

    @FXML
    private void initialize() {
        bombListView.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    validerChoix();
                    // Optionnel : effet visuel de clic sur le bouton
                    selectionButton.arm();
                    Platform.runLater(() -> selectionButton.disarm());
                    event.consume();
                    break;
                case ESCAPE:
                    annulerChoix();
                    event.consume();
                    break;
                default:
                    break;
            }
        });
    }

    public void setScenePrincipale(Scene scenePrincipale) {
        this.scenePrincipale = scenePrincipale;
    }

    public void setFacade(Facade facade) {
        this.facade = facade;
    }

    public void setBombList(ObservableList<AbstractBomb> bombList) {
        Map<String, BombGroup> groupedMap = new LinkedHashMap<>();

        for (AbstractBomb bomb : bombList) {
            if (bomb == null) continue; // sécurité
            String name = bomb.getName();
            if (groupedMap.containsKey(name)) {
                groupedMap.get(name).increment();
            } else {
                groupedMap.put(name, new BombGroup(bomb, 1));
            }
        }

        ObservableList<BombGroup> groupedList = FXCollections.observableArrayList(groupedMap.values());
        bombListView.setItems(groupedList);

        if (!groupedList.isEmpty()) {
            bombListView.getSelectionModel().selectFirst(); // sélectionne le 1er élément
            bombListView.requestFocus();
        }

        // Listener pour mettre à jour l'affichage des infos et la visibilité du bouton
        bombListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                bombNameLabel.setText(newVal.getBomb().getName());
                bombDescriptionArea.setText(newVal.getBomb().getDescription());
                bombQuantityLabel.setText(String.valueOf(newVal.getQuantity()));

                try {
                    bombImageView.setImage(Uploader.getImage(newVal.getBomb().getName() + ".png"));
                } catch (Exception e) {
                    bombImageView.setImage(null);
                }

                selectionButton.setVisible(true);
                selectionButton.setDisable(false);
            } else {
                selectionButton.setVisible(false);
                selectionButton.setDisable(true);
            }
        });

        // Si la liste est vide, on masque le bouton
        if (groupedList.isEmpty()) {
            selectionButton.setVisible(false);
            selectionButton.setDisable(true);
        }
    }



    @FXML
    private void validerChoix() {
        BombGroup selectedGroup = bombListView.getSelectionModel().getSelectedItem();
        if (selectedGroup != null) {
            facade.setBombPriority(selectedGroup.getBomb().getName());
            setBombList(FXCollections.observableArrayList(facade.getPlayer().getInventaireBomb()));
        }
        annulerChoix();
    }

    @FXML
    private void annulerChoix() {
        facade.setPaused(false);
        Stage stage = (Stage) bombListView.getScene().getWindow();
        stage.setScene(scenePrincipale);
    }
}

class BombGroup {
    private final AbstractBomb bomb;
    private int quantity;

    public BombGroup(AbstractBomb bomb, int quantity) {
        this.bomb = bomb;
        this.quantity = quantity;
    }

    public AbstractBomb getBomb() {
        return bomb;
    }

    public int getQuantity() {
        return quantity;
    }

    public void increment() {
        quantity++;
    }

    @Override
    public String toString() {
        return bomb.getName() + " (x" + quantity + ")";
    }
}

