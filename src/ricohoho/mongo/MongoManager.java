package ricohoho.mongo; 

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;


public class MongoManager {
	
	//info complemantaire 
	//REchere des film avec un critere sans une array par exmple rico_film.path="xx"
	//https://docs.mongodb.com/v4.0/tutorial/query-array-of-documents/
	//db.getCollection('films').find( { 'RICO_FICHIER.path': { $eq:"C:\\tempo\\test\\" } })
	//en java
	//findIterable = collection.find(eq("RICO_FICHIER.path", "C:\\tempo\\test\\"));

	
	//DB db = null;
	public MongoDatabase database = null;
	private static final Logger logger = LoggerFactory.getLogger(MongoManager.class);
	public MongoClient mongoClient = null;

	
	public MongoManager(String dbMongoHost,int dbMongoPort,String dbMongoName) {
	 try (
			MongoClient mongoClient = MongoClients.create("mongodb://"+dbMongoHost+":"+dbMongoPort)) {
            //MongoDatabase database = mongoClient.getDatabase("ricofilm");
			this.database = mongoClient.getDatabase("ricofilm");
			this.mongoClient = mongoClient;
		} catch (Exception e) {					
			e.printStackTrace();
		}	
	}

	 /* 
	public void  MongoManagerX(String dbMongoHost,int dbMongoPort,String dbMongoName, String userName, String password) {		
		logger = LoggerFactory.getLogger(MongoManager.class);
		String userPassword="";
		if (!userName.equals("")) {
			userPassword=userName+":"+password+"@";
		}		
		String uriDbCnx = "mongodb://"+userPassword+dbMongoHost+":"+dbMongoPort+"/"+dbMongoName ;
		logger.debug("uriDbCnx = "+uriDbCnx );
		MongoClient mongoClient = new MongoClient(
				new MongoClientURI(uriDbCnx)
		);
		//========================================
		//Pour l'instant on utilise les deux objet
		// a terme tout migrer vers this.database
		//========================================		
		this.db= mongoClient.getDB(dbMongoName);
		this.database = mongoClient.getDatabase(dbMongoName);
	}
	*/

