package ricohoho.themoviedb;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.experimental.theories.Theories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import ricohoho.mongo.MongoManager;

public class TheMovieDb {
	

	String pathFilm = null;
	List<String> listeFilm=null;
	public boolean addDb=false;


	/**
	 * Tratement d'un dosssier de Film
	 * @param pathFilm : Oath du soosier contentnat les fichier film
	 * @param addDb : Ajout : true/false  dans la base
	 * @param downloadImagePoster : Téléchargemebnt des images poster : True / False
	 */
	void  traiteDossierFilm( String pathFilm ,boolean addDb,boolean downloadImagePoster	 ) {
		
		List<Fichier> listeFichier=null;
		LogText logText = new LogText(pathFilm,"log.txt");
		MongoManager mongoManager=null;
		
		if (addDb==true) {
        	mongoManager=new MongoManager("ricofilm");
        }
		
		//1]liste des fichiers de film
		System.out.println("1]========================== Parse dossier ==========================");
		listeFichier=parseDossier(pathFilm);
		

		//2] Recherche du film sur TheMovieDb
		String nomFilm="";
		String nomFichier=null;
		List<String> listeNomFilmPossible=null;
		List<Film> filmListMatch1Fichier=null; 
		
		 for(int i=0; i<listeFichier.size(); i++) {
			 	nomFichier=listeFichier.get(i).nom;
			 	System.out.println("");
	            System.out.println("2]==========================Fichier["+(i+1)+"]:[["+nomFichier+"]]==========================");
	            FilmFichier filmFichierAnalyse= extractNomFilm(nomFichier);
	            listeNomFilmPossible =filmFichierAnalyse.listeNomFilmPossible;
	            //pourl'instant on prend le premier
	            Film filmRico=null;
	            int j=0;
	            filmListMatch1Fichier=new ArrayList<Film>();
	            //On parcour tout les titre possible de film (obtenu en fct du nom de ficier)
	            while (j<listeNomFilmPossible.size() && filmRico==null){
		            nomFilm=listeNomFilmPossible.get(j);
		            System.out.println("2.2]==========================Fichier nétoyé ["+(i+1)+","+(j+1)+"/"+listeNomFilmPossible.size()+"]:[["+nomFilm+"]]==========================");		        	
		            //filmRico=
		            //REcherche des films dans TheMovieDb matchant une partie du nom du Fichier
		            filmListMatch1Fichier=getFilmTheMovieDb(nomFilm);
		            if(filmListMatch1Fichier.size()>0) {
		            	//On recherche celui qui MAtche l'année (si presente dans le fichier : filmFichierAnalyse.anneeFilm
		            	//Si aucunne annee correspond on prend le prermier
		            	int k=0;
		            	while  (k<filmListMatch1Fichier.size() &&  filmRico==null ) {
		            		System.out.println("2.2]==========================film ["+(k+1)+"/"+filmListMatch1Fichier.size()+"] MovieDB Qui MAtch ["+(i+1)+","+(j+1)+"]:[["+filmListMatch1Fichier.get(k).getTitle()+"]]==========================");
		            		//Annee Du Fichier
		            		int annee_fichier=filmFichierAnalyse.anneeFilm;
		            		
		            		//Annee de film despuis TheMovieDB
		            		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");		                    
		                    Date date_themoviedb=null;
		                    int anne_themoviedb=0;
							try {
								date_themoviedb = formatter.parse(filmListMatch1Fichier.get(k).getRelease_date());
							    Calendar cal = Calendar.getInstance();
							    cal.setTime(date_themoviedb);
							    anne_themoviedb = cal.get(Calendar.YEAR);	
							} catch (java.text.ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}		                    
	
							//oN COMPARE
		                    System.out.println("annee_fichier="+annee_fichier+"/anne_themoviedb="+anne_themoviedb);
		            		if (annee_fichier==anne_themoviedb) {		            					            			
		            			filmRico=filmListMatch1Fichier.get(k);
		            			System.out.println("2.3]==========================Le Film ["+i+"] qui match est["+j+","+k+"]:[["+filmRico.original_title+"]]==========================");
		            		}
		            		k++;
		            	}
		            	//Si pas de correspondance anné => on prend le premier qui match!! 
		            	//==> amélioepeut etre ...
		            	if(filmRico==null) {
		            		filmRico=filmListMatch1Fichier.get(0);
		            		System.out.println("2.3]==========================Le Film  ["+i+"] qui match  pardefaut (0) ["+j+",0]:[["+filmRico.original_title+"]]==========================");		    
		            	}
		            }
		            
		            j++;
	            }    
	            
	            
	            if(filmRico != null) {	            		            
		            String image = filmRico.getPoster_path();
		            System.out.println("--image(getPoster_path)="+image);
		            //2020 ne fonctionne plaus , il faut allez chercher les images dans l'url suivante pour Matrix par exemple
		            //https://api.themoviedb.org/3/movie/603-the-matrix/images?api_key=bd5b73151b4a5a2ac5b34aca8bfe555a&language=en-US&include_image_language=FR
		            //soit get file_path
		            
		            JSONObject _DBObjectImage= getFilmTheMovieDbImage(filmRico.id);
		            image = (String)_DBObjectImage.get("file_path");
		            System.out.println("--image(file_path)="+image);
		            
		            if (image != null) { 
			            if ( !image.equals("") ) {
			            	//System.out.println("2.2] download image poster");
			            	System.out.println("2.4]==========================download image poster==========================");
			            	downloadImage(image,pathFilm,nomFichier.substring(0,nomFichier.length() - 4)+".jpg");
			            	logText.writeToFile("===>OK" + "\t" +nomFichier+"\t"+filmRico.getTitle() );
			            } else {
			            	logText.writeToFile("===>OO" + "\t" +nomFichier+"\t"+filmRico.getTitle() +"\t"+"Pas d'image");
			            }
		            } else {
		            	logText.writeToFile("===>OO" + "\t" +nomFichier+"\t"+filmRico.getTitle() +"\t"+"Pas d'image");
		            }	
		            
		            //Mémorisation des infos du films dans une base MongoDB
		            if (addDb==true) {		            			            	
		            	//Recherche si l'id du film est déjà présent dans la bd
		            	BasicDBObject whereQuery = new BasicDBObject();
		     		    whereQuery.put("id", filmRico.id);
		     		    BasicDBObject fields = new BasicDBObject();
		     		    fields.put("id", 1);
		     		    int i_nb_matchBD= mongoManager.selectDB("films", whereQuery, fields);
		     		    if(i_nb_matchBD==0) {
			            	System.out.println("2.4]==========================Insertion DB==========================");
		     		    	DBObject _DBObject= getFilmTheMovieDbDetail(filmRico.id);			     		    		     		    	
		     		    	mongoManager.insertJSON("films",_DBObject);
		     		    	
		     		    	//Maj avec les infosspecifique du fichier
		     		    	BasicDBObject query = new BasicDBObject();
		     		    	query.put("id", filmRico.id);
		     		    	BasicDBObject newDocument = new BasicDBObject();
		     		    	newDocument.put("RICO.insertDate", new Date());
			     			newDocument.put("RICO.path", pathFilm);
			     			newDocument.put("RICO.file", nomFichier);
			     			newDocument.put("RICO.size", listeFichier.get(i).taille);
			     			newDocument.put("RICO.fileDate", listeFichier.get(i).dateFile);
			     			BasicDBObject updateObj = new BasicDBObject();
			     			updateObj.put("$set", newDocument);
			     			mongoManager.updateDB("films",query,updateObj);
		     		    	
		     		    }else{
		     		    	System.out.println("2.4]==========================Pas d'Insertion DB : deja existant==========================");
		     		    }
		            }
		            
	            } else {
	            	logText.writeToFile("===>KO" + "\t" + nomFichier+"\t!!!!!"+ nomFilm);	
		        }
		 }		
		
	}
	
	
	/**
	 *  Retrun les ficier de films d'un dossier!! 
	 * @param path
	 */
	static List<Fichier> parseDossier(String path) {
		List<Fichier> listeFichier=null;
		//TODO 				 
		FileMAnager fileMAnager = new FileMAnager();
		fileMAnager.initFilm(path);		
		listeFichier= fileMAnager.listeFichiersFilm;
		
		return listeFichier;
		
	}
	
