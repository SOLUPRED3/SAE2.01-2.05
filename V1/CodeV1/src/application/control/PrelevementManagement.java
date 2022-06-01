package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.PrelevementManagementController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Prelevement;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PrelevementManagement implements Initializable {


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    private Stage primaryStage;
    private PrelevementManagementController pm;
    private DailyBankState dbs;
    private Client clientDesComptes;

    /**
     * Constructueur de ComptesManagement
     * @param _parentStage
     * @param _dbstate
     * @param client
     * @param cpt
     */
    public PrelevementManagement(Stage _parentStage, DailyBankState _dbstate, Client client, CompteCourant cpt) {

        this.clientDesComptes = client;
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
            this.primaryStage.setTitle("Gestion des virements");
            this.primaryStage.setResizable(false);

            this.pm = loader.getController();
            this.pm.initContext(this.primaryStage, this, _dbstate, client);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Prelevement> getPrelevementDunClient() {
        return new ArrayList<>() ;
        // À faire

    }

    /**
     * Procédure qui permet d'afficher la page du compte.
     */
    public void doPrelevementManagementDialog() {
        this.pm.displayDialog();
    }


}
