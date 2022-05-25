package application.view;


import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import application.DailyBankState;
import application.control.EmployesManagement;
import application.tools.ConstantesIHM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;


public class EmployesManagementController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private EmployesManagement em;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private ObservableList<Employe> ole;

	
	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, EmployesManagement _em, DailyBankState _dbstate) {
		this.em = _em;
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
		this.ole = FXCollections.observableArrayList();
		this.lvEmployes.setItems(this.ole);
		this.lvEmployes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvEmployes.getFocusModel().focus(-1);
		this.lvEmployes.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
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
	private ListView<Employe> lvEmployes;
	@FXML
	private Button btnVoirEmploye;
	@FXML
	private Button btnModifEmploye;

	
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

		// Recherche des employés en BD
		ArrayList<Employe> listeEmp;
		listeEmp = this.em.getlisteComptes(numCompte, debutNom, debutPrenom);

		this.ole.clear();
		for (Employe emp : listeEmp) {
			this.ole.add(emp);
		}

		this.validateComponentState();
	}
	

	/**
	 * Modifie un employé.
	 */
	@FXML
	private void doModifierEmploye() {
		int selectedIndice = this.lvEmployes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Employe empMod = this.ole.get(selectedIndice);
			Employe result = this.em.modifierEmploye(empMod);
			if (result != null) {
				this.ole.set(selectedIndice, result);
			}
		}
	}
	

	/**
	 * Montre en lecture seule les informations d'un employé.
	 */
	@FXML
	private void doVoirEmploye() {
		int selectedIndice = this.lvEmployes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.em.voirEmploye(this.ole.get(selectedIndice));
		}
	}

	
	/**
	 * Ajoute un nouvel employé.
	 */
	@FXML
	private void doNouvelEmploye() {
		Employe employe;
		employe = this.em.nouvelEmploye();
		if (employe != null) {
			this.ole.add(employe);
		}
	}

	
	/**
	 * Gere les boutons en fonction des situations.
	 **/
	private void validateComponentState() {
		int selectedIndice = this.lvEmployes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			if (!ConstantesIHM.estInactif(this.lvEmployes.getSelectionModel().getSelectedItem())) {
				this.btnModifEmploye.setDisable(false);
			} else {
				this.btnModifEmploye.setDisable(true);
			}
			this.btnVoirEmploye.setDisable(false);
		} else {
			this.btnModifEmploye.setDisable(true);
			this.btnVoirEmploye.setDisable(true);
		}
	}
	
}
