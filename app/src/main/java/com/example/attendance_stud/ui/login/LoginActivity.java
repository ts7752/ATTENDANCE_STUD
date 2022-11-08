package com.example.attendance_stud.ui.login;

// 11.02 14:52 교수용 학생용 구분을 위해 Type추가함.
import android.app.Activity;
//import android.arch.lifecycle.Observer;
//import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.annotation.StringRes;
//import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.attendance_stud.BeaconActivity;
import com.example.attendance_stud.ListActivity;
import com.example.attendance_stud.R;
import com.example.attendance_stud.ui.login.LoginViewModel;
import com.example.attendance_stud.ui.login.LoginViewModelFactory;
import com.example.attendance_stud.databinding.ActivityLoginBinding;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    public String name_pass;

    public EditText usernameEditText;

    public static Context context;

    long backKeyPressedTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       usernameEditText = findViewById(R.id.username);

        context = this;

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        // 로그인 버튼의 이벤트를 체크하기 위한 Listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            // 로그인 버튼이 선택되어을 경우
            @Override
            public void onClick(View v) {
                name_pass = usernameEditText.getText().toString();

                loadingProgressBar.setVisibility(View.VISIBLE);
                //loginViewModel.login(usernameEditText.getText().toString(),
                //        passwordEditText.getText().toString());

                // ListActivity 화면 생성
                Intent intent = new Intent( getApplicationContext(), ListActivity.class );
                //Log.i("log message","TRACE_A");
                // 넘길 인자값 설정
                intent.putExtra( "userId", usernameEditText.getText().toString() );
                intent.putExtra( "userPasswrd", passwordEditText.getText().toString() );


                //Log.i("log message","TRACE_B");
                //Log.i("log message",bkSync.getJsonStr());

                //Log.i("log message",bkSync.getStatus().toString());
                // 화면에 보여준
                startActivity( intent );
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();


    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
    // 하단위 뒤로가기 버튼이 선택되었을때
    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() < backKeyPressedTime + 2000) {
            super.onBackPressed();
            // 프로그램 종료
            finishAffinity();
        } else {
            // 메세지 박스로 간단히 출력
            Toast.makeText(LoginActivity.this, "뒤로 버튼을 한번 더 터치하시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
        backKeyPressedTime = System.currentTimeMillis();
    }



}