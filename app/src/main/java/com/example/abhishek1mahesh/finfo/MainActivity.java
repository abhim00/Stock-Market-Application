package com.example.abhishek1mahesh.finfo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import org.apache.commons.lang3.StringUtils;


/*
*
* This is a stock market application. It receives information received from an API and displays it on the screen.
 * There is use of a custom adapter and fragment to display the information. You can search up a stock and add it on the top.
 * You can swipe to delete a stock you dont want. For additional information click on a stock in the list view and see additional
  * info in the fragment below.
  *
  *
  * Unfortunately: until today morning (5/18) , the free API I was using is now discontinued- so the information that would be downloaded
  * is not working. So instead to show that the app works I created a CSVFile and am reading from the CSVFile to demonstrate how the app
  * would work. When I was writing to the file, I saved the actual link that contained the User's previously added stocks.
  * And on delete I edited the link and saved it to change the User's wanted stocks. Because the link no longer works, that functionality
  * is appearedly not there, but if you see the code, you will see that it is handled. Similarly, because the CSVFile is hardcoded,
  * you cannot request new stocks through the section on the top. But once again if you see the link it works.
*
*
*
*
*
* */


public class MainActivity extends AppCompatActivity {

    //WIDGETS

    Button addButton;
    EditText enterStockField;
    TextView recentTextView;

    ListView listView;
    int listViewPosition;


    private static final String STOCKLOG = "STOCKLOG" ;
    URL url;
    InputStream inputStream;
    URLConnection connection;

    String wantedSecurities;
    String recentStockName;
    String tempString;
    ArrayList<Security> securities = new ArrayList<Security>();
    ArrayList<String> tempsecurities = new ArrayList<String>();

    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;
    BottomFragment bottomFragment;


    String apiUrlPiece = "GOOG+YHOO+VZ+CVA+AET";

    String filename = "stockListFile.json";
    String brString = "";

    List<String[]> newARR;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.layout_activity_main);
        tempsecurities.add("GOOG");
        tempsecurities.add("YHOO");
        tempsecurities.add("VZ");
        tempsecurities.add("CVA");
        tempsecurities.add("AET");



        GetStockInformation stockInfoThread = new GetStockInformation();
        stockInfoThread.execute();



        listViewPosition = 0;

        listView = (ListView)findViewById(R.id.listView);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        bottomFragment = new BottomFragment();
        fragmentTransaction.add(R.id.bottom_activity_main_layout,bottomFragment, "bottomFragment");
        fragmentTransaction.commit();

        //CREATE ADAPTER
        final CustomAdapter customAdapter = new CustomAdapter(this, R.layout.layout_custom_listview_item, securities);
        //SET ADAPTER
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                listViewPosition = position;
                BottomFragment frag = (BottomFragment)getSupportFragmentManager().findFragmentByTag("bottomFragment");
                frag.setFragmentTextViews();
            }
        });
        Log.d("FRAG",securities.size()+"");

        enterStockField = (EditText)findViewById(R.id.addSecuritiesTextField_id);
        addButton = (Button)findViewById(R.id.addnewbutton_id);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiUrlPiece = apiUrlPiece+"+"+enterStockField.getText();
                customAdapter.clear();
                customAdapter.notifyDataSetChanged();
                GetStockInformation stockInfoThread = new GetStockInformation();
                stockInfoThread.execute();

                Toast.makeText(MainActivity.this, "Added at Bottom", Toast.LENGTH_SHORT).show();


                Log.d("APIURL",apiUrlPiece);
            }
        });

        /*
        * SWIPE DELETE LISTVIEW
        */

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                Log.d("INLOOP","INLOOP");
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    String s = securities.get(position).getSymbol();
                                    apiUrlPiece = apiUrlPiece.replace(s+"+","");
                                    Log.d("APIURLDEL",apiUrlPiece+"-------SYMBOL IS "+s+"-----Position is "+position);
                                    newARR.remove(0);
                                    securities.remove(position);
                                    customAdapter.notifyDataSetChanged();


                                }


                            }
                        });
        listView.setOnTouchListener(touchListener);



    }

    /*
    *
    * ONCREATE ENDS HERE
    *
    *
    *
    *
    *
    *
    * CUSTOM ADAPTER STARTS HERE
    *
    *
    * */

    public class CustomAdapter extends ArrayAdapter<Security> {
        Context mainActivityContext;
        int layoutId;
        List<Security> securityList;

        public CustomAdapter(Context context, int resource, List<Security> objects) {
            super(context, resource, objects);
            mainActivityContext = context;
            layoutId = resource;
            securityList = objects;

        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater =
                    (LayoutInflater)mainActivityContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            View adapterLayout = layoutInflater.inflate(layoutId,null);

            TextView nameText = (TextView)adapterLayout.findViewById(R.id.securityNameTextView);
            TextView percentChangeText = (TextView)adapterLayout.findViewById(R.id.percentChangeTextView);
            TextView askText = (TextView)adapterLayout.findViewById(R.id.asktextView);

            nameText.setTextColor(Color.WHITE);
            percentChangeText.setTextColor(Color.WHITE);
            askText.setTextColor(Color.WHITE);
            //Log.d("FFG",securityList.get(position).getAsk()+"   "+position);

            if (securityList.get(position).getChange().contains("+")){
                percentChangeText.setTextColor(Color.GREEN);
            }else percentChangeText.setTextColor(Color.RED);
            nameText.setText(securityList.get(position).getName());
            percentChangeText.setText(securityList.get(position).getChange());
            askText.setText(securityList.get(position).getAsk());



            return adapterLayout;
        }
    }

    /*
    *
    *
    * CUSTOM ADAPTER ENDS HERE
    *
    *
    *
    *
    * ASYNC THREAD STARTS HERE
    *
    *
    *
    * */


    public class GetStockInformation extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
//http://download.finance.yahoo.com/d/quotes.csv?s=AAPL&f=nacohgvra2s
            try {
                url = new URL("http://download.finance.yahoo.com/d/quotes.csv?s="+apiUrlPiece+"&f=nl1cohgvra2s");
                Log.d("APIURL",apiUrlPiece);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                connection = url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                //inputStream = connection.getInputStream();
                inputStream = getResources().openRawResource(R.raw.quotes4);
                CSVFile csvFile = new CSVFile(inputStream);
                newARR = csvFile.read();
                for (int x = 0; x<newARR.size(); x++)
                {
                    String[] mostRecentArray = newARR.get(x);
                    Security security = new Security(mostRecentArray[0],mostRecentArray[1],mostRecentArray[2],mostRecentArray[3],
                            mostRecentArray[4],mostRecentArray[5],mostRecentArray[6],mostRecentArray[7],mostRecentArray[8],mostRecentArray[9]);



                    securities.add(0,security);
                    Log.d("SEC",securities.get(x).getSymbol());

                }
                Log.d("INPUT",inputStream+"");
            } catch (Exception e) {
                e.printStackTrace();
            }




//            try {
//                /*BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//
//                CSVReader reader = new CSVReader(bufferedReader);
//                List<String[]> myResults = reader.readAll();
//                for (int x = 0; x<myResults.size(); x++)
//                {
//                    String[] mostRecentArray = myResults.get(x);
//                    Security security = new Security(mostRecentArray[0],mostRecentArray[1],mostRecentArray[2],mostRecentArray[3],
//                            mostRecentArray[4],mostRecentArray[5],mostRecentArray[6],mostRecentArray[7],mostRecentArray[8],mostRecentArray[9]);
//
//
//
//                    securities.add(0,security);
//                    Log.d("SEC",securities.get(x).getSymbol());
//
//                }*/
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


            // securities = new ArrayList<Security>();
            // Log.d(STOCKLOG,myResults.size()+" -->SIZE ");


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //recentTextView.setText(securities.get(0).getName());

            BottomFragment frag = (BottomFragment)getSupportFragmentManager().findFragmentByTag("bottomFragment");
            frag.setFragmentTextViews();







        }
    }

    /*
    *
    * ASYNC THREAD ENDS HERE
    * */

    public ArrayList<Security> returnSecurities(){
        return securities;
    }

    public int returnListViewPosition(){
        return listViewPosition;
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            OutputStreamWriter writer = new OutputStreamWriter(openFileOutput(filename, Context.MODE_WORLD_WRITEABLE));

            writer.write(apiUrlPiece);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput(filename)));
            while((brString = reader.readLine()) != null){
                apiUrlPiece = brString;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



