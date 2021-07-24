package com.example.taskapp;

import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class editActivity extends AppCompatActivity {

    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //TODO:Dodanie przekazania aktualnego rekordu

        cancelButton = findViewById(R.id.btn_cancelEdit);
        cancelButton.setOnClickListener(v -> {
            finish();
        });
    }
}