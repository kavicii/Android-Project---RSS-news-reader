package com.jimmy.miniproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DrawerLayout dl;
    ActionBarDrawerToggle t;
    NavigationView nv;
    ListView lvRss;
    ArrayList<String> titles;
    ArrayList<String> links;
    ArrayList<String> description;
    ArrayList<String> newRss;
    ArrayList<String> newName;
    String rssUrl;
    int addTime = 0;
    SubMenu sMenu = null;
    String sBarTitle = "RSS reader";
    String barTitle;
    Boolean play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dl = (DrawerLayout)findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);
        lvRss = (ListView) findViewById(R.id.lv);

        titles = new ArrayList<String>();
        links = new ArrayList<String>();
        description = new ArrayList<String>();
        newRss = new ArrayList<String>();
        newName = new ArrayList<String>();

        dl.addDrawerListener(t);
        t.syncState();

        rssUrl = "http://rthk9.rthk.hk/rthk/news/rss/c_expressnews_cinternational.xml";
        barTitle =  sBarTitle;
        play = true;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView)findViewById(R.id.nv);


        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                String itemTitle = (String)item.getTitle();
                int id = item.getItemId();
                switch(id) {
                    case R.id.bbc:
                        rssUrl = "http://feeds.bbci.co.uk/news/rss.xml";
                        break;
                    case R.id.abc:
                        rssUrl = "https://abcnews.go.com/abcnews/internationalheadlines";
                        break;
                    case R.id.rthke:
                        rssUrl = "http://rthk9.rthk.hk/rthk/news/rss/e_expressnews_einternational.xml";
                        break;
                    case R.id.rthkc:
                        rssUrl = "http://rthk9.rthk.hk/rthk/news/rss/c_expressnews_cinternational.xml";
                        break;
                    case R.id.ad:
                        rssUrl = "http://rss.appleactionews.com/rss.xml";
                        break;
                    case R.id.im:
                        rssUrl = "https://theinitium.com/newsfeed/";
                        break;
                    case R.id.add:
                        Intent intent = new Intent(MainActivity.this, addRssActivity.class);
                        startActivityForResult(intent,2);
                        break;
                    default:
                        for(int i = 0;i<newName.size();i++){
                            if(itemTitle.equals(newName.get(i))){
                                rssUrl = newRss.get(i);
                            }
                        }
                }
                if(id!=R.id.add){
                    barTitle = sBarTitle+ " --- " + itemTitle;
                    save();
                    getSupportActionBar().setTitle(barTitle);
                    new ProcessInBackground().execute();
                }
                dl.closeDrawers();
                return true;
            }
        });

        lvRss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, descriptionActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("title", titles.get(position));
                bundle.putString("link", links.get(position));
                bundle.putString("description", description.get(position));
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        load();
        getSupportActionBar().setTitle(barTitle);
        if(play&&(!videoActivity.end)){
            Intent intent = new Intent(MainActivity.this, videoActivity.class);
            startActivityForResult(intent,1);
        }
        new ProcessInBackground().execute();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    public InputStream getInputStream(URL url)
    {
        try{
            return url.openConnection().getInputStream();
        }
        catch (IOException e){
            return null;
        }
    }

    public class ProcessInBackground extends AsyncTask<Integer, Void, Exception>
    {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Busy loading rss feed...please wait...");
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(Integer... params) {

            try
            {
                URL url = new URL(rssUrl);

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                 factory.setNamespaceAware(false);

                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(getInputStream(url), "UTF_8");

                boolean insideItem = false;

                int eventType = xpp.getEventType();

                titles.clear();
                links.clear();
                description.clear();

                while (eventType != XmlPullParser.END_DOCUMENT)
                {
                    if (eventType == XmlPullParser.START_TAG)
                    {
                        if (xpp.getName().equalsIgnoreCase("item"))
                        {
                            insideItem = true;
                        }
                        else if (xpp.getName().equalsIgnoreCase("title"))
                        {
                            if (insideItem)
                            {
                                titles.add(xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("guid"))
                        {
                            if (insideItem)
                            {
                                links.add(xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("description"))
                        {
                            if (insideItem)
                            {
                                description.add(xpp.nextText());
                            }
                        }
                    }
                    else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item"))
                    {
                        insideItem = false;
                    }

                    eventType = xpp.next();
                }
            }
            catch (Exception e)
            {
                exception = e;
            }
            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, titles);

            lvRss.setAdapter(adapter);

            progressDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1:
                SharedPreferences sp = getSharedPreferences("sp",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                play = data.getBooleanExtra("checked",true);
                break;
            case 2:
                Menu menu = nv.getMenu();

                newRss.add(data.getStringExtra("url"));
                newName.add(data.getStringExtra("title"));

                if(addTime == 0){
                    sMenu = menu.addSubMenu("added");
                }
                sMenu.add(newName.get(addTime));

                addTime++;
                break;
        }

        save();
    }

    public void save(){
        SharedPreferences sp = getSharedPreferences("sp",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("rssUrl",rssUrl);
        editor.putString("barTitle",barTitle);
        editor.putBoolean("again",play);

        for(int i = -1;i<newName.size();i++){

            if(i != -1){
                String keyOfName = ("N"+ i);
                String ketOfRSS = ("R"+ i);
                editor.putString(keyOfName,newName.get(i));
                editor.putString(ketOfRSS,newRss.get(i));
            }
            else {
                editor.putString("size", String.valueOf(newName.size()));
            }

            editor.apply();
        }

    }

    public void load() {
        SharedPreferences sp = getSharedPreferences("sp", MODE_PRIVATE);

        int count = Integer.parseInt(sp.getString("size","0"));
        rssUrl = sp.getString("rssUrl","http://rthk9.rthk.hk/rthk/news/rss/c_expressnews_cinternational.xml");
        barTitle = sp.getString("barTitle","RSS reader");
        play = sp.getBoolean("again",true);

        if(count!=0){
            Menu menu = nv.getMenu();

            if(addTime == 0){
                sMenu = menu.addSubMenu("added");
            }

            for(int i = 0;i<count;i++){
                newRss.add(sp.getString("R"+i,""));
                newName.add(sp.getString("N"+i,""));

                sMenu.add(newName.get(addTime));

                addTime++;
            }
        }

    }

}
