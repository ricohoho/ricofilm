package ricohoho.mongo;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.push;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.DBCursor;

public class MongoManager {
	
	//			info complemantaire 
	//REchere des film avec un critere sans une array par exmple rico_film.path="xx"
	//https://docs.mongodb.com/v4.0/tutorial/query-array-of-documents/
	//db.getCollection('films').find( { 'RICO_FICHIER.path': { $eq:"C:\\tempo\\test\\" } })
	//en java
	//findIterable = collection.find(eq("RICO_FICHIER.path", "C:\\tempo\\test\\"));

	
	MongoClient mongoClient = null;
	String dbMongoName="";
	MongoClient mongo =null;
	DB db = null;
	MongoDatabase database = null;
	
	public MongoManager(String dbMongoName) {
		this.dbMongoName=dbMongoName;
		//this.mongo = new MongoClient("192.168.1.18", 27017);
		this.mongo = new MongoClient("127.0.0.1", 27017);
		this.db = mongo.getDB(dbMongoName);			
		MongoClient mongoClient = new MongoClient();
		this.database = mongoClient.getDatabase(dbMongoName);
	}
	
	
	/**
	 * Insertion d'un document
	 * @param collectionName
	 * @param _DBObject
	 */
	public void insertJSON(String collectionName,DBObject  _DBObject) {
		 try {
			 DBCollection table = this.db.getCollection(collectionName);
			 table.insert(_DBObject);
				System.out.println("Done");
		    //} catch (UnknownHostException e) {
			//e.printStackTrace();
		 } catch (MongoException e) {
			e.printStackTrace();
		 }
		
	}
	
	//
	/**
	 * Query de document
	 * @param collectionName
	 * @param whereQuery
	 * @param fields
	 * @return Nb de ligne qui match
	 */
	public int selectDB(String collectionName, BasicDBObject whereQuery ,BasicDBObject fields) {			  
		    DBCollection table = this.db.getCollection(collectionName);
		    DBCursor cursor = table.find(whereQuery, fields);
		    int i_i= 0;
		    while (cursor.hasNext()) {
		        System.out.println(cursor.next());
		        i_i++;
		    }
		    return i_i;
	}
	
	/**
	 * Query de document
	 * @param collectionName
	 * @param whereQuery
	 * @return Liste des documents 
	 */
	public List<Document>  selectDBDoc(String collectionName, BasicDBObject whereQuery) {
		List<Document> arrayItem = null; 		
		MongoCollection<Document> collection = this.database.getCollection(collectionName);
		
		List<Document> documentsRico = (List<Document>) collection.find(whereQuery).into(new ArrayList<Document>());
		
		return documentsRico;
	}
	
	
	/**
	 * Mise a jour documents
	 * @param collectionName
	 * @param query
	 * @param updateObj
	 */
	public WriteResult updateDB(String collectionName,BasicDBObject query, BasicDBObject updateObj) {
		DBCollection table = this.db.getCollection(collectionName);
		com.mongodb.WriteResult wr = table.update(query, updateObj);
		System.out.println("deleteDB() Resultat de l' update, nb de doc : "+wr.getN());
		return wr;		
	}
	
	/**
	 * Delete de documents 
	 * @param collectionName
	 * @param query
	 * @return
	 */
	public WriteResult deleteDB(String collectionName,BasicDBObject query) {
		DBCollection table = this.db.getCollection(collectionName);
		com.mongodb.WriteResult wr = table.remove(query);
		System.out.println("deleteDB() Resultat de la suppression, nb de doc : "+wr.getN());
		return wr;		
	}
	
	
	
	/** permet de créer ou d'ajouter des item a une liste dans JSON
	 * db.collection.update({_id:1},{$push:{scores:{type:"quiz", score:99}}})
	 * 
	 * collection.update("{_id:1}").with("{$push:{scores:{type:#, score:#}}}", "quiz", 99);
	 * 
	 * @param collectionName
	 * @param query
	 * @param updateObj
	 */
	
	/**
	 * Ajout d'un item dans un array d'un document
	 * @param collectionName
	 * @param filter
	 * @param arrayName
	 * @param list
	 */
	public void arrayAddItem(String collectionName,Bson filter,String arrayName,List<BasicDBObject> list) {
		
		//==init exemple ===
		/*
		arrayName  = "menuitem";
		filter = eq("id", "file2");
		
	    BasicDBObject obj1 = new BasicDBObject();
	    obj1.put("value","X1");
	    obj1.put("onclick", "one");

	    BasicDBObject obj2 = new BasicDBObject();
	    obj2.put("value","X2");
	    obj2.put("onclick", "two");

	    list = new ArrayList<>();
	    list.add(obj1); 
	    list.add(obj2);  
		*/
		
		MongoCollection<Document> collection = this.database.getCollection(collectionName);
		
		//Methode 1 ==
		//Bson filter = eq("id", "file2");
		//Bson change = push("Subscribed Topics", "Some Topic");
	    //collection.updateOne(filter, change);
	    
		//MEthode 2 ==
	    UpdateResult updateOne = collection.updateOne(filter, Updates.pushEach(arrayName, list));
	    
	}
	

	
	
	public UpdateResult arrayAddItem2(String collectionName,Bson filter,String arrayName,List<Document> list) {

		MongoCollection<Document> collection = this.database.getCollection(collectionName);		
	    UpdateResult updateOne = collection.updateOne(filter, Updates.pushEach(arrayName, list));
	    return updateOne;
	}
	
	
	/**
	 * Suppresion d'un element d'une list
	 * @param collectionName : Nom de la collection
	 * @param query : Indentification du doc
	 * @param arrayName : Nom de la liste
	 * @param deleteItemParamName/deleteItemParamName  : IDentification del'item de la liste à supprimer
	 */
	public void arrayRemoveItem(String collectionName,BasicDBObject query,String arrayName, String deleteItemParamName, String delteItemValeur) {
		//https://stackoverflow.com/questions/17061665/mongodb-remove-item-from-array
		MongoCollection<Document> collection = this.database.getCollection(collectionName);
		
		BasicDBObject obj1 = new BasicDBObject();
		    obj1.put(deleteItemParamName,delteItemValeur);
		
		 UpdateResult _UpdateResult = collection.updateOne(query,new BasicDBObject("$pull", new BasicDBObject(arrayName, obj1))
				 
		);		 
		System.out.println("_UpdateResult="+_UpdateResult);
	}
	
	
	/**
	 * Suppression d'un item d'un proporité array d'un doc
	 * @param collectionName
	 * @param query
	 * @param arrayName
	 * @param deleteItemParamName
	 * @param delteItemValeur
	 */
	public void arrayRemoveItem2(String collectionName,Bson query,String arrayName, String deleteItemParamName, String delteItemValeur) {
		//https://stackoverflow.com/questions/17061665/mongodb-remove-item-from-array
		MongoCollection<Document> collection = this.database.getCollection(collectionName);
		
		BasicDBObject obj1 = new BasicDBObject();
		    obj1.put(deleteItemParamName,delteItemValeur);
		
		 UpdateResult _UpdateResult = collection.updateOne(query,new BasicDBObject("$pull", new BasicDBObject(arrayName, obj1))
				 
		);		 
		System.out.println("_UpdateResult="+_UpdateResult);
	}
	
/**
 *  Renvoi la liste des ITEM d'une liste d'un document
 * @param collectionName
 * @param filter = filtrage du document
 * @param ArrayName
 * @return
 */
	public List<Document>   arrayListITem(String collectionName,Bson filter, String ArrayName) {
		
		List<Document> arrayItem = null; 
		//System.out.println("arrayListItem Debut");
		
		MongoCollection<Document> collection = this.database.getCollection(collectionName);
		
		List<Document> documentsRico = (List<Document>) collection.find(filter).into(new ArrayList<Document>());
		
		
		//System.out.println("documentsRico size "+documentsRico.size());
		for (Document docRico : documentsRico) {
			String value=docRico.getString("value");
			//System.out.println("Value="+value);			
			arrayItem = (List<Document>) docRico.get(ArrayName);
			/*
			for (Document item : arrayItem) {
				System.out.println("arrayListITem : menu onclick = " + item.getString("onclick"));
			}
			*/
		}
		//System.out.println("arrayListItem Fin");
		
		return arrayItem ;		
	}
	
