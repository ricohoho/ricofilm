package ricohoho.tools;
//2024/10/27 Correction  Git



import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.json.simple.JSONObject;
import ricohoho.themoviedb.TheMovieDb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bson.Document;

public class CorrectionResumeEnFR {
    public static void main(String[] args) throws ParseException {
        // Connexion à la base de données MongoDB
        //String uri = "mongodb://localhost:27017";  // Remplace par ton URI MongoDB si nécessaire
        //MongoClient mongoClient = MongoClients.create(uri);

        String dbMongoHost="davic.mkdh.fr";
        String dbMongoPort="27017";
        String dbMongoName="ricofilm";
        String userName="ricoAdmin";
        String password="rineka5993";
        String userPassword="";
        if (!userName.equals("")) {
            userPassword=userName+":"+password+"@";
        }

        String uriDbCnx = "mongodb://"+userPassword+dbMongoHost+":"+dbMongoPort+"/"+dbMongoName ;
        System.out.println("uriDbCnx = " + uriDbCnx);
        MongoClient mongoClient = MongoClients.create(uriDbCnx);
        MongoDatabase database = mongoClient.getDatabase("ricofilm"); // Remplace par le nom de ta base de données
        MongoCollection<Document> collection = database.getCollection("films");

        // Définir la date "2023-11-17"
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateAjout = dateFormat.parse("2023-11-17");
        //dateAjout = dateFormat.parse("2022-12-16");

        MongoCursor<Document> cursor = collection.find(Filters.gte("UPDATE_DB_DATE", dateAjout)).iterator();


        // Parcours de tous les documents de la collection "Films"
//        MongoCursor<Document> cursor = collection.find().iterator();


        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                // Récupération de l'ID du film
                int filmId = document.getInteger("id",0);
                String overview = document.getString("overview");
                System.out.println("Processing film ID: " + filmId);

                //if (filmId==714339) {
                //recherche du nouvle Overview
                //JSONObject json;
                Document _DBObject;
                TheMovieDb theMovieDB = new TheMovieDb();
                _DBObject = theMovieDB.getFilmTheMovieDbDetail(filmId);
                // Mise  jour du champ "overview" avec la valeur "New"
                overview  = (String) _DBObject.get("overview");
                collection.updateOne(Filters.eq("id", filmId), Updates.set("overview", overview ));
                //}
                System.out.println("Updated film with ID: " + filmId);
            }
        } finally {
            cursor.close();
        }

        // Fermeture de la connexion MongoDB x
        mongoClient.close();
    }

}