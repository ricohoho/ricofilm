package ricohoho.themoviedb;
//2024/10/24 Correction bloccage Git

import java.util.List;
import java.io.File;

public class FileManagerTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		File _File  = new java.io.File("D:\\DATA\\e.fassel\\Downloads\\log_20221105-b.txt");
		System.out.println(_File.getAbsolutePath());
		System.out.println(_File.getName());
		System.out.println(_File.getPath());
		System.out.println(_File.getParent());
		
		//parseDossier("D:\\DATA\\e.fassel\\Downloads\\",true);
		

	}

	
	
	static List<Fichier> parseDossier(String path, boolean avecSsDossier) {
		List<Fichier> listeFichier=null;
		//TODO 				 
		FileMAnager fileMAnager = new FileMAnager(); 
		fileMAnager.initFilm(path,avecSsDossier);		
		listeFichier= fileMAnager.listeFichiersFilm;
		System.out.println("Nb de film ="+listeFichier.size());
		return listeFichier;
		
	}
}
