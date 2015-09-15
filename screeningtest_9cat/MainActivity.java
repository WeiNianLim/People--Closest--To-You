package weinianlim.screeningtest_9cat;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * This class triggers the process to retrieve data from mongolab and display
 * the top 5 people closest to the user.
 *
 * @author William Lim
 * @version 1.0
 * @since 2015-08-22
 */

public class MainActivity extends ActionBarActivity {

    TextView txtName1, txtName2, txtName3, txtWelcome,txtName4, txtName5;//, txtName6, txtName7, txtName8, txtName9, txtName10;
    ArrayList<String> arrayOutput = new ArrayList<String>();
    ArrayList<String> arrayInput = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle userInfo = getIntent().getExtras();
        txtWelcome = (TextView) findViewById(R.id.MAtxtWelcome);
        txtWelcome.setText("Welcome " + userInfo.getString("Name"));
        arrayInput.add(userInfo.getString("Longitude"));
        arrayInput.add(userInfo.getString("Latitude"));
        GetDataMongoDB();
    }

   private void GetDataMongoDB() {

       GetDataAsyncTask task = new GetDataAsyncTask();

       try {

           arrayOutput = task.execute(arrayInput).get();

           txtName1 = (TextView) findViewById(R.id.MAtxtName1);
           txtName2 = (TextView) findViewById(R.id.MAtxtName2);
           txtName3 = (TextView) findViewById(R.id.MAtxtName3);
           txtName4 = (TextView) findViewById(R.id.MAtxtName4);
           txtName5 = (TextView) findViewById(R.id.MAtxtName5);

           txtName1.setText(arrayOutput.get(1));
           txtName2.setText(arrayOutput.get(2));
           txtName3.setText(arrayOutput.get(3));
           txtName4.setText(arrayOutput.get(4));
           txtName5.setText(arrayOutput.get(5));

       } catch (InterruptedException e) {
           e.printStackTrace();
       } catch (ExecutionException e) {
           e.printStackTrace();
       }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
