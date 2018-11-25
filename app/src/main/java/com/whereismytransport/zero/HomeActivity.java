package com.whereismytransport.zero;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button letsgobutton = (Button) findViewById(R.id.Letsgo);
        letsgobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                Toast bread = Toast.makeText(getApplicationContext(), R.string.app_info, Toast.LENGTH_LONG);
                bread.show();
                i.putExtra("Value2", "This value two ActivityTwo");
                // set the request code to any code you like,
                // you can identify the callback via this code
                startActivity(i);
            }
        });
    }

}
