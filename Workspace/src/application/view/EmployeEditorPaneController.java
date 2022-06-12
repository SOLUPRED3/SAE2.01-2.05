package application.view;


import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
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
import model.data.Employe;
import model.orm.exception.ApplicationException;
import model.orm.exception.Order;
import model.orm.exception.Table;


public class EmployeEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Employe employeEdite;
	private EditionMode em;
	private Employe employeResult;

	
	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, DailyBankState _dbstate) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	
	public Employe displayDialog(Employe employe, EditionMode mode) {
		this.em = mode;
		if (employe == null) {
			this.employeEdite = new Employe(0, "", "", "", "", "", this.dbs.getEmpAct().idAg, "N");
		} else {
			this.employeEdite = new Employe(employe);
		}
		this.employeResult = null;
		switch (mode) {
		
		case CREATION:
			this.txtIdEmp.setDisable(true);
			this.txtNom.setDisable(false);
			this.txtPrenom.setDisable(false);
			this.txtLogin.setDisable(false);
			this.txtMotPasse.setDisable(false);
			this.rbGuichetier.setSelected(true);
			this.rbChefAgence.setSelected(false);
			this.rbActif.setSelected(true);
			this.rbInactif.setSelected(false);
			if (!ConstantesIHM.estInactif(this.employeEdite)) {
				this.rbActif.setDisable(false);
				this.rbInactif.setDisable(false);
			} else {
				this.rbActif.setDisable(true);
				this.rbInactif.setDisable(true);
			}
			this.lblMessage.setText("Informations sur le nouvel employé");
			this.butOk.setText("Ajouter");
			this.butCancel.setText("Annuler");
			break;
			
		case MODIFICATION:
			this.txtIdEmp.setDisable(true);
			this.txtNom.setDisable(false);
			this.txtPrenom.setDisable(false);
			this.txtLogin.setDisable(false);
			this.txtMotPasse.setDisable(false);
			this.rbGuichetier.setSelected(true);
			this.rbChefAgence.setSelected(false);
			this.rbActif.setSelected(true);
			this.rbInactif.setSelected(false);
			if (!ConstantesIHM.estInactif(this.employeEdite)) {
				this.rbActif.setDisable(false);
				this.rbInactif.setDisable(false);
			} else {
				this.rbActif.setDisable(true);
				this.rbInactif.setDisable(true);
			}
			this.lblMessage.setText("Informations employé");
			this.butOk.setText("Modifier");
			this.butCancel.setText("Annuler");
			break;
			
		case VISUALISATION:
			this.txtIdEmp.setDisable(true);
			this.txtNom.setDisable(true);
			this.txtPrenom.setDisable(true);
			this.rbGuichetier.setDisable(true);
			this.rbChefAgence.setDisable(true);
			this.txtLogin.setDisable(true);
			this.txtMotPasse.setDisable(true);
			this.rbActif.setDisable(true);
			this.rbInactif.setDisable(true);
			this.rbActif.setSelected(true);
			this.rbInactif.setSelected(false);
			this.butOk.setVisible(false);
			this.lblMessage.setText("Informations employé");
			this.butCancel.setText("Fermer");
			break;
			
		case SUPPRESSION:
			// ce mode n'est pas utilisé pour les Clients :
			// la suppression d'un client n'existe pas il faut que le chef d'agence
			// bascule son état "Actif" à "Inactif"
			ApplicationException ae = new ApplicationException(Table.NONE, Order.OTHER, "SUPPRESSION CLIENT NON PREVUE", null);
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			break;
		}
		
		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
			// rien pour l'instant
		}
		
		// initialisation du contenu des champs
		this.txtIdEmp.setText("" + this.employeEdite.idEmploye);
		this.txtNom.setText(this.employeEdite.nom);
		this.txtPrenom.setText(this.employeEdite.prenom);
		this.txtLogin.setText(this.employeEdite.login);
		this.txtMotPasse.setText(this.employeEdite.motPasse);
		
		if (this.employeEdite.droitsAccess.equals(ConstantesIHM.AGENCE_GUICHETIER)) {
			this.rbGuichetier.setSelected(true);
		} else if (this.employeEdite.droitsAccess.equals(ConstantesIHM.AGENCE_CHEF)) {
			this.rbGuichetier.setSelected(false);
		}
		if (ConstantesIHM.estInactif(this.employeEdite)) {
			this.rbInactif.setSelected(true);
		} else {
			this.rbInactif.setSelected(false);
		}

		this.employeResult = null;
		this.primaryStage.showAndWait();
		return this.employeResult;
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
	private TextField txtIdEmp;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private TextField txtLogin;
	@FXML
	private TextField txtMotPasse;
	@FXML
	private RadioButton rbGuichetier;
	@FXML
	private RadioButton rbChefAgence;
	@FXML
	private RadioButton rbActif;
	@FXML
	private RadioButton rbInactif;
	@FXML
	private ToggleGroup droitsAccesGroup;
	@FXML
	private ToggleGroup actifInactif2;
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
		this.employeResult = null;
		this.primaryStage.close();
	}

	
	/**
	 * Confirme l'ajoute/modification d'un employé.
	 */
	@FXML
	private void doAjouter() {
		switch (this.em) {
		case CREATION:
			if (this.isSaisieValide()) {
				this.employeResult = this.employeEdite;
				this.primaryStage.close();
			}
			break;
		case MODIFICATION:
			if (this.isSaisieValide()) {
				this.employeResult = this.employeEdite;
				this.primaryStage.close();
			}
			break;
		case SUPPRESSION:
			this.employeResult = this.employeEdite;
			this.primaryStage.close();
			break;
		}
	}
	

	/**
	 * @return true si tous les champs d'ajout/modification sont correctes, sinon false.
	 */
	private boolean isSaisieValide() {
		this.employeEdite.nom = this.txtNom.getText().trim();
		this.employeEdite.prenom = this.txtPrenom.getText().trim();
		this.employeEdite.login = this.txtLogin.getText().trim();
		this.employeEdite.motPasse = this.txtMotPasse.getText().trim();
		if (this.rbGuichetier.isSelected()) {
			this.employeEdite.droitsAccess = ConstantesIHM.AGENCE_GUICHETIER;
		} else {
			this.employeEdite.droitsAccess = ConstantesIHM.AGENCE_CHEF;
		}
		if (this.rbInactif.isSelected()) {			

			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Confirmation");
			alert.setContentText("Êtes-vous certain(e) de rendre inactif ce client ?\nTous ses comptes seront clôturés.\n(Il s'agit d'une action irréversible)");			
			alert.getButtonTypes().setAll(ButtonType.YES,ButtonType.NO);
			Optional<ButtonType> response = alert.showAndWait();
			
			if(response.orElse(null) == ButtonType.YES) {
				this.employeEdite.estInactif = ConstantesIHM.EMPLOYE_INACTIF;
			}
		} else {
			this.employeEdite.estInactif = ConstantesIHM.EMPLOYE_ACTIF;
		}

		if (this.employeEdite.nom.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le nom ne doit pas être vide !",
					AlertType.WARNING);
			this.txtNom.requestFocus();
			return false;
		}
		if (this.employeEdite.prenom.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le prénom ne doit pas être vide !",
					AlertType.WARNING);
			this.txtPrenom.requestFocus();
			return false;
		}

		/*if (!this.employeEdite.droitsAccess.equals("chefAgence") && !this.employeEdite.droitsAccess.equals("guichetier")) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Les droits d'accès ne sont pas valables",
					AlertType.WARNING);
			this.txtDroitAccess.requestFocus();
			return false;
		}*/

		if (this.employeEdite.login.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le login ne doit pas être vide !",
					AlertType.WARNING);
			this.txtLogin.requestFocus();
			return false;
		}
		
		if (this.employeEdite.motPasse.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le mot de passe ne doit pas être vide !",
					AlertType.WARNING);
			this.txtMotPasse.requestFocus();
			return false;
		}

		return true;
	}
}
