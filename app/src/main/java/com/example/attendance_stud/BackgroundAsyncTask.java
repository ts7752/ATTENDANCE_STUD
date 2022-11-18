package com.example.attendance_stud;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public class BackgroundAsyncTask extends AsyncTask<String, Integer, String> {

    String jsonStr=null;
    Boolean jsonStat = false;

    @Override
    protected void onPreExecute() {
        // Executed before 'doInBackground'
        // You can handle user interface (UI) here
        Log.i("log message","TRACE1");
    }
    // String... strings 는 인자값을 고정하여 받는게 아닌 가변적으로 받을수 있다
    @Override
    protected String doInBackground(String... strings) {
        // Expensive process should be here
        // Don't use any control like Button, TextBox, Label or ListView etc

        // Call this function to call "onProgressUpdate", you can send progress of work using this function.
        // We can call this function from loop or tell downloading file progress

        String result = "";
        JSONObject responseJSON = null;

        try {

            Log.i("log message","TRACE2");
            // WEB URL 설정
            String URL = "http://43.201.59.250:8080/ATTENDANCE/".concat(strings[2]);     // 최종 URL 설정
            java.net.URL urls = new java.net.URL(URL);     // URL 객체 만들때, 생성자 인자로 URL 넣어주기

            // 커넥션 설정
            HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
            StringBuilder sb = new StringBuilder();

            // 연결
            if (conn != null) {

                Log.i("log message","TRACE3");
                conn.setConnectTimeout(10000);       // 서버에 연결되는 TIMEOUT 설정
                conn.setRequestMethod("POST");       // HTTP 통신 방식 설정
                conn.setRequestProperty("Content-Type", "application/json");   // 보낼 타입
                conn.setUseCaches(false);
                conn.setDoOutput(true);                // POST로 데이터 넘겨줄때 사용
                conn.setDoInput(true);             // InputStream으로 서버 응답 받음

                JSONObject jsonObject = new JSONObject();  // 보낼 데이터 JSONObject에 담기

                // 최초 로그인시 아래 3 개항목 JSON 형태로 만듦
                if(strings[2].equals("Login")) {
                    jsonObject.put("id", strings[0]);
                    jsonObject.put("pass", strings[1]);
                    jsonObject.put("userType", "STD");
                } else {
                    // 출결등록시 넘어온 항목으로 JSON 만뜲
                    jsonObject.put("id", strings[0]);
                    jsonObject.put("userType",strings[3]);
                    jsonObject.put("CLASS_NO", strings[4]);
                    jsonObject.put("TT_ORDER", strings[5]);
                    jsonObject.put("TT_DAY", strings[6]);
                    jsonObject.put("TYPE", strings[7]);
                    jsonObject.put("SBST", strings[8]);
                }

                Log.i("log message","TRACE4");

                Log.i("jsonObject",jsonObject.toString());

                // 한글 설정상 UTF 로 설정
                conn.getOutputStream().write(jsonObject.toString().getBytes(StandardCharsets.UTF_8));

                Log.i("log message","TRACE5");
                // 서버로 부터 리턴되는 문자열을 받기 준비
                OutputStream os = conn.getOutputStream();  //RequestBody에 Data 담기
                Log.i("log message","TRACE6");

                // 서버로 보낼 JSOn 을 쏨
                os.write(jsonObject.toString().getBytes());    //Data 세팅

                Log.i("log message","TRACE7");
                os.flush();          // RequestBody에 Data 입력

                // 연결이 정상인지
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) { //연결 성공시

                    Log.i("log message","TRACE8");
                    InputStream is = conn.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    byte[] byteBuffer = new byte[1024];
                    int nLength;

                    Log.i("log message","TRACE9");

                    // web 에서 전달해주는 값을 받아오기
                    while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                        baos.write(byteBuffer, 0, nLength);
                    }
                    Log.i("log message","TRACE10");

                    byte[] byteData = baos.toByteArray();

                    Log.i("log message","TRACE11");
                    //  결과를 JSON 을 변환
                    responseJSON = new JSONObject(new String(byteData));

                    // 받은 JSON 을 문자열로 변환
                    result = responseJSON.toString();

                    Log.i("log message",result);



                    //usernameEditText.setText(responseJSON.get("userId").toString());
                    //usernameEditText.setText(responseJSON.get("RSLT_CD").toString());

                    // 필요한 정보 추출
                    //result = responseJSON.get("Name").toString();
                    //result = responseJSON.get("Message").toString();

                    //Intent intent = new Intent( getApplicationContext(), ListActivity.class );

                    //intent.putExtra( "OBJ_ID", responseJSON.toString() );

                    //startActivity( intent );

                }
                // 연결 끊기
                //result = responseJSON.toString();
                conn.disconnect();

                //return result;
                Log.i("log message","TRACE12");
                Log.i("log message",result);

                //return result;
                // 결과 셋팅
                jsonStr = result;
                // 처리완료 확인
                jsonStat = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("log message","TRACE8");
        }

        //return result;
        //Log.i("log message","TRACE12");
        //Log.i("log message",result);

        return "";

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        // set progress percent progress[0]
        // or frequently UI work
        // progress[0] is comming from 'publishProgress(50);'
    }

    @Override
    protected void onPostExecute(String a) {
        // Update user interface (UI)
        // value of parameter result is coming from 'doInBackground'
        // After 'doInBackground' it calls 'onPostExecute'
        //Log.e("log message","TRACE13");
        //Log.e("log message",result1);
        //jsonStr = result1;
        Log.i("log message","test".concat(this.getStatus().toString()));
    }

    // 값리턴
    public String getJsonStr()
    {
        return jsonStr;
    }
    // 값리턴
    public Boolean getJsonStat()
    {
        return jsonStat;
    }
}
