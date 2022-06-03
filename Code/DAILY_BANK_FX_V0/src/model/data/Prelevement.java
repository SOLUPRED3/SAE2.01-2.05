package model.data;

import javafx.scene.control.DatePicker;

public class Prelevement {
    private DatePicker date ;
    private double montant ;
    public String estCloture;
    private int idNumCompte ;

    public Prelevement(DatePicker date, Double montant, String estCloture, int idNumCompte){
        super();
        this.date = date ;
        this.montant = montant ;
        this.estCloture = estCloture ;
        this.idNumCompte = idNumCompte ;
    }

    public Prelevement(Prelevement pl) {
        this(pl.date, pl.montant, pl.estCloture, pl.idNumCompte);
    }

    public Prelevement() {
        this(new DatePicker(), 0.0, "N", 0);
    }

}
