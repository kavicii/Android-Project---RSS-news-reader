package com.jimmy.miniproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class descriptionActivity extends AppCompatActivity {

    String sTitle, sLink,sDescription;//s for selected
    TextView tTitle,tDescription;//t for text view
    Button bGo;//b for button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        getSupportActionBar().setTitle("Description");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tTitle = (TextView)findViewById(R.id.txtTitle);
        tDescription = (TextView)findViewById(R.id.txtDescription);
        bGo = (Button)findViewById(R.id.btnGo);

        Bundle bundle = this.getIntent().getExtras();

        sTitle = bundle.getString("title");
        sLink  = bundle.getString("link");
        sDescription  = bundle.getString("description");

        tTitle.setText(sTitle);
        tDescription.setText(sDescription);
        tDescription.setMovementMethod(new ScrollingMovementMethod());

        bGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(sLink);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

    }
}
