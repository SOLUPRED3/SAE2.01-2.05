package application.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import application.DailyBankState;
import application.control.ExceptionDialog;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.AccessCompteCourant;
import model.orm.exception.ApplicationException;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.Table;

public class ClientEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientEdite;
	private EditionMode em;
	private Client clientResult;

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, DailyBankState _dbstate) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	public Client displayDialog(Client client, EditionMode mode) {
		this.em = mode;
		if (client == null) {
			this.clientEdite = new Client(0, "", "", "", "", "", "N", this.dbs.getEmpAct().idAg);
		} else {
			this.clientEdite = new Client(client);
		}
		
		this.clientResult = null;		
		switch (mode) {
		
		case CREATION:
			this.txtIdcli.setDisable(true);
			this.txtNom.setDisable(false);
			this.txtPrenom.setDisable(false);
			this.txtTel.setDisable(false);
			this.txtMail.setDisable(false);
			this.rbActif.setSelected(true);
			this.rbInactif.setSelected(false);
			if ((ConstantesIHM.isAdmin(this.dbs.getEmpAct()) && !ConstantesIHM.estInactif(this.clientEdite))) {
				this.rbActif.setDisable(false);
				this.rbInactif.setDisable(false);
			} else {
				this.rbActif.setDisable(true);
				this.rbInactif.setDisable(true);
			}
			this.lblMessage.setText("Informations sur le nouveau client");
			this.butOk.setText("Ajouter");
			this.butCancel.setText("Annuler");
			break;
			
		case VISUALISATION:
			this.txtIdcli.setDisable(true);
			this.txtNom.setDisable(true);
			this.txtPrenom.setDisable(true);
			this.txtTel.setDisable(true);
			this.txtMail.setDisable(true);
			this.txtAdr.setDisable(true);
			this.rbActif.setDisable(true);
			this.rbInactif.setDisable(true);
			this.rbActif.setSelected(true);
			this.rbInactif.setSelected(false);
			this.butOk.setVisible(false);
			this.lblMessage.setText("Informations client");
			this.butCancel.setText("Fermer");
			break;
			
		case MODIFICATION:
			this.txtIdcli.setDisable(true);
			this.txtNom.setDisable(false);
			this.txtPrenom.setDisable(false);
			this.txtTel.setDisable(false);
			this.txtMail.setDisable(false);
			this.rbActif.setSelected(true);
			this.rbInactif.setSelected(false);
			if ((ConstantesIHM.isAdmin(this.dbs.getEmpAct()) && !ConstantesIHM.estInactif(this.clientEdite))) {
				this.rbActif.setDisable(false);
				this.rbInactif.setDisable(false);
			} else {
				this.rbActif.setDisable(true);
				this.rbInactif.setDisable(true);
			}
			this.lblMessage.setText("Informations client");
			this.butOk.setText("Modifier");
			this.butCancel.setText("Annuler");
			break;
			
		case SUPPRESSION:
			// ce mode n'est pas utilisé pour les Clients :
			// la suppression d'un client n'existe pas il faut que le chef d'agence
			// bascule son état "Actif" à "Inactif"
			ApplicationException ae = new ApplicationException(Table.NONE, Order.OTHER, "SUPPRESSION CLIENT NON PREVUE",
					null);
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();

			break;
			
		}
				
		// initialisation du contenu des champs
		this.txtIdcli.setText("" + this.clientEdite.idNumCli);
		this.txtNom.setText(this.clientEdite.nom);
		this.txtPrenom.setText(this.clientEdite.prenom);
		this.txtAdr.setText(this.clientEdite.adressePostale);
		this.txtMail.setText(this.clientEdite.email);
		this.txtTel.setText(this.clientEdite.telephone);

		if (ConstantesIHM.estInactif(this.clientEdite)) {
			this.rbInactif.setSelected(true);
		} else {
			this.rbInactif.setSelected(false);
		}

		this.clientResult = null;
		this.primaryStage.showAndWait();
		return this.clientResult;
	}
	

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}
	

	// Attributs de la scene + actions
	@FXML
	private Label lblMessage;
	@FXML
	private TextField txtIdcli;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private TextField txtAdr;
	@FXML
	private TextField txtTel;
	@FXML
	private TextField txtMail;
	@FXML
	private RadioButton rbActif;
	@FXML
	private RadioButton rbInactif;
	@FXML
	private ToggleGroup actifInactif;
	@FXML
	private Button butOk;
	@FXML
	private Button butCancel;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	
	/**
	 * Annule la création/modification d'un employé et ferme la fenêtre.
	 */
	@FXML
	private void doCancel() {
		this.clientResult = null;
		this.primaryStage.close();
	}

	
	@FXML
	private void doAjouter() {
		switch (this.em) {
		
		case CREATION:
			if (this.isSaisieValide()) {
				this.clientResult = this.clientEdite;
				this.primaryStage.close();
			}
			break;
			
		case MODIFICATION:
			if (this.isSaisieValide()) {
				this.clientResult = this.clientEdite;
				this.primaryStage.close();
			}
			break;
			
		case SUPPRESSION:
			this.clientResult = this.clientEdite;
			this.primaryStage.close();
			break;
		}
	}
	
	
	/**
	 * Vérifie et rend inactif un client.
	 */
	private void doRendreInactif() {
		AccessCompteCourant acc = new AccessCompteCourant();
		ArrayList<CompteCourant> alComptes;
		
		try {
			alComptes = acc.getCompteCourants(this.clientEdite.idNumCli);
		} catch (DataAccessException dae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, dae);
			ed.doExceptionDialog();
			this.primaryStage.close();
			return;
		} catch (DatabaseConnexionException dce) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, dce);
			ed.doExceptionDialog();
			this.primaryStage.close();
			return;
		}
		
		boolean desactivable = true;
		for (CompteCourant compte: alComptes) {
			if (compte.estCloture.equals("N")) {
				desactivable = false;
				break;
			}
		}
		
		if (desactivable) {
			this.clientEdite.estInactif = ConstantesIHM.CLIENT_INACTIF;
		} else {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setContentText("Ce client ne peut pas être désactivé car certains de ses comptes ne sont pas clôturés.");
			alert.showAndWait();
			this.clientEdite.estInactif = ConstantesIHM.CLIENT_ACTIF;
		}
	}

	
	/**
	 * @return true si tous les champs d'ajout/modification sont correctes, sinon false.
	 */
	private boolean isSaisieValide() {
		this.clientEdite.nom = this.txtNom.getText().trim();
		this.clientEdite.prenom = this.txtPrenom.getText().trim();
		this.clientEdite.adressePostale = this.txtAdr.getText().trim();
		this.clientEdite.telephone = this.txtTel.getText().trim();
		this.clientEdite.email = this.txtMail.getText().trim();
		if (this.rbInactif.isSelected()) {			

			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Confirmation");
			alert.setContentText("Êtes-vous certain(e) de rendre inactif ce client ?\nTous ses comptes seront clôturés.\n(Il s'agit d'une action irréversible)");			
			alert.getButtonTypes().setAll(ButtonType.YES,ButtonType.NO);
			Optional<ButtonType> response = alert.showAndWait();
			
			if(response.orElse(null) == ButtonType.YES) {
				this.doRendreInactif();				
			}
		}

		if (this.clientEdite.nom.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le nom ne doit pas être vide",
					AlertType.WARNING);
			this.txtNom.requestFocus();
			return false;
		}
		if (this.clientEdite.prenom.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le prénom ne doit pas être vide",
					AlertType.WARNING);
			this.txtPrenom.requestFocus();
			return false;
		}

		String regex = "(0)[1-9][0-9]{8}";
		if (!Pattern.matches(regex, this.clientEdite.telephone) || this.clientEdite.telephone.length() > 10) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le téléphone n'est pas valable",
					AlertType.WARNING);
			this.txtTel.requestFocus();
			return false;
		}
		regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
				+ "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
		if (!Pattern.matches(regex, this.clientEdite.email) || this.clientEdite.email.length() > 20) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le mail n'est pas valable",
					AlertType.WARNING);
			this.txtMail.requestFocus();
			return false;
		}

		return true;
	}
	
}
