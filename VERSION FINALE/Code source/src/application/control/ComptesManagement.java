package application.control;


import java.util.ArrayList;
import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ComptesManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.AccessCompteCourant;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.Table;


public class ComptesManagement {

	private Stage primaryStage;
	private ComptesManagementController cmc;
	private DailyBankState dbs;
	private Client clientDesComptes;
	private PrelevementManagement pm;

	
	/**
	 * Constructueur de ComptesManagement
	 * @param _parentStage
	 * @param _dbstate
	 * @param client
	 */
	public ComptesManagement(Stage _parentStage, DailyBankState _dbstate, Client client) {		
		this.clientDesComptes = client;
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(ComptesManagementController.class.getResource("comptesmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+50, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des comptes");
			this.primaryStage.setResizable(false);

			this.cmc = loader.getController();
			this.cmc.initContext(this.primaryStage, this, this.pm, _dbstate, client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Procédure qui permet d'afficher la page du compte.
	 */
	public void doComptesManagementDialog() {
		this.cmc.displayDialog();
	}

	
	/**
	 * Procédure qui permet de gérer les opérations d'un client sur un compte.
	 * @param cpt : compte concerné
	 */
	public void gererOperations(CompteCourant cpt) {
		OperationsManagement om = new OperationsManagement(this.primaryStage, this.dbs, this.clientDesComptes, cpt);
		om.doOperationsManagementDialog();
	}
	
	
	/**
	 * Procédure qui permet de gérer le prélèvement d'un compte
	 * @param compte : compte concerné
	 */
	public void gererPrelevement(CompteCourant compte) {
		PrelevementManagement pm = new PrelevementManagement(this.primaryStage, this.dbs, compte);
		pm.doPrelevementManagementDialog();
	}
	
	
	/**
	 * Fonction qui permet de créer un compte et de l'enregistrer en base de données.
	 * @return le compte créé
	 */
	public CompteCourant creerCompte() {
		CompteCourant compte;
		AccessCompteCourant accessCompte = new AccessCompteCourant();
		CompteEditorPane cep = new CompteEditorPane(this.primaryStage, this.dbs);
		compte = cep.doCompteEditorDialog(this.clientDesComptes, null, EditionMode.CREATION);
		//System.out.println(compte.toString());  
		if (compte != null) {
			try {
				
				accessCompte.enregistrerCompte(compte);
				
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
		return compte;
	}


	/**
	 * Procédure qui permet de clôturer un compte et d'enregistrer les modifications dans la base de données.
	 */
	public void cloturerCompte() {
		AccessCompteCourant accessCompte = new AccessCompteCourant();
		try {
			int numCompte = cmc.getNumCompte();
			System.out.println(numCompte);
			accessCompte.cloturerCompte(numCompte);
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
	 * Fonction qui permet de retourner un compte avec ses informations modifiées.
	 * @param client : client à qui appartient le compte
	 * @param compte : compte concerné
	 * @return le compte modifié, null en cas de problème
	 */
	public CompteCourant modifierCompte(Client client, CompteCourant compte) {
		CompteEditorPane cep = new CompteEditorPane(this.primaryStage, this.dbs);
		CompteCourant result = cep.doCompteEditorDialog(client, compte, EditionMode.MODIFICATION);
		if (result != null) {
			try {
				AccessCompteCourant ac = new AccessCompteCourant();
				ac.updateCompteCourant(result);
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
	 * @return une ArrayList contenant les comptes d'un client, vide si pas de clients
	 */
	public ArrayList<CompteCourant> getComptesDunClient() {
		ArrayList<CompteCourant> listeCpt = new ArrayList<>();
		try {
			AccessCompteCourant acc = new AccessCompteCourant();
			listeCpt = acc.getCompteCourants(this.clientDesComptes.idNumCli);
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeCpt = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			listeCpt = new ArrayList<>();
		}
		return listeCpt;
	}
	
}
