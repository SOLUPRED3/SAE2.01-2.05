package application.control;

import java.util.ArrayList;
import java.util.Random;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ComptesManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.AgenceBancaire;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.AccessAgenceBancaire;
import model.orm.AccessClient;
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
			this.cmc.initContext(this.primaryStage, this, _dbstate, client);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doComptesManagementDialog() {
		this.cmc.displayDialog();
	}

	public void gererOperations(CompteCourant cpt) {
		OperationsManagement om = new OperationsManagement(this.primaryStage, this.dbs, this.clientDesComptes, cpt);
		om.doOperationsManagementDialog();
	}

	public CompteCourant creerCompte() {
		CompteCourant compte;
		AccessCompteCourant accessCompte = new AccessCompteCourant();
		AccessAgenceBancaire accessAgence = new AccessAgenceBancaire() ;
		AccessClient accessClient = new AccessClient() ; 
		CompteEditorPane cep = new CompteEditorPane(this.primaryStage, this.dbs);
		compte = cep.doCompteEditorDialog(this.clientDesComptes, null, EditionMode.CREATION);
		System.out.println(compte.toString());  
		if (compte != null) {
			try {
				// Temporaire jusqu'à implémentation

				// TODO : enregistrement du nouveau compte en BDD (la BDD donne de nouvel id
				// dans "compte")
				

				/* ------ Peut être utile plus tard --------
				  
				 
				ArrayList<AgenceBancaire> listeAgence = accessAgence.getAgenceBancaires() ; 

				ArrayList<Client> fullClients = new ArrayList<>();
				
				for(int i = 0 ; i < listeAgence.size(); i ++) {
					ArrayList<Client> listeClients = accessClient.getClients(listeAgence.get(i).idAg, 0, null, null) ;
					fullClients.addAll(listeClients) ; 
				}
				
				ArrayList<CompteCourant> finalComptes = new ArrayList<>() ; 
				for(int i = 0 ; i < listeAgence.size() ; i++) {
					for(int j = 0 ; i < fullClients.size() ; i++) {
						ArrayList<CompteCourant> listeComptes = accessCompte.getCompteCourants(fullClients.get(i).idNumCli) ;
						finalComptes.addAll(listeComptes) ; 
					}
				}
				
				for (int i = 0 ; i < finalComptes.size() ; i++) {
					if(finalComptes.get(i).idNumCompte > compte.idNumCompte) {
						compte.idNumCompte+=1 ; 
					}
				}
				*/
				
				accessCompte.enregistrerCompte(compte.idNumCli, compte.debitAutorise, compte.solde, compte.estCloture);
				
				
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
	
	public int gen() {
	    Random r = new Random( System.currentTimeMillis() );
	    return ((1 + r.nextInt(9)) * 10000 + r.nextInt(10000));
	}
	
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
