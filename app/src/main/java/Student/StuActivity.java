package Student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facedemo.R;
import com.example.yi.myproject.ExitApplication;
import com.example.yi.myproject.MyProject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

import Teacher.CheckinPage;
import Teacher.Password_change;
import Teacher.TeaActivity;
import Teacher.Teacher_Info;

public class StuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    String name, pass;
    String S_name;
    public Bitmap bitmap;
    FragmentTransaction transaction;
    NavigationView navigationView;
    TextView above,below;
    Fragment uploadImageFragment, studentInfoFragment, recordFragment, changePassFragment, exitFragment;
    int State=0;
    private SharedPreferences sp;
    SharedPreferences.Editor editor;
    static final int UPLOADIMAGE = 0, STUDENTINFO = 1, RECORD = 2, CHANGEPASS = 3,EXIT = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    private void init(){
        setContentView(R.layout.stu_drawer);
        getNameAndPass();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.START);
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View nav_header =  navigationView.getHeaderView(0);
        above = (TextView)nav_header.findViewById(R.id.above);
        below = (TextView)nav_header.findViewById(R.id.below);
        below.setText(name);
        new link().start();
        drawer.openDrawer(Gravity.START);

        //设置全屏
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        initialFragmentRecord();
    }
    public void getNameAndPass(){
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        pass = intent.getStringExtra("pass");
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            transaction = getSupportFragmentManager().beginTransaction();
            if(msg.what == UPLOADIMAGE) {
                if (uploadImageFragment == null) {
                    uploadImageFragment = new UploadImage();
                    transaction.add(R.id.frame_layout, uploadImageFragment);
                }
                hideall();
                //放入course信息
                Bundle bundle = new Bundle();
                bundle.putString("Sid", name);
                uploadImageFragment.setArguments(bundle);

                transaction.show(uploadImageFragment);
                transaction.commit();
            }else if(msg.what == STUDENTINFO){
                if(studentInfoFragment == null){
                    studentInfoFragment = new Student_Info();
                    transaction.add(R.id.frame_layout, studentInfoFragment);
                }
                hideall();

                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("pass", pass);
                studentInfoFragment.setArguments(bundle);

                transaction.show(studentInfoFragment);
                transaction.commit();
            }else if(msg.what == RECORD){
                if(recordFragment == null){
                    recordFragment = new Record();
                    transaction.add(R.id.frame_layout, recordFragment);
                }
                hideall();

                Bundle bundle = new Bundle();
                bundle.putString("Sid", name);
                recordFragment.setArguments(bundle);

                transaction.show(recordFragment);
                transaction.commit();
            }else if(msg.what == CHANGEPASS){
                if(changePassFragment == null){
                    changePassFragment = new Password_change();
                    transaction.add(R.id.frame_layout, changePassFragment);
                }
                hideall();

                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("pass", pass);
                changePassFragment.setArguments(bundle);

                transaction.show(changePassFragment);
                transaction.commit();

            }else if(msg.what == EXIT){

            }

        }
    };
    private void initialFragmentUploadImage(){
        handler.sendEmptyMessage(UPLOADIMAGE);
        //initCheckinFragment();
    }
    private void initialFragmentStudentInfo(){
        handler.sendEmptyMessage(STUDENTINFO);
    }
    private void initialFragmentRecord(){
        handler.sendEmptyMessage(RECORD);
    }
    private void initialFragmentChangePass(){
        handler.sendEmptyMessage(CHANGEPASS);
    }
    private void initialFragmentExit(){
        handler.sendEmptyMessage(EXIT);
    }

    private void hideall(){
        if(uploadImageFragment != null){
            transaction.hide(uploadImageFragment);
        }
        if(studentInfoFragment != null){
            transaction.hide(studentInfoFragment);
        }
        if(recordFragment != null){
            transaction.hide(recordFragment);
        }
        if(changePassFragment != null){
            transaction.hide(changePassFragment);
        }
        if(exitFragment != null){
            transaction.hide(exitFragment);
        }
    }

    private void saveInfo(int state) {
        sp = getSharedPreferences("data", MODE_PRIVATE);//data为保存的SharedPreferences文件名
        editor = sp.edit();
        editor.putInt("State", state);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_upLoadImage) {
            initialFragmentUploadImage();
        } else if (id == R.id.nav_stuInfo) {
            initialFragmentStudentInfo();
        } else if (id == R.id.nav_record) {
            initialFragmentRecord();
        } else if (id == R.id.nav_changePass) {
            initialFragmentChangePass();
        } else if (id == R.id.nav_exit) {
            initialFragmentExit();
            Intent intent = new Intent(this, MyProject.class);
            //点击退出登录时，设置State为0并保存
            State = 0;
            saveInfo(State);
            startActivity(intent);
        }else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private long mExitTime;
    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            Intent intent = new Intent();
            intent.setClass(this, ExitApplication.class);
            //其中FinishActivity 是个空的Activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    class link extends Thread {
        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            String url = new basicInfo().getinfoUrl();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", name);
                jsonObject.put("pass", pass);
                jsonObject.put("type", "1");//"1" 表示已登录状态
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String data = jsonObject.toString();
            String info = UserService.posttoServerforResult(url, data);
            if (info != null) {
                Log.i("aaa", info);
                String str[] = info.split(",");
                for (int i = 0; i < str.length; i++) {
                    if (i == 1) S_name = str[i];
                }
            }
            //显示名字
            Log.i("STU_ACTIVIT",S_name);
            //if (null != S_name)
            //获取图片
            new link1().start();
            handler.post(runnable);
            super.run();
        }
    }
    class link1 extends Thread {
        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            String url = new basicInfo().getiamgeurl();
            InputStream image = UserService.loginOfGetimage(url, name);
            bitmap = BitmapFactory.decodeStream(image);
            handler.post(runnable1);
        }
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            above.setText(S_name);
        }
    };
    Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            View nav_header =  navigationView.getHeaderView(0);
            ImageView img = nav_header.findViewById(R.id.imageView);
            Log.i("StuActivity",img==null?"NULL":"NOT NULL");
            if(bitmap != null && img != null)
                img.setImageBitmap(bitmap);
        }
    };
    public interface CallbackChangeImg{
        void setImg();
    }
}
