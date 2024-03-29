package Teacher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.facedemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherInfo extends Fragment {
    private String T_name;
    private TextView realname;
    private TextView username;//账号
    private GridView myCourse;//课程
    private View view;

    String name;
    String pass;
    String course;
    String info;
    String type = "1";//访问类型，表示获取老师的信息

    String url;
    GestureDetector gesture;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this.getActivity()).inflate(R.layout.look_info,container,false);
        //Log.i("Teacher_Lookinfo", view==null?"NULL":"Not NULL");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化控件
        init();
        //setgesture();
        setInfo();
    }
    /*private void setgesture() {
        gesture = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            public boolean onFling(MotionEvent e1, MotionEvent e2, float X, float Y) {
                if (Math.abs(e2.getY() - e1.getY()) > 100) {
                    //Toast.makeText(courseRecord.this, "ERROR OPERATION", Toast.LENGTH_SHORT).show();
                    return true;
                }
                //向右滑
                if (e1.getRawX() - e2.getRawX() > 100) {
                    return true;
                }

                //向左滑
                if (e2.getRawX() - e1.getRawX() > 100) {
                    finish();
                    return true;
                }
                return super.onFling(e1, e2, X, Y);
            }
        });
    }

    public boolean onTouchEvent(MotionEvent event) {
        gesture.onTouchEvent(event);
        return super.onTouchEvent(event);
    }*/
    private void init() {
        //设置全屏幕，即系统可见ui，且actionbar设置为透明
        realname = getView().findViewById(R.id.realname);
        username = getView().findViewById(R.id.username);
        myCourse = getView().findViewById(R.id.myclasses);

        View decorView = getActivity().getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if(Build.VERSION.SDK_INT >= 21){
            getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void setInfo() {
        getinfo();
        if (T_name != null)
            realname.setText(T_name);
        else
            Log.i("aaa", "null");
        username.setText(name);
        setGridview();
    }



   class link extends Thread {
        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            url=new basicInfo().getinfoUrl();
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("name",name);
                jsonObject.put("pass",pass);
                jsonObject.put("type",type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String data=jsonObject.toString();
            String info=UserService.posttoServerforResult(url,data);
            if(info!=null){
                String str[]=info.split(",");
                for(int i=0;i<str.length;i++){
                    if(i==0) course=str[i];
                    if(i==1) T_name=str[i];
                }
            }
            setInfo();
            super.run();
        }
    }

    private void getinfo() {
        Bundle bundle = getArguments();
        name = bundle.getString("name");
        pass = bundle.getString("pass");
        info=  bundle.getString("info");

        if(info!=null){
            String str[]=info.split(",");
            for(int i=0;i<str.length;i++){
                if(i==0) course=str[i];
                if(i==1) T_name=str[i];
            }
        }
    }

    //将课程名显示在gridview上
    private void setGridview() {
        String[] names = {"", ""};
        if (!course.equals("null")) {
            Log.i("aaa", course);
            String[] str = course.split(";");
            names = new String[str.length];
            for (int i = 0; i < str.length; i++) {
                names[i] = str[i];
            }
        }

        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < names.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", names[i]);
            data.add(map);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), data, R.layout.course_item, new String[]{"name"},
                new int[]{R.id.textView}
        );
        myCourse.setAdapter(simpleAdapter);
        //设置gridview的监听
        myCourse.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!("null").equals(course) && course.length() > 0) {
                    Log.i("nocome", course.length() + "");
                    String str[] = course.split(";");
                    for (int i = 0; i < str.length; i++) {
                        if (id == i) {
                            Log.i("aaa", str[i]);
                            toDetailedrecord(str[i]);
                        }
                    }
                }
            }
        });
    }
    private void toDetailedrecord(String course) {
        Intent intent = new Intent(getActivity(), Recorddetailed.class);
        intent.putExtra("course", course);
        startActivity(intent);
    }
}
