package com.example.attendance_stud;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.attendance_stud.ui.login.LoginActivity;

public class Seclect extends AppCompatActivity {

    Button sel_login, sel_reg;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seclect);

        sel_login = findViewById(R.id.sel_login);
        sel_reg = findViewById(R.id.sel_reg);

        sel_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        sel_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent1);
            }
        });
    }
}