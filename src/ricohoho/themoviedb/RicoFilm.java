package ricohoho.themoviedb;

import java.util.Date;
import java.util.List;

public class RicoFilm {

	public static void main(String[] args) {
		System.out.println("Extraction Information fichier films");
		
		
		String pathFilm = null;
		List<String> listeFilm=null;
		boolean addDb=false;
		boolean downloadImagePoster = false;
		
		TheMovieDb theMovieDb = new TheMovieDb();
		if (args.length >0)
			pathFilm = args[0];
			
		else {
			//theMovieDb.pathFilm = "F:\\Film\\2017\\201710\\";
			//theMovieDb.pathFilm = "C:\\tempo\\test\\";
			addDb=true;
			//theMovieDb.pathFilm = "\\\\nos-rico\\video\\Films\\2017\\01_02_03\\";
			pathFilm = "\\\\NOS-RICO\\video\\Films\\2019\\201904\\";
		}
		
		downloadImagePoster=true;
		
		
		LogText logText = new LogText(pathFilm,"log.txt");
		logText.writeToFile("----------------------"+"\t"+new Date().toString()+"\t");
		
		theMovieDb.traiteDossierFilm(pathFilm, addDb, downloadImagePoster);
		 
		 System.out.println("Fin :-)");

	}

}
