package application.view;


import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import application.DailyBankState;
import application.control.ClientsManagement;
import application.tools.ConstantesIHM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;


public class ClientsManagementController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private ClientsManagement cm;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private ObservableList<Client> olc;

	
	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, ClientsManagement _cm, DailyBankState _dbstate) {
		this.cm = _cm;
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
		this.olc = FXCollections.observableArrayList();
		this.lvClients.setItems(this.olc);
		this.lvClients.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvClients.getFocusModel().focus(-1);
		this.lvClients.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
		this.validateComponentState();
	}

	
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	
	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	
	// Attributs de la scene + actions
	@FXML
	private TextField txtNum;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private ListView<Client> lvClients;
	@FXML
	private Button btnVoirClient;
	@FXML
	private Button btnModifClient;
	@FXML
	private Button btnComptesClient;

	
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
	 * Recherche des employés dans la base de données et les affiches sur l'interface.
	 */
	@FXML
	private void doRechercher() {
		int numCompte;
		try {
			String nc = this.txtNum.getText();
			if (nc.equals("")) {
				numCompte = -1;
			} else {
				numCompte = Integer.parseInt(nc);
				if (numCompte < 0) {
					this.txtNum.setText("");
					numCompte = -1;
				}
			}
		} catch (NumberFormatException nfe) {
			this.txtNum.setText("");
			numCompte = -1;
		}

		String debutNom = this.txtNom.getText();
		String debutPrenom = this.txtPrenom.getText();

		if (numCompte != -1) {
			this.txtNom.setText("");
			this.txtPrenom.setText("");
		} else {
			if (debutNom.equals("") && !debutPrenom.equals("")) {
				this.txtPrenom.setText("");
			}
		}

		// Recherche des clients en BD. cf. AccessClient > getClients(.)
		// numCompte != -1 => recherche sur numCompte
		// numCompte != -1 et debutNom non vide => recherche nom/prenom
		// numCompte != -1 et debutNom vide => recherche tous les clients
		ArrayList<Client> listeCli;
		listeCli = this.cm.getlisteComptes(numCompte, debutNom, debutPrenom);

		this.olc.clear();
		for (Client cli : listeCli) {
			this.olc.add(cli);
		}
		
		this.validateComponentState();
	}
	

	/**
	 * Ouvre le gestionnaire des comptes d'un client.
	 */
	@FXML
	private void doComptesClient() {
		int selectedIndice = this.lvClients.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Client client = this.olc.get(selectedIndice);
			this.cm.gererComptesClient(client);
		}
	}


	/**
	 * Permet de réaliser une simulation d'emprunt en tant que chef d'agence.
	 */
	@FXML
	private void doSimulation() {
		if(this.dbs.isChefDAgence()) {
			this.cm.realiserSimulation();
		} else {
			Alert alertinfo = new Alert(Alert.AlertType.WARNING);
			alertinfo.setHeaderText("Seul les chefs d'agence peuvent effectuer une simulation !");
			alertinfo.setTitle("Erreur");
			alertinfo.show();
		}
	}
	

	/**
	 * Modifie un client.
	 */
	@FXML
	private void doModifierClient() {
		int selectedIndice = this.lvClients.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Client cliMod = this.olc.get(selectedIndice);
			Client result = this.cm.modifierClient(cliMod);
			if (result != null) {
				this.olc.set(selectedIndice, result);
			}
		}
	}
	

	/**
	 * Montre en lecture seule les informations d'un client.
	 */
	@FXML
	private void doVoirClient() {
		int selectedIndice = this.lvClients.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.cm.voirClient(this.olc.get(selectedIndice));
		}
	}

	
	/**
	 * Ajoute un nouveau client
	 */
	@FXML
	private void doNouveauClient() {
		Client client;
		client = this.cm.nouveauClient();
		if (client != null) {
			this.olc.add(client);
		}
	}
	

	/**
	 * Gére les boutons en fonction des situations.
	 **/
	private void validateComponentState() {
		int selectedIndice = this.lvClients.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			if (!ConstantesIHM.estInactif(this.lvClients.getSelectionModel().getSelectedItem())) {
				this.btnModifClient.setDisable(false);
			} else {
				this.btnModifClient.setDisable(true);
			}
			this.btnComptesClient.setDisable(false);
			this.btnVoirClient.setDisable(false);
		} else {
			this.btnModifClient.setDisable(true);
			this.btnComptesClient.setDisable(true);
			this.btnVoirClient.setDisable(true);
		}
	}
	
}
