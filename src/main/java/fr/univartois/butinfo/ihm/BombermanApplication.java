package fr.univartois.butinfo.ihm;

import fr.univartois.butinfo.ihm.controller.ControleEntree;
import fr.univartois.butinfo.ihm.model.Facade;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BombermanApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view/bomberman.fxml"));
        Parent root = fxmlLoader.load();

        ControleEntree controller = fxmlLoader.getController();
        Facade facade = new Facade();
        facade.setControlerFacade(controller);

        controller.setFacade(facade);
        facade.initGame();

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Bomberman");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
