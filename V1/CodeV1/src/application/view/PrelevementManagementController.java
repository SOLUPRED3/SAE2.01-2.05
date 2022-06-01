package application.view;

import application.DailyBankState;
import application.control.ComptesManagement;
import application.control.PrelevementManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Prelevement;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PrelevementManagementController implements Initializable {

    // Etat application
    private DailyBankState dbs;
    private PrelevementManagement pm;

    // Fenêtre physique
    private Stage primaryStage;

    // Données de la fenêtre
    private Client clientDesComptes;
    private ObservableList<Prelevement> olPrelevement;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void initContext(Stage _primaryStage, PrelevementManagement _pm, DailyBankState _dbstate, Client client) {
        this.pm = _pm;
        this.primaryStage = _primaryStage;
        this.dbs = _dbstate;
        this.clientDesComptes = client;
        this.configure();
    }

    private void configure() {
        String info;
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
        this.olPrelevement = FXCollections.observableArrayList();
        this.lvPrelevement.setItems(this.olPrelevement);
        this.lvPrelevement.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.lvPrelevement.getFocusModel().focus(-1);
        this.lvPrelevement.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

        info = this.clientDesComptes.nom + "  " + this.clientDesComptes.prenom + "  (id : "
                + this.clientDesComptes.idNumCli + ")";
        this.lblInfosClient.setText(info);

        this.loadList();
        this.validateComponentState();
    }

    public void displayDialog() {
        this.primaryStage.showAndWait();
    }

    private Object closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
        return null;
    }

    // Attributs de la scene + actions
    @FXML
    private Label lblInfosClient;
    @FXML
    private ListView<Prelevement> lvPrelevement;
    @FXML
    private Button btnModifierCompte;
    @FXML
    private Button btnCloturerPrelevement;
    @FXML
    private Button btnAjoutPrelevement;
    @FXML
    private Button btnVoirPrelevement;

    @FXML
    private void doCancel() {
        this.primaryStage.close();
    }

    private void loadList () {
        ArrayList<Prelevement> listeCpt;
        listeCpt = this.pm.getPrelevementDunClient();
        this.olPrelevement.clear();
        for (Prelevement pl : listeCpt) {
            this.olPrelevement.add(pl);
        }
    }


    private void validateComponentState() {
        /*

         */
    }
}
