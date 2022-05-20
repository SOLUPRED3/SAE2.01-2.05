package application.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.ComptesManagement;
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	/*
	 * Permet de fermer la page en appuyant sur le bouton annuler.
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}
	
	/*
	 * Procédure qui permet d'appeler la fonction pour voir les opérations.
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
	
	/*
	 * Procédure qui permet de modifier un compte.
	 */
	@FXML
	private void doModifierCompte() {
	}
	
	
	/*
	 * Procédure qui permet de clôturer un compte en appuyant sur le bouton.
	 */
	@FXML
<<<<<<< HEAD
	private void doCloturerCompte() {
		this.cm.cloturerCompte() ;
		this.loadList();
		
=======
	private void doSupprimerCompte() {
		this.cm.supprimerCompte() ;
		this.loadList();		
>>>>>>> 988a6be7dc6bef29bb600fed5d98dd7fdd3d32ca
	}
	
	/*
	 * Fonction qui permet de savoir si le compte sélectionné sur la viewlist est clôturé ou non.
	 */
	private String getEstCloture() {
		if(this.lvComptes.getSelectionModel().getSelectedIndex() >= 0 && this.lvComptes.getSelectionModel().getSelectedItem().estCloture.equals("N")) {
			return this.lvComptes.getSelectionModel().getSelectedItem().estCloture ; 
		}
		else return "O" ; 
	}

<<<<<<< HEAD
	
	/*
	 * Permet d'appeler la fonction creerCompte() en appuyant sur le boutons "Créer Compte".
	 */
=======
>>>>>>> 988a6be7dc6bef29bb600fed5d98dd7fdd3d32ca
	@FXML
	private void doNouveauCompte() {
		CompteCourant compte;
		compte = this.cm.creerCompte();
		if (compte != null) {
			this.olCompteCourant.add(compte);
			this.loadList();
		}		
	}
	
	/*
	 * Procédure qui permet de régénérer la viewlist.
	 */
	private void loadList () {
		ArrayList<CompteCourant> listeCpt;
		listeCpt = this.cm.getComptesDunClient();
		this.olCompteCourant.clear();
		for (CompteCourant co : listeCpt) {
			this.olCompteCourant.add(co);
		}
	}
	
	/*
	 * Procédure qui permet de gérer les boutons en fonction des situations.
	 */
	private void validateComponentState() {
        this.btnModifierCompte.setDisable(true);
        this.btnSupprCompte.setDisable(true);

        int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
        if(selectedIndice >=0) {
            if (getEstCloture().equals("O")) {
                this.btnVoirOpes.setDisable(false);
                this.btnSupprCompte.setDisable(true);
                this.btnModifierCompte.setDisable(true);
            } else {
                this.btnVoirOpes.setDisable(false);
                this.btnSupprCompte.setDisable(false);
                this.btnModifierCompte.setDisable(false);
            }
        }
        else {
            this.btnVoirOpes.setDisable(true);
            this.btnSupprCompte.setDisable(true);
            this.btnModifierCompte.setDisable(true);
        }        
    }
}
