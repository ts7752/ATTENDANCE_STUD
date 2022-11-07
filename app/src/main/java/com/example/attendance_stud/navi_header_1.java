package com.example.attendance_stud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.attendance_stud.data.model.ListViewModal;

import org.w3c.dom.Text;

public class navi_header_1 extends AppCompatActivity {

 TextView textView6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi_header1);

        textView6 = (TextView)findViewById(R.id.textView6);

        ((ListActivity)ListActivity.context).setUserId();
        ((ListActivity)ListActivity.context).getUserId();


    }


}