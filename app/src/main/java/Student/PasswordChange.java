package Student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.facedemo.R;
import com.example.yi.myproject.MyProject;

import org.json.JSONException;
import org.json.JSONObject;

import Student.UserService;
import Student.basicInfo;

import static android.content.Context.MODE_PRIVATE;

public class PasswordChange extends Fragment {
    View view;
    EditText oldpassText, newpassText, confirmpassText;
    Button confirm;
    String oldpass, newpass, confirmpass;
    String pass, name;
    String url;
    String type = "3";

    private SharedPreferences sp;
    SharedPreferences.Editor editor;
    int State = 0;
    GestureDetector gesture;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this.getActivity()).inflate(R.layout.change_password,container,false);

        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setgesture();
        init();
        setButton();
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
        View decorView = getActivity().getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if(Build.VERSION.SDK_INT >= 21){
            getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        oldpassText = getView().findViewById(R.id.write_oldpassword);
        newpassText = getView().findViewById(R.id.write_newpassword);
        confirmpassText = getView().findViewById(R.id.confirnewpsd);
        confirm = getView().findViewById(R.id.buttonConfirmChange);
    }

    private void setButton() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gettext();
                if (!pass.equals(oldpass)) {
                    Toast.makeText(getContext(), "旧密码错误", Toast.LENGTH_SHORT).show();
                    oldpassText.setError("错误");
                    oldpassText.requestFocus();
                } else if (("").equals(newpass)) {
                    Toast.makeText(getContext(), "新密码不能为空", Toast.LENGTH_SHORT).show();
                    newpassText.setError("错误");
                    newpassText.requestFocus();
                } else if (!newpass.equals(confirmpass)) {
                    Toast.makeText(getContext(), "两次密码不一致", Toast.LENGTH_SHORT).show();
                } else {
                    //连接服务器修改密码
                    new link().start();
                }
            }
        });
    }

    private void gettext() {
        oldpass = oldpassText.getText().toString();
        newpass = newpassText.getText().toString();
        confirmpass = confirmpassText.getText().toString();

        Bundle bundle = getArguments();
        pass = bundle.getString("pass");
        name = bundle.getString("name");
    }

    class link extends Thread {
        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            url = new basicInfo().getinfoUrl();
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("name",name);
                jsonObject.put("pass",newpass);
                jsonObject.put("type",type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String data=jsonObject.toString();
            String str = UserService.posttoServerforResult(url,data);
            if (null != str) {
                Looper.prepare();
                Toast.makeText(getContext(), "修改成功,请重新登录", 1).show();
                back_main();
                Looper.loop();
            } else {
                Looper.prepare();
                Toast.makeText(getContext(), "服务器连接失败", 1).show();
                Looper.loop();
            }

            super.run();
        }
    }

    private void back_main() {
        Intent intent = new Intent(getActivity(), MyProject.class);
        //点击退出登录时，设置State为0并保存
        State = 0;
        saveInfo(State);
        startActivity(intent);
    }

    private void saveInfo(int state) {
        sp = getActivity().getSharedPreferences("data", MODE_PRIVATE);//data为保存的SharedPreferences文件名
        editor = sp.edit();
        editor.putInt("State", state);
        editor.putString("password", "");
        editor.putString("role","student");
        editor.apply();
    }


}
