package application.view;

import application.DailyBankState;
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
import oracle.jdbc.proxy.annotation.Pre;

import java.net.URL;
import java.util.ArrayList;
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

    // Attributs de la scène :
    // Données du Stage :

    @FXML
    private ListView<Prelevement> lvPrelevement;
    @FXML
    private Label lblInfosCompte;
    @FXML
    private Button btnVoirHistorique;
    @FXML
    private Button btnModifierPrelevement;
    @FXML
    private Button btnSupprPrelevement;
    @FXML
    private Button btnAjoutPrelevement;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
    public void initContext(Stage _primaryStage, PrelevementManagement _pm, DailyBankState _dbstate, CompteCourant compte) {
        this.pm = _pm;
        this.primaryStage = _primaryStage;
        this.dbs = _dbstate;
        this.compteDuClient = compte;
        this.configure();
    }

    private void configure() {
        String info;
        //String info;
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
        this.olPrelevement = FXCollections.observableArrayList();
        this.olPrelevement = FXCollections.observableArrayList();
        this.lvPrelevement.setItems(this.olPrelevement);
        this.lvPrelevement.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.lvPrelevement.getFocusModel().focus(-1);
        this.lvPrelevement.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
        if(this.compteDuClient != null){
            info = "IDNUMCOMPTE : " + this.compteDuClient.idNumCompte + "  SOLDE : " + this.compteDuClient.solde  + " DÉCOUVERT AUTORISÉ : " + this.compteDuClient.debitAutorise;
            this.lblInfosCompte.setText(info);
        }
       this.validateComponentState();
        this.lvPrelevement.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
        this.loadList();
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

    /**
     * @return true si le compte sélectionné est clôturé, sinon false
     */
    private boolean compteInactif() {
        if(this.compteDuClient.estCloture.equals("N")) {
            return false;
        }
        return true;
    }


    public void displayDialog() {
        System.out.println(this.primaryStage);
        this.primaryStage.showAndWait();
    }

    /**
     * Créé un compte.
     */
    @FXML
    private void doNouveauPrelevement() {
        Prelevement prelevement;
        prelevement = this.pm.creerPrelevement();
        if (prelevement != null) {
            this.olPrelevement.add(prelevement);
            this.loadList();
        }
    }

    /**
     * Recharge la ViewList de comptes.
     */
    private void loadList () {
        ArrayList<Prelevement> listePl;
        listePl = this.pm.getPrelevementComptes();
        this.olPrelevement.clear();
        for (Prelevement pl : listePl) {
            this.olPrelevement.add(pl);
        }
    }


    private void validateComponentState() {
        int selectedIndice = this.lvPrelevement.getSelectionModel().getSelectedIndex();
        if (!compteInactif()) {
            if (selectedIndice >= 0) {
                this.btnVoirHistorique.setDisable(false);
                this.btnSupprPrelevement.setDisable(false);
                this.btnModifierPrelevement.setDisable(false);
                this.btnAjoutPrelevement.setDisable(false);
            } else {
                this.btnVoirHistorique.setDisable(true);
                this.btnSupprPrelevement.setDisable(true);
                this.btnModifierPrelevement.setDisable(true);
                this.btnVoirHistorique.setDisable(true);
            }
        } else {
            if (selectedIndice >= 0) {
                this.btnVoirHistorique.setDisable(false);
            } else {
                this.btnVoirHistorique.setDisable(true);
            }
            this.btnSupprPrelevement.setDisable(true);
            this.btnModifierPrelevement.setDisable(true);
        }
        if (this.compteInactif()) {
            this.btnAjoutPrelevement.setDisable(true);
        } else {
            this.btnAjoutPrelevement.setDisable(false);
        }
    }



}