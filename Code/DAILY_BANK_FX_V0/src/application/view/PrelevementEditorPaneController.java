package application.view;


import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Prelevement;
import model.orm.exception.ManagementRuleViolation;

import javax.xml.soap.Text;


public class PrelevementEditorPaneController implements Initializable {

    // Etat application
    private DailyBankState dbs;

    // Fenêtre physique
    private Stage primaryStage;

    // Données de la fenêtre
    private EditionMode em;
    private Prelevement prelevementEdit;
    private Prelevement prelevementResult;
    private CompteCourant compteDuClient;


    // Manipulation de la fenêtre
    public void initContext(Stage _primaryStage, DailyBankState _dbstate) {
        this.primaryStage = _primaryStage;
        this.dbs = _dbstate;
        this.configure();
    }


    private void configure() {
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
        this.montantTXT.focusedProperty().addListener((t, o, n) -> this.focusMontant(t, o, n));
    }


    public Prelevement displayDialog(CompteCourant compte, Prelevement pl, EditionMode mode) {
        this.compteDuClient = compte;
        this.em = mode;
        ObservableList<String> list;
        if (pl == null) {
            this.prelevementEdit = new Prelevement(1, 0, 0, compteDuClient.idNumCompte, "Falsimagne");

        } else {
            this.prelevementEdit = new Prelevement(pl);
        }
        this.prelevementResult = null;
        switch (mode) {
            case CREATION:
                this.beneficiaireTXT.setDisable(false);
                this.montantTXT.setDisable(false);
                this.lblMessage.setText("Informations sur le prélèvement");
                this.btnOk.setText("Effectuer prélèvement");
                this.btnCancel.setText("Annuler prélèvement");

                list = FXCollections.observableArrayList();
                    list.add(ConstantesIHM.TYPE_OP_8);

                this.cbTypeOpe.setItems(list);
                this.cbTypeOpe.getSelectionModel().select(0);

                break;

            case MODIFICATION:
                this.montantTXT.setDisable(false);
                this.dateTXT.setDisable(false);
                this.beneficiaireTXT.setDisable(true);
                this.lblMessage.setText("Modification du compte");
                this.btnOk.setText("Modifier");
                this.btnCancel.setText("Annuler");

                list = FXCollections.observableArrayList();

                list = FXCollections.observableArrayList();
                list.add(ConstantesIHM.TYPE_OP_8);

                this.cbTypeOpe.setItems(list);
                this.cbTypeOpe.getSelectionModel().select(0);
                break;
        }

        // Paramétrages spécifiques pour les chefs d'agences
        if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
            // rien pour l'instant
        }

        this.prelevementResult = null;
        this.primaryStage.showAndWait();

        return this.prelevementResult;
    }


    // Gestion du stage
    private Object closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
        return null;
    }


    private Object focusMontant(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
                                  boolean newPropertyValue) {
        if (oldPropertyValue) {
            int val = this.prelevementEdit.montant;
            try {
                val = Integer.parseInt(this.montantTXT.getText().trim());
                if (val < 0) {
                    throw new NumberFormatException();
                }
                this.prelevementEdit.montant = val;
            } catch (NumberFormatException nfe) {
                this.montantTXT.setText("" + val);
            }
        }
        return null;
    }



    // Attributs de la scene + actions
    @FXML
    private Label datelbl ;
    @FXML
    private Label montantlbl;
    @FXML
    private Label beneficiairelbl;
    @FXML
    private Label lblMessage;
    @FXML
    private Label typeOperation;
    @FXML
    private DatePicker dateTXT ;
    @FXML
    private TextField montantTXT;
    @FXML
    private TextField beneficiaireTXT;
    @FXML
    private Button btnOk;
    @FXML
    private Button btnCancel;
    @FXML
    private ComboBox<String> cbTypeOpe;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }


    @FXML
    private void doCancel() {
        this.prelevementResult = null;
        this.primaryStage.close();
    }


    @FXML
    private void doAjouter() {
        switch (this.em) {
            case CREATION:
                if (this.isSaisieValide()) {
                    this.prelevementResult = this.prelevementEdit;
                    this.primaryStage.close();
                }
                break;
            case MODIFICATION:
                if (this.isSaisieValide()) {
                    this.prelevementResult = this.prelevementEdit;
                    this.primaryStage.close();
                }
                break;
        }

    }



    private boolean isSaisieValide() {
        this.prelevementEdit.montant = Integer.valueOf(this.montantTXT.getText());

		/*if (this.prelevementEdit.debitAutorise < 0) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Violation des règles");
			alert.setHeaderText("Changement du découvert impossible, le montant doit être positif ou nul !");
			alert.showAndWait();
			return false;
		 }*/

        if (this.prelevementEdit.montant < 0) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Violation des règles");
            alert.setHeaderText("Changement du découvert impossible !");
            alert.showAndWait();
            return false;
        }

        return true;
    }

}