	/**
	 * ===> Renvoi les items d'un array filtrés ( par findItemParamName , findItemValeur) pour des ou 1 doc filtrés par filter dans une collection : collectionName
	 * @param collectionName
	 * @param filter
	 * @param ArrayName
	 * @param paramName
	 * @param valeur
	 * @return
	 */
	public List<Document> arrayListITemFind(String collectionName,Bson filter, String ArrayName, String findItemParamName, String findItemValeur) {
	    
		System.out.println("arrayListITemFind Debut");
		System.out.println("arrayListITemFind Filtrage sur document "+filter+" dans list :"+ArrayName+" :  "+findItemParamName +"="+findItemValeur);
		List<Document> arrayItemFiltered = new ArrayList<Document>(); 
		List<Document> arrayItem  = arrayListITem(collectionName,filter, ArrayName);
		
	    //obj1.put("value","X1");
	    //obj1.put("onclick", "one");
		for (Document item : arrayItem) {
			System.out.println("arrayListITemFind : menu ("+findItemParamName+") = " + item.getString(findItemParamName));
			if (item.getString(findItemParamName).equals(findItemValeur)) {
				arrayItemFiltered.add(item);
			}
		}
	
		System.out.println("arrayListITemFind Fin");
		return arrayItemFiltered;
		
	}
	
	/**
	 *  Cette fct recherche les item d'une array qui match les parametres se trouvant dans findItemParamName_findItemValeur
	 * @param collectionName
	 * @param filter
	 * @param ArrayName
	 * @param findItemParamName_findItemValeur liste des "param,valeur"
	 * @return
	 */
	public List<Document> arrayListITemFind(String collectionName,Bson filter, String ArrayName, ArrayList<String> findItemParamName_findItemValeur) {
	    
		//System.out.println("arrayListITemFind Debut");
		
		List<Document> arrayItemFiltered = new ArrayList<Document>(); 
		List<Document> arrayItem  = arrayListITem(collectionName,filter, ArrayName);
		
		boolean bParamCheck=true;
		String paramName="";
		String valeurName="";
		

		for (Document item : arrayItem) {
			bParamCheck=true;
			//System.out.println("fil file=[" + item.getString("file")+"]");
			for (String paramName_Valeur : findItemParamName_findItemValeur) {						
				paramName=paramName_Valeur.split("/")[0].trim();
				valeurName=paramName_Valeur.split("/")[1].trim();
				//System.out.println("paramName_Valeur1="+paramName_Valeur);
				//System.out.println("paramName_Valeur=2["+valeurName.trim()+"]=["+item.getString(paramName)+"]");
				//System.out.println("Check="+item.getString(paramName).equals(valeurName));
				
				bParamCheck= bParamCheck && item.getString(paramName).equals(valeurName);
			}
			
			//System.out.println("bParamCheck:"+bParamCheck); 
			//On ajoute le fichier trouvé dans la liste d'ITEM
			if (bParamCheck) {
				arrayItemFiltered.add(item);
			}
		}
	
		//System.out.println("Nbre d'elt de la liste qui match:"+arrayItemFiltered.size());
		
		return arrayItemFiltered;
		
	}
	
	
	
	/**
	 * Mise a jour de propriété (updateParamName avec la valeur updateParamValue) d'item d'array filtrér (findItemParamName, String findItemValeur) d'un document. 
	 * @param collectionName
	 * @param filter
	 * @param arrayName
	 * @param findItemParamName
	 * @param findItemValeur
	 * @param updateParamName
	 * @param updateParamValue
	 */
	public void arrayListITemUpdate(String collectionName,Bson filter, String arrayName, String findItemParamName, String findItemValeur, String updateParamName, String updateParamValue) {
		//1 : Reccuepration des item a modifier
		List<Document> arrayItemFiltered = arrayListITemFind(collectionName, filter, arrayName, findItemParamName, findItemValeur);
		for (Document item : arrayItemFiltered) {
			//2 : modification de la bonne propiétyé
			item.remove(updateParamName);
			item.append(updateParamName, updateParamValue);
			//3 : suppression de l'item dans la base
			arrayRemoveItem2(collectionName, filter, arrayName, findItemParamName, findItemValeur);
		}
		//3  : ajout des item modifiés  dans la base
		arrayAddItem2(collectionName, filter, arrayName, arrayItemFiltered);		
		
	}
	
}



