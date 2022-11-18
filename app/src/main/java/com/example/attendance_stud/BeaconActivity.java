package com.example.attendance_stud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendance_stud.data.model.ListViewModal;
import com.example.attendance_stud.ui.login.LoginActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.service.ScanJob;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class BeaconActivity extends AppCompatActivity {

    // 비콘 관련 라이브러리는  altBeacon 사 library 사용 및 가이드 적용
    // lin 폴더에 android-beacon-library-2.17.1.aar 포함되어 있음
    // java 에서의 lib 는 .jar 형태지만 .arr 경우는 jar  포함한 소스도 포함되어 있음

    ImageButton beacon_back, imt_help;

    private TextView beacontime;

    //TextView textView;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    Boolean findBeacon = false;

    protected static final String TAG = "BeaconActivity";
    private BeaconManager beaconManager;

    ListViewModal listViewModal = new ListViewModal();

    String objId;

    long now = System.currentTimeMillis();
    Date mDate = new Date(now);
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");
    String Time = sdfNow.format(mDate);

    Region pRegion;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);

        beacon_back = (ImageButton) findViewById(R.id.beacon_back) ;
        imt_help = (ImageButton) findViewById(R.id.imt_help);
        beacontime = findViewById(R.id.beacon_time);
        ShowTimeMethod();

        imt_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(BeaconActivity.this);
                ad.setIcon(R.mipmap.baeacon_scan_help_2_round);
                ad.setTitle("도움말");
                ad.setMessage("강의실이 검색되지 않아요\n\n1. 블루투스를 껐다가 다시 켜주세요\n2. GPS(위치)가 켜져 있는지 확인해 주세요.\n3. 휴대폰을 재부팅 해 주세요.\n4. 네트워크 초기화를 진행 해주세요.");

                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    }
                });
                ad.show();
            }
        });

        // 피호출시 인자값 확인위해 선언
        Intent intent = getIntent();

        // 인자항목을 단건으로 in,out 관리를 위해 새로 선언
        listViewModal = new ListViewModal();
        // 기본 비톤검색 실패로 설정
        findBeacon = false;

        beacon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getApplicationContext(),ListActivity.class);

                intent2.putExtra("userId",listViewModal.getUserId());
                intent2.putExtra("userPasswrd",listViewModal.getPass());

                startActivity(intent2);
            }
        });

        // 인자값이 잇는 경우
        if( intent.getExtras() != null ) {
            // 인자값을 단건의 in,out 을 수월하게 하기위해 셋팅
            listViewModal.setUserId(intent.getExtras().get("userId").toString());
            listViewModal.setPass(intent.getExtras().get("userPasswrd").toString());
            listViewModal.setTtOrder(Integer.valueOf(intent.getExtras().get("TT_ORDER").toString()));
            listViewModal.setTtTimeStart(intent.getExtras().get("TT_TIME_START").toString());
            listViewModal.setTtTimeEnd(intent.getExtras().get("TT_TIME_END").toString());
            listViewModal.setTtCourseNm(intent.getExtras().get("TT_COURSE_NM").toString());
            listViewModal.setTtTeachNm(intent.getExtras().get("TT_TEACH_NM").toString());
            listViewModal.setTtClassRoom(intent.getExtras().get("TT_CLASS_ROOM").toString());
            listViewModal.setTtStaffNum(Integer.valueOf(intent.getExtras().get("TT_STAFF_NUM").toString()));
            listViewModal.setTtClassNo(intent.getExtras().get("TT_CLASS_NO").toString());
            listViewModal.setTtUuid1(intent.getExtras().get("TT_UUID1").toString());
            listViewModal.setTtUuid2(intent.getExtras().get("TT_UUID2").toString());
            listViewModal.setTtUuid3(intent.getExtras().get("TT_UUID3").toString());
            listViewModal.setTtUuid4(intent.getExtras().get("TT_UUID4").toString());
            listViewModal.setTtUuid5(intent.getExtras().get("TT_UUID5").toString());
            listViewModal.setTtUuid6(intent.getExtras().get("TT_UUID6").toString());
            listViewModal.setGubun(intent.getExtras().get("GUBUN").toString());
            listViewModal.setTtDay(intent.getExtras().get("TT_DAY").toString());
        }

        // TextView 에 출력하기 위한 수강정보 문자열 생성
        String listBuf = listViewModal.getTtOrder().toString().concat(" 교시\n");
        listBuf = listBuf.concat(Time).concat(" ").concat(listViewModal.getTtTimeStart()).concat("~").concat(listViewModal.getTtTimeEnd()).concat("\n");
        listBuf = listBuf.concat(listViewModal.getTtClassRoom()).concat(".").concat(listViewModal.getTtCourseNm()).concat(".").concat(listViewModal.getTtTeachNm()).concat("\n");

        // TextView 에 내용 출력력
        TextView textView = findViewById(R.id.textView);
        textView.setText(listBuf);
        // 글자색 검정
        textView.setTextColor(Color.BLACK);

        // 권한체크
         permissionCheck(); // 퍼미션 체크



        //퍼미션이 부여되지 않으면 작동하지 않는다.

        //textView = findViewById(R.id.tv_message);
        // alBeacon Api 가이드 : 메니저선언
        beaconManager = BeaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().clear();

        // alBeacon Api 가이드 : 비컨의 종류별 다 검색 가능하도록 설정
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        beaconManager.setRegionStatePersistenceEnabled(false); // 이거 중요, 빼먹지 말것!

        TextView textView2 = findViewById(R.id.textView2);
        Button btn = findViewById(R.id.button2);

        // // alBeacon Api 가이드 :검색 시작
        beaconManager.addMonitorNotifier(new MonitorNotifier() {

            //들어올때 인식 (비콘 범위에 들어온경우)
            @Override
            public void didEnterRegion(Region region) {
                Log.d(TAG, "I just saw an beacon for the first time!" + this);

                //인식되면 range, identify 시작
                beaconManager.startRangingBeacons(region);
                pRegion = region;
            }

            //나갈때 인식 ( 비콘 범위를 빠져나간 경우)
            @Override
            public void didExitRegion(Region region) {
                Log.d(TAG, "I no longer see an beacon");

                //objId = null;
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.d(TAG, "I have just switched from seeing/not seeing beacons: " + state + region);

            }
        });

        //findBeacon = false;

        beaconManager.addRangeNotifier(new RangeNotifier() {

            //인식되면 range, identify 시작
            // // alBeacon Api 가이드 : 비톤이 인식된경우
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                for (Beacon beacon: beacons)  {
                    // id 를 추출
                    String uuid=beacon.getId1().toString();
                    String major=beacon.getId2().toString();
                    String minor=beacon.getId3().toString();
                    Log.d(TAG, "This beacon has identifiers:"+beacon.getId1()+", "+beacon.getId2()+", "+beacon.getId3());
                    Log.d(TAG, "This beacon is far away from "+beacon.getDistance()+"m");

                    Log.d(TAG, "listViewModal:"+listViewModal.getTtUuid1()+", "+listViewModal.getTtUuid2()+", "+listViewModal.getTtUuid3());
                    Log.d(TAG, "listViewModal:"+listViewModal.getTtUuid4()+", "+listViewModal.getTtUuid5()+", "+listViewModal.getTtUuid6());
                    Log.d(TAG, "listViewModal:"+uuid+", "+major+", "+minor);
                    Log.d(TAG, "listViewModal:"+uuid+", "+major+", "+minor);

                    // ID 와 전 Servlet 호출시 시간표외 id 받아온것과 비교
                    if ( (uuid.equals(listViewModal.getTtUuid1()) && major.equals(listViewModal.getTtUuid2()) && minor.equals(listViewModal.getTtUuid3())) ||
                            (uuid.equals(listViewModal.getTtUuid4()) && major.equals(listViewModal.getTtUuid5()) && minor.equals(listViewModal.getTtUuid6())) )
                        {
                            // 일치하면 비톤 찾기성공
                            findBeacon = true;
                            Log.d("BREAK","");

                                // TextView 에 출력
                                textView2.setText("강의실이 검색됩니다. 전자출결 가능합니다.");
                                textView2.setTextColor(Color.BLUE);
                                // 버튼 선택가능하도록 열어주기
                                btn.setEnabled(true);
                            // for 문 빠져나가시
                            break;
                        }

                }
                // 일치하는 비콘이 없으면
                if (!findBeacon) {
                    {
                        textView2.setTextColor(Color.RED);
                        textView2.setText("강의실이 확인되지 않습니다.");
                        // 버튼 선택 못하도록 닫기
                        btn.setEnabled(false);
                    }
                }

                //beaconManager.stopRangingBeacons(region);
                //beaconManager.stopMonitoring(region);
                //beaconManager.stopRangingBeacons(region);

                Log.d(TAG, "done");

            }
        });


        // alBeacon Api 가이드 : 모니터링 시작
        //"74278bda-b644-4520-8f0c-720eaf059935"
        beaconManager.startMonitoring(new Region("myMonitoringUniqueId", null, null, null));

    }

    //비콘 시간표시 액티비티.
    public void ShowTimeMethod() {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                beacontime.setText(DateFormat.getDateTimeInstance().format(new Date()));
            }
        };

        Runnable task = new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){}
                    handler.sendEmptyMessage(1);
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }


    // alBeacon Api 가이드
    //위치 permission이 부여되야 한다.
    public void permissionCheck(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }
        getLocation();
    }
    // alBeacon Api 가이드
    //permission이 부여되야 한다.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "access denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    // alBeacon Api 가이드
    //Get location
    public void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        Location myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (myLocation == null)
        {
            myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }
    }
    // 출결버튼 선택시
    public void OnClick(View view) {
        try {

            // 비콘 검색 종료
            beaconManager.stopRangingBeacons(pRegion);
            beaconManager.stopMonitoring(pRegion);

            // Servlet 호출을 위해 선언
            BackgroundAsyncTask bkSync = new BackgroundAsyncTask();

            // 출결 등록을 위해 관련 항목 전달
            bkSync.execute(listViewModal.getUserId(),listViewModal.getPass(),"Attendance","STD",listViewModal.getTtClassNo()
                    ,String.valueOf(listViewModal.getTtOrder()),listViewModal.getTtDay(),"S","");

            // 처리결과 받을때까지 대기
            while (true) {
                if (bkSync.getJsonStat() == true) break;
            }

            // 결과 받아오기
            objId = bkSync.getJsonStr();

            JSONObject responseJSON = new JSONObject(objId);

            // 다이얼로그 출력
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // PROC_CNT 는 출결등록의 결과림
            if(responseJSON.getInt("PROC_CNT") > 0)
            {
                builder.setMessage("출결 처리되었습니다.");

            } else {
                builder.setMessage("오류가 발생하였습니다.다시 시도해주세요.");
            }
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                // 확인버튼이 선택되면
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                     // 확인시 리스트 화면으로 이동
                    // 뒤로 가기버튼 동작과 동일
                    onBackPressed();

                    //Intent intent = new Intent( getApplicationContext(), ListActivity.class );

                    //intent.putExtra( "userId", listViewModal.getUserId() );
                    //intent.putExtra( "userPasswrd", listViewModal.getPass() );

                    //startActivity( intent );

                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            // activity 닫기
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        // 뒤로 가기버튼시 ListView 를 다시 보여줌
        // 다시 보여줄때 리스트 재조회하여 보여줌
        Intent intent = new Intent( getApplicationContext(), ListActivity.class );

        intent.putExtra( "userId", listViewModal.getUserId() );
        intent.putExtra( "userPasswrd", listViewModal.getPass() );

        startActivity( intent );

        //finish();
    }

}


