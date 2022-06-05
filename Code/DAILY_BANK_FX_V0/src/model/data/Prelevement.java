package model.data;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;

public class Prelevement {
    public int idPrelevement ;
    public int dateReccurence ;
    public int montant ;
    public String beneficiaire ;
    public int idNumCompte ;


    public Prelevement(int idP, int date, int montant, int idNumCompte, String beneficiaire){
        super();
        this.idPrelevement = idP;
        this.dateReccurence = date;
        this.montant = montant ;
        this.idNumCompte = idNumCompte ;
        this.beneficiaire = beneficiaire ;
    }

    public Prelevement(Prelevement pl) {
        this(pl.idPrelevement, pl.dateReccurence, pl.montant, pl.idNumCompte, pl.beneficiaire);
    }

    public Prelevement() {
        this(1, 0,0, 0, "Falsimagne");
    }

    @Override
    public String toString() {
        String s = "" + String.format("%02d", this.idPrelevement) + " : Date= " + this.dateReccurence +
                 "  ;  Montant = " + String.format("%d", this.montant) + " Bénéficiaire : " + this.beneficiaire;
        return s;
    }
}