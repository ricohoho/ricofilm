package ricohoho.themoviedb;
//2024/10/24 Correction bloccage Git

import static com.mongodb.client.model.Filters.eq;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import ricohoho.ffmpeg.StreamFilm;
import ricohoho.mongo.MongoManager;

public class TheMovieDb {
	


	//public boolean addDb=false;
	String dbMongoHost;
	int dbMongoPort;
	
	String dbMongoName;
	String dbUSerName;
	String dbPAssword;

	
	/**
	 * Constructor
	 * @param dbMongoHost  : le host de mongodb
	 * @param dbMongoPort : le port de mongodb
	 */
	public TheMovieDb(String dbMongoHost,int dbMongoPort, String dbMongoName,String dbUSerName,String dbPAssword) {
		this.dbMongoHost=dbMongoHost;
		this.dbMongoPort=dbMongoPort;
		this.dbMongoName=dbMongoName;
		this.dbUSerName=dbUSerName;
		this.dbPAssword=dbPAssword;
		Logger logger = LoggerFactory.getLogger(TheMovieDb.class);
		logger.debug("dbMongoHost="+dbMongoHost);
		logger.debug("dbMongoPort="+dbMongoPort);
		logger.debug("dbMongoName="+dbMongoName);
		logger.debug("dbUSerName="+dbUSerName);
		logger.debug("dbPAssword="+dbPAssword);
	}

	public TheMovieDb() {

	}


	
/**
 * Pour un dossier : Supprime les films de la base absent du dossier 	
 * @param serveurName : nom du serveur
 * @param pathFilm : Dossier de stockage des fichiers films
 */

// Version 20221102 : remplace la version sans le parametre 
//version 2 de la focntion en prenant ne charge les sous dossiers ssDoossier
void traiterDossierSupprimeFilmDBFichierAbsent(String serveurName, String pathFilm ,boolean addDb, boolean avecSsDossier) {

	Logger logger = LoggerFactory.getLogger(TheMovieDb.class);
	logger.info("traiterDossierSupprimeFilmDBFichierAbsent : debut");

	List<Fichier> listeFichier;
	//LogText logText = new LogText(pathFilm,"log.txt");
	MongoManager mongoManager = null;

	if (addDb) {
		mongoManager = new MongoManager(dbMongoHost, dbMongoPort, dbMongoName, dbUSerName, dbPAssword);
	}


	//1 : Liste des dichier film du dossier
	logger.info("1]========================== Parse dossier ==========================");
	listeFichier = parseDossier(pathFilm, avecSsDossier);

	//Map<String, Fichier> mapFichier = listeFichier.stream().collect(Collectors.toMap(Fichier::getNom, item -> item));
	Map<String, Fichier> mapFichier = listeFichier.stream().collect(Collectors.toMap(Fichier::getPathEtName, item -> item));


	//2 : liste des film dans la base avec ce dossier : path = pathFilm et serveur_name=serveurName 
	String collectionName = "films";
	Document query = new Document();
	query.put("RICO_FICHIER.serveur_name", serveurName);

	if (avecSsDossier) {
		///https://www.freeformatter.com/regex-tester.html
		// signifie de prendre les sous doosier aussi
		//Attention les \ pour Windows il faut les doubler
		pathFilm = pathFilm.replace("\\", "\\\\");
		query.put("RICO_FICHIER.path", new Document("$regex", pathFilm));
	} else
		query.put("RICO_FICHIER.path", pathFilm);

	List<Document> docQuiMAtch = null;
	if (mongoManager != null) {
		docQuiMAtch = mongoManager.selectDBDoc(collectionName, query);
	}

	//3 : Comparaison des deux listes
	//Pour chaque filmDeLaBase, si n'appartient pas � la liste des dossier
	//	=>supression du fichier
	//	=>Si plus de fichier ==> suppression du film de la base !
	if (docQuiMAtch != null) {
		logger.info("Select : " + docQuiMAtch.size());
		for (Document doc : docQuiMAtch) {
			int doc_id = doc.getInteger("id");
			String film_title = doc.getString("title");
			//20251019 ERic : nouvelle version avec nouvelle verion driver MongoDb
			//BsonDocument bsonDocument = doc.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry());
			CodecRegistry codecRegistry = mongoManager.database.getCodecRegistry();
			BsonDocument bsonDocument = doc.toBsonDocument(BsonDocument.class,codecRegistry);
			
			BsonArray rico_fichierArray = bsonDocument.getArray("RICO_FICHIER");
			List<BsonValue> rico_fichier_list = rico_fichierArray.getValues();
			logger.info("[" + film_title + "/" + doc_id + "] Nbre de fichier :" + rico_fichierArray.size());
			int nb_suppression = 0;
			for (BsonValue rico_fic : rico_fichier_list) {

				BsonDocument rico_fic_bsondoc = rico_fic.asDocument();
				String serveur_name = rico_fic_bsondoc.get("serveur_name").asString().getValue();
				String path = rico_fic_bsondoc.get("path").asString().getValue();
				String file = rico_fic_bsondoc.get("file").asString().getValue();

				logger.debug("serveur_name/path/file:" + serveur_name + "/" + path + "/" + file);
				if (serveurName.contentEquals(serveur_name) && mapFichier.get(path + file) == null) {
					logger.info("recheche du film su rle disque :" + file);
					logger.info("match:" + mapFichier.get(file));
					logger.info("Suppresion du fichier du film [" + doc_id + ":" + path + "\\" + file);
					//Suppression de l'item dans l'aaray RICO_FICHIER
					Document query2 = new Document();
					query2.put("id", doc_id);
					String arrayName = "RICO_FICHIER";

					//EF 2020/08/02 : debog => on supprime l'item RICOFICHIER sur ces 2 attributs file et path (et pas juste le FIchier)
					ArrayList<String> filtreArrayParam = new ArrayList<String>(Arrays.asList("path", "file"));
					ArrayList<String> filtreArrayValue = new ArrayList<String>(Arrays.asList(path, file));

					mongoManager.arrayRemoveItem(collectionName, query2, arrayName, filtreArrayParam, filtreArrayValue);
					nb_suppression++;
	//				 }	else {
	//					 logger.info("PAs de Suppresion du fichier du film ["+doc_id+":"+path + "\\"+file);
	//				 }
				} else {
					logger.info("PAs de Suppresion du fichier du film [" + doc_id + ":" + path + "\\" + file);
				}
			}

			if (nb_suppression == rico_fichier_list.size()) {
				//===> Suppression du FILM !
				Document query2 = new Document();
				query2.put("id", doc_id);
				mongoManager.deleteDB(collectionName, query2);
				logger.info("Suppresion du film [" + doc_id + "]");
			}
		}
	}
	//Fermeture de la connexion MongoDB
	if (mongoManager!=null) {
		mongoManager.close();
	}
}
	
	
	
