package model.data;

import application.tools.ConstantesIHM;

public class Employe {

	public int idEmploye;
	public String nom, prenom, droitsAccess, estInactif;
	public String login, motPasse;

	public int idAg;

	public Employe(int idEmploye, String nom, String prenom, String droitsAccess, String login, String motPasse, int idAg, String estInactif) {
		super();
		this.idEmploye = idEmploye;
		this.nom = nom;
		this.prenom = prenom;
		this.droitsAccess = droitsAccess;
		this.login = login;
		this.motPasse = motPasse;
		this.idAg = idAg;
		this.estInactif = estInactif;
	}

	public Employe(Employe e) {
		this(e.idEmploye, e.nom, e.prenom, e.droitsAccess, e.login, e.motPasse, e.idAg, e.estInactif);
	}

	public Employe() {
		this(-1000, null, null, null, null, null, -1000, ConstantesIHM.EMPLOYE_ACTIF);
	}

	@Override
	public String toString() {
		/*return "Employe [idEmploye=" + this.idEmploye + ", nom=" + this.nom + ", prenom=" + this.prenom
				+ ", droitsAccess=" + this.droitsAccess + ", login=" + this.login + ", motPasse=" + this.motPasse
				+ ", idAg=" + this.idAg + "]";*/
		return "[" + this.idEmploye + "]  " + this.nom.toUpperCase() + " " + this.prenom + "(" + this.login + ")";
	}

}
