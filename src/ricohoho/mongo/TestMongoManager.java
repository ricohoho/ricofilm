package ricohoho.mongo;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import ricohoho.themoviedb.LogText;

public class TestMongoManager {

	// Config local
	//static String dbMongoHost="localhost";
	//static String dbUSer ="";
	//static String dbPAssword	=""	;
	static int dbMongoPort	=27017;
	static String dbMongoName	="ricofilm"		;
	
	
	//config distante
	static String dbMongoHost="davic.mkdh.fr";
	static String dbUSer ="ricoAdmin";
	static String dbPAssword	="rineka5993"		;
	
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
       	//arrayListITem(mongoManager) ;
       	//arrayListITemFind(mongoManager);
       	//arrayListITemUpdate(mongoManager);
       	arrayListITemFind2(mongoManager);
	}

	/**
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
    	query.put("RICO_FICHIER.serveur_name", "NOS-RICO");
    	query.put("RICO_FICHIER.path", "\\\\NOS-RICO\\video\\Films\\2019\\201904\\");
    	
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
	
	
	
	static void arrayListITem(MongoManager mongoManager) {
		Bson filter = eq("id", "file2");
		mongoManager.arrayListITem("films", filter,"menuitem");
	}
	
	
	static void arrayListITemFind(MongoManager mongoManager) {
		Bson filter = eq("id", "file2");
		List<Document> arrayItemFiltered = null;
		
		ArrayList<String> filtreArray = new ArrayList<String>(Arrays.asList("value/X3","onclick/one2"));
		
		String arrayName  = "menuitem";
		arrayItemFiltered= mongoManager.arrayListITemFind("films", filter,arrayName,filtreArray);
		
		//arrayItemFiltered= mongoManager.arrayListITemFind("films", filter,"menuitem","value","X3");
		System.out.println("List Filtré");
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
		String nomFichier="10ème Chambre, Instants d'Audience (2004) - Raymond Depardon - Copie.avi";
		ArrayList<String> filtreArray = new ArrayList<String>(Arrays.asList("path/"+pathFilm,"file/	"+nomFichier));		     						     				
		arrayItemFiltered= mongoManager.arrayListITemFind("films", filter,arrayName,filtreArray);
		//System.out.println("lliste filtre find "+arrayItemFiltered.size());
		//Si size =0 ==> insertion FILM existant , mais fichier différent 
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
	 * Modification d'une proporité d'un itme dans uen liste d'objet
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
