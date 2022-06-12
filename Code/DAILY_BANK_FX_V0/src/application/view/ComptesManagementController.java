package application.view;


import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import application.DailyBankState;
import application.control.ComptesManagement;
import application.control.PrelevementManagement;
import application.tools.AlertUtilities;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.AgenceBancaire;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.AccessAgenceBancaire;
import model.orm.AccessCompteCourant;
import model.orm.AccessOperation;
import model.orm.AccessTypeOperation;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.swing.*;


public class ComptesManagementController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private ComptesManagement cm;
	private PrelevementManagement pm;
	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDesComptes;
	private ObservableList<CompteCourant> olCompteCourant;

	
	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, ComptesManagement _cm, PrelevementManagement _pm, DailyBankState _dbstate, Client client) {
		this.cm = _cm;
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.clientDesComptes = client;
		this.configure();
	}

	
	private void configure() {
		String info;
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
		this.olCompteCourant = FXCollections.observableArrayList();
		this.lvComptes.setItems(this.olCompteCourant);
		this.lvComptes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvComptes.getFocusModel().focus(-1);
		this.lvComptes.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

		info = this.clientDesComptes.nom + "  " + this.clientDesComptes.prenom + "  (id : "
				+ this.clientDesComptes.idNumCli + ")";
		this.lblInfosClient.setText(info);

		this.loadList();
		this.validateComponentState();
	}
	

	public void displayDialog() {
		this.primaryStage.showAndWait();
	}
	
	
	/**
	 * @return le numéro du compte sélectionné.
	 */
	public int getNumCompte() {
		int value = lvComptes.getSelectionModel().getSelectedItem().idNumCompte ;
		//String value = Integer.toString(numCompte) ;
		return value ; 
	}
	

	// Gestion du stage

	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}
	

	// Attributs de la scene + actions
	@FXML
	private Label lblInfosClient;
	@FXML
	private ListView<CompteCourant> lvComptes;
	@FXML
	private Button btnVoirOpes;
	@FXML
	private Button btnModifierCompte;
	@FXML
	private Button btnSupprCompte;
	@FXML
	private Button btnAjoutCompte;
	@FXML
	private Button btnVoirPrelevement;
	@FXML
	private Button btnGenererPDF;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/**
	 * Ouvre le gestionnaire des virements d'un compte.
	 */
	@FXML
	private void doVoirPrelevement() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant compte = this.olCompteCourant.get(selectedIndice);
			if(compte != null){
				this.cm.gererPrelevement(compte);
			}
		}
		this.loadList();
		this.validateComponentState();
	}
	
	/**
	 * Annule la création/modification d'un employé et ferme la fenêtre.
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}


	@FXML
	private void doGenererPDF() {
		try {
			this.genererPDF();
			AlertUtilities.showAlert(this.primaryStage, "PDF exporté", null, "Le relevé de comptes a bien été généré en PDF.",
					Alert.AlertType.INFORMATION);
		} catch (Exception e) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur", null, "Erreur lors de la génération du PDF !\n\n"+e,
					Alert.AlertType.ERROR);
			File fichierHTML = new File("resource/todelete.html");
			fichierHTML.delete();
		}
	}


	/**
	 * Génère un relevé de compte en PDF
	 * @throws Exception en cas d'erreur de conversion
	 */
	private void genererPDF() throws Exception {
		//Récupération du fichier HTML
		File fichier = new File("resource/rlvTemplate.html");
		Document document = Jsoup.parse(fichier, "UTF-8");

		//Initialisations des accesseurs
		AccessAgenceBancaire aab = new AccessAgenceBancaire();
		AccessOperation ao = new AccessOperation();
		AccessCompteCourant acc = new AccessCompteCourant();

		//Récupération de l'agence du client
		AgenceBancaire agence = aab.getAgenceBancaire(this.clientDesComptes.idAg);

		//Récupération des comptes du client
		ArrayList<CompteCourant> alComptes = acc.getCompteCourants(this.clientDesComptes.idNumCli);

		//MAJ des infos de l'agence sur le document HTML
		document.getElementById("adr_agence").html(agence.nomAg + " " + agence.adressePostaleAg);

		//MAJ des infos du client sur le document HTML
		document.getElementById("nom_client").html(this.clientDesComptes.prenom + " " + this.clientDesComptes.nom.toUpperCase());
		document.getElementById("adr_client").html(this.clientDesComptes.adressePostale);

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
		String dateReleve = dateFormat.format(new Date()); //Date du relevé

		//MAJ de la synthèse des comptes sur le document HTML
		document.getElementsByClass("date").html(dateReleve);

		String innerComptes = "";
		for (CompteCourant compte: alComptes) {

			String etatCompte = "";
			if (compte.estCloture.equals("O")) { etatCompte = " (clôturé)"; }
			String soldeColor = "black";
			if (compte.solde < 0) { soldeColor = "red"; }

			innerComptes +=
				"<tr>" +
					"<td class=\"compte\">Compte de dépot n°<b>" + compte.idNumCompte + "</b>" + etatCompte + "</td>" +
					"<td class=\"solde\" style='color:" + soldeColor + "'>Solde : <b>" + compte.solde + "</b>€</td>" +
				"</tr>";
		}
		document.select("#tablo-soldes > table").html(innerComptes);

		//MAJ des infos des opérations sur le document HTML
		innerComptes = "";
		for (CompteCourant compte: alComptes) {

			String etatCompte = "";
			if (compte.estCloture.equals("O")) { etatCompte = "<span style='font-weight:normal; display:inline; color:#ABABAB'> (clôturé)</span>"; }
			String soldeColor = "black";
			if (compte.solde < 0) { soldeColor = "red"; }

			 innerComptes +=
				"<section class='info-comptes'>" +
					"<span style='font-size:25px; color:#5C76D6'><b>Compte de dépot n°" + compte.idNumCompte + "</b>" + etatCompte + "</span>" +
					"<div class='tablo-comptes'>" +
						"<span style='font-size:25px'><b>Détail des opérations précédentes au " + dateReleve + "</b></span>" +
						"<span style='font-size:20px; margin-top:20px; text-align:left; margin-left:2px; color:" + soldeColor + "'>Solde : <b>" + compte.solde + "</b>€</span>" +
						"<table>";

			ArrayList<Operation> alOperation = ao.getOperations(compte.idNumCompte); //Récupération des opérations du compte
			for (Operation operation: alOperation) {
				String dateOp = dateFormat.format(operation.dateOp); //Date du relevé
				innerComptes +=
					"<tr>" +
						"<td class=\"date\">Le " + dateOp + "</td>\n" +
						"<td class=\"type\">" + operation.idTypeOp + "</td>\n" +
						"<td class=\"montant\">Montant : <b>" + operation.montant + "</b>€</td>\n" +
					"</tr>";
			}

			innerComptes += "</table></div></section>";
		}
		document.select("article").html(document.select("article").html() + innerComptes);

		//Nom du fichier PDF
		dateFormat = new SimpleDateFormat("dd-MM-YYYY");
		dateReleve = dateFormat.format(new Date()); //Date du relevé
		String nomPDF = "Relevé de compte de " +
				this.clientDesComptes.prenom + " " + this.clientDesComptes.nom.toUpperCase() +
				" du " + dateReleve;
		document.title(nomPDF);

		PrintWriter sortie = new PrintWriter("resource/todelete.html");
		String html = org.jsoup.parser.Parser.unescapeEntities(document.outerHtml(), true);
		sortie.println(html);
		sortie.close();

		this.HTMLtoPDF("resource/todelete.html", nomPDF);
	}


	/**
	 * Convertie un fichier HTMl en fichier PDF.
	 * @param source : chemin du fichier HTML source
	 * @param nomFichier : nom du fichier PDF de destination
	 * @throws Exception
	 */
	public void HTMLtoPDF(String source, String nomFichier) throws Exception {
		//Altérnative pour les OS autres que Windows...
		String os = System.getProperty("os.name").toLowerCase(), dlPath = "";
		if (os.contains("osx") || os.contains("nix") || os.contains("aix") || os.contains("nux")) {
			AlertUtilities.showAlert(this.primaryStage, "Attention", null,
					"Cette fonctionnalité n'est pas encore disponible pour le système d'exploitaiton utilisé." +
							"Le fichier sera disponible directement dans vos téléchargements.\n",
					Alert.AlertType.WARNING);
			dlPath = System.getProperty("user.home")+"/Downloads/" + nomFichier + ".pdf";
		} else {
			FileDialog fd = new FileDialog(new JFrame(), "Enregistrer le relevé de comptes", FileDialog.SAVE);
			fd.setFile(nomFichier + ".pdf");
			fd.setVisible(true);
			dlPath = fd.getFiles()[0].getAbsolutePath();

		}

		File fichierHTML = new File(source);
		String contenu = fichierHTML.toURI().toURL().toString();

		OutputStream out = new FileOutputStream(dlPath);
		ITextRenderer renderer = new ITextRenderer();

		renderer.setDocument(contenu);
		renderer.layout();
		renderer.createPDF(out);

		out.close();
		fichierHTML.delete();
	}
	
	
	/**
	 * Appelle la fonction pour voir les opérations.
	 */
	@FXML
	private void doVoirOperations() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.olCompteCourant.get(selectedIndice);
			this.cm.gererOperations(cpt);
		}
		this.loadList();
		this.validateComponentState();
	}
	
	
	/**
	 * Modifie un compte.
	 */
	@FXML
	private void doModifierCompte() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Client clientCompte = clientDesComptes ;
			CompteCourant compte = this.olCompteCourant.get(selectedIndice);
			CompteCourant result = this.cm.modifierCompte(clientCompte, compte) ;
			if (result != null) {
				this.olCompteCourant.set(selectedIndice, result);
			}
		}
	}
	
	
	/**
	 * Vérifie et clôture un compte.
	 */
	@FXML
	private void doCloturerCompte() {
		CompteCourant compte = this.lvComptes.getSelectionModel().getSelectedItem();
        if(compte.solde == 0) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Confirmation");
			alert.setContentText("Êtes-vous certain(e) de vouloir clôturer ce compte ? ");
			
			alert.getButtonTypes().setAll(ButtonType.YES,ButtonType.NO);	
			Optional<ButtonType> response = alert.showAndWait();
			
			if (response.orElse(null) == ButtonType.YES) {
				this.cm.cloturerCompte() ;
				this.loadList();
			} else if(response.orElse(null) == ButtonType.NO) {
				System.out.println("On reste encore un peu...");
			}
        } else {
        	Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setContentText("Ce compte ne peut pas être clôturé car son solde n'est pas nul.");
			alert.showAndWait();
        }

	}
	

	/**
	 * @return true si le compte sélectionné est clôturé, sinon false
	 */
	private boolean estCloture() {
		if(this.lvComptes.getSelectionModel().getSelectedIndex() >= 0 && this.lvComptes.getSelectionModel().getSelectedItem().estCloture.equals("N")) {
			return false; 
		}
		return true;
	}

	
	/**
	 * @return true si le client à qui appartient le compte sélectionné est ianctif, sinon false
	 */
	private boolean clientInactif() {
		return this.clientDesComptes.estInactif.equals("O");
	}
	
	
	/**
	 * Créé un compte.
	 */
	@FXML
	private void doNouveauCompte() {
		CompteCourant compte;
		compte = this.cm.creerCompte();
		if (compte != null) {
			this.olCompteCourant.add(compte);
			this.loadList();
		}		
	}
	
	
	/**
	 * Recharge la ViewList de comptes. 
	 */
	private void loadList () {
		ArrayList<CompteCourant> listeCpt;
		listeCpt = this.cm.getComptesDunClient();
		this.olCompteCourant.clear();
		for (CompteCourant co : listeCpt) {
			this.olCompteCourant.add(co);
		}
	}
	
	
	/**
	 * Gére les boutons en fonction des situations.
	 */
	private void validateComponentState() {		
        int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
        if (!this.estCloture()) {
        	if (selectedIndice >= 0) {
        		this.btnVoirOpes.setDisable(false);
                this.btnSupprCompte.setDisable(false);
                this.btnModifierCompte.setDisable(false);
				this.btnVoirPrelevement.setDisable(false);
        	} else {
        		this.btnVoirOpes.setDisable(true);
                this.btnSupprCompte.setDisable(true);
                this.btnModifierCompte.setDisable(true);
				this.btnVoirPrelevement.setDisable(true);

        	}
        } else {
        	if (selectedIndice >= 0) {
        		this.btnVoirOpes.setDisable(false);
        	} else {
        		this.btnVoirOpes.setDisable(true);
        	}
        	this.btnSupprCompte.setDisable(true);
			this.btnVoirPrelevement.setDisable(true);
            this.btnModifierCompte.setDisable(true);
        }        
        if (this.clientInactif()) {
			this.btnAjoutCompte.setDisable(true);
		} else {
			this.btnAjoutCompte.setDisable(false);
		}
	}
}
