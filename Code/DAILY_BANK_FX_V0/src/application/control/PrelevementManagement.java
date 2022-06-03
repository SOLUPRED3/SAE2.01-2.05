package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.ComptesManagementController;
import application.view.PrelevementManagementController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;

import java.net.URL;
import java.util.ResourceBundle;

public class PrelevementManagement {
    private Stage primaryStage;
    private PrelevementManagementController pmc;
    private DailyBankState dbs;
    private CompteCourant compteDuClient;

    /**
     * Constructueur de ComptesManagement
     * @param _parentStage
     * @param _dbstate
     * @param compte
     */
    public PrelevementManagement(Stage _parentStage, DailyBankState _dbstate, CompteCourant compte) {

        this.compteDuClient = compte;
        this.dbs = _dbstate;
        try {
            FXMLLoader loader = new FXMLLoader(PrelevementManagementController.class.getResource("prelevementmanagement.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, root.getPrefWidth()+50, root.getPrefHeight()+10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.primaryStage = new Stage();
            this.primaryStage.initModality(Modality.WINDOW_MODAL);
            this.primaryStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
            this.primaryStage.setScene(scene);
            this.primaryStage.setTitle("Gestion des prélèvements");
            this.primaryStage.setResizable(false);

            this.pmc = loader.getController();
            this.pmc.initContext(this.primaryStage, this, _dbstate, compteDuClient);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Procédure qui permet d'afficher la page du compte.
     */
    public void doPrelevementManagementDialog() {
        this.pmc.displayDialog();
    }

}
