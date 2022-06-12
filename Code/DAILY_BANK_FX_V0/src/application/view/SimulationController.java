package application.view;


import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import application.control.Simulation;
import application.tools.AlertUtilities;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class SimulationController implements Initializable {

	// Etat application
	private Simulation sm;
	
	// Fenetre physique
	private Stage primaryStage;


	// Manipulation de la fenetre
	public void initContext(Stage _primaryStage, Simulation _sm) {
		this.sm = _sm;
		this.primaryStage = _primaryStage;
		this.configure();
	}	


	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
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
	private TextField txtCapital;
	@FXML
	private TextField txtDuree;
	@FXML
	private TextField txtTauxInteret;
	@FXML
	private TextField txtTauxAssurance;
	@FXML
	private Button btnSimuler;
	@FXML
	private Button btnAnnuler;


	@Override
	public void initialize(URL location, ResourceBundle resources) {}


	/**
	 * Permet de fermer l'interface
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}


	@FXML
	private void doSimulation() {
		//Permet de definir un format d'affichage pour mettre en forme les resultats
		DecimalFormat df = new DecimalFormat("0.00");
		
		if(this.isSaisieValide()) {
			double capital = Double.parseDouble(this.txtCapital.getText().toString());
            double duree = Double.parseDouble(this.txtDuree.getText().toString());
            double tauxinteret = Double.parseDouble(this.txtTauxInteret.getText().toString())/100;
            double mensualite = capital*((tauxinteret/100/12)/(1-Math.pow(1+tauxinteret/100/12, -duree*12)));
            double tauxassurance = Double.parseDouble(this.txtTauxAssurance.getText().toString())/100;
            double mensualiteAssurance = mensualite + (tauxassurance*capital/100/12);
            
			Alert alertinfo = new Alert(AlertType.INFORMATION);
			alertinfo.setHeaderText("La mensualité serait de : " + df.format(mensualite) + "€.\nLa mensualité avec assurance serait de : " + df.format(mensualiteAssurance) + "€.");
			alertinfo.setTitle("Résultat de simulation");
			alertinfo.show();
		}
	}


	/**
	 * Verifie si toutes les valeurs entrées sont valides.
	 * @return true si toutes les valeurs sont correctes, sinon false
	 */
	private boolean isSaisieValide() {
		int tmp;

		//Capital
		try {
			tmp = Integer.parseInt(this.txtCapital.getText());
			if (tmp > 0) {
				this.txtCapital.setStyle("-fx-text-fill:black; -fx-font-weight: normal;");
			} else {
				this.txtCapital.setStyle("-fx-text-fill:red; -fx-font-weight: bold;");
				AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le capital doit être supérieur à 0 !", AlertType.WARNING);
				this.txtCapital.requestFocus();
				return false;
			}
			
		} catch (NumberFormatException e) {
			this.txtCapital.setStyle("-fx-text-fill:red; -fx-font-weight: bold;");
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le capital n'est pas valide !", AlertType.WARNING);
			this.txtCapital.requestFocus();
			return false;
		}

		//Durée
		try {
			tmp = Integer.parseInt(this.txtDuree.getText());
			if (tmp > 0) {
				this.txtDuree.setStyle("-fx-text-fill:black; -fx-font-weight: normal;");
			} else {
				this.txtDuree.setStyle("-fx-text-fill:red; -fx-font-weight: bold;"); 
				AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "La durée doit être supérieure à 0 !", AlertType.WARNING);
				this.txtDuree.requestFocus();
				return false;
			}
		} catch (NumberFormatException e) {
			this.txtDuree.setStyle("-fx-text-fill:red; -fx-font-weight: bold;"); 
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "La durée n'est pas valide !", AlertType.WARNING);
			this.txtDuree.requestFocus();
			return false;
		}

		//Taux d'intérêt
		try {
			tmp = Integer.parseInt(this.txtTauxInteret.getText());
			if (tmp > 0 && tmp <= 100) {
				this.txtTauxInteret.setStyle("-fx-text-fill:black; -fx-font-weight: normal;");
			} else {
				this.txtTauxInteret.setStyle("-fx-text-fill:red; -fx-font-weight: bold;"); 
				AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le taux d'intérêt doit être compris entre 0 et 100 !", AlertType.WARNING);
				this.txtTauxInteret.requestFocus();
				return false;
			}
			
		} catch (NumberFormatException e) {
			this.txtTauxInteret.setStyle("-fx-text-fill:red; -fx-font-weight: bold;");
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le taux d'intérêt n'est pas valide !", AlertType.WARNING);
			this.txtTauxInteret.requestFocus();
			return false;
		}

		//Taux d'assurance
		try {
			tmp = Integer.parseInt(this.txtTauxAssurance.getText());
			if (tmp > 0 && tmp <= 100) {
				this.txtTauxAssurance.setStyle("-fx-text-fill:black; -fx-font-weight: normal;");
			} else {
				this.txtTauxAssurance.setStyle("-fx-text-fill:red; -fx-font-weight: bold;");
				AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le taux d'assurance doit être compris entre 0 et 100 !", AlertType.WARNING);
				this.txtTauxAssurance.requestFocus();
				return false;
			}
		} catch (NumberFormatException e) {
			this.txtTauxAssurance.setStyle("-fx-text-fill:red; -fx-font-weight: bold;"); 
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le taux d'assurance n'est pas valide !", AlertType.WARNING);
			this.txtTauxAssurance.requestFocus();
			return false;
		}

		//Pas d'erreur
		return true;
	}

}
