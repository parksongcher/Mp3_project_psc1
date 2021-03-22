package com.example.mp3_project_psc1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MusicAdapter.OnItemClickListener {

    private DrawerLayout drawerLayout;
    private FrameLayout frameLayout;
    private RecyclerView recyclerView;
    private RecyclerView recyclerLike;
    private EditText edtSearch;
    private ImageButton imgSearch;

    private LinearLayout bgColor;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayoutManager linearLayoutManager_like;
    private MusicAdapter musicAdapter;
    private MusicAdapter musicAdapter_like;

    private MusicDBHelper musicDBHelper;

    private ArrayList<MusicData> musicDataArrayList = new ArrayList<>();

    private ArrayList<MusicData> musicLikeArrayList = new ArrayList<>();

    private ArrayList<MusicData> findMusicDataArrayList = new ArrayList<>();

    private Fragment player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("성철 MP3");

        //id 찾기 함수
        findViewByIdFunc();

        //SD카드 외부접근 설정 함수
        requestPermissionsFunc();

        //DBHelper 인스턴스
        musicDBHelper = MusicDBHelper.getInstance(getApplicationContext());

        //Adapter 생성
        musicAdapter      = new MusicAdapter(getApplicationContext());
        musicAdapter_like = new MusicAdapter(getApplicationContext());

        //linearLayoutManager 인스턴스
        linearLayoutManager      = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager_like = new LinearLayoutManager(getApplicationContext());

        //reclerView에 Adapter 세팅
        recyclerView.setAdapter(musicAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        //reclerView에 Manager 세팅
        recyclerLike.setAdapter(musicAdapter_like);
        recyclerLike.setLayoutManager(linearLayoutManager_like);

        //음악 리스트 가져오기
        musicDataArrayList = musicDBHelper.compareArrayList();

        //음악 DB에 저장하기
        insertDB(musicDataArrayList);

        //Adapter에 data 세팅
        recyclerViewListUpdata(musicDataArrayList);
        likeRecyclerViewListUpdate(getLikeList());

        //플래그먼트 지정
        replaceFrag();

        //imgSearch 클릭 이벤트
        imgSearch.setOnClickListener(view -> {
            findMusicDataArrayList = musicDBHelper.findMusicSinger(edtSearch.getText().toString().trim());

            if (findMusicDataArrayList.isEmpty()) {
                Toast.makeText(getApplicationContext(), "가져오기 실패", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), "가져오기 성공", Toast.LENGTH_SHORT).show();
            }
            //Adapter에 dataList 세팅
            musicAdapter.setMusicList(findMusicDataArrayList);


            //recyclerView에 Adapter 세팅
            recyclerView.setAdapter(musicAdapter);
            musicAdapter.notifyDataSetChanged();
        });

        //recyclerView 클릭 이벤트
        musicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(View v, int pos) {
                //Player 화면처리
                ((Player)player).setPlayerData(pos, true);
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        //like_recyclerView 클릭 이벤트
        musicAdapter_like.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(View v, int pos) {
                //Player 화면처리
                ((Player)player).setPlayerData(pos, false);
                drawerLayout.closeDrawer(Gravity.RIGHT);
            }
        });

        // 프레임 레이아웃 스와이프 -> DrawerLayout 열기
        frameLayout.setOnTouchListener(new View.OnTouchListener() {

            float x1, x2, y1, y2, dx, dy;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        break;

                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        y2 = event.getY();

                        dx = x2 - x1;
                        dy = y2 - y1;

                        if (Math.abs(dx) > Math.abs(dy)) {
                            if (dx > 0)
                                drawerLayout.openDrawer(Gravity.LEFT, true);
                            else
                                drawerLayout.openDrawer(Gravity.RIGHT, true);

                        }
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.menu1, menu);
        return true;
    }

    //메뉴바에서 색 변경하기
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //id 찾기 함수 = 이게 있어야 색이 바뀐다.
        findViewByIdFunc();
        switch (item.getItemId()){
            case R.id.itemRed      : bgColor.setBackgroundColor(Color.RED);      return true;
            case R.id.itemGreen    : bgColor.setBackgroundColor(Color.GREEN);    return true;
            case R.id.itemBlue     : bgColor.setBackgroundColor(Color.BLUE);     return true;
            case R.id.itemYello    : bgColor.setBackgroundColor(Color.YELLOW);   return true;
            case R.id.itemWhite    : bgColor.setBackgroundColor(Color.WHITE);    return true;
            case R.id.itemMyColor  : bgColor.setBackgroundColor(Color.GRAY);     return true;
            default : toastMessage("메뉴를 잘 선택해주세요.");     break;
        }
        return false;
    }

    //Toast메세지 함수 만듬
    private void toastMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    //플래그먼트 지정
    private void replaceFrag() {
        player = new Player();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.frameLayout, player);
        ft.commit();
    }

    //like Adapter에 data 세팅
    private void likeRecyclerViewListUpdate(ArrayList<MusicData> arrayList) {
        //Adapter에 dataList 세팅
        musicAdapter_like.setMusicList(arrayList);

        //recyclerView에 Adapter 세팅
        recyclerLike.setAdapter(musicAdapter_like);
        musicAdapter_like.notifyDataSetChanged();
    }


    private void findRecyclerViewListUpdata(ArrayList<MusicData> arrayList) {

        //Adapter에 dataList 세팅
        musicAdapter.setMusicList(arrayList);

        //recyclerView에 Adapter 세팅
        recyclerView.setAdapter(musicAdapter);
        musicAdapter.notifyDataSetChanged();
    }

    //Adapter에 data 세팅
    private void recyclerViewListUpdata(ArrayList<MusicData> arrayList) {

        //Adapter에 dataList 세팅
        musicAdapter.setMusicList(arrayList);

        //recyclerView에 Adapter 세팅
        recyclerView.setAdapter(musicAdapter);
        musicAdapter.notifyDataSetChanged();
    }

    //음악 DB에 저장하기
    private void insertDB(ArrayList<MusicData> arrayList) {

        boolean retunValue = musicDBHelper.insertMusicDataToDB(arrayList);

        if (retunValue) {
            Toast.makeText(getApplicationContext(), "삽입 성공", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(), "삽입 실패", Toast.LENGTH_SHORT).show();
        }
    }


    //좋아요 리스트 가져오기
    private ArrayList<MusicData> getLikeList() {

        musicLikeArrayList = musicDBHelper.saveLikeList();

        if (musicLikeArrayList.isEmpty()) {
            Toast.makeText(getApplicationContext(), "가져오기 성공", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(), "가져오기 실패", Toast.LENGTH_SHORT).show();
        }
        return musicLikeArrayList;
    }

    //View id 찾기 함수
    private void findViewByIdFunc() {
        drawerLayout = findViewById(R.id.drawerLayout);
        frameLayout  = findViewById(R.id.frameLayout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerLike = findViewById(R.id.recyclerLike);
        imgSearch    = findViewById(R.id.imgSearch);
        edtSearch    = findViewById(R.id.edtSearch);
        bgColor      = findViewById(R.id.bgColor);
    }

    //SD카드 외부접근 설정
    private void requestPermissionsFunc() {
        ActivityCompat.requestPermissions(this,
                new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);
    }

    @Override
    public void onItemClick(View v, int pos) {

    }

    public ArrayList<MusicData> getMusicDataArrayList() {
        return musicDataArrayList;
    }

    public ArrayList<MusicData> getMusicLikeArrayList() {
        return musicLikeArrayList;
    }

    public MusicAdapter getMusicAdapter_like() {
        return musicAdapter_like;
    }

    @Override
    protected void onStop() {
        super.onStop();

        boolean returnValue = musicDBHelper.updateMusicDataToDB(musicDataArrayList);

        if (returnValue) {
            Toast.makeText(getApplicationContext(), "업데이트 성공", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(), "업데이트 실패", Toast.LENGTH_SHORT).show();
        }
    }
}