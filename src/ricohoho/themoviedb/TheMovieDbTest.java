package ricohoho.themoviedb;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TheMovieDbTest {

	
	static String dbMongoHost="localhost";
	static int dbMongoPort=27017;
	static String dbName="ricofilm";
	static String dbUSerName="";
	static String dbPAssword="";
	
	//config distante
	//static String dbMongoHost="davic.mkdh.fr";
	//static String dbUSer ="ricoAdmin";
	//static String dbPAssword	="rineka5993"		;
	
	public static void main(String[] args) {
		//traiterDossierSupprimeFilmDBFichierAbsent();
		//traiterDossierFilm();
		FilmFichier nom = extractNomFilm("Un.Mari.De.Trop.FRENCH.DVDRip.XviD-ZANBiC.FUCK.[emule-island.com].avi");
		nom = extractNomFilm("dragon.fighter.french.dvdrip.xvid-thewarrior777.fuck.[emule-island.com].avi");
		//testPAtterDate("Fellini -  RmaFederico Fellini - Roma [1972].avi");
		
		
	}
	
	static FilmFichier extractNomFilm(String nomFicier) {
		TheMovieDb theMovieDb = new TheMovieDb(dbMongoHost,dbMongoPort,dbName,dbUSerName,dbPAssword);
		FilmFichier retourn = theMovieDb.extractNomFilm(nomFicier);
		List<String> listeNomFilmPossible=null;
		listeNomFilmPossible =retourn.listeNomFilmPossible;
		int i_i=0;
		for (String snom : listeNomFilmPossible) {
			System.out.println("["+i_i+"]"+listeNomFilmPossible.get(i_i));
			i_i++;
		}
		//System.out.println(listeNomFilmPossible.get(0));
		return retourn;
	}
	
	static void  testPAtterDate(String sfichier) {
		Pattern p = Pattern.compile("(19)[0-9]+[0-9]|(20)[0-9]+[0-9]+");
		Matcher m = p.matcher(sfichier);
		int anneeFilm=0;
		//On prend la premiere annee du fichier !
		if(m.find()) {
			anneeFilm = Integer.parseInt(m.group());
			System.out.println("anneeFilm:"+anneeFilm);
		}
	}
	
	static void traiterDossierSupprimeFilmDBFichierAbsent() {
		TheMovieDb theMovieDb = new TheMovieDb(dbMongoHost,dbMongoPort,dbName,dbUSerName,dbPAssword);
		boolean avecSsDossier = true;
		theMovieDb.traiterDossierSupprimeFilmDBFichierAbsent("POR80090940", "C:\\tempo\\test\\test\\", true,avecSsDossier);
	}
	
	static void traiterDossierFilm() { 
		boolean addDb=true;
		boolean downloadImagePoster = true;
		String serveurName ="POR80090940"; //NOS-RICO et DAVIC.MKDH.FR
		//String serveurName ="NOS-RICO"; 
		
	
		//String pathFilm = "F:\\Film\\2017\\201710\\";
		String pathFilm = "C:\\tempo\\test\\"; 
		//String pathFilm = "\\\\nos-rico\\video\\Films\\2017\\01_02_03\\";
		//String pathFilm = "\\\\NOS-RICO\\video\\Films\\2019\\201904\\";			
		
		TheMovieDb theMovieDb = new TheMovieDb(dbMongoHost,dbMongoPort,dbName,dbUSerName,dbPAssword);
		String memoRicoFIlm="BD";
		theMovieDb.traiteDossierFilm(serveurName,pathFilm, memoRicoFIlm, downloadImagePoster,1);
	}
	
	
}