	/**
	 * 
	 * @param listFichier
	 * @return
	 */
	List<String> extractNomFilm(List<String> listFichier) {
		List<String> listeFilm=null;
		//TODO 
		return listeFilm;
		
	}
	
	
	FilmFichier extractNomFilm(String sfchier) {
		List<String> listeNomFilmPossible=new ArrayList<String>();
		FilmFichier filmFichier=new FilmFichier();
		filmFichier.nomFichier=sfchier;
		
		try {
		
			//TODO
			//Exemple1
			//10ème Chambre, Instants d'Audience (2004) - Raymond Depardon
			//Exemple 2
			//20th.Century.Women.2016.FRENCH.BDRip.XviD-EXTREME
	
			
			//0):suppression des extension
			sfchier=sfchier.substring(0,sfchier.lastIndexOf("."));
			
			//1):Suppressio des '.' =>' '
			sfchier=sfchier.replace(".", " ");
			
			//-- On determine une chaine année dans lenom du fichier
			//Pattern p = Pattern.compile("[19|20][0-9][0-9]+");
			Pattern p = Pattern.compile("(19)|(20)[0-9]+[0-9]+");
			Matcher m = p.matcher(sfchier);
			int anneeFilm=0;
			//On prend la première annee du fichier !
			if(m.find()) {
				anneeFilm = Integer.parseInt(m.group());
			    System.out.println("anneeFilm:"+anneeFilm);
			}
			/*
			while (m.find()) {
			    anneeFilm = Integer.parseInt(m.group());
			    System.out.println("anneeFilm:"+anneeFilm);
			    // append n to list
			}
			*/
			
			if(anneeFilm>0) {
				sfchier=sfchier.substring(0,sfchier.indexOf(new Integer(anneeFilm).toString()));
				filmFichier.anneeFilm=anneeFilm;
			}
			
			//--Remplace "ème" --> "e"
			sfchier= sfchier.replace("ème", "e");			
			if (sfchier.indexOf("(")>0) sfchier= sfchier.substring(0, sfchier.indexOf("("));
			if (sfchier.indexOf("[")>0) sfchier= sfchier.substring(0, sfchier.indexOf("["));
			
			sfchier=sfchier.replace("VOSTFR", "");
			
			listeNomFilmPossible.add(sfchier);	
			
			if (sfchier.indexOf("-")>0) {
				String [] splitString= sfchier.split("-");
				listeNomFilmPossible.add(splitString[0]);
				//System.out.println(splitString.length);
				if (splitString.length>1) {
					//System.out.println("splitString[1]:"+splitString[1]);
					if (splitString[1]!=null)  listeNomFilmPossible.add(splitString[1]);
				}
			}
		} catch (Exception e) {
			System.out.println("Erreur extract nom du film");
		}
		
		//System.out.println("listeNomFilmPossible:"+listeNomFilmPossible);
		filmFichier.listeNomFilmPossible=listeNomFilmPossible;
		return filmFichier;		
	}
	
