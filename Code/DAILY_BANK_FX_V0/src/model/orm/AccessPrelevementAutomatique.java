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


}
