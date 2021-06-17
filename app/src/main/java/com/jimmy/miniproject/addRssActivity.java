package com.jimmy.miniproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class addRssActivity extends AppCompatActivity {

    TextView url1,title1,result;
    Button check,add;
    boolean IsRss = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rss);

        getSupportActionBar().setTitle("Add RSS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        url1 = (TextView) findViewById(R.id.txtUrl);
        title1 = (TextView)findViewById(R.id.txtTitle);
        result = (TextView)findViewById(R.id.txtResult);
        check = (Button)findViewById(R.id.btnCheck);
        add = (Button)findViewById(R.id.btnAdd);
        result.setText("Please enter the RSS address and check");

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ProcessInBackground().execute();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("url",url1.getText().toString());
                intent.putExtra("title",title1.getText().toString());
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        url1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                add.setEnabled(false);
                IsRss = false;
            }
        });

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
        ProgressDialog progressDialog = new ProgressDialog(addRssActivity.this);

        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Checking RSS...please wait...");
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(Integer... params) {

            try
            {
                URL url = new URL(url1.getText().toString());

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(getInputStream(url), "UTF_8");

                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT)
                {
                    if (eventType == XmlPullParser.START_TAG)
                    {
                        if (xpp.getName().equalsIgnoreCase("item"))
                        {
                            IsRss = true;
                        }
                    }
                    eventType = xpp.next();
                }
            }
            catch (Exception e)
            {
                exception = e;
            }
            finally {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        result.setText("It is not a correct RSS address. \nIf you want to add new RSS feed, Please enter and check again. ");

                    }
                });
                if (IsRss){
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            result.setText("It is a right URL address. \nNow you can click the \"add\" button to add the RSS feed.");
                            add.setEnabled(true);

                        }
                    });
                }
            }
            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);

            progressDialog.dismiss();
        }

    }

}
