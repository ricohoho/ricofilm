package ricohoho.themoviedb;

import java.util.Date;

public class Fichier {
	String path="";
	String nom="";
	double taille=0;
	Date dateFile=null;
	
	public Fichier(String path, String nom, double taille,Date dateFile) {
		super();
		this.path = path;
		this.nom = nom;
		this.taille = taille;
		this.dateFile=dateFile;
	}
	public String getPath() {
		return path;
	}
	public Date getDateFile() {
		return dateFile;
	}
	public void setDateFile(Date dateFile) {
		this.dateFile = dateFile;
	}
	public void setTaille(double taille) {
		this.taille = taille;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public double getTaille() {
		return taille;
	}
	public void setTaille(int taille) {
		this.taille = taille;
	}
	
}
