package model.orm;

import model.data.CompteCourant;
import model.data.Prelevement;
import model.orm.exception.*;
import oracle.jdbc.proxy.annotation.Pre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AccessPrelevementAutomatique {
    public AccessPrelevementAutomatique() {

    }

    /**
     * Enregistre un compte avec ses différentes valeurs en paramètres.
     * @param pfPrelevement : compte à enregistrer
     * @throws DataAccessException
     * @throws DatabaseConnexionException
     * @throws RowNotFoundOrTooManyRowsException
     */
    public void enregistrerPrelevement(Prelevement pfPrelevement)
            throws DataAccessException, DatabaseConnexionException, RowNotFoundOrTooManyRowsException {

        try {
            Connection con = LogToDatabase.getConnexion();

            String query = "INSERT INTO PRELEVEMENTAUTOMATIQUE VALUES (" + "seq_id_prelevauto.NEXTVAL" + " ," + "?" + ", " + "?" +
                    ", " + "?" + ", " + "?" + ")";

            PreparedStatement pst = con.prepareStatement(query);
            if(pfPrelevement.montant > 0){
                pst.setInt(1, pfPrelevement.montant) ;
            }
            else pst.setInt(1, pfPrelevement.montant) ;

            pst.setInt(2, pfPrelevement.dateReccurence);
            pst.setString(3, pfPrelevement.beneficiaire); ;
            pst.setInt(4, pfPrelevement.idNumCompte) ;

            int result = pst.executeUpdate();

            System.err.println(result);
            if (result != 1) {
                con.rollback();
                throw new RowNotFoundOrTooManyRowsException(Table.Client, Order.INSERT,
                        "Insert anormal (insert de moins ou plus d'une ligne)", null, result);
            } else {
                con.commit();
            }

            System.err.println(query);
            pst.close();

        } catch (SQLException e) {
            throw new DataAccessException(Table.CompteCourant, Order.SELECT, "Erreur accès", e);
        }
    }

    /**
     * Recherche d'un Prelevement à partir de son id (IdPrelevement).
     * @param idPrelevement id du compte (clé primaire)
     * @return Le compte ou null si non trouvé
     * @throws RowNotFoundOrTooManyRowsException
     * @throws DataAccessException
     * @throws DatabaseConnexionException
     */
    public Prelevement getPrelevementCourant(int idPrelevement)
            throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
        try {
            Prelevement pl;

            Connection con = LogToDatabase.getConnexion();

            String query = "SELECT * FROM PRELEVEMENTAUTOMATIQUE where" + " IDPRELEV = ?";

            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, idPrelevement);

            System.err.println(query);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int idNumCompteTrouve = rs.getInt("IDNUMCOMPTE");
                int id = rs.getInt("IDPRELEV");
                int montant = rs.getInt("MONTANT");
                String beneficiaire = rs.getString("BENEFICIAIRE");
                int dateRecurrente = rs.getInt("DATERECURRENTE");

                pl = new Prelevement(id, dateRecurrente, montant, idNumCompteTrouve, beneficiaire);
            } else {
                rs.close();
                pst.close();
                return null;
            }

            if (rs.next()) {
                throw new RowNotFoundOrTooManyRowsException(Table.CompteCourant, Order.SELECT,
                        "Recherche anormale (en trouve au moins 2)", null, 2);
            }
            rs.close();
            pst.close();
            return pl;
        } catch (SQLException e) {
            throw new DataAccessException(Table.CompteCourant, Order.SELECT, "Erreur accès", e);
        }
    }

    /**
     * Mise à jour d'un CompteCourant.
     *
     * cc.idNumCompte (clé primaire) doit exister seul cc.debitAutorise est mis à
     * jour cc.solde non mis à jour (ne peut se faire que par une opération)
     * cc.idNumCli non mis à jour (un cc ne change pas de client)
     *
     * @param pl IN cc.idNumCompte (clé primaire) doit exister seul
     * @throws RowNotFoundOrTooManyRowsException
     * @throws DataAccessException
     * @throws DatabaseConnexionException
     * @throws ManagementRuleViolation
     */
    public void updatePrelevement(Prelevement pl) throws RowNotFoundOrTooManyRowsException, DataAccessException,
            DatabaseConnexionException, ManagementRuleViolation {
        try {

            Prelevement plAvant = this.getPrelevementCourant(pl.idNumCompte);
            if (pl.montant < 0) {
                pl.montant = -pl.montant;
            }
            Connection con = LogToDatabase.getConnexion();

            String query = "UPDATE PRELEVEMENTAUTOMATIQUE SET " + "DATERECURRENTE = ?" + ", " + "MONTANT = ? " + "WHERE IDPRELEV = ?";

            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, pl.dateReccurence);
            pst.setInt(2, pl.montant);
            pst.setInt(3, pl.idPrelevement);

            System.err.println(query);

            int result = pst.executeUpdate();
            pst.close();
            if (result != 1) {
                con.rollback();
                throw new RowNotFoundOrTooManyRowsException(Table.CompteCourant, Order.UPDATE,
                        "Update anormal (update de moins ou plus d'une ligne)", null, result);
            }
            con.commit();
        } catch (SQLException e) {
            throw new DataAccessException(Table.CompteCourant, Order.UPDATE, "Erreur accès", e);
        }
    }

    /**
     * Recherche des CompteCourant d'un client à partir de son id.
     *
     * @param idNumCompte du client dont on cherche les comptes
     * @return Tous les CompteCourant de idNumCli (ou liste vide)
     * @throws DataAccessException
     * @throws DatabaseConnexionException
     */
    public ArrayList<Prelevement> getPrelevement(int idNumCompte)
            throws DataAccessException, DatabaseConnexionException {
        ArrayList<Prelevement> alResult = new ArrayList<>();

        try {
            Connection con = LogToDatabase.getConnexion();
            String query = "SELECT * FROM PRELEVEMENTAUTOMATIQUE where IDNUMCOMPTE = ?";
            query += " ORDER BY IDNUMCOMPTE";

            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, idNumCompte);
            System.err.println(query);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int idNumCompteTrouve = rs.getInt("IDNUMCOMPTE");
                int id = rs.getInt("IDPRELEV");
                int montant = rs.getInt("MONTANT");
                String beneficiaire = rs.getString("BENEFICIAIRE");
                int dateRecurrente = rs.getInt("DATERECURRENTE");

                alResult.add(new Prelevement(id, dateRecurrente, montant, idNumCompte, beneficiaire));
            }
            rs.close();
            pst.close();
        } catch (SQLException e) {
            throw new DataAccessException(Table.CompteCourant, Order.SELECT, "Erreur accès", e);
        }

        return alResult;
    }

    /**
     * Clôture un compte dans la base de données.
     * @param pfIdPrelev : le numéro du compte à clôturer
     * @throws DataAccessException
     * @throws DatabaseConnexionException
     * @throws RowNotFoundOrTooManyRowsException
     */
    public void deletePrelevement(int pfIdPrelev)
            throws DataAccessException, DatabaseConnexionException, RowNotFoundOrTooManyRowsException {

        try {
            Connection con = LogToDatabase.getConnexion();

            String query = "DELETE FROM PRELEVEMENTAUTOMATIQUE "+ "WHERE IDPRELEV = ?" ;

            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, pfIdPrelev);
            int result = pst.executeUpdate();

            System.err.println(result);
            if (result != 1) {
                con.rollback();
                throw new RowNotFoundOrTooManyRowsException(Table.Client, Order.INSERT,
                        "Suppression anormale (suppression de moins ou plus d'une ligne)", null, result);
            } else {
                con.commit();
            }

            System.err.println(query);
            pst.close();

        } catch (SQLException e) {
            throw new DataAccessException(Table.CompteCourant, Order.SELECT, "Erreur accès", e);
        }

    }


}
