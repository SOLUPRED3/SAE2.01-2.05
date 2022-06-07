package application.view;


import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import application.DailyBankState;
import application.control.ExceptionDialog;
import application.tools.AlertUtilities;
import application.tools.CategorieOperation;
import application.tools.ConstantesIHM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.AccessCompteCourant;
import model.orm.exception.ApplicationException;


public class OperationEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private CategorieOperation categorieOperation;
	private CompteCourant compteEdite;
	private Operation operationResultat;

	
	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, DailyBankState _dbstate) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	
	public Operation displayDialog(CompteCourant cpte, CategorieOperation mode) {
		this.categorieOperation = mode;
		this.compteEdite = cpte;

		String info;
		ObservableList<String> list;

		switch (mode) {
		case DEBIT:

			info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);
			this.btnOk.setText("Effectuer débit");
			this.btnCancel.setText("Annuler débit");


			// Chef d'agence ?

			if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
				this.rBtnYes.setDisable(false);
				this.lblDecouvertAutorise.setDisable(false);
				this.rBtnNo.setDisable(false);

			} else {
				this.rBtnYes.setDisable(true);
				this.lblDecouvertAutorise.setDisable(true);
				this.rBtnNo.setDisable(true);
			}
			((VBox) this.gpCenterPane.getParent()).getChildren().remove(gpCenterPane);			

			list = FXCollections.observableArrayList();

			for (String tyOp : ConstantesIHM.OPERATIONS_DEBIT_GUICHET) {
				list.add(tyOp);
			}

			this.cbTypeOpe.setItems(list);
			this.cbTypeOpe.getSelectionModel().select(0);
			break;
			
		case CREDIT:
			info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde);
			this.lblMessage.setText(info);			
			((VBox) this.gpCenterPane.getParent()).getChildren().remove(gpCenterPane);
			((VBox) this.rButtonGrid.getParent()).getChildren().remove(rButtonGrid);
			this.btnOk.setText("Effectuer crédit");
			this.btnCancel.setText("Annuler crédit");
			this.rBtnYes.setVisible(false);
			this.rBtnNo.setVisible(false);
			this.lblDecouvertAutorise.setVisible(false);

			list = FXCollections.observableArrayList();

			for (String tyOp : ConstantesIHM.OPERATIONS_CREDIT_GUICHET) {
				list.add(tyOp);
			}

			this.cbTypeOpe.setItems(list);
			this.cbTypeOpe.getSelectionModel().select(0);
			break;
			
		case VIREMENT:
			info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			((VBox) this.rButtonGrid.getParent()).getChildren().remove(rButtonGrid);
			this.lblMessage.setText(info);			
			this.lblNoCompte.setVisible(true);
			this.cbNoCompte.setVisible(true);
			this.btnOk.setText("Effectuer virement");
			this.btnCancel.setText("Annuler virement");
			this.rBtnYes.setVisible(false);
			this.rBtnNo.setVisible(false);
			this.lblDecouvertAutorise.setVisible(false);

			list = FXCollections.observableArrayList();

			for (String tyOp : ConstantesIHM.OPERATIONS_VIREMENT_GUICHET) {
				list.add(tyOp);
			}

			this.cbTypeOpe.setItems(list);
			this.cbTypeOpe.getSelectionModel().select(0);
			
			ObservableList<String> listeComptes = FXCollections.observableArrayList();
			ArrayList<CompteCourant> alComptes = this.getComptesCourants();			
						
			for (CompteCourant compte: alComptes) {
				if (compte.estCloture.equals("N") && compte.idNumCompte != this.compteEdite.idNumCompte) {
					listeComptes.add(""+compte.idNumCompte);
				}
			}
			
			if (listeComptes.size() == 0) {
				AlertUtilities.showAlert(this.primaryStage, "Erreur", "Ce client n'a pas suffisamment de comptes pour pouvoir effectuer un virement.",
						null, AlertType.ERROR);
				return null;
			}
			
			this.cbNoCompte.setItems(listeComptes);
			this.cbNoCompte.getSelectionModel().select(0);
			break;	
		}


		this.operationResultat = null;
		this.cbTypeOpe.requestFocus();
		this.primaryStage.showAndWait();
		return this.operationResultat;
	}
	

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}
	

	// Attributs de la scene + actions
	@FXML
	private GridPane gpCenterPane;
	@FXML
	private Label lblMessage;
	@FXML
	private Label lblMontant;
	@FXML
	private Label lblNoCompte;
	@FXML
	private Label lblDecouvertAutorise;
	@FXML
	private ComboBox<String> cbTypeOpe;
	@FXML
	private TextField txtMontant;
	@FXML
	private ComboBox<String> cbNoCompte;
	@FXML
	private Button btnOk;
	@FXML
	private Button btnCancel;
	@FXML
	private RadioButton rBtnYes;
	@FXML
	private RadioButton rBtnNo;
	@FXML
	private GridPane rButtonGrid;




	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	
	/**
	 * Annule la création/modification d'un employé et ferme la fenêtre.
	 */
	@FXML
	private void doCancel() {
		this.operationResultat = null;
		this.primaryStage.close();
	}

	
	/**
	 * Ajoute une nouvelle opération
	 */
	@FXML
	private void doAjouter() {
		switch (this.categorieOperation) {
		case VIREMENT:
		case DEBIT:
			// règles de validation d'un débit :
			// - le montant doit être un nombre valide
			// - et si l'utilisateur n'est pas chef d'agence,
			// - le débit ne doit pas amener le compte en dessous de son découvert autorisé
			this.txtMontant.getStyleClass().remove("borderred");
			this.lblMontant.getStyleClass().remove("borderred");
			this.lblMessage.getStyleClass().remove("borderred");
			double montant;

			String info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);

			try {
				montant = Double.parseDouble(this.txtMontant.getText().trim());
				if (montant <= 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}

			if(this.dbs.isChefDAgence() && this.rBtnNo.isSelected() || !this.dbs.isChefDAgence()){
				if(this.compteEdite.solde - montant < this.compteEdite.debitAutorise){
					info = "Dépassement du découvert ! - Cpt. : " + this.compteEdite.idNumCompte + "  "
							+ String.format(Locale.ENGLISH, "%.02f", this.compteEdite.solde) + "  /  "
							+ String.format(Locale.ENGLISH, "%d", this.compteEdite.debitAutorise);
					this.lblMessage.setText(info);
					this.txtMontant.getStyleClass().add("borderred");
					this.lblMontant.getStyleClass().add("borderred");
					this.lblMessage.getStyleClass().add("borderred");
					this.txtMontant.requestFocus();
					return;
				}
			}

			if(this.dbs.isChefDAgence() && this.rBtnYes.isSelected()){
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Gestion des débit");
				alert.setHeaderText("Êtes vous certain que c'est un débit exceptionnel ?");
				alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

				Optional<ButtonType> response = alert.showAndWait();
				if (response.orElse(null) == ButtonType.NO) {
					return;
				} else if(response.orElse(null) == ButtonType.YES) {
					System.out.println("On reste encore un peu...");
					this.txtMontant.requestFocus();
				}
			}

			
			String typeOp = this.cbTypeOpe.getValue();
			this.operationResultat = new Operation(-1, montant, null, null, this.compteEdite.idNumCli, typeOp);
			this.primaryStage.close();
			break;
			
		case CREDIT:
			// règles de validation d'un cédit :
			// - le montant doit être un nombre valide
			double montantC;
			this.txtMontant.getStyleClass().remove("borderred");
			this.lblMontant.getStyleClass().remove("borderred");
			this.lblMessage.getStyleClass().remove("borderred");
			
			String infoC = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%.02f", this.compteEdite.solde);
			this.lblMessage.setText(infoC);

			try {
				montantC = Double.parseDouble(this.txtMontant.getText().trim());
				if (montantC <= 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			
			String typeOpC = this.cbTypeOpe.getValue();
			this.operationResultat = new Operation(-1, montantC, null, null, this.compteEdite.idNumCli, typeOpC);
			this.primaryStage.close();
			break;	
		}
	}
	
	
	/**
	 * @return un ArrayList des comptes du client édité, vide si pas de comptes.
	 */
	private ArrayList<CompteCourant> getComptesCourants() {
		AccessCompteCourant ac = new AccessCompteCourant();
		ArrayList<CompteCourant> alComptes = new ArrayList<CompteCourant>();
		try {
			alComptes = ac.getCompteCourants(this.compteEdite.idNumCli);
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
		}
		return alComptes;
	}
	
	
	/**
	 * @return le numéro du compte de destination pour un virement, -1 en cas d'erreur
	 */
	public int getDestinationID() {
		if (this.categorieOperation == CategorieOperation.VIREMENT) {
			try {
				return Integer.valueOf(this.cbNoCompte.getSelectionModel().getSelectedItem());
			} catch (NumberFormatException e) {
				//System.out.println("Fatal error: prerare for self-destruct");
			}
		}
		return -1;
	}
	
}
