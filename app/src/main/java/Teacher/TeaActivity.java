package Teacher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.TransactionTooLargeException;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.facedemo.R;
import com.example.yi.myproject.MyProject;

import org.json.JSONException;
import org.json.JSONObject;

public class TeaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String name;
    String pass;
    String course;
    String info;
    String type = "1";//类型2表示获取课程
    int State = 0;
    String url;
    //ProgressBar pb;
    private SharedPreferences sp;
    SharedPreferences.Editor editor;

    final int CheckInPage = 0;
    final int LookForInfo = 1;
    final int ChangePass = 2;
    final int BackMain = 3;
    FragmentTransaction transaction;
    Fragment checkInFragment, lookForInfoFragment, changePassFragment, exitFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        init();
        getNameandPass();
        initialFragmentCheckInfo();
    }

    private void init(){
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
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //沉浸式layout
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

    }
    private void getNameandPass() {
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        pass = intent.getStringExtra("pass");
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            transaction = getSupportFragmentManager().beginTransaction();
            if(msg.what == CheckInPage) {
                if (checkInFragment == null) {
                    checkInFragment = new CheckinPage();
                    transaction.add(R.id.frame_layout, checkInFragment);
                }
                hideall();
                //放入course信息
                Bundle bundle = new Bundle();
                bundle.putString("Courses", course);
                checkInFragment.setArguments(bundle);

                transaction.show(checkInFragment);
                transaction.commit();
            }else if(msg.what == LookForInfo){
                if(lookForInfoFragment == null){
                    lookForInfoFragment = new Teacher_Info();
                    transaction.add(R.id.frame_layout, lookForInfoFragment);
                }
                hideall();

                Bundle bundle = new Bundle();
                bundle.putString("info", info);
                bundle.putString("name", name);
                bundle.putString("pass", pass);
                lookForInfoFragment.setArguments(bundle);

                transaction.show(lookForInfoFragment);
                transaction.commit();
            }else if(msg.what == ChangePass){
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
            }else if(msg.what == BackMain){

            }

        }
    };
    private void initialFragmentCheckInfo(){
        new link().start();
        //initCheckinFragment();
    }
    private void initialFragmentLookInfo(){
        new link2().start();
    }
    private void initialFragmentChangePass(){
        handler.sendEmptyMessage(ChangePass);
    }

    private void hideall(){
        if(checkInFragment != null){
            transaction.hide(checkInFragment);
        }
        if(lookForInfoFragment != null){
            transaction.hide(lookForInfoFragment);
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
        editor.commit();
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_checkInPage) {
            initialFragmentCheckInfo();
        } else if (id == R.id.nav_lookInfo) {
            initialFragmentLookInfo();
        } else if (id == R.id.nav_changePass) {
            initialFragmentChangePass();
        } else if (id == R.id.nav_exit) {
            Intent intent = new Intent(this, MyProject.class);
            //点击退出登录时，设置State为0并保存
            State = 0;
            saveInfo(State);
            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    class link extends Thread {
        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            url = new basicInfo().getinfoUrl();
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("name",name);
                jsonObject.put("pass",pass);
                type="2";//获取课程
                jsonObject.put("type",type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String data=jsonObject.toString();
            course = UserService.posttoServerforResult(url,data);
            Log.i("Course",course);
            if (null != course) {
                //pb.setVisibility(View.INVISIBLE);
                //通过handler进入考勤页面
                handler.sendEmptyMessage(CheckInPage);
            } else {
                //pb.setVisibility(View.INVISIBLE);
                Looper.prepare();
                Toast.makeText(TeaActivity.this, "连接服务器失败", 1).show();
                Looper.loop();
            }

            super.run();
        }
    }

    class link2 extends Thread {
        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            url = new basicInfo().getinfoUrl();
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("name",name);
                jsonObject.put("pass",pass);
                type="1";//获取老师的信息
                jsonObject.put("type",type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String data=jsonObject.toString();

            info=UserService.posttoServerforResult(url,data);
            //Log.i("returndata",info);
            if (info != null) {
                //pb.setVisibility(View.INVISIBLE);
                //进入查看信息界面
                handler.sendEmptyMessage(LookForInfo);
            } else {
                //pb.setVisibility(View.INVISIBLE);
                Looper.prepare();
                Toast.makeText(TeaActivity.this, "连接服务器失败", 1).show();
                Looper.loop();
            }
            super.run();
        }
    }


}
