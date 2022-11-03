package com.example.attendance_stud;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//import com.example.attendance_prof.data.model.BeaconScan;
//import com.example.attendance_prof.data.model.ListViewModal;
import com.example.attendance_stud.data.model.ListViewModal;
import com.example.attendance_stud.ui.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
 // 11.02 14:00 교수용 학생용 구분을 위해 Type추가함.
public class ListActivity extends AppCompatActivity {

    private ListView list;
    private String objId;

    private String listBuf;

    private String username;
    private String password;
    //Type 추가함.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        //Type값 저장을 위해 JSONObject 선언.
        JSONObject jsonObject = new JSONObject();

        objId = null;


        Intent intent = getIntent();

        list = (ListView) findViewById(R.id.list);
        List<String> data = new ArrayList<>();

        List<ListViewModal> dataModel = new ArrayList<ListViewModal>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        list.setAdapter(adapter);

        // 현재 일자 추출
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");
        String Time = sdfNow.format(mDate);

        // LoginActivity 에서 인자값이 있엇는지 확인
        if( intent.getExtras() != null ) {
            //if( intent.getExtras().containkey( "OBJ_ID" ) ) {
            // 인자값을 가져오기

            //로그인엑티비티에서 입력된 값 확인.
            username = intent.getExtras().get( "userId" ).toString();
            password = intent.getExtras().get( "userPasswrd" ).toString();
            //}
            try {
                // 백그라운드로 WEB service(Servlet) 호출
                BackgroundAsyncTask bkSync = new BackgroundAsyncTask();

                // URL 주소는 동일하고 뒷단에 Servlet 명만 다르게 넘김
                bkSync.execute(username,password,"Login");

                // 위 bkSync.execute 작업으로 url 연결하여 서비스 호출 및 결과를 받아올때까지 기다림
                while (true) {
                    // 체크를 해봐서 리턴값이 있으면 while 빠져 나와서 후단 로직 수행
                    if (bkSync.getJsonStat() == true) break;
                }
                // JSON 결과물을 받아오기
                objId = bkSync.getJsonStr();


            } catch (Exception e) {
                e.printStackTrace();
            }
            // 호출결과를 화면에 출력해주는거 외에 선택이 되었을때 몇번째 대상인지를 알기 위해 in,out 클래스 생성
            ListViewModal listViewModal = new ListViewModal();
            try {
                // 결과물을 JSON 으로 사용하도록 선언
                JSONObject responseJSON = new JSONObject(objId);

                // id/password 결과가 다른경우
                if (responseJSON.get("RSLT_CD").toString().equals("N"))
                {
                    // 메세지박스로 출력
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("아이디 또는 비밀번호가 일치하지 않습니다.");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // 확인시 리스트 화면으로 이동
                            // 뒤로 가기 이벤트 함수를 호출
                            onBackPressed();

                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    // 메세지박스 보여줌
                    alertDialog.show();
                }

                // id/password 결과가 같은경우
                // JSON 내에 시간표 배열을 찾는다
                JSONArray jsonArray = responseJSON.getJSONArray("TT_LIST");
                // 대상 건수만큼 loop  처리

                //Type 조건문 추가함

                    for (int i = 0; i < jsonArray.length(); i++) {

                        // 한개의 배열내에 항목을 추출하기위해 object 로 변환
                        JSONObject object = jsonArray.getJSONObject(i);
                        listViewModal = new ListViewModal();

                        listBuf = null;

                        // ListViewModal 단일 in,out 에 값 셋팅
                        listViewModal.setUserId(responseJSON.getString("userId"));
                        listViewModal.setPass(responseJSON.getString("userPasswrd"));
                        //Type추가함. 수업 리스트에 보여주는 것이라 빼도 될것으로 예상됌. (추가했을 때 에러 발생하였음 11.03 10:25)
                        listViewModal.setTtOrder(object.getInt("TT_ORDER"));
                        listViewModal.setTtTimeStart(object.getString("TT_TIME_START"));
                        listViewModal.setTtTimeEnd(object.getString("TT_TIME_END"));
                        listViewModal.setTtCourseNm(object.getString("TT_COURSE_NM"));
                        listViewModal.setTtTeachNm(object.getString("TT_TEACH_NM"));
                        listViewModal.setTtClassRoom(object.getString("TT_CLASS_ROOM"));
                        listViewModal.setTtStaffNum(object.getInt("TT_STAFF_NUM"));
                        listViewModal.setTtClassNo(object.getString("TT_CLASS_NO"));
                        listViewModal.setTtUuid1(object.getString("TT_UUID1"));
                        listViewModal.setTtUuid2(object.getString("TT_UUID2"));
                        listViewModal.setTtUuid3(object.getString("TT_UUID3"));
                        listViewModal.setGubun(object.getString("GUBUN"));
                        listViewModal.setTtDay(object.getString("TT_DAY"));

                        // 한행의 대상을 add ( 다건으로 쌓임)
                        dataModel.add(listViewModal);

                        // ListView 에 보여주기 위한 jSON 리턴항목을 문자열 조합
                        listBuf = object.getString("TT_ORDER").concat(" 교시\n");
                        listBuf = listBuf.concat(Time).concat(" ").concat(object.getString("TT_TIME_START")).concat("~").concat(object.getString("TT_TIME_END")).concat("\n");
                        listBuf = listBuf.concat(object.getString("TT_CLASS_ROOM")).concat(".").concat(object.getString("TT_COURSE_NM")).concat(".").concat(object.getString("TT_TEACH_NM")).concat("\n");
                        listBuf = listBuf.concat("출결상태 : ").concat(object.getString("GUBUN"));

                        // 조합한 문자영을 추가 ( 결국 리스트 뷰에 한행 추가됨)
                        data.add(listBuf);

                    }

                // 시간표 배열이 한건도 없는 경우
                if (jsonArray.length() == 0) {
                    data.add("강의 시간표 목록이 없습니다.");
                    listViewModal.setGubun("출석");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();



        // ListView 내에 한줄 선택되기를 기다림
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // 실제 화면에서 한개의 행이 선택되었을떄
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //itemList.remove(i);
                //adapter.notifyDataSetChanged();
                // 리턴 받은 출석 구분이 준비면 출석 처리하고 아닌경우는 화면 변경없음
                if (dataModel.get(i).getGubun().equals("준비")) {
                    // i 값이 리스트뷰에서 몇번째가 선택되엇는지를 알려주므로

                    Log.i("log message", "no:" + i);

                    Log.i("log message", dataModel.get(i).getUserId());
                    Log.i("log message", dataModel.get(i).getPass());
                    Log.i("log message", String.valueOf(dataModel.get(i).getTtOrder()));
                    Log.i("log message", dataModel.get(i).getTtTimeStart());
                    Log.i("log message", dataModel.get(i).getTtTimeEnd());
                    Log.i("log message", dataModel.get(i).getTtCourseNm());
                    Log.i("log message", dataModel.get(i).getTtTeachNm());
                    Log.i("log message", dataModel.get(i).getTtClassRoom());
                    Log.i("log message", String.valueOf(dataModel.get(i).getTtStaffNum()));
                    Log.i("log message", dataModel.get(i).getTtClassNo());
                    Log.i("log message", dataModel.get(i).getTtUuid1());
                    Log.i("log message", dataModel.get(i).getTtUuid2());
                    Log.i("log message", dataModel.get(i).getTtUuid3());
                    Log.i("log message", dataModel.get(i).getGubun());


                    //Type에 UserType 값 저장.

                    // BeaconActivity 사용을 위한 선언

                    // 학생일 경우 출석채크로 넘기기 위해 설정. 임의로 추가함.
                        Intent intent = new Intent(getApplicationContext(), BeaconActivity.class);

                        // 위에서 배열로 저장해놓은 항목의 값을 추출하여 인자값으로 만듦
                        //userType 추가함.
                        intent.putExtra("userId", dataModel.get(i).getUserId());
                        intent.putExtra("userPasswrd", dataModel.get(i).getPass());
                        intent.putExtra("TT_ORDER", dataModel.get(i).getTtOrder());
                        intent.putExtra("TT_TIME_START", dataModel.get(i).getTtTimeStart());
                        intent.putExtra("TT_TIME_END", dataModel.get(i).getTtTimeEnd());
                        intent.putExtra("TT_COURSE_NM", dataModel.get(i).getTtCourseNm());
                        intent.putExtra("TT_TEACH_NM", dataModel.get(i).getTtTeachNm());
                        intent.putExtra("TT_CLASS_ROOM", dataModel.get(i).getTtClassRoom());
                        intent.putExtra("TT_STAFF_NUM", dataModel.get(i).getTtStaffNum().toString());
                        intent.putExtra("TT_CLASS_NO", dataModel.get(i).getTtClassNo());
                        intent.putExtra("TT_UUID1", dataModel.get(i).getTtUuid1());
                        intent.putExtra("TT_UUID2", dataModel.get(i).getTtUuid2());
                        intent.putExtra("TT_UUID3", dataModel.get(i).getTtUuid3());
                        intent.putExtra("GUBUN", dataModel.get(i).getGubun());
                        intent.putExtra("TT_DAY", dataModel.get(i).getTtDay());

                        //Log.i("log message",bkSync.getStatus().toString());
                        // BeaconActivity 화면 호출
                        startActivity(intent);

                    // 접속자가 교수일 것을 대비.
                }

                //Toast.makeText(getApplicationContext(), (i+1)+"번째 아이템이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // 뒤로가기 버튼시 로그인 화면 이동하기
        Intent intent = new Intent( getApplicationContext(), LoginActivity.class );
        startActivity( intent );
    }

}