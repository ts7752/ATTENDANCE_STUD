package com.example.attendance_stud;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {

    final static private String URL = "http://qwert3113.dothome.co.kr/Register.php";
    private Map<String, String> map;

    //입력받은 값들을 위 URL로 넘겨주는 역할.
    public RegisterRequest(String userId, String userPassword, String userName, String userGubun,String userHakgua, int userPhonenumber ,int userHakbun, Response.Listener<String> listner){
        super(Method.POST,URL,listner,null);

        map = new HashMap<>();
        map.put("userId",userId);
        map.put("userPassword",userPassword);
        map.put("userName",userName);
        map.put("userGubun",userGubun);
        map.put("userHakgua",userHakgua);
        map.put("userPhonenumber",userPhonenumber + "");
        map.put("userHakbun",userHakbun + "");
    }

    @Nullable
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
