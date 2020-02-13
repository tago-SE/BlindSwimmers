package com.example.bluetoothsniffer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TrainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button submitButton;
    private Button backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        submitButton = findViewById(R.id.submit_button);
        backButton = findViewById(R.id.back_button);


        submitButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        System.out.println("TRAIN ACTIVITY!!!");
    }

    @Override
    public void onClick(View v) {

        if(v == submitButton){
            showToast("Data submitted");
        }

        if(v == backButton){
            finish();
        }

    }


    protected void showToast(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
