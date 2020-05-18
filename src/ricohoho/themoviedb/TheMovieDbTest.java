package ricohoho.themoviedb;

import java.util.Date;
import java.util.List;

public class TheMovieDbTest {

	public static void main(String[] args) {
		//traiterDossierSupprimeFilmDBFichierAbsent();
		traiterDossierFilm();
		
	}

	static void traiterDossierSupprimeFilmDBFichierAbsent() {
		TheMovieDb theMovieDb = new TheMovieDb();
		theMovieDb.traiterDossierSupprimeFilmDBFichierAbsent("POR80090940", "C:\\tempo\\test\\", true);
	}
	
	static void traiterDossierFilm() {
		boolean addDb=false;
		boolean downloadImagePoster = false;
		String serveurName ="POR80090940"; //NOS-RICO et DAVIC.MKDH.FR
		//String serveurName ="NOS-RICO"; 
		

		//String pathFilm = "F:\\Film\\2017\\201710\\";
		String pathFilm = "C:\\tempo\\test\\";	
		//String pathFilm = "\\\\nos-rico\\video\\Films\\2017\\01_02_03\\";
		//String pathFilm = "\\\\NOS-RICO\\video\\Films\\2019\\201904\\";			
		
		TheMovieDb theMovieDb = new TheMovieDb();
		theMovieDb.traiteDossierFilm(serveurName,pathFilm, addDb, downloadImagePoster);
	}
	
	
}
