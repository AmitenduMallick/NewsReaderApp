package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> titles=new ArrayList<>();
    ArrayList<String> links=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Downloader task=new Downloader();
        String result="";
        try{
            result=task.execute("https://newsapi.org/v2/top-headlines?sources=google-news-in&apiKey=f8979058916046329ac1f4ada7fc8dca").get();
        }catch (Exception e){
            e.printStackTrace();
        }

        ListView listView=findViewById(R.id.listView);
        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,titles);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getApplicationContext(),LinkViewMainActivity.class);
                intent.putExtra("links",links.get(position));
                startActivity(intent);
            }
        });

    }

    public class Downloader extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try{
                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while(data!=-1){
                    char current=(char)data;
                    result+=current;
                    data=reader.read();
                }
                JSONObject jsonObject=new JSONObject(result);
                String articleinfo=jsonObject.getString("articles");
                JSONArray articlearray=new JSONArray(articleinfo);
                String title="";
                String link="";
                for(int i=0;i<articlearray.length();i++){
                    JSONObject jsonPart=articlearray.getJSONObject(i);
                    title=jsonPart.getString("title");
                    link=jsonPart.getString("url");
                    titles.add(title);
                    links.add(link);
                }



                return result;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }
}