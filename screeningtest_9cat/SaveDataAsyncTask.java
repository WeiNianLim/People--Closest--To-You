package weinianlim.screeningtest_9cat;

import android.os.AsyncTask;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import static java.util.Arrays.asList;

/**
 * This class connects the appliction to mongolab online database and stores all the data in it.
 *
 * @author William Lim
 * @version 1.0
 * @since 2015-08-22
 */
public class SaveDataAsyncTask extends AsyncTask<String[], Void, Void> {

    @Override
    protected Void doInBackground(String[]... params) {

        String[] inputArray = params[0];
        final String[] name = new String[1];

        MongoClientURI uri  = new MongoClientURI("mongodb://will:will@ds057862.mongolab.com:57862/codingassignment");
        MongoClient client = new MongoClient(uri);
        MongoDatabase database = client.getDatabase("codingassignment");
        MongoCollection<Document> collection = database.getCollection("myCollection");
        FindIterable<Document> iterable = collection.find(new Document("Name", new Document("$eq",inputArray[0])));
        iterable.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                name[0] = String.valueOf(document.get("Name"));
            }
        });

        // This if-else statement check if the user's data is available in the database. If yes, user's information will be updated
        // else a new profile will be created
        if (name[0] != null && !name[0].isEmpty()) {

            collection.updateOne(new Document("Name", name[0]), new Document("$set", new Document().append("loc",new Document().append("type", "Point").append("coordinates",
                    asList(Double.parseDouble(inputArray[1]), Double.parseDouble(inputArray[2]))))));

        } else {

            collection.insertOne(new Document().append("Name", inputArray[0]).append("loc",
                    new Document().append("type", "Point").append("coordinates", asList(Double.parseDouble(inputArray[1]), Double.parseDouble(inputArray[2])))));

        }
        return null;
    }
}