	/**
	 * Traitement d'un dosssier de Film
	 * @param pathFilm : Path du dossier contenat les fichier film
	 * @param memoRicoFIlm :
	 * 			BD :   via la base de donnee direct
	 * 			HTTP : via les Web service
	 * 			NON	 :  pas de memorisation
	 * @param downloadImagePoster : Telechargemebnt des images poster : True / False
	 * return int[0] => nombre total de fichier traite
	return int[1] => nombre d'erreur
	return int[2] => nombre d'erreur d'image
	return int[3] => nombre d'insertion Film
	return int[4] => nombre d'insertion de fichier
	 */
	public int[] traiteDossierFilm( String serveurName, String pathFilm ,String  memoRicoFIlm,boolean downloadImagePoster, int sousDossier	 ) {
									
		
		// return int[0] => nombre total de fichier trait�
		// return int[1] => nombre d'erreur de recherche de film
		// return int[2] => nombre d'erreur d'image
		// return int[3] => nombre d'insertion Film
		// return int[4] => nombre d'insertion de fichier		
		int[] retourParse = new int[5];
		retourParse[1]=0; // Nbre de ficier, film non trouv�
		retourParse[2]=0; // Nbre d'image non trouv�
		retourParse[3]=0; // Nbre d'image non trouv�
		retourParse[4]=0; // Nbre d'image non trouv�
		
		Logger logger = LoggerFactory.getLogger(TheMovieDb.class);
		logger.debug( "traiteDossierFilm : debut"); 
		List<Fichier> listeFichier;
		List<String> listesSsDossier;
		
		//Il faut que le path se termine par un / ou \ pour les comparaison dans la base ensuite ..
		if (!pathFilm.substring(pathFilm.length()-1).equals(File.separator)) {
			pathFilm = pathFilm + File.separator;
		}
		
		LogText logText = new LogText(pathFilm,"log.txt");
		MongoManager mongoManager=null;
		
		if (memoRicoFIlm.equals("BD")) {
        	mongoManager=new MongoManager(dbMongoHost,dbMongoPort,dbMongoName, dbUSerName,dbPAssword);
        }
		
		//1]liste des fichiers de film
		logger.info("1]========================== Parse dossier ==========================");
		//listeFichier=parseDossier(pathFilm);
		
		FileMAnager fileMAnager = new FileMAnager();
		fileMAnager.initFilm(pathFilm, false);		
		listeFichier= fileMAnager.listeFichiersFilm;
		listesSsDossier= fileMAnager.listeSsDossier;
		retourParse[0] = fileMAnager.listeFichiersFilm.size();
		
		

		//2] Recherche du film sur TheMovieDb
		String nomFilm="";
		String nomFichier;
		List<String> listeNomFilmPossible;
		List<Film> filmListMatch1Fichier;
		
		 for(int i=0; i<listeFichier.size(); i++) {
			 	nomFichier=listeFichier.get(i).nom;
			 	logger.info("");
			 	logger.info("2]==========================Fichier["+(i+1)+"]:[["+nomFichier+"]]==========================");
	            FilmFichier filmFichierAnalyse= extractNomFilm(nomFichier);
	            listeNomFilmPossible =filmFichierAnalyse.listeNomFilmPossible;
	            //pourl'instant on prend le premier
	            Film filmRico=null;
	            int j;
	            
	            //2021/12/27
	            //Cas paticulier on l'on indique l'id MoviedB du film xxxx[[14325]]yyyy => movi
	            //Dans ce pas pal la peine d'annalyser le nom et compareer avec des recherches dans MovieDB
	            if (filmFichierAnalyse.filmId!=0) {
	            	filmRico = new Film(filmFichierAnalyse.filmId);
	            	logger.info("2.1]==========================filmId directe :[["+filmFichierAnalyse.filmId+"]]==========================");
	            }
	            
	            //On parcour tout les titre possible de film (obtenu en fct du nom de fichier)
			 	j=0;
	            while (j<listeNomFilmPossible.size() && filmRico==null){
		            nomFilm=listeNomFilmPossible.get(j);
		            logger.info("2.2]==========================Fichier n�toy� ["+(i+1)+","+(j+1)+"/"+listeNomFilmPossible.size()+"]:[["+nomFilm+"]]==========================");		        	
		            //filmRico=
		            //REcherche des films dans TheMovieDb matchant une partie du nom du Fichier
		            filmListMatch1Fichier=getFilmTheMovieDb(nomFilm);
		            if(filmListMatch1Fichier.size()>0) {
		            	//On recherche celui qui MAtche l'annee (si presente dans le fichier : filmFichierAnalyse.anneeFilm
		            	//Si aucunne annee correspond on prend le prermier
		            	int k=0;
		            	while  (k<filmListMatch1Fichier.size() &&  filmRico==null ) {
		            		//film ["+(k+1)+"/"+filmListMatch1Fichier.size()+"] MovieDB Qui MAtch
		            		logger.info("2.2.1]========================== comparaison fichier : ["+nomFilm+"] avec film ["+filmListMatch1Fichier.get(k).getTitle()+"]");
		            		logger.info("2.2.2]========================== comparaison fichier -> ["+(i+1)+","+(j+1)+"]: avec film ["+(k+1)+"/"+filmListMatch1Fichier.size()+"]");
		            		//Annee Du Fichier
		            		int annee_fichier=filmFichierAnalyse.anneeFilm;
		            		
		            		//Annee de film despuis TheMovieDB
		            		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");		                    
		                    Date date_themoviedb=null;
		                    int anne_themoviedb;
							try {
								
								//Si annee movieDb est null
								if (filmListMatch1Fichier.get(k).getRelease_date()!=null && !filmListMatch1Fichier.get(k).getRelease_date().equals("") ) { //&& date_themoviedb!=null ) {		
									date_themoviedb = formatter.parse(filmListMatch1Fichier.get(k).getRelease_date());
									Calendar cal = Calendar.getInstance();
									cal.setTime(date_themoviedb);
									anne_themoviedb = cal.get(Calendar.YEAR);
								} else {
									anne_themoviedb=1900;
								}
							} catch (java.text.ParseException e) {
								e.printStackTrace();
								anne_themoviedb=1900;
							} catch (Exception e ) {
								e.printStackTrace();
								logger.warn("Exception:"+e);
								anne_themoviedb=1900;
							}
	
							//oN COMPARE
							logger.info("2.2.3]========================== comparaison fichier -> annee_fichier="+annee_fichier+"/anne_themoviedb="+anne_themoviedb);		                    
		            		if (annee_fichier==anne_themoviedb) {		            					            			
		            			filmRico=filmListMatch1Fichier.get(k);
		            			//logger.info("2.3]==========================Le Film ["+i+"] qui match est["+j+","+k+"]:[["+filmRico.original_title+"]]==========================");
		            			logger.info("2.3]======================== !!!  Le Gagant pour l'ann�e du fichier ["+nomFilm+"] est :[["+filmRico.getTitle()+"]] !!! ======================");
		            		}
		            		k++;
		            	}
		            	//Si pas de correspondance ann�e => on prend le premier qui match!! 
		            	//==> am�lioepeut etre ...
		            	if(filmRico==null) {
		            		filmRico=filmListMatch1Fichier.get(0);
		            		//logger.info("2.3]==========================Le Film  ["+i+"] qui match  pardefaut (0) ["+j+",0]:[["+filmRico.original_title+"]]==========================");
		            		logger.info("2.3]======================== !!!  Le Gagant par defaut du fichier ["+nomFilm+"] est :[["+filmRico.getTitle()+"]] !!! ======================");
		            	}
		            	
		            } else {
		            	logger.info("2.2.1]========================== comparaison fichier : ["+nomFilm+"] avec <rien trouv� dans TheMovieDB>");
		            }
		            
		            j++;
	            }    
	            
	            
	            if(filmRico != null) {	            		    
	            	if (downloadImagePoster) {
			            String image = filmRico.getPoster_path();
			            logger.debug("--image(getPoster_path)="+image);
			            //2020 ne fonctionne plaus , il faut allez chercher les images dans l'url suivante pour Matrix par exemple
			            //https://api.themoviedb.org/3/movie/603-the-matrix/images?api_key=bd5b73151b4a5a2ac5b34aca8bfe555a&language=en-US&include_image_language=FR
			            //soit get file_path
			            
			            JSONObject _DBObjectImage= getFilmTheMovieDbImage(filmRico.id);
			            try {
			            	image = (String)_DBObjectImage.get("file_path");
			            } catch(Exception e) {
			            	logger.info("==== >Pas d'image");
			            	image=null;
			            }
			            
			            
			            if (image != null) {
			            	logger.debug("--image(file_path)="+image);
				            if ( !image.equals("") ) {
				            	//System.out.println("2.2] download image poster");
				            	logger.info("2.4]==========================download image poster==========================");
				            	downloadImage(image,pathFilm,nomFichier.substring(0,nomFichier.length() - 4)+".jpg");
				            	logText.writeToFile("===>OK" + "\t" +nomFichier+"\t"+filmRico.getTitle() );
				            } else {
				            	logText.writeToFile("===>OO" + "\t" +nomFichier+"\t"+filmRico.getTitle() +"\t"+"Pas d'image");
				            	retourParse[2]=retourParse[2]+1;
				            }
			            } else {
			            	logText.writeToFile("===>OO" + "\t" +nomFichier+"\t"+filmRico.getTitle() +"\t"+"Pas d'image");
			            	retourParse[2]=retourParse[2]+1; 
			            }	
	            	}
		            //M�morisation des infos du films dans une base MongoDB
		            if (memoRicoFIlm.equals("BD")) {		            			           
		            	logger.info("Memorisation dans RicoFIlm direct par cnx BD");
		            	//Recherche si l'id du film est d�j� pr�sent dans la bd
		            	Document whereQuery = new Document();
		     		    whereQuery.put("id", filmRico.id);
		     		    Document fields = new Document();
		     		    fields.put("id", 1);
		     		    logger.info("On recherche si le film id=["+filmRico.id+"] existe dans la base ? ");


		     		    int i_nb_matchBD= mongoManager.selectDB("films", whereQuery, fields);
		     		    if(i_nb_matchBD==0) {
		     		    	logger.info("2.4]==========================Insertion DB==========================");
		     		    	Document _DBObject= getFilmTheMovieDbDetail(filmRico.id);
							//20241019 ERic : nouvelle version avec nouvelle verion driver MongoDb
							//Document doc = new Document(_DBObject.toMap());
		     		    	mongoManager.insertJSON("films",_DBObject);

		     				//==Ajout partie Fichier
		     				String arrayName  = "RICO_FICHIER";
		     				Bson filter = eq("id",  filmRico.id);		     						     			   
		     			    //BasicDBObject newDocument = new BasicDBObject();
							Document newDocument = new Document();
		     			   
		     			    newDocument.put("serveur_name", serveurName);
		     			    newDocument.put("insertDate", new Date());
			     			newDocument.put("path", pathFilm);
			     			newDocument.put("file", nomFichier);
			     			newDocument.put("size", listeFichier.get(i).taille);
			     			newDocument.put("fileDate", listeFichier.get(i).dateFile);

							//20221221 : Recherche des informations  vidé/audio du fichiers
							List<Document> InfoFilmSstreams = (new StreamFilm(new File(pathFilm+nomFichier))).getInformationsFile();
							newDocument.put("InfoStreams", InfoFilmSstreams);

		     			    //List<BasicDBObject> list = new ArrayList<>();
							List<Document> list = new ArrayList<>();
		     			    list.add(newDocument); 		     			    
		     				mongoManager.arrayAddItem2("films",filter,arrayName,list);
		     				retourParse[3]++;//nb d'inseetion de film
		     				retourParse[4]++;//nb d'inseetion de fichier
		     				
		     		    	
/*	     		    	
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
*/			     			
		     				//20101205
		     				//Ajout d'une date d'ajout dans la base
		     				Document query = new Document();
		     		    	query.put("id", filmRico.id);
							Document newDocumentBO = new Document();
							newDocumentBO.put("UPDATE_DB_DATE", new Date());
			     			//newDocument.put("UPDATE_DB_DATE", listeFichier.get(i).dateFile);
			     			Document updateObj = new Document();
			     			updateObj.put("$set", newDocumentBO);
			     			mongoManager.updateDB("films",query,updateObj);
		     				
		     		    	
		     		    }else{
		     		    	logger.info("2.4]==========================Film deja existant==========================");	
		     		    	//Recherche si le fichier est le meme !! si non ajout du fichier
		     		    	String arrayName  = "RICO_FICHIER";
		     		    	Bson filter = eq("id", filmRico.id);
		     				List<Document> arrayItemFiltered ;
		     				//ArrayList<String> filtreArray = new ArrayList<String>(Arrays.asList("path||"+pathFilm,"file||"+nomFichier));		     						     				
		     				
		     				ArrayList<String> filtreArrayParam = new ArrayList<String>(Arrays.asList("path","file"));
		     				ArrayList<String> filtreArrayValue = new ArrayList<String>(Arrays.asList(pathFilm,nomFichier));
		     				
		     				
		     		    	arrayItemFiltered= mongoManager.arrayListITemFind("films", filter,arrayName,filtreArrayParam,filtreArrayValue);
		     		    	//System.out.println("lliste filtre find "+arrayItemFiltered.size());
		     		    	//Si size =0 ==> insertion FILM existant , mais fichier différent 
		     		    	if (arrayItemFiltered.size()==0) {
		     		    		logger.info("FILM existant , mais path / fichier diff�rent ==> Insertion nouveau Fihcier");
		     		    		//==Ajout partie Fichier
			     				arrayName  = "RICO_FICHIER";
			     				filter = eq("id",  filmRico.id);
								Document newDocument = new Document();
			     			    newDocument.put("serveur_name", serveurName);
			     			    newDocument.put("insertDate", new Date());
				     			newDocument.put("path", pathFilm);
				     			newDocument.put("file", nomFichier);
				     			newDocument.put("size", listeFichier.get(i).taille);
				     			newDocument.put("fileDate", listeFichier.get(i).dateFile);

								//20221221 : Recherche des informations  vidé/audio du fichiers
								List<Document> InfoFilmSstreams = (new StreamFilm(new File(pathFilm+nomFichier))).getInformationsFile();
								newDocument.put("InfoStreams", InfoFilmSstreams);

			     			    List<Document> list = new ArrayList<>();
			     			    list.add(newDocument); 		     			    
			     				mongoManager.arrayAddItem2("films",filter,arrayName,list);

			     				//20101205
			     				//Ajout d'une date d'ajout dans la base
			     				//BasicDBObject query = new BasicDBObject();
			     		    	//query.put("id", filmRico.id);

								///plus utilisé ?
								//BasicDBObject newDocumentBO = new BasicDBObject();
								//EF 20230326 : On ne met plus a jour UPDATE_DB_DATE car pour l'instant on ne dissocie pas dans le filtre l'update de la creation
								//newDocumentBO.put("UPDATE_DB_DATE", new Date());
				     			//newDocument.put("UPDATE_DB_DATE", listeFichier.get(i).dateFile);

								// a voir si nécessaire ?
								//BasicDBObject updateObj = new BasicDBObject();
				     			//updateObj.put("$set", newDocumentBO);
				     			//mongoManager.updateDB("films",query,updateObj);
			     				
			     				retourParse[4]++;//nb d'inseetion de fichier
		     		    	} else {
		     		    		logger.info("FILM existant , et path / fichier existant ==> aucune action");
								Document doc_RICO_FILM = arrayItemFiltered.get(0);
								List<Document> InfoFilmSstreams= (List<Document>) doc_RICO_FILM.get("InfoStreams");
								if (InfoFilmSstreams==null ) {
									logger.info("FILM existant , Mais besoin d'ajout des infos Stream");
									//==> mise a jour su chmaps stram dans le bon RICOFILM[]
									//1 - Suppression du Rico_FILM qui match
										logger.info("Suppresion du fichier du film ["+filmRico.id+":"+pathFilm + "\\"+nomFichier);
										//Suppression de l'item dans l'aaray RICO_FICHIER
										Document query2 = new Document();
										query2.put("id",filmRico.id);
										//EF 2020/08/02 : on supprime l'item RICOFICHIER sur ces 2 attributs file et path (et pas juste le FIchier)
										ArrayList<String> filtreArrayParamX = new ArrayList<String>(Arrays.asList("path","file"));
										ArrayList<String> filtreArrayValueX = new ArrayList<String>(Arrays.asList(pathFilm,nomFichier));
										mongoManager.arrayRemoveItem("films", query2, arrayName, filtreArrayParamX,filtreArrayValueX);
									//2 - creation du RicoFilm avec le bon stream
										//Recherche du Stream
										logger.info("Ajout du fichier du film + Stream  ["+filmRico.id+":"+pathFilm + "\\"+nomFichier);
										List<Document> InfoFilmSstreams2 = (new StreamFilm(new File(pathFilm+nomFichier))).getInformationsFile();
										doc_RICO_FILM.put("InfoStreams", InfoFilmSstreams2);
										List<Document> list = new ArrayList<>();
										list.add(doc_RICO_FILM);
										mongoManager.arrayAddItem2("films",filter,arrayName,list);
								} else {
									logger.info("FILM existant , et  infos Stream OK");
								}
		     		    	}
		     		    }
		            } else if (memoRicoFIlm.equals("HTTP")){
		            	//Add via WebSrvREST
		            	logger.info("Memorisation dans RicoFIlm via serveice REST");
		            	//Recherche si l'id du film est deja present dans la bd
		        		String p_name = "id:"+filmRico.id;		
		        		FilmRestManager _FilmRestManager= new FilmRestManager("localhost","3000");		        		
		        		int i_nb_matchBD = _FilmRestManager.getFilmsCount(  p_name ) ;		        		 
			     		if(i_nb_matchBD==0) {
			     			logger.info("2.4]==========================Insertion DB via REST ==========================");
			     			JSONObject json= getFilmTheMovieDbDetailJson(filmRico.id);
			     			
			     			//==Ajout partie Fichier
		     				//String arrayName  = "RICO_FICHIER";
		     				//TODO A completer  : 1 ajout de la partie Rico_FICHIER dans le json du FILM issu de MovieDB
			     			
			     			_FilmRestManager.addFilm( json );
			     		} else {
			     			logger.info("2.4]==========================Film deja existant==========================");	
			     			//Recherche si le fichier est le meme !! si non ajout du fichier
		     				//TODO A completer 1 : recherche d'un fichier dans un film
			     			//TODO A completer 2  : insertion d'un fichier dans un film
			     			
			     		}
		            	
		            } else {
		            	logger.info("PAs de m�morisation du film dans Rico FIlm");
		            }
		            
	            } else {
	            	logText.writeToFile("===>KO" + "\t" + nomFichier+"\t!!!!!"+ nomFilm);
	            	retourParse[1] =retourParse[1] +1; 
		        }
		 }		
		 
		 
		 //Gestion des Ss Dossier
		 if (sousDossier>0) {
			 int[] retourSsDossier = new int[5];
			 for(int i=0; i<listesSsDossier.size(); i++) {
				 logger.info("traiteDossierFilm Appel reccurssif : "+listesSsDossier.get(i));				 
				 retourSsDossier = traiteDossierFilm(  serveurName,  listesSsDossier.get(i)+File.separator ,memoRicoFIlm,downloadImagePoster, sousDossier-1);
				 retourParse[0] =retourParse[0] + retourSsDossier[0];
				 retourParse[1] =retourParse[1] + retourSsDossier[1];
				 retourParse[2] =retourParse[2] + retourSsDossier[2];
				 retourParse[3] =retourParse[3] + retourSsDossier[3];
				 retourParse[4] =retourParse[4] + retourSsDossier[4];
			 }
		 }

		 //Fermeture de la connexion MongoDB
		 if (mongoManager!=null) {
			mongoManager.close();
		}

		 logger.info( "traiteDossierFilm : fin");
		 return retourParse;
	}
	
	
	/**
	 *  Retrun les ficier de films d'un dossier!! 
	 * @param path
	 */
	static List<Fichier> parseDossier(String path, boolean avecSsDossier) {
		List<Fichier> listeFichier=null;
		//TODO 				 
		FileMAnager fileMAnager = new FileMAnager(); 
		fileMAnager.initFilm(path,avecSsDossier);		
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
	
	/**
	 * 
	 * @param sfchier
	 * @return
	 */
	FilmFichier extractNomFilm(String sfchier) {
		Logger logger = LoggerFactory.getLogger(TheMovieDb.class);
		logger.debug( "extractNomFilm : debut"); 
		List<String> listeNomFilmPossible=new ArrayList<String>();
		FilmFichier filmFichier=new FilmFichier();
		filmFichier.nomFichier=sfchier;
		
		try {
		
			//TODO
			//Exemple1
			//10�me Chambre, Instants d'Audience (2004) - Raymond Depardon
			
			//Exemple 2
			//20th.Century.Women.2016.FRENCH.BDRip.XviD-EXTREME
	
			//exemple 3
			//Un.Mari.De.Trop.FRENCH.DVDRip.XviD-ZANBiC.FUCK.[emule-island.com].avi
			
			//0):suppression des extension
			sfchier=sfchier.substring(0,sfchier.lastIndexOf("."));
			
			//1):Suppressio des '.' =>' '
			sfchier=sfchier.replace(".", " ");
			
			logger.debug("sfchier:"+sfchier);
			
			
			
			//20211227 ajout de la possibili� d'indiquer l'Id MovidDB da,ns le nom du fichier
			// Extraction de MovieDB iD dansd le nom du fichier, pour gerer les cas ou la detection est coplexe
			Pattern p = Pattern.compile("\\[\\[[0-9]+\\]\\]");
			Matcher m = p.matcher(sfchier);
			logger.info(p.toString());
			long MovieDbID=0;
			while (m.find()) {
				String element =m.group();
				MovieDbID = Long.parseLong(element.replace("[","").replace("]", ""));
				logger.info("MovieDbID:"+MovieDbID);
			    // append n to list
			}			
			filmFichier.filmId=MovieDbID;
			
			//-- On determine une chaine ann�e dans lenom du fichier
			//Pattern p = Pattern.compile("[19|20][0-9][0-9]+");
			 p = Pattern.compile("(19)[0-9]+[0-9]|(20)[0-9]+[0-9]+");
			 m = p.matcher(sfchier);
			int anneeFilm=0;
			//On prend la premi�re annee du fichier !
			if(m.find()) {
				anneeFilm = Integer.parseInt(m.group());
				logger.info("anneeFilm:"+anneeFilm);
			}
			/*
			while (m.find()) {
			    anneeFilm = Integer.parseInt(m.group());
			    System.out.println("anneeFilm:"+anneeFilm);
			    // append n to list
			}
			*/							
			
			if(anneeFilm>0) {
				//SI le ficier commence par une ann�e pas de traiement => on supprime pas la date on consid�re qu'elle fait partie du nom du film 
				if (sfchier.indexOf(new Integer(anneeFilm).toString())>0) {
					sfchier=sfchier.substring(0,sfchier.indexOf(new Integer(anneeFilm).toString())).trim();
					filmFichier.anneeFilm=anneeFilm;
				}
			}
			
			
			
			
			
			
			//--Remplace "�me" --> "e"
			sfchier= sfchier.replace("�me", "e");			
			if (sfchier.indexOf("(")>0) sfchier= sfchier.substring(0, sfchier.indexOf("("));
			if (sfchier.indexOf("[")>0) sfchier= sfchier.substring(0, sfchier.indexOf("["));
			
			//Cas 3 di fichier xxxFRENCHyyy.avi ajouter xxx dans la liste de film
			if (sfchier.toUpperCase().indexOf("FRENCH")>0) listeNomFilmPossible.add(sfchier.substring(0, sfchier.toUpperCase().indexOf("FRENCH")));
			
			//Cas 4 du fichier xxxVOSTFRyyy.avi ajouter xxx dans la liste de film
			if (sfchier.toUpperCase().indexOf("VOSTFR")>0) listeNomFilmPossible.add(sfchier.substring(0, sfchier.toUpperCase().indexOf("VOSTFR")));
			
			//Cas 5 du fichier xxxTRUEFRENCHyyy.avi ajouter xxx dans la liste de film
			if (sfchier.toUpperCase().indexOf("TRUEFRENCH")>0) listeNomFilmPossible.add(sfchier.substring(0, sfchier.toUpperCase().indexOf("TRUEFRENCH")));
			
			//Cas 5 du fichier xxxx264yyy.avi ajouter xxx dans la liste de film
			if (sfchier.toUpperCase().indexOf("x264")>0) listeNomFilmPossible.add(sfchier.substring(0, sfchier.toUpperCase().indexOf("x264")));
			
			//Cas 6 du fichier xxxx720yyy.avi ajouter xxx dans la liste de film
			if (sfchier.toUpperCase().indexOf("720")>0) listeNomFilmPossible.add(sfchier.substring(0, sfchier.toUpperCase().indexOf("720")));
			
			//CAS 7 du fichier xxxxDVDRIPyyy.avi ajouter xxx dans la liste de film
			if (sfchier.toUpperCase().indexOf("DVDRIP")>0) listeNomFilmPossible.add(sfchier.substring(0, sfchier.toUpperCase().indexOf("DVDRIP")));
			
			//CAS 8 du fichier xxxxH264yyy.avi ajouter xxx dans la liste de film
			if (sfchier.toUpperCase().indexOf("H264")>0) listeNomFilmPossible.add(sfchier.substring(0, sfchier.toUpperCase().indexOf("H264")));
			
			sfchier=sfchier.replace("VOSTFR", "");
			sfchier=sfchier.replace("x264", "");
			sfchier=sfchier.replace(" FRENCH", "");
			sfchier=sfchier.replace(" TRUEFRENCH", "");
			sfchier=sfchier.replace("True French", "");			
			sfchier=sfchier.replace(" VF", "");
			sfchier=sfchier.replace("DVDRip", "");
			
			
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
			logger.error("{}",e);
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
		Logger logger = LoggerFactory.getLogger(TheMovieDb.class);
		logger.debug( "getFilmTheMovieDb : debut"); 
		//String sURL="https://api.themoviedb.org/3/movie/603-the-matrix?api_key=bd5b73151b4a5a2ac5b34aca8bfe555a&language=en-US";
		//String imageRetrun="";
		Film filmRico = null;
		List<Film> filmList= new ArrayList<Film>();
		try {
			film=   URLEncoder.encode(film, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error("{}",e1);
		}
		//String sURL = "https://api.themoviedb.org/3/search/movie?api_key=bd5b73151b4a5a2ac5b34aca8bfe555a&language=en-US&query="+film+"&page=1&include_adult=false";
		String sURL = "https://api.themoviedb.org/3/search/movie?api_key=bd5b73151b4a5a2ac5b34aca8bfe555a&language=FR&query="+film+"&page=1&include_adult=false";
		
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
			System.out.println("total_results="+total_results);
			
			// loop array
            JSONArray films = (JSONArray) jsonObject.get("results");
            
            GsonBuilder gsonBuilder=  new GsonBuilder();
        	gsonBuilder.setDateFormat("yyyy-MM-dd");
        	Gson gson =gsonBuilder.create();
        	
            for(int i=0; i<films.size(); i++){
                JSONObject objFilm =(JSONObject)films.get(i);// films.getJSONObject(i);
                //System.out.println("Film "+i);                
                
                //On ne prend que la premier
                //if (i==0) {
                	//imageRetrun=poster_path;                	
                	String a_jason_string=objFilm.toJSONString();  
                	//filmRico.setRelease_date(release_date);
                 	filmRico = gson.fromJson(a_jason_string, Film.class);
                 	filmList.add(filmRico);
                //}
                /*
                long id = (Long)objFilm.get("id");
                String original_title = (String)objFilm.get("original_title");
                String title = (String)objFilm.get("title");
                String poster_path=(String)objFilm.get("poster_path");
                String   release_date= (String)objFilm.get("release_date");
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
			logger.error("{}",e);
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
		Logger logger = LoggerFactory.getLogger(TheMovieDb.class);
		logger.debug( "getFilmTheMovieDbImage : debut"); 
		//https://api.themoviedb.org/3/movie/603-the-matrix/images?api_key=bd5b73151b4a5a2ac5b34aca8bfe555a&language=en-US&include_image_language=FR
		String sURL = "https://api.themoviedb.org/3/movie/"+filmId+"/images?api_key=bd5b73151b4a5a2ac5b34aca8bfe555a&language=en-US&include_image_language=FR,EN,null";
		logger.debug("getFilmTheMovieDbImage : sURL="+sURL);
		String sReturn= UrlManager.getUrl( sURL);
		logger.debug("getFilmTheMovieDbImage : sReturn="+sReturn);
		JSONObject objFilmImage=null;
			
		try {
			
			
			//Creation du parser
            JSONParser parser = new JSONParser();
   		 	Object obj;		    
   		 	
   		 	//parse su JSON retroun�
			obj = parser.parse(sReturn);			
			JSONObject jsonObject = (JSONObject) obj;
			//System.out.println("testObjetJSONJSONObject=");
			//System.out.println(jsonObject);
						
			//Reccuperation dans un tableau de la partie POSTERS
            JSONArray filmsImage = (JSONArray) jsonObject.get("posters");
			
			
            //On prende la premi�re image ! !uniquement
            if (filmsImage.size()>0) {
            	objFilmImage =(JSONObject)filmsImage.get(0);// films.getJSONObject(i);
            } else { //si pas de poster => reccuperation dans  "backdrops"
            	filmsImage = (JSONArray) jsonObject.get("backdrops");
            	if (filmsImage.size()>0) {
            		objFilmImage =(JSONObject)filmsImage.get(0);// films.getJSONObject(i);
            	} else {            	
            		filmsImage=null;
            	}
            }
             
                                                	         
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("{}",e);
		}
		return objFilmImage;
         
	}
	
	
	/*
	 * Recherche du Json du detail d'un film
	 */
	 public Document getFilmTheMovieDbDetail(long  filmId ) {
		Logger logger = LoggerFactory.getLogger(TheMovieDb.class);
		logger.debug( "getFilmTheMovieDbDetail : debut"); 
		//String sURL="https://api.themoviedb.org/3/movie/603?api_key=bd5b73151b4a5a2ac5b34aca8bfe555a&append_to_response=credits,videos"
		String sURL = "https://api.themoviedb.org/3/movie/"+filmId+"?api_key=bd5b73151b4a5a2ac5b34aca8bfe555a&language=fr-FR&append_to_response=credits,videos";
		logger.debug("sURL="+sURL);
		String sReturn= UrlManager.getUrl( sURL);
		logger.debug("sReturn="+sReturn);
		Document obj=null;
			
		try {
			/* 
			sReturn = new String( sReturn);
			obj = (DBObject) JSON.parse(sReturn);
			*/
			obj = Document.parse(sReturn);
						
			
			//System.out.println("testObjetJSONJSONObject=");
			logger.debug("{}",obj);
			
			//DBObject obj = (DBObject) JSON.parse("sample_json");
                                                	         
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("{}",e);
		}
		return obj;
         
	} 
	 
	/*
	 * Recherche du Json du detail d'un film (identique � getFilmTheMovieDbDetail mais revoi un JSONObject
	 * https://developers.themoviedb.org/3/movies/get-movie-details
	 */
	public static JSONObject getFilmTheMovieDbDetailJson(long filmId) {
			Logger logger = LoggerFactory.getLogger(TheMovieDb.class);
			logger.debug( "getFilmTheMovieDbDetailJson : debut");
			//String sURL="https://api.themoviedb.org/3/movie/603?api_key=bd5b73151b4a5a2ac5b34aca8bfe555a&append_to_response=credits,videos"
			String sURL = "https://api.themoviedb.org/3/movie/"+filmId+"?api_key=bd5b73151b4a5a2ac5b34aca8bfe555a&language=fr-FR&append_to_response=credits,videos";
			logger.info("sURL="+sURL);
			String sReturn= UrlManager.getUrl( sURL);
			logger.debug("sReturn="+sReturn);
			JSONObject objRequest=null;
				
			 JSONParser parser = new JSONParser();
			 Object obj;
			 try {			
				 obj = parser.parse(sReturn);		
				 objRequest =(JSONObject)obj;							
				 logger.debug("{}",obj);					                                                	      
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("{}",e);
			}
			return objRequest;	         
		}
	
/**
 * Download d'un fichier image poster du film	
 * @param image
 * @param path
 * @param destinationFile
 */
	 void downloadImage(String image,String path,String destinationFile) {
		Logger logger = LoggerFactory.getLogger(TheMovieDb.class);
		logger.debug( "downloadImage : debut"); 
		/*11/01/2020 l'url a changer
		--image 'petite'
		https://image.tmdb.org/t/p/w300_and_h450_bestv2/LCcZvB2Ynxg7JOQgviGwZ3l66L.jpg
		--image agrandie
		https://image.tmdb.org/t/p/w600_and_h900_bestv2/LCcZvB2Ynxg7JOQgviGwZ3l66L.jpg
		*/
		//String uRL="https://image.tmdb.org/t/p/w640/";
		String uRL="https://image.tmdb.org/t/p/w600_and_h900_bestv2/";		
		
		String imageUrl =uRL + image;
		logger.debug("Download : "+imageUrl + "to "+path+destinationFile);
		try {
			UrlManager.saveImage(imageUrl, path+destinationFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

/*
public void closeMongoClient() {
	// TODO Auto-generated method stub
	this.mongoClient.close();
	throw new UnsupportedOperationException("Unimplemented method 'closeMongoClient'");
}
	*/
	
}
