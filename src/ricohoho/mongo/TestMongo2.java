package ricohoho.mongo; 

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

public class TestMongo2 {
	
	
  public static void main(String[] args)
  {
    MongoClient mongoClient = new MongoClient();
    MongoDatabase database = mongoClient.getDatabase("pushExampleDb");
    MongoCollection<Document> collection = database.getCollection("pushExampleCollection");

    String sensorType = "Temperature"; 

    // try to load existing document from MongoDB
    Document document = collection.find(eq("Sensor Type", sensorType)).first();
    if(document == null)
    {
      // no test document, let's create one!
      document = new Document("Sensor Type", sensorType);

      // insert it into MongoDB
      collection.insertOne(document);

      // read it back from MongoDB
      document = collection.find(eq("Sensor Type", sensorType)).first();
    }

    // see what it looks like in JSON (on the first run you will notice that it has got an "_id" but no "Subscribed Topics" array yet)
    System.out.println(document.toJson());

    // update the document by adding an entry to the "Subscribed Topics" array
    Bson filter = eq("Sensor Type", sensorType);
    Bson change = push("Subscribed Topics", "Some Topic");
    collection.updateOne(filter, change);

    // read one more time from MongoDB
    document = collection.find(eq("Sensor Type", sensorType)).first();

    // see what the document looks like in JSON (on the first run you will notice that the "Subscribed Topics" array has been created and has got one element in it)
    System.out.println(document.toJson());

    mongoClient.close();
  }
}
