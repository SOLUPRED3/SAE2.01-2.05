package model.orm;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import application.tools.ConstantesIHM;
import javafx.scene.control.Alert;
import model.data.Employe;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;


public class AccessEmploye {

	public AccessEmploye() {}

	
	/**
	 * Recherche un employé par son login et mot-de-passe.
	 * @param login : login de connexion
	 * @param password : mot-de-passe de connexion
	 * @return un Employe, ou null si non-trouvé
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public Employe getEmploye(String login, String password) throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		Employe employeTrouve;
		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM EMPLOYE WHERE" + " login = ?" + " AND motPasse = ?";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setString(1, login);
			pst.setString(2, password);
			ResultSet rs = pst.executeQuery();

			//System.err.println(query);

			if (rs.next()) {
				int idEmployeTrouve = rs.getInt("idEmploye");
				String nom = rs.getString("nom");
				String prenom = rs.getString("prenom");
				String droitsAccess = rs.getString("droitsAccess");
				String loginTROUVE = rs.getString("login");
				String motPasseTROUVE = rs.getString("motPasse");
				int idAgEmploye = rs.getInt("idAg");
				String estInactif = rs.getString("estInactif");
				
				employeTrouve = new Employe(idEmployeTrouve, nom, prenom, droitsAccess, loginTROUVE, motPasseTROUVE,
						idAgEmploye, estInactif);
				
				if (ConstantesIHM.estInactif(employeTrouve)) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Confirmation");
					alert.setContentText("Ce compte a été désactivé.");
					alert.showAndWait();
					return null;
				}
			} else {
				rs.close();
				pst.close();
				// Non trouvé
				return null;
			}

			if (rs.next()) {
				// Trouvé plus de 1 ... bizarre ...
				rs.close();
				pst.close();
				throw new RowNotFoundOrTooManyRowsException(Table.Employe, Order.SELECT,
						"Recherche anormale (en trouve au moins 2)", null, 2);
			}
			rs.close();
			pst.close();
			return employeTrouve;
		} catch (SQLException e) {
			throw new DataAccessException(Table.Employe, Order.SELECT, "Erreur accès", e);
		}
	}
	

	/**
	 * Insert un employé.
	 * @param client IN/OUT : employé à insérer (tous les attributs IN sauf idEmploye en OUT)
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public void insertEmploye(Employe employe)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "INSERT INTO EMPLOYE VALUES (" + "seq_id_employe.NEXTVAL" + ", " + "?" + ", " + "?" + ", "
					+ "?" + ", " + "?" + ", " + "?" + ", " + "?" + ", " + "?)";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setString(1, employe.nom);
			pst.setString(2, employe.prenom);
			pst.setString(3, employe.droitsAccess);
			pst.setString(4, employe.login);
			pst.setString(5, employe.motPasse);
			pst.setInt(6, employe.idAg);
			pst.setString(7, employe.estInactif);

			//System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();

			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.Employe, Order.INSERT,
						"Insert anormal (insert de moins ou plus d'une ligne)", null, result);
			}

			query = "SELECT seq_id_employe.CURRVAL from DUAL";

			//System.err.println(query);
			
			PreparedStatement pst2 = con.prepareStatement(query);

			ResultSet rs = pst2.executeQuery();
			rs.next();
			int numEmpBase = rs.getInt(1);
			con.commit();
			rs.close();
			pst2.close();
			employe.idEmploye = numEmpBase;
		} catch (SQLException e) {
			throw new DataAccessException(Table.Employe, Order.INSERT, "Erreur accès", e);
		}
	}
	

	/**
	 * Met à jour un employé.
	 * @param employe IN : employé à modifier (employe.idEmploye (clé primaire) doit exister)
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public void updateEmploye(Employe employe)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "UPDATE EMPLOYE SET " + "nom = " + "? , " + "prenom = " + "? , " + "droitsAccess = "
					+ "? , " + "login = " + "? , " + "motPasse = " + "? , " + "estInactif = " + "?" + " " + "WHERE idEmploye = ? ";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setString(1, employe.nom);
			pst.setString(2, employe.prenom);
			pst.setString(3, employe.droitsAccess);
			pst.setString(4, employe.login);
			pst.setString(5, employe.motPasse);
			pst.setString(6, employe.estInactif);
			pst.setInt(7, employe.idEmploye);			

			//System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();
			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.Employe, Order.UPDATE,
						"Update anormal (update de moins ou plus d'une ligne)", null, result);
			}
			con.commit();
		} catch (SQLException e) {
			throw new DataAccessException(Table.Employe, Order.UPDATE, "Erreur accès", e);
		}
	}
	
	
	/**
	 * Recherche des employés paramétrés (tous/un seul par id/par nom-prénom). /!\LA RECHERCHE PAR PRENOM NE MARCHE PAS/!\
	 * @param idAg : id de l'agence dont on cherche les employés
	 * @param idNumCli : vaut -1 si il n'est pas spécifié sinon numéro recherché
	 * @param debutNom : vaut "" si il n'est pas spécifié sinon sera le nom/prenom recherchés
	 * @param debutPrenom cf. @param debutNom
	 * @return ArrayList du ou des employés recherchés, vide si non-trouvé(s)
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public ArrayList<Employe> getEmployes(int idAg, int idEmploye, String debutNom, String debutPrenom)
			throws DataAccessException, DatabaseConnexionException {
		ArrayList<Employe> alResult = new ArrayList<>();

		try {
			Connection con = LogToDatabase.getConnexion();
			PreparedStatement pst;
			String query;
			
			if (idEmploye != -1) {
				query = "SELECT * FROM EMPLOYE where idAg = ?";
				query += " AND idEmploye = ?";
				query += " ORDER BY nom";
				pst = con.prepareStatement(query);
				pst.setInt(1, idAg);
				pst.setInt(2, idEmploye);

			} else if (!debutNom.equals("")) {
				debutNom = debutNom.toUpperCase() + "%";
				debutPrenom = debutPrenom.toUpperCase() + "%";
				query = "SELECT * FROM EMPLOYE where idAg = ?";
				query += " AND UPPER(nom) like ?" + " AND UPPER(prenom) like ?";
				query += " ORDER BY nom";
				pst = con.prepareStatement(query);
				pst.setInt(1, idAg);
				pst.setString(2, debutNom);
				pst.setString(3, debutPrenom);
				
			} else {
				query = "SELECT * FROM Employe where idAg = ?";
				query += " ORDER BY nom";
				pst = con.prepareStatement(query);
				pst.setInt(1, idAg);
			}
			
			//System.err.println(query + " nom : " + debutNom + " prenom : " + debutPrenom + "#");

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int idNumEmp = rs.getInt("idEmploye");
				String nom = rs.getString("nom");
				String prenom = rs.getString("prenom");
				String droitsAccess = rs.getString("droitsAccess");
				droitsAccess = (droitsAccess == null ? "" : droitsAccess);
				String login = rs.getString("login");
				login = (login == null ? "" : login);
				String motPasse = rs.getString("motPasse");
				motPasse = (motPasse == null ? "" : motPasse);
				int idAgEmp = rs.getInt("idAg");
				String estInactif = rs.getString("estInactif");
				estInactif = (estInactif == null ? "" : estInactif);

				alResult.add(
						new Employe(idNumEmp, nom, prenom, droitsAccess, login, motPasse, idAgEmp, estInactif));
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			throw new DataAccessException(Table.Employe, Order.SELECT, "Erreur accès", e);
		}
		return alResult;
	}
	
}
