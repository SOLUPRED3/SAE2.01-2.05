package application.view;

import application.DailyBankState;
import application.control.PrelevementManagement;
import application.tools.ConstantesIHM;
import application.tools.PairsOfValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.*;
import model.orm.AccessCompteCourant;
import model.orm.AccessOperation;
import model.orm.AccessPrelevementAutomatique;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import oracle.jdbc.proxy.annotation.Pre;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
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
    @FXML
    private Button btnExecPrelevement;


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
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
        this.olPrelevement = FXCollections.observableArrayList();
        this.lvPrelevement.setItems(this.olPrelevement);
        this.lvPrelevement.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.lvPrelevement.getFocusModel().focus(-1);
        this.lvPrelevement.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
        setLabelMessage(this.compteDuClient);
       this.validateComponentState();
        this.lvPrelevement.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
        this.loadList();
    }

    private void setLabelMessage(CompteCourant compte){
        if(compte != null){
            String info = "IDNUMCOMPTE : " + this.compteDuClient.idNumCompte + "  SOLDE : " + this.compteDuClient.solde  + " DÉCOUVERT AUTORISÉ : " + this.compteDuClient.debitAutorise;
            this.lblInfosCompte.setText(info);
        }
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

    public int getNumIdPrelev(){
        return this.lvPrelevement.getSelectionModel().getSelectedItem().idPrelevement;
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

    @FXML
    private void executePrelevement(){
        int selectedIndice = this.lvPrelevement.getSelectionModel().getSelectedIndex();
        LocalDate local = LocalDate.now();
        int jour = local.getDayOfMonth();
        int idNumCompte = this.olPrelevement.get(selectedIndice).idNumCompte;
        int montant = this.olPrelevement.get(selectedIndice).montant;
        if(this.olPrelevement.get(selectedIndice).dateReccurence == jour){
            try {
                if(this.compteDuClient.solde > 0) {
                    if (this.compteDuClient.solde - this.olPrelevement.get(selectedIndice).montant > this.compteDuClient.debitAutorise) {
                        AccessOperation ac = new AccessOperation();
                        ac.insertDebit(idNumCompte, montant, ConstantesIHM.TYPE_OP_8);
                        this.compteDuClient = loadCompte();
                        setLabelMessage(this.compteDuClient);
                    }
                }
                else if(this.compteDuClient.solde < 0){
                    if(this.compteDuClient.solde - this.olPrelevement.get(selectedIndice).montant < this.compteDuClient.debitAutorise){
                        AccessOperation ac = new AccessOperation();
                        ac.insertDebit(idNumCompte, montant, ConstantesIHM.TYPE_OP_8);
                        this.compteDuClient = loadCompte();
                        setLabelMessage(this.compteDuClient);
                    }
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Exécution du prélèvement");
                    alert.setHeaderText("Vous ne pouvez pas exécuter ce prélèvement, le découvert serait dépassé.");
                    alert.showAndWait();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Exécution du prélèvement");
            alert.setHeaderText("Vous ne pouvez pas exécuter ce prélèvement, nous ne sommes pas encore le " + this.olPrelevement.get(selectedIndice).dateReccurence );
            alert.showAndWait();
        }
    }

    @FXML
    private void doModifierPrelevement() {
        int selectedIndice = this.lvPrelevement.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            CompteCourant compteClient = compteDuClient ;
            Prelevement prelevement = this.olPrelevement.get(selectedIndice);
            Prelevement result = this.pm.modifierPrelevement(compteClient, prelevement) ;
            if (result != null) {
                this.olPrelevement.set(selectedIndice, result);
            }
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

    private CompteCourant loadCompte(){
        AccessCompteCourant ac = new AccessCompteCourant();
        try {
            CompteCourant compte = ac.getCompteCourant(compteDuClient.idNumCompte);
            return compte ;
        } catch (RowNotFoundOrTooManyRowsException e) {
            e.printStackTrace();
        } catch (DataAccessException e) {
            e.printStackTrace();
        } catch (DatabaseConnexionException e) {
            e.printStackTrace();
        }
        return null ;
    }


    private void validateComponentState() {
        int selectedIndice = this.lvPrelevement.getSelectionModel().getSelectedIndex();
        if (!compteInactif()) {
            if (selectedIndice >= 0) {
                this.btnVoirHistorique.setDisable(false);
                this.btnSupprPrelevement.setDisable(false);
                this.btnModifierPrelevement.setDisable(false);
                this.btnAjoutPrelevement.setDisable(false);
                this.btnExecPrelevement.setDisable(false);
            } else {
                this.btnVoirHistorique.setDisable(true);
                this.btnSupprPrelevement.setDisable(true);
                this.btnModifierPrelevement.setDisable(true);
                this.btnVoirHistorique.setDisable(true);
                this.btnExecPrelevement.setDisable(true);
            }
        } else {
            if (selectedIndice >= 0) {
                this.btnVoirHistorique.setDisable(false);
            } else {
                this.btnVoirHistorique.setDisable(true);
            }
            this.btnSupprPrelevement.setDisable(true);
            this.btnExecPrelevement.setDisable(true);
            this.btnModifierPrelevement.setDisable(true);
        }
        if (this.compteInactif()) {
            this.btnAjoutPrelevement.setDisable(true);
        } else {
            this.btnAjoutPrelevement.setDisable(false);
        }
    }

    /**
     * Vérifie et clôture un compte.
     */
    @FXML
    private void doSupprimerPrelevement() {
        Prelevement prelevement = this.lvPrelevement.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Êtes-vous certain(e) de vouloir supprimer ce prélèvement ? ");

        alert.getButtonTypes().setAll(ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> response = alert.showAndWait();

        if (response.orElse(null) == ButtonType.YES) {
            this.pm.deletePrelevement() ;
            this.loadList();
        } else if(response.orElse(null) == ButtonType.NO) {
            System.out.println("On reste encore un peu...");
        }

    }


}