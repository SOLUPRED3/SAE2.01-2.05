package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.CompteEditorPaneController;
import application.view.PrelevementEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Prelevement;

public class PrelevementEditorPane {
    private Stage primaryStage;
    private PrelevementEditorPaneController pelc;

    /**
     * Constructeur qui initialise la page d'édition des comptes.
     * @param _parentStage
     * @param _dbstate
     */
    public PrelevementEditorPane(Stage _parentStage, DailyBankState _dbstate) {

        try {
            FXMLLoader loader = new FXMLLoader(CompteEditorPaneController.class.getResource("prelevementeditorpane.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, root.getPrefWidth()+200, root.getPrefHeight()+100);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.primaryStage = new Stage();
            this.primaryStage.initModality(Modality.WINDOW_MODAL);
            this.primaryStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
            this.primaryStage.setScene(scene);
            this.primaryStage.setTitle("Gestion d'un compte");
            this.primaryStage.setResizable(false);

            this.pelc = loader.getController();
            this.pelc.initContext(this.primaryStage, _dbstate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fonction qui lance la page d'édition des comptes et retourne le compte en train d'être édité.
     * @param compteCourant
     * @param pl
     * @param em
     * @return
     */
    public Prelevement doCompteEditorDialog(CompteCourant compteCourant, Prelevement pl, EditionMode em) {
        return this.pelc.displayDialog(compteCourant, pl, em);
    }
}