	public MongoManager(String dbMongoHost, int dbMongoPort, String dbMongoName, String userName, String password) {
        try {
            String userPassword = "";
            if (!userName.isEmpty()) {
                userPassword = userName + ":" + password + "@";
            }
            String uriDbCnx = "mongodb://" + userPassword + dbMongoHost + ":" + dbMongoPort + "/" + dbMongoName;
            System.out.println("uriDbCnx = " + uriDbCnx);

            MongoClient mongoClient = MongoClients.create(uriDbCnx);
            this.database = mongoClient.getDatabase(dbMongoName);
			this.mongoClient = mongoClient;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * Insertion d'un document
	 * @param collectionName
	 * @param _DBObject
	 */
	//deprecated DB
	/*
	public void insertJSON_XXXXX(String collectionName,DBObject  _DBObject) {
		 try {
			 DBCollection table =  this.db.getCollection(collectionName);
			 table.insert(_DBObject);
				logger.debug("Insert Done");
		    //} catch (UnknownHostException e) {
			//e.printStackTrace();
		 } catch (MongoException e) {
			e.printStackTrace();
			logger.error("Exception "+e.toString());
		 }
		
	}*/
	
	//20241019 ERic : nouvelle version avec Document
	public void insertJSON(String collectionName, Document document) {
    try {
        MongoCollection<Document> collection = this.database.getCollection(collectionName);
        collection.insertOne(document);
        logger.debug("Insert Done");
    } catch (Exception e) {
        e.printStackTrace();
        logger.error("Exception " + e.toString());
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
	public int selectDB_BSON(String collectionName, BasicDBObject whereQuery ,BasicDBObject fields) {
		//ef modif db par database
		    		    		    
		    MongoCollection<Document> collection = database.getCollection(collectionName);		    
		    FindIterable<Document> findIterable = collection.find(whereQuery);
		    findIterable.projection(fields);
		    MongoCursor<Document> cursorDatabase = findIterable.iterator();
		    int i_j= 0;
		    while (cursorDatabase.hasNext()) {
		        logger.debug(cursorDatabase.next().toString());
		        i_j++;
		    }
		  
		    return i_j;
	}
	
	//deprecated !!! DB
	/*
	public int selectDB(String collectionName, BasicDBObject whereQuery ,BasicDBObject fields) {
		//ef modif db par database
		    		    		    		    
		    DBCollection table = this.db.getCollection(collectionName);
		    DBCursor cursor = table.find(whereQuery, fields);
		    int i_i= 0;
		    while (cursor.hasNext()) {
		        logger.debug(cursor.next().toString());
		        i_i++;
		    }
		    
		    return i_i;
	}
	*/
	//20251019 ERic : nouvelle version avec Document
	public int selectDB(String collectionName, Document whereQuery, Document fields) {

 	if (database == null) throw new IllegalStateException("database is null");
    if (collectionName == null) throw new IllegalArgumentException("collectionName is null");
    if (whereQuery == null) throw new IllegalArgumentException("whereQuery is null");
    if (fields == null) throw new IllegalArgumentException("fields is null");
    	MongoCollection<Document> collection = this.database.getCollection(collectionName);
    	FindIterable<Document> cursor = collection.find(whereQuery).projection(fields);
    	int count = 0;
    	for (Document doc : cursor) {
       	 	logger.debug(doc.toJson());
			count++;
    	}
    	return count;
}

	/**
	 * Query de document
	 * @param collectionName
	 * @param whereQuery
	 * @return Liste des documents 
	 */
	public List<Document>  selectDBDoc(String collectionName, Document whereQuery) {
		logger.info("collectionName="+collectionName);
		logger.info("whereQuery="+whereQuery);
		//List<Document> arrayItem = null; 		
		MongoCollection<Document> collection = this.database.getCollection(collectionName);		
		List<Document> documentsRico = (List<Document>) collection.find(whereQuery).into(new ArrayList<Document>());		
		return documentsRico;
	}
	
	
	/*
	public WriteResult updateDB(String collectionName,BasicDBObject query, BasicDBObject updateObj) {
		DBCollection table = (DBCollection) this.db.getCollection(collectionName);
		com.mongodb.WriteResult wr = table.update(query, updateObj);
		logger.info("deleteDB() Resultat de l' update, nb de doc : "+wr.getN());
		return wr;		
	}
	*/
	/*
	public UpdateResult updateDB_old(String collectionName, Document query, Document updateObj) {
    	MongoCollection<Document> collection = this.database.getCollection(collectionName);
    	// L'opération moderne d'update nécessite l'utilisation de $set ou autre opérateur
    	Document updateOperation = new Document("$set", updateObj);
    	UpdateResult result = collection.updateMany(query, updateOperation);
    	logger.info("updateDB() Résultat de l'update, nb de doc : " + result.getModifiedCount());
    	return result;
	}*/

	public UpdateResult updateDB(String collectionName, Document query, Document updateOperation) {
    	MongoCollection<Document> collection = this.database.getCollection(collectionName);
    	UpdateResult result = collection.updateMany(query, updateOperation);
    	logger.info("updateDB() Résultat de l'update, nb de doc : " + result.getModifiedCount());
    	return result;
	}
	
	/*
	public WriteResult deleteDB(String collectionName,BasicDBObject query) {		
		DBCollection table =  this.db.getCollection(collectionName);
		com.mongodb.WriteResult wr = table.remove(query);
		logger.info("deleteDB() Resultat de la suppression, nb de doc : "+wr.getN());
		return wr;		
	}
	*/
	//20241019 ERic : nouvelle version avec Document
	public DeleteResult deleteDB(String collectionName, Document query) {
    	MongoCollection<Document> collection = this.database.getCollection(collectionName);
    	DeleteResult result = collection.deleteMany(query);
    	logger.info("deleteDB() Résultat de la suppression, nb de doc : " + result.getDeletedCount());
    	return result;
}
	
	
	
	/** permet de creer ou d'ajouter des item a une liste dans JSON
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
	public void arrayAddItem(String collectionName,Bson filter,String arrayName,List<Document> list) {
		
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
	    collection.updateOne(filter, Updates.pushEach(arrayName, list));
	    
	}
	

	
	
	public UpdateResult arrayAddItem2(String collectionName,Bson filter,String arrayName,List<Document> list) {
		UpdateResult updateOne=null;
		try {
			System.out.println("arrayAddItem2 list="+list.toString());
			MongoCollection<Document> collection = this.database.getCollection(collectionName);
			updateOne = collection.updateOne(filter, Updates.pushEach(arrayName, list));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Excepiotn : "+e);
		}
	    return updateOne;
	}
	
	
	/**
	 * Suppresion d'un element d'une list
	 * @param collectionName : Nom de la collection
	 * @param query : Indentification du doc
	 * @param arrayName : Nom de la liste
	 * @param deleteItemParamName/deleteItemParamName  : IDentification del'item de la liste � supprimer
	 */
	public void arrayRemoveItem(String collectionName,Document query,String arrayName, String deleteItemParamName, String delteItemValeur) {
		//https://stackoverflow.com/questions/17061665/mongodb-remove-item-from-array
		MongoCollection<Document> collection = this.database.getCollection(collectionName);
		
		BasicDBObject obj1 = new BasicDBObject();
		obj1.put(deleteItemParamName,delteItemValeur);
		
		 UpdateResult _UpdateResult = collection.updateOne(query,new BasicDBObject("$pull", new BasicDBObject(arrayName, obj1))
				 
		);		 
		logger.info("_UpdateResult="+_UpdateResult);
	}
	
	/**
	 * Suppression d'un item d'un proporit� array d'un doc, � partir de plusioeurs valurs d'attribut
	 * @param collectionName
	 * @param query
	 * @param arrayName
	 * @param findItemParamName
	 * @param findItemValeur
	 */
	
	public void arrayRemoveItem(String collectionName,Document query,String arrayName, ArrayList<String> findItemParamName,ArrayList<String>  findItemValeur) {
		//https://stackoverflow.com/questions/17061665/mongodb-remove-item-from-array
		logger.debug("collectionName/arrayName="+collectionName+"/"+arrayName);
		
		MongoCollection<Document> collection = this.database.getCollection(collectionName);		
		BasicDBObject obj1 = new BasicDBObject();
		//obj1.put(deleteItemParamName,delteItemValeur);
		
		String deleteItemParamName="";
		String delteItemValeur="";
		int i_i=0;
		for (String paramName : findItemParamName) {	
			deleteItemParamName = paramName.trim();					
			delteItemValeur = findItemValeur.get(i_i).trim();
			logger.debug("deleteItemParamName/delteItemValeur:"+deleteItemParamName+"/"+delteItemValeur);		
			obj1.put(deleteItemParamName,delteItemValeur);
			i_i++;
		}	
		
		
		 UpdateResult _UpdateResult = collection.updateOne(query,new BasicDBObject("$pull", new BasicDBObject(arrayName, obj1))
				 
		);		 
		logger.info("_UpdateResult="+_UpdateResult);
	}
	
	
	
	/**
	 * 
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
		logger.info("_UpdateResult="+_UpdateResult);
	}
	
/**
 *  Renvoi la liste des ITEM d'une liste d'un document
 * @param collectionName
 * @param filter = filtrage du document
 * @param ArrayName
 * @return
 */
	public List<Document> arrayListITem(String collectionName,Bson filter, String ArrayName) {
		
		List<Document> arrayItem = null; 
		//System.out.println("arrayListItem Debut");		
		MongoCollection<Document> collection = this.database.getCollection(collectionName);	
		List<Document> documentsRico = (List<Document>) collection.find(filter).into(new ArrayList<Document>());	
		
		//System.out.println("documentsRico size "+documentsRico.size());
		for (Document docRico : documentsRico) {
			//String value=docRico.getString("value");
			//System.out.println("Value="+value);			
			arrayItem = (List<Document>) docRico.get(ArrayName);
			
			for (Document item : arrayItem) {
				System.out.println("arrayListITem : menu onclick = " + item.getString("onclick"));
			}
			
		}
		//System.out.println("arrayListItem Fin");
		
		return arrayItem ;		
	}
	
	/**
	 * ===> Renvoi les items d'un array filtr�s ( par findItemParamName , findItemValeur) pour des ou 1 doc filtr�s par filter dans une collection : collectionName
	 * @param collectionName
	 * @param filter
	 * @param ArrayName
	 * @return
	 */
	public List<Document> arrayListITemFind(String collectionName,Bson filter, String ArrayName, String findItemParamName, String findItemValeur) {
	    
		logger.debug("arrayListITemFind Debut");
		logger.debug("arrayListITemFind Filtrage sur document "+filter+" dans list :"+ArrayName+" :  "+findItemParamName +"="+findItemValeur);
		List<Document> arrayItemFiltered = new ArrayList<Document>(); 
		List<Document> arrayItem  = arrayListITem(collectionName,filter, ArrayName);
		
	    //obj1.put("value","X1");
	    //obj1.put("onclick", "one");
		for (Document item : arrayItem) {
			logger.debug("arrayListITemFind : menu ("+findItemParamName+") = " + item.getString(findItemParamName));
			if (item.getString(findItemParamName).equals(findItemValeur)) {
				arrayItemFiltered.add(item);
			}
		}
	
		logger.debug("arrayListITemFind Fin");
		return arrayItemFiltered;
		
	}
	
	/**
	 *  Cette fct recherche les item d'une array qui match les parametres se trouvant dans findItemParamName_findItemValeur
	 * @param collectionName
	 * @param filter
	 * @param ArrayName
	 * @param findItemParamName  : list des nom des cl� 
	 * @param findItemValeur : liste des nom des valeurs
	 * @return
	 */
	public List<Document> arrayListITemFind(String collectionName,Bson filter, String ArrayName, ArrayList<String> findItemParamName,ArrayList<String>  findItemValeur) {
	    
		//System.out.println("arrayListITemFind Debut");
		
		List<Document> arrayItemFiltered = new ArrayList<Document>(); 
		List<Document> arrayItem  = arrayListITem(collectionName,filter, ArrayName);
		
		boolean bParamCheck=true;
		//String paramName="";
		String valeurName="";
		int i_i;
		
		if (arrayItem != null) {
			for (Document item : arrayItem) {
				bParamCheck=true;
				//System.out.println("fil file=[" + item.getString("file")+"]");
				i_i=0;
				for (String paramName : findItemParamName) {	
					paramName = paramName.trim();					
					valeurName = findItemValeur.get(i_i).trim();
					logger.debug("paramName_Valeur=["+paramName+"]="+valeurName+"<=>"+item.getString(paramName)+"]");
					//System.out.println("Check="+item.getString(paramName).equals(valeurName));
					
					bParamCheck= bParamCheck && item.getString(paramName).equals(valeurName);
					i_i++;
				}				
				//System.out.println("bParamCheck:"+bParamCheck); 
				//On ajoute le fichier trouv� dans la liste d'ITEM
				if (bParamCheck) {
					arrayItemFiltered.add(item);
				}
			}
		}
	
		//System.out.println("Nbre d'elt de la liste qui match:"+arrayItemFiltered.size());
		
		return arrayItemFiltered;
		
	}
	
	
	
	/**
	 * Mise a jour de propri�t� (updateParamName avec la valeur updateParamValue) d'item d'array filtr�r (findItemParamName, String findItemValeur) d'un document. 
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
			//2 : modification de la bonne propi�ty�
			item.remove(updateParamName);
			item.append(updateParamName, updateParamValue);
			//3 : suppression de l'item dans la base
			arrayRemoveItem2(collectionName, filter, arrayName, findItemParamName, findItemValeur);
		}
		//3  : ajout des item modifi�s  dans la base
		arrayAddItem2(collectionName, filter, arrayName, arrayItemFiltered);		
		
	}
	
	
	//M%Ethode d'affichage .. utilisation pour les aggregations
	 Block<Document> printBlock = new Block<Document>() {
	        @Override
	        public void apply(final Document document) {
	            System.out.println(document.toJson());
	        }
	  };
	
	
	  
	  //Mise a jour de la date UPDATE_DB_DATE par la date max dateFile, afin de pourvoir 
	  //ulrterieurement trier par ce champs : les derniers films ajout�
	  public void aggregationTest(String collectionName) {
		  MongoCollection<Document> collection = database.getCollection(collectionName);
		
		  
		  /* => syntax ok
		  collection.aggregate(
			      Arrays.asList(
			          Aggregates.project(
			              Projections.fields(
			                    Projections.excludeId(),
			                    Projections.include("original_title"),
			                    Projections.computed(
			                            "RICO_FICHIERX",
			                            //new Document("$arrayElemAt", Arrays.asList("$RICO_FICHIER", 0))
			                            new Document("$max", Arrays.asList("$RICO_FICHIER", 0))
			                    )
			              )
			          )
			      )
			).forEach(printBlock);
			*/		
		  
		  
		  //1) Liste de tt les film avec ID zet la liste des dates RICO_FICHIER.fileDate
			AggregateIterable<Document> aggregate = collection.aggregate(
				  Arrays.asList(
				          //Aggregates.match(Filters.eq("categories", "Bakery")),
				          Aggregates.group("$id", Accumulators.max("max","$RICO_FICHIER.fileDate"))
				  )
				);
		  
			 //MongoCursor<Document> cursor = aggregate.iterator();
			 
			  //2) On parcour tt les doc et on reccup�re la date MAx ! 
			 // Print for demo
			 BasicDBObject query = null;
			 BasicDBObject newDocument = null;
			 BasicDBObject updateObj = null;
			 Date datMax = null;
			 for (Document dbObject : aggregate)
			 {
			     System.out.println(dbObject);
			     //String  __id = dbObject.get("_id");
			     System.out.println("ID="+dbObject.get("_id"));


			    Integer filmRico_id=dbObject.getInteger("_id");
				System.out.println("Value="+filmRico_id);			
				List<Date> arrayItem = (List<Date>) dbObject.get("max");
				System.out.println("arrayItem="+  arrayItem );
				if (arrayItem != null &&  arrayItem.size()>0 ) {
					 for (Date _date : arrayItem) {
						 System.out.println("Date="+  _date );
					 }
					 datMax = Collections.max(arrayItem);
					 System.out.println("DateMAX="+datMax);
				} else {
					Calendar calendar = Calendar.getInstance();
					calendar.set(1900, 01, 01, 0, 0, 0);
					datMax = calendar.getTime();
				}
					 
				//3) Mise a jour de la base de donn�e
				query = new BasicDBObject();
				query.put("id", filmRico_id);
				newDocument = new BasicDBObject();
				newDocument.put("UPDATE_DB_DATE", datMax);			     			
				//newDocument.put("UPDATE_DB_DATE", listeFichier.get(i).dateFile);
				updateObj = new BasicDBObject();
				updateObj.put("$set", newDocument);
				Document queryDoc = new Document(query.toMap());
				Document updateObjDoc = new Document(updateObj.toMap());
				this.updateDB(collectionName,queryDoc,updateObjDoc);
				
			 }
			 			 		
	  }

	  public void close() {
		this.mongoClient.close();
	  }
	  
}



