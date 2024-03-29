package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.PrelevementManagementController;
import javafx.fxml.FXMLLoader;
import model.data.Client;
import model.orm.AccessCompteCourant;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.CompteCourant;
import model.data.Prelevement;
import model.orm.AccessPrelevementAutomatique;
import model.orm.exception.Order;
import model.orm.exception.Table;

import java.util.ArrayList;


public class PrelevementManagement {
    private Stage primaryStage;
    private PrelevementManagementController pmc;
    private DailyBankState dbs;
    private CompteCourant compteDuClient;

    /**
     * Constructueur de PrelevementManagement
     * @param _parentStage
     * @param _dbstate
     * @param compte
     */
    public PrelevementManagement(Stage _parentStage, DailyBankState _dbstate, CompteCourant compte) {

        this.compteDuClient = compte;
        this.dbs = _dbstate;
        try {
            FXMLLoader loader = new FXMLLoader(PrelevementManagementController.class.getResource("prelevementmanagement.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, root.getPrefWidth()+50, root.getPrefHeight()+10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.primaryStage = new Stage();
            this.primaryStage.initModality(Modality.WINDOW_MODAL);
            this.primaryStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
            this.primaryStage.setScene(scene);
            this.primaryStage.setTitle("Gestion des prélèvements");
            this.primaryStage.setResizable(false);

            this.pmc = loader.getController();
            this.pmc.initContext(this.primaryStage, this, _dbstate, compteDuClient);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fonction qui permet de créer un prélèvement et de l'enregistrer en base de données.
     * @return
     */
    public Prelevement creerPrelevement(){
        Prelevement prelevement;
        AccessPrelevementAutomatique accessPrelevement = new AccessPrelevementAutomatique();
        PrelevementEditorPane plc = new PrelevementEditorPane(this.primaryStage, this.dbs);
        prelevement = plc.doCompteEditorDialog(this.compteDuClient, null, EditionMode.CREATION);

        if(prelevement != null){
            try {

                accessPrelevement.enregistrerPrelevement(prelevement);

                // if JAMAIS vrai
                // existe pour compiler les catchs dessous
                if (Math.random() < -1) {
                    throw new ApplicationException(Table.CompteCourant, Order.INSERT, "todo : test exceptions", null);
                }

            } catch (DatabaseConnexionException e) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
                ed.doExceptionDialog();
                this.primaryStage.close();
            } catch (ApplicationException ae) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
                ed.doExceptionDialog();
            }
        }
        return prelevement;
    }


    /**
     * Fonction qui permet de retourner un prélèvement avec ses informations modifiées.
     * @param compte : compte à qui appartient le prélèvement
     * @param prelevement : prélèvement concerné
     * @return le prélèvement modifié, null en cas de problème
     */
    public Prelevement modifierPrelevement(CompteCourant compte, Prelevement prelevement) {
        PrelevementEditorPane pep = new PrelevementEditorPane(this.primaryStage, this.dbs);
        Prelevement result = pep.doCompteEditorDialog(compte, prelevement, EditionMode.MODIFICATION);
        if (result != null) {
            try {
                AccessPrelevementAutomatique ac = new AccessPrelevementAutomatique();
                ac.updatePrelevement(result);
            } catch (DatabaseConnexionException e) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
                ed.doExceptionDialog();
                result = null;
                this.primaryStage.close();
            } catch (ApplicationException ae) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
                ed.doExceptionDialog();
                result = null;
            }
        }
        return result;
    }


    /**
     * @return ArrayList contenant les prélèvments d'un compte, vide si pas de comptes.
     */
    public ArrayList<Prelevement> getPrelevementComptes() {
        ArrayList<Prelevement> listePl = new ArrayList<>();
        try {
            AccessPrelevementAutomatique apl = new AccessPrelevementAutomatique();
            listePl = apl.getPrelevement(this.compteDuClient.idNumCompte);
        } catch (DatabaseConnexionException e) {
            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
            ed.doExceptionDialog();
            this.primaryStage.close();
            listePl = new ArrayList<>();
        } catch (ApplicationException ae) {
            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
            ed.doExceptionDialog();
            listePl = new ArrayList<>();
        }
        return listePl;
    }

    /**
     * Procédure qui permet de supprimer un prélèvement et d'enregistrer les modifications dans la base de données.
     */
    public void deletePrelevement() {
        AccessPrelevementAutomatique ap = new AccessPrelevementAutomatique();
        try {
            int idPrelev = pmc.getNumIdPrelev();
            ap.deletePrelevement(idPrelev);
        } catch (DatabaseConnexionException e) {
            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
            ed.doExceptionDialog();
            this.primaryStage.close();
        } catch (ApplicationException ae) {
            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
            ed.doExceptionDialog();
        }
    }

    /**
     * Procédure qui permet d'afficher la page du compte.
     */
    public void doPrelevementManagementDialog() {
        this.pmc.displayDialog();
    }

}
