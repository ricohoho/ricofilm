package ricohoho.mongo;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import ricohoho.themoviedb.LogText;

public class TestMongoManager {

	// Config local
	static String dbMongoHost="localhost";
	static String dbUSer ="";
	static String dbPAssword	=""	;
	
	
	static int dbMongoPort	=27017;
	static String dbMongoName	="ricofilm"		;
	
	
	//config distante
//	static String dbMongoHost="davic.mkdh.fr";
//	static String dbUSer ="ricoAdmin";
//	static String dbPAssword	="rineka5993"		;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		MongoManager mongoManager=null;
       	//mongoManager=new MongoManager("test1");
		mongoManager=new MongoManager(dbMongoHost,dbMongoPort,dbMongoName,dbUSer,dbPAssword);
		
       	select(mongoManager) ;
		//add(mongoManager);
       	//update(mongoManager);
       	//updateArrayAdd(mongoManager);
       	//removeDBArray(mongoManager) ;
       	//removeDBArray2(mongoManager) ;
       	//arrayListITem(mongoManager) ;
       	//arrayListITemFind(mongoManager);
       	//arrayListITemUpdate(mongoManager);
       	//arrayListITemFind2(mongoManager);

		updateArrayAdd2(mongoManager);
       	//updateFIlmDateDb(mongoManager);
	}
	
	
	//Mise a jour de la date UPDATE_DB_DATE par la date max dateFile, afin de pourvoir 
	//ulrterieurement trier par ce champs : les derniers films ajoute
	static void updateFIlmDateDb(MongoManager mongoManager) {			
		 //List<DBObject> myList = null;
	     //DBCursor myCursor=myCollection.find().sort(new BasicDBObject("date",-1)).limit(10);
	     //myList = myCursor.toArray();
		String collectionName="films";
		mongoManager.aggregationTest(collectionName);

	}

	/*	
	 * db.collection.update({_id:1},{$push:{scores:{type:"quiz", score:99}}})
	 * 
	 * collection.update("{_id:1}").with("{$push:{scores:{type:#, score:#}}}", "quiz", 99);
	 * 
	 * @param mongoManager
	 */	
	static void select(MongoManager mongoManager) {
		String collectionName="films";
		BasicDBObject query = new BasicDBObject();
    	//query.put("id", 125742);
    	query.put("RICO_FICHIER.serveur_name", "davic.mkdh.fr");
    	//exemple d'utilisation d'un REGEX de recherhe : tt les ficl auant un path like '/streaming/films/%/
    	//reqete RobetMongo : db.getCollection('films').find({"RICO_FICHIER.path":{'$regex' : '/streaming/films/.+/', '$options' : 'i'}})
    	//Exemple : https://avaldes.com/mongodb-java-using-find-and-query-operations-example-tutorial/
    	query.put("RICO_FICHIER.path", new BasicDBObject("$regex","/streaming/films/.+/"));

		//Bson filter = eq("id", 256316);
		//query.put("id", new BasicDBObject("$regex","/streaming/films/.+/"));
    	
    	BasicDBObject fields = new BasicDBObject();  
	    fields.put("original_title", "");
	    List<Document> arrayItem = mongoManager.selectDBDoc( collectionName,  query ) ;
	    //int nb = mongoManager.selectDB( collectionName,  query , fields) ;
	    System.out.println("Select : "+arrayItem.size());
	    //System.out.println("Select : "+nb);
		for (Document doc : arrayItem ) {
			int dod_id = doc.getInteger("id");
			System.out.println("dod_id="+dod_id);
		}
	}
	
	/**
	 * 
	 * @param mongoManager
	 */
	static void  removeDBArray(MongoManager mongoManager) {
		
		System.out.println(" debut removeDBArray");
		String collectionName="films";
		BasicDBObject query = new BasicDBObject();
    	query.put("id", "file2");
		String arrayName="menuitem";
		
		String deleteItemParamName="value";
		String delteItemValeur="New";
		mongoManager.arrayRemoveItem(collectionName, query, arrayName, deleteItemParamName,delteItemValeur);
	}
	

	
	static void  removeDBArray2(MongoManager mongoManager) {	
		System.out.println(" debut removeDBArray2 : test de suppression d'item de array de film");
		String collectionName="films_test";
		BasicDBObject query = new BasicDBObject();
    	query.put("id", 256316);
		String arrayName="RICO_FICHIER";
		
		ArrayList<String> findItemParamName= new ArrayList<String>(Arrays.asList("path","file"));
		ArrayList<String>  findItemValeur= new ArrayList<String>(Arrays.asList("/coucou/","Tabloid.Truth.2014.720p.HDRip.AVC.asiacinephage.mp4"));
		
		//ArrayList<String> findItemParamName= new ArrayList<String>(Arrays.asList("file"));
		//ArrayList<String>  findItemValeur= new ArrayList<String>(Arrays.asList("Tabloid.Truth.2014.720p.HDRip.AVC.asiacinephage.mp4"));
		
		mongoManager.arrayRemoveItem(collectionName, query, arrayName, findItemParamName,findItemValeur);
		//mongoManager.arrayRemoveItem(collectionName, query, arrayName, "file","Tabloid.Truth.2014.720p.HDRip.AVC.asiacinephage.mp4");
	}
	
	
	static void arrayListITem(MongoManager mongoManager) {
		Bson filter = eq("id", "file2");
		mongoManager.arrayListITem("films", filter,"menuitem");
	}
	
	
	static void arrayListITemFind(MongoManager mongoManager) {
		Bson filter = eq("id", "file2");
		List<Document> arrayItemFiltered = null;
		
		//ArrayList<String> filtreArray = new ArrayList<String>(Arrays.asList("value/X3","onclick/one2"));
		ArrayList<String> filtreArrayParam = new ArrayList<String>(Arrays.asList("value","onclick"));
		ArrayList<String> filtreArrayValue = new ArrayList<String>(Arrays.asList("X3","one2"));
			
		
		String arrayName  = "menuitem";
		arrayItemFiltered= mongoManager.arrayListITemFind("films", filter,arrayName,filtreArrayParam,filtreArrayValue);
		
		//arrayItemFiltered= mongoManager.arrayListITemFind("films", filter,"menuitem","value","X3");
		System.out.println("List Filtre");
		for (Document item : arrayItemFiltered ) {
			System.out.println("item"+item.toString());
		}
	};
	
	static void arrayListITemFind2(MongoManager mongoManager) {
	
		//Recherche si le fichier est le meme !! si non ajout du fichier
		String arrayName  = "RICO_FICHIER";
		Bson filter = eq("id", 125742);
		List<Document> arrayItemFiltered = null;		     						   
		String pathFilm="C:\\tempo\\test\\";
		String nomFichier="10 eme Chambre, Instants d'Audience (2004) - Raymond Depardon - Copie.avi";
		ArrayList<String> filtreArray = new ArrayList<String>(Arrays.asList("path/"+pathFilm,"file/	"+nomFichier));		
		ArrayList<String> filtreArrayParam = new ArrayList<String>(Arrays.asList("path","file"));
		ArrayList<String> filtreArrayValue = new ArrayList<String>(Arrays.asList("pathFilm","nomFichier"));
			
		
		
		arrayItemFiltered= mongoManager.arrayListITemFind("films", filter,arrayName,filtreArrayParam,filtreArrayValue);
		//System.out.println("lliste filtre find "+arrayItemFiltered.size());
		//Si size =0 ==> insertion FILM existant , mais fichier different
	 	System.out.println("size:"+arrayItemFiltered.size());
	 	
	}
	
	static void  updateArrayAdd(MongoManager mongoManager) {

		
		//==init exemple ===
		String arrayName  = "menuitem";
		Bson filter = eq("id", "file2");
		
	    BasicDBObject obj1 = new BasicDBObject();
	    obj1.put("value","X3");
	    obj1.put("onclick", "one");

	    BasicDBObject obj2 = new BasicDBObject();
	    obj2.put("value","X4");
	    obj2.put("onclick", "two");

	    List<BasicDBObject> list = new ArrayList<>();
	    list.add(obj1); 
	    list.add(obj2); 
		
		mongoManager.arrayAddItem("films",filter,arrayName,list);
			
			
	}

	/**
	 * Exemple e creatin d'un doc avec un ARRAY qui conteint un ARRAY
	 * @param mongoManager
	 */
	static void  updateArrayAdd2(MongoManager mongoManager) {
		//==init exemple ===
		String arrayName  = "RICO_FICHIER";
		Bson filter = eq("id", 256316);


		Document str1 = new Document();
		str1.put("codec_type","VIDEO");
		str1.put("CODEC", "H260");

		Document str2 = new Document();
		str2.put("codec_type","AUDIO");
		str2.put("CODEC", "AAC3");

		List<Document> streams = new ArrayList<>();
		streams.add(str1);
		streams.add(str2);



		Document obj1 = new Document();
		obj1.put("serveur_name","NOS-RICOX");
		obj1.put("path", "/volume1/video/Films/2014/201409/");
		obj1.put("file", "Tabloid.Truth.2014.720p.HDRip.AVC.asiacinephage.mp4");
		obj1.put("streams", streams);


		Document obj2 = new Document();
		obj2.put("serveur_name","NOS-RICOX");
		obj2.put("path", "/volume1/video/Films/2014/201AAA/");
		obj2.put("file", "Tabloid.Truth.2014.720p.HDRip.AVC.asiacinephage2222.mp4");

		List<Document> list = new ArrayList<>();
		list.add(obj1);
		list.add(obj2);



		UpdateResult updateResult=  mongoManager.arrayAddItem2("films_test",filter,arrayName,list);
		System.out.println("getModifiedCount="+updateResult.getModifiedCount());
		System.out.println("getMatchedCount="+updateResult.getMatchedCount());


	}





	/**
	 * Modification d'une proporite d'un itme dans uen liste d'objet
	 * @param mongoManager
	 */
	static void arrayListITemUpdate(MongoManager mongoManager) {
		Bson filter = eq("id", "file2");
		List<Document> arrayItemFiltered = null;
		String collectionName="films";
		String  arrayName="menuitem";
		String findItemParamName="value";
		String findItemValeur="X3";
		String updateParamName="onclick" ;
		String updateParamValue="one2";
		mongoManager.arrayListITemUpdate( collectionName, filter,  arrayName, findItemParamName, findItemValeur, updateParamName, updateParamValue) ;		
	}
	
	
	
	/*
	 * simple update
	 */
	static void  update(MongoManager mongoManager) {
		BasicDBObject query = new BasicDBObject();
	    	query.put("id", "file2");
	    	BasicDBObject newDocument = new BasicDBObject();
	    	
	    	BasicDBObject fields = new BasicDBObject();  
	    	fields.put("id", 1);
   		    int i_nb_matchBD= mongoManager.selectDB("films", query, fields);
   		    System.out.println("i_nb_matchBD"+i_nb_matchBD);
	    	
	    	newDocument.put( "value" , "FileX");
			BasicDBObject updateObj = new BasicDBObject();
			updateObj.put("$set", newDocument);
			mongoManager.updateDB("films",query,updateObj);
			System.out.println("Fun Update");
	}
	
	/**
	 * Add row
	 * @param mongoManager
	 */
	static void add(MongoManager mongoManager) {
		String sJSON="{\r\n" + 
				"  \"id\": \"file2\",\r\n" + 
				"  \"value\": \"File\",\r\n" +  
				"    \"menuitem\": [\r\n" + 
				"      {\"value\": \"New\", \"onclick\": \"CreateNewDoc()\"},\r\n" + 
				"      {\"value\": \"Open\", \"onclick\": \"OpenDoc()\"},\r\n" + 
				"      {\"value\": \"Close\", \"onclick\": \"CloseDoc()\"}\r\n" + 
				"    ]\r\n" + 
				"}";
			
		DBObject obj=null;
		
		try {
			
			obj = (DBObject) JSON.parse(sJSON);
			System.out.println(obj);
                                                	         
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
       	mongoManager.insertJSON("films",obj);
	}
}
