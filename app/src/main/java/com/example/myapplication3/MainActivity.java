package com.example.myapplication3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
     private EditText editText_login_id;
     private EditText editText_login_password;
     private Button button_login;
     private TextView textView_to_register;
     private String responsedata;
     private int code;
     private String Msg;
     private String username;
     private MHandler mHandler=new MHandler();
     private HashMap<String,String> map =new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText_login_id=(EditText)findViewById(R.id.edittext_login_ID);
        editText_login_password=(EditText)findViewById(R.id.edittext_login_password);
        textView_to_register=(TextView)findViewById(R.id.textview_to_register);
        button_login=(Button)findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login_ID=editText_login_id.getText().toString();
                String login_password=editText_login_password.getText().toString();
                map.put("username",login_ID);
                map.put("password",login_password);
                sendPostNetRequest("https://www.wanandroid.com/user/login\n",map);
            }
        });
        textView_to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MainActivity2.class);
                startActivity(intent);
            }
        });
    }

    private class MHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Jsontext();
            if(code==-1){
                Toast.makeText(MainActivity.this,Msg,Toast.LENGTH_SHORT).show();
            }
            else {
                try {
                    JSONObject jsonObject=new JSONObject(responsedata);
                    JSONObject jsonObjectdata=jsonObject.getJSONObject("data");
                    username=jsonObjectdata.getString("username");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                Intent intent=new Intent(MainActivity.this,MainActivity3.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this,"登录成功,你好 "+username,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendPostNetRequest(String mUrl, HashMap<String,String>params){
        new Thread(
                ()->{
                    try {
                        URL url=new URL(mUrl);
                        HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);
                        connection.setDoOutput(true);
                        connection.setDoInput(true);
                        StringBuilder dataTowrite=new StringBuilder();
                        for(String key : params.keySet()){
                            dataTowrite.append(key).append("=").append(params.get(key)).append("&");
                        }
                        connection.connect();
                        OutputStream outputStream=connection.getOutputStream();
                        outputStream.write(dataTowrite.substring(0,dataTowrite.length()-1).getBytes());
                        InputStream in=connection.getInputStream();
                         responsedata=StreamToString(in);
                         Message message=new Message();
                         message.obj=responsedata;
                         mHandler.sendMessage(message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
        ).start();
    }

    private String StreamToString(InputStream in){
        StringBuilder sb=new StringBuilder();
        String oneline;
        BufferedReader reader=new BufferedReader(new InputStreamReader(in));
        try {
            while((oneline=reader.readLine())!=null){
                sb.append(oneline).append('\n');
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                in.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private void Jsontext(){
        try {
            JSONObject jsonObject=new JSONObject(responsedata);
            code=jsonObject.getInt("errorCode");
            Msg=jsonObject.getString("errorMsg");
        }catch (JSONException e){
            e.printStackTrace();
        }

    }
}