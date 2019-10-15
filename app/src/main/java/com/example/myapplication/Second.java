package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Second extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle arguments = getIntent().getExtras();
        assert arguments.getString("name") !=null;
        String name = arguments.getString("name");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);
        TextView welcomeName=(TextView)findViewById(R.id.WelcomeName);
        welcomeName.setText("Hello,  "+ name +" !!" );
    }
}