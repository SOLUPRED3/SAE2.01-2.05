package application.view;

import application.DailyBankState;
import application.control.ClientsManagement;
import application.control.ComptesManagement;
import application.control.PrelevementManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Prelevement;

import java.net.URL;
import java.util.ResourceBundle;

public class PrelevementManagementController implements Initializable {
    // Etat application
    private DailyBankState dbs;
    private PrelevementManagement pm;

    // Fenêtre physique
    private Stage primaryStage;

    // Données de la fenêtre :

    private CompteCourant compteDuClient;
    private ObservableList<Prelevement> olPrelevement;

    // Données du Stage :

    @FXML
    private ListView<Prelevement> lvPrelevement;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void initContext(Stage _primaryStage, PrelevementManagement _pm, DailyBankState _dbstate, CompteCourant compte) {
        this.pm = _pm;
        this.primaryStage = _primaryStage;
        this.dbs = _dbstate;
        this.compteDuClient = compteDuClient;
    }

   private void configure() {
        //String info;
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
        /*this.olPrelevement = FXCollections.observableArrayList();
        this.lvPrelevement.setItems(this.olPrelevement);
        this.lvPrelevement.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.lvPrelevement.getFocusModel().focus(-1);
        //this.lvPrelevement.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
        */
    }

    private Object closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
        return null;
    }

    /**
     * Annule la création/modification d'un employé et ferme la fenêtre.
     */
    @FXML
    private void doCancel() {
        this.primaryStage.close();
    }


    public void displayDialog() {
        System.out.println(this.primaryStage);
        this.primaryStage.showAndWait();
    }


}
