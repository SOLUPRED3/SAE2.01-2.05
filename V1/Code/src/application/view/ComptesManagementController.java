package application.view;


import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import application.DailyBankState;
import application.control.ComptesManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;


public class ComptesManagementController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private ComptesManagement cm;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDesComptes;
	private ObservableList<CompteCourant> olCompteCourant;

	
	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, ComptesManagement _cm, DailyBankState _dbstate, Client client) {
		this.cm = _cm;
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.clientDesComptes = client;
		this.configure();
	}

	
	private void configure() {
		String info;
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
		this.olCompteCourant = FXCollections.observableArrayList();
		this.lvComptes.setItems(this.olCompteCourant);
		this.lvComptes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvComptes.getFocusModel().focus(-1);
		this.lvComptes.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

		info = this.clientDesComptes.nom + "  " + this.clientDesComptes.prenom + "  (id : "
				+ this.clientDesComptes.idNumCli + ")";
		this.lblInfosClient.setText(info);

		this.loadList();
		this.validateComponentState();
	}
	

	public void displayDialog() {
		this.primaryStage.showAndWait();
	}
	
	
	/**
	 * @return le numéro du compte sélectionné.
	 */
	public int getNumCompte() {
		int value = lvComptes.getSelectionModel().getSelectedItem().idNumCompte ;
		//String value = Integer.toString(numCompte) ;
		return value ; 
	}
	

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}
	

	// Attributs de la scene + actions
	@FXML
	private Label lblInfosClient;
	@FXML
	private ListView<CompteCourant> lvComptes;
	@FXML
	private Button btnVoirOpes;
	@FXML
	private Button btnModifierCompte;
	@FXML
	private Button btnSupprCompte;
	@FXML
	private Button btnAjoutCompte;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	
	/**
	 * Annule la création/modification d'un employé et ferme la fenêtre.
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}
	
	/**
	 * Appelle la fonction pour voir les opérations.
	 */
	@FXML
	private void doVoirOperations() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.olCompteCourant.get(selectedIndice);
			this.cm.gererOperations(cpt);
		}
		this.loadList();
		this.validateComponentState();
	}
	
	/**
	 * Modifie un compte.
	 */
	@FXML
	private void doModifierCompte() {
	}
	
	
	/**
	 * Vérifie et clôture un compte.
	 */
	@FXML
	private void doCloturerCompte() {
		CompteCourant compte = this.lvComptes.getSelectionModel().getSelectedItem();
        if(compte.solde == 0) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Confirmation");
			alert.setContentText("Êtes-vous certain(e) de vouloir clôturer ce compte ? ");
			
			alert.getButtonTypes().setAll(ButtonType.YES,ButtonType.NO);	
			Optional<ButtonType> response = alert.showAndWait();
			
			if (response.orElse(null) == ButtonType.YES) {
				this.cm.cloturerCompte() ;
				this.loadList();
			} else if(response.orElse(null) == ButtonType.NO) {
				System.out.println("On reste encore un peu...");
			}
        } else {
        	Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setContentText("Ce compte ne peut pas être clôturé car son solde n'est pas nul.");
			alert.showAndWait();
        }
	}
	

	/**
	 * @return true si le compte sélectionné est clôturé, sinon false
	 */
	private boolean estCloture() {
		if(this.lvComptes.getSelectionModel().getSelectedIndex() >= 0 && this.lvComptes.getSelectionModel().getSelectedItem().estCloture.equals("N")) {
			return false; //this.lvComptes.getSelectionModel().getSelectedItem().estCloture; 
		}
		return true;
	}
	
	
	/**
	 * Créé un compte.
	 */
	@FXML
	private void doNouveauCompte() {
		CompteCourant compte;
		compte = this.cm.creerCompte();
		if (compte != null) {
			this.olCompteCourant.add(compte);
			this.loadList();
		}		
	}
	
	
	/**
	 * Recharge la ViewList de comptes. 
	 */
	private void loadList () {
		ArrayList<CompteCourant> listeCpt;
		listeCpt = this.cm.getComptesDunClient();
		this.olCompteCourant.clear();
		for (CompteCourant co : listeCpt) {
			this.olCompteCourant.add(co);
		}
	}
	
	
	/**
	 * Gére les boutons en fonction des situations.
	 */
	private void validateComponentState() {		
        int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
        if (!this.estCloture()) {
        	if (selectedIndice >= 0) {
        		this.btnVoirOpes.setDisable(false);
                this.btnSupprCompte.setDisable(false);
                this.btnModifierCompte.setDisable(false);
        	} else {
        		this.btnVoirOpes.setDisable(true);
                this.btnSupprCompte.setDisable(true);
                this.btnModifierCompte.setDisable(true);                
        	}
        	this.btnAjoutCompte.setDisable(false);
        } else {
        	if (selectedIndice >= 0) {
        		this.btnVoirOpes.setDisable(false);                
        	} else {
        		this.btnVoirOpes.setDisable(true);
        	}
        	this.btnSupprCompte.setDisable(true);
            this.btnModifierCompte.setDisable(true);
            this.btnAjoutCompte.setDisable(true);
        }
        this.btnModifierCompte.setDisable(true); //TOMODIF
	}
	
}
