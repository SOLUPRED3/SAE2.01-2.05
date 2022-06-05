package model.data;
import javafx.scene.control.DatePicker;
public class Prelevement {
    private int dateReccurence ;
    public double montant ;
    public String estCloture;
    private String beneficiaire ;
    public int idNumCompte ;


    public Prelevement(int date, Double montant, String estCloture, int idNumCompte, String beneficiaire){
        super();
        this.dateReccurence = date ;
        this.montant = montant ;
        this.estCloture = estCloture ;
        this.idNumCompte = idNumCompte ;
        this.beneficiaire = beneficiaire ;
    }

    public Prelevement(Prelevement pl) {
        this(pl.dateReccurence, pl.montant, pl.estCloture, pl.idNumCompte, pl.beneficiaire);
    }

    public Prelevement() {
        this(1, 0.0, "N", 0, "Falsimagne");
    }

}