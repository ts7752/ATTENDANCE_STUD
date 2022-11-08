package com.example.attendance_stud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.attendance_stud.data.model.ListViewModal;

import org.w3c.dom.Text;

public class navi_header_1 extends AppCompatActivity {

 TextView textView6;

    TextView textViewNm;
    TextView textViewDnm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.navi_header);

        // Log.d("test","trest");
       //setContentView(R.layout.activity_navi_header1);

       //textView6 = (TextView)findViewById(R.id.textView6);

          ((ListActivity)ListActivity.context).setUserId();
          ((ListActivity)ListActivity.context).getUserId();

        //textViewNm = findViewById(R.id.tv_name);
        //textViewDnm = findViewById(R.id.tv_info);

        //textViewNm.setText(((ListActivity)ListActivity.context).getItem1()+"/"+((ListActivity)ListActivity.context).getItem2());
        //textViewDnm.setText(((ListActivity)ListActivity.context).getItem3());

        //textViewNm.setText("test");
    }


}