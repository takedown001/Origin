package com.origin.esports;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.origin.esports.Originconfig.URL;
import com.origin.esports.adapter.ItemAdapter;
import com.origin.esports.data.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewsActivity extends AppCompatActivity {


    public NewsActivity() {
        // Required empty public constructor
    }
    private ProgressDialog pDialog;
    private ArrayList<HashMap<String, String>> offersListNews;
    String title, descrion;
    RecyclerView recyclerView;
    JSONArray jsonArray = new JSONArray();
    private boolean error;
    JSONParserString jsonParser = new JSONParserString();
    private List<News> itemList = new ArrayList<>();
    private ItemAdapter itemAdapter;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.include);
        setSupportActionBar(toolbar);
        toolbar.setTitle((CharSequence) "News & Updates");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        itemList = new ArrayList();
        itemAdapter = new ItemAdapter(NewsActivity.this,itemList);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //     initData();
        recyclerView.setAdapter(itemAdapter);
        // Hashmap for ListView
        offersListNews = new ArrayList<>();



        new OneLoadAllProducts().execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    class OneLoadAllProducts extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewsActivity.this);
            pDialog.setMessage("Loading Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();


        }

        @Override
        protected String doInBackground(String... strings) {

           JSONObject params = new JSONObject();
            String rq = null;
            try {
                params.put("", "");
                rq = jsonParser.makeHttpRequest(URL.News, params);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return rq;
        }

        protected void onPostExecute(String s) {
            pDialog.dismiss();
            if (s == null || s.isEmpty()) {
                Toast.makeText(NewsActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                return;
            }
            try {

                JSONObject ack = new JSONObject(s);
                // Log.d("test", String.valueOf(ack));
                String decData = Helper.profileDecrypt(ack.get("Data").toString(), ack.get("Hash").toString());
                if (!Helper.verify(decData, ack.get("Sign").toString(), JSONParserString.publickey)) {
                    Toast.makeText(NewsActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    JSONObject json = new JSONObject(decData);
                    jsonArray = json.getJSONArray("news");
                    error = json.getBoolean("error");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<>();

                        map.put("title", c.getString("title"));
                        map.put("description", c.getString("description"));

                        offersListNews.add(map);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!error) {
                for (int i = 0; i < offersListNews.size(); i++) {
                    title = offersListNews.get(i).get("title");
                    descrion = offersListNews.get(i).get("description");
                    //  Log.d("title", title);
                    News newsAdapter = new News();
                    newsAdapter.setTitle(title);
                    newsAdapter.setDescription(descrion);
                    itemList.add(newsAdapter);
                    itemAdapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(NewsActivity.this, "something went wrong", Toast.LENGTH_SHORT);
            }
        }


    }
}