	/**
	 *  Appel WebService themoviedb de recherche d'info d'un film
	 * @param film
	 * @return : Liste des films qui match
	 */
	List<Film> getFilmTheMovieDb(String film ) {
		//String sURL="https://api.themoviedb.org/3/movie/603-the-matrix?api_key=bd5b73151b4a5a2ac5b34aca8bfe555a&language=en-US";
		//String imageRetrun="";
		Film filmRico = null;
		List<Film> filmList= new ArrayList<Film>();
		try {
			film=   URLEncoder.encode(film, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String sURL = "https://api.themoviedb.org/3/search/movie?api_key=bd5b73151b4a5a2ac5b34aca8bfe555a&language=en-US&query="+film+"&page=1&include_adult=false";
		
		String sReturn= UrlManager.getUrl( sURL);
		//System.out.println("retour http :"+sReturn);
		//JSONArray ja = new JSONArray();
		
		 JSONParser parser = new JSONParser();
		 Object obj;
		try {
			
			obj = parser.parse(sReturn);
			
			JSONObject jsonObject = (JSONObject) obj;
			//System.out.println("testObjetJSONJSONObject=");
			//System.out.println(jsonObject);
			
			long total_results = (Long) jsonObject.get("total_results");
			//System.out.println("total_results="+total_results);
			
			// loop array
            JSONArray films = (JSONArray) jsonObject.get("results");
            
            GsonBuilder gsonBuilder=  new GsonBuilder();
        	gsonBuilder.setDateFormat("yyyy-MM-dd");
        	Gson gson =gsonBuilder.create();
        	
            for(int i=0; i<films.size(); i++){
                JSONObject objFilm =(JSONObject)films.get(i);// films.getJSONObject(i);
                //System.out.println("Film "+i);
                long id = (Long)objFilm.get("id");
                String original_title = (String)objFilm.get("original_title");
                String title = (String)objFilm.get("title");
                String poster_path=(String)objFilm.get("poster_path");
                String   release_date= (String)objFilm.get("release_date");
                //On ne prend que la premier
                //if (i==0) {
                	//imageRetrun=poster_path;                	
                	String a_jason_string=objFilm.toJSONString();  
                	//filmRico.setRelease_date(release_date);
                 	filmRico = gson.fromJson(a_jason_string, Film.class);
                 	filmList.add(filmRico);
                //}
                /*
                System.out.println("----- Film("+i+")");
                System.out.println("--"+ id);
                System.out.println("--"+original_title);
                System.out.println("--"+release_date+"/"+filmRico.release_date);
                System.out.println("--"+title);
                System.out.println("----- ");
                */
            }  

            
	         
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	/*
		if (filmList.size()>0)
			return filmList.get(0);
		else
			return null;
		*/
		return filmList;
         
	}
	
	/*
	 * Recherche du Json du detail IMAGE d'un film
	 */
	JSONObject getFilmTheMovieDbImage(long  filmId ) {
		//https://api.themoviedb.org/3/movie/603-the-matrix/images?api_key=bd5b73151b4a5a2ac5b34aca8bfe555a&language=en-US&include_image_language=FR
		String sURL = "https://api.themoviedb.org/3/movie/"+filmId+"/images?api_key=bd5b73151b4a5a2ac5b34aca8bfe555a&language=en-US&include_image_language=FR,EN,null";
		System.out.println("getFilmTheMovieDbImage : sURL="+sURL);
		String sReturn= UrlManager.getUrl( sURL);
		System.out.println("getFilmTheMovieDbImage : sReturn="+sReturn);
		JSONObject objFilmImage=null;
			
		try {
			
			
			//Creation du parser
            JSONParser parser = new JSONParser();
   		 	Object obj;		    
   		 	
   		 	//parse su JSON retrouné
			obj = parser.parse(sReturn);			
			JSONObject jsonObject = (JSONObject) obj;
			//System.out.println("testObjetJSONJSONObject=");
			//System.out.println(jsonObject);
						
			//Reccuperation dans un tableau de la partie POSTERS
            JSONArray filmsImage = (JSONArray) jsonObject.get("posters");
			
			
            //On prende la première image ! !uniquement
             objFilmImage =(JSONObject)filmsImage.get(0);// films.getJSONObject(i);
           
             
                                                	         
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objFilmImage;
         
	}
	
	
	/*
	 * Recherche du Json du detail d'un film
	 */
	 DBObject getFilmTheMovieDbDetail(long  filmId ) {
		//String sURL="https://api.themoviedb.org/3/movie/603?api_key=bd5b73151b4a5a2ac5b34aca8bfe555a&append_to_response=credits,videos"
		String sURL = "https://api.themoviedb.org/3/movie/"+filmId+"?api_key=bd5b73151b4a5a2ac5b34aca8bfe555a&append_to_response=credits,videos";
		System.out.println("sURL="+sURL);
		String sReturn= UrlManager.getUrl( sURL);
		System.out.println("sReturn="+sReturn);
		DBObject obj=null;
			
		try {
			
			obj = (DBObject) JSON.parse(sReturn);
						
			
			//System.out.println("testObjetJSONJSONObject=");
			System.out.println(obj);
			
			//DBObject obj = (DBObject) JSON.parse("sample_json");
                                                	         
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
         
	}
	
/**
 * Download d'un fichier image poster du film	
 * @param image
 * @param path
 * @param destinationFile
 */
	 void downloadImage(String image,String path,String destinationFile) {
		/*11/01/2020 l'url a changer
		--image 'petite'
		https://image.tmdb.org/t/p/w300_and_h450_bestv2/LCcZvB2Ynxg7JOQgviGwZ3l66L.jpg
		--image agrandie
		https://image.tmdb.org/t/p/w600_and_h900_bestv2/LCcZvB2Ynxg7JOQgviGwZ3l66L.jpg
		*/
		//String uRL="https://image.tmdb.org/t/p/w640/";
		String uRL="https://image.tmdb.org/t/p/w600_and_h900_bestv2/";		
		
		String imageUrl =uRL + image;
		System.out.println("Download"+imageUrl + "to "+path+destinationFile);
		try {
			UrlManager.saveImage(imageUrl, path+destinationFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
