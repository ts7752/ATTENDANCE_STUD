package com.example.attendance_stud;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.attendance_stud.ui.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;
    //회원가입 기능 구현 확인완료 11.11 17:22
public class RegisterActivity extends AppCompatActivity {

    private EditText et_id,et_pass,et_name,et_phonenumber,et_hakgua,et_hakbun;
    private Button btn_register;
    private Button btn_register_back;
    private TextView et_gubun;
    private Spinner spinner;

    String id,pass,name,phonenumber,hakgua,hakbun;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_register = findViewById(R.id.btn_register);
        et_id = findViewById(R.id.et_id);
        et_pass = findViewById(R.id.et_pass);
        et_name = findViewById(R.id.et_name);
        et_hakgua = findViewById(R.id.et_hakgua);
        et_gubun = findViewById(R.id.et_gubun);
        et_hakbun = findViewById(R.id.et_hakbun);
        et_phonenumber = findViewById(R.id.et_phonenumber);
        btn_register_back = findViewById(R.id.btn_register_back);
        spinner = findViewById(R.id.spinner);

        et_gubun.setInputType(InputType.TYPE_NULL);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                et_gubun.setText(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // 버튼 기본 설정 (비활성화)
        btn_register.setEnabled(false);

        et_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            //값이 없다면 비활성화 모든 값이 들어가 있다면 활성화
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                id = et_id.getText().toString();
                pass = et_pass.getText().toString();
                name = et_name.getText().toString();
                phonenumber = et_phonenumber.getText().toString();
                hakgua = et_hakgua.getText().toString();
                hakbun = et_hakbun.getText().toString();

                //문자열이 0보다 적다면 버튼 비활성화
                if (id.length() <= 0){
                    if (pass.length() <= 0){
                        if (name.length() <= 0){
                            if (phonenumber.length() <= 0){
                                if (hakgua.length() <= 0){
                                    if (hakbun.length() <= 0){
                                        btn_register.setEnabled(false);

                                    }
                                }
                            }
                        }
                    }
                }
                //모든 값이 입력되었다면 버튼 활성화
                else {
                    btn_register.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




        btn_register_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentback = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intentback);
            }
        });


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText에 현재 입력되어 있는 값을 가져온다.
                String userId = et_id.getText().toString();
                String userPass = et_pass.getText().toString();
                String userName = et_name.getText().toString();
                String userGubun = et_gubun.getText().toString();
                String userHakgua = et_hakgua.getText().toString();
                int userPhonenumber = Integer.parseInt(et_phonenumber.getText().toString());
                int userHakbun = Integer.parseInt(et_hakbun.getText().toString());

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success){ //회원등록 성공
                                Toast.makeText(getApplicationContext(),"회원가입에 성공 하였습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(getApplicationContext(),"회원가입에 실패 하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //registerRequest.class로 넘기기
                RegisterRequest registerRequest = new RegisterRequest(userId,userPass,userName,userGubun,userHakgua,userPhonenumber,userHakbun,responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);


            }
        });

    }
}