package weinianlim.screeningtest_9cat;

import android.os.AsyncTask;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.ArrayList;

import static java.util.Arrays.asList;

/**
 * This class uses the geospatial indexes and Queries to sort out the distance between othe people and the user.
 * After sorting out the distances, it retrieve other user's name.
 *
 * @author William Lim
 * @version 1.0
 * @since 2015-08-23
 */
public class GetDataAsyncTask extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {

    protected ArrayList<String> doInBackground(ArrayList<String>... params) {

        final ArrayList<String> output = new ArrayList<String>();

        final MongoClientURI uri  = new MongoClientURI("mongodb://will:will@ds057862.mongolab.com:57862/codingassignment");
        MongoClient client = new MongoClient(uri);
        MongoDatabase database = client.getDatabase("codingassignment");
        MongoCollection<Document> collection = database.getCollection("myCollection");
        // Using geoNear command to calculate the distance
        collection.createIndex(new Document("loc", "2dsphere"));
        AggregateIterable<Document> iterable1 = collection.aggregate(asList(new Document("$geoNear", new Document().append("near",
                        new Document().append("type", "Point").append("coordinates", asList(Double.parseDouble(params[0].get(0)),
                                Double.parseDouble(params[0].get(1))))).append("distanceField", "dist.calculated")
                                .append("spherical", true).append("includeLocs", "dist.location").append("num", 10)),
                                            new Document("$sort", new Document("created_at", -1))));

        iterable1.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                output.add(String.valueOf(document.get("Name")));
                // The below commented line contains the distance calculated and coordinates as well
                // output.add(String.valueOf(document.get("dist")));
            }
        });

        return output;
    }

    @Override
    protected void onPostExecute(ArrayList<String> output) {
        super.onPostExecute(output);
    }
}

