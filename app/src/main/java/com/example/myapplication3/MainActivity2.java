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

public class MainActivity2 extends AppCompatActivity {
       private EditText editText_register_id;
       private EditText editText_register_password1;
       private EditText editText_register_password2;
       private Button button_register;
       private TextView textView;
       private int code;
       private String Msg;
       private String username;
       private HashMap<String,String>map=new HashMap<>();
       private String responsedata;
       private MHandler mHandler=new MHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        editText_register_id=(EditText)findViewById(R.id.edittext_register_ID);
        editText_register_password1=(EditText)findViewById(R.id.edittext_register_password1);
        editText_register_password2=(EditText)findViewById(R.id.edittext_register_password2);
        textView=(TextView)findViewById(R.id.back);
        button_register=(Button)findViewById(R.id.button_register);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String register_id=editText_register_id.getText().toString();
                String register_password1=editText_register_password1.getText().toString();
                String register_password2=editText_register_password2.getText().toString();
                map.put("username",register_id);
                map.put("password",register_password1);
                map.put("repassword",register_password2);
                sendPostNetRequest("https://www.wanandroid.com/user/register\n",map);
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity2.this,MainActivity.class);
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
                Toast.makeText(MainActivity2.this,Msg,Toast.LENGTH_SHORT).show();
            }
            else {
                try {
                    JSONObject jsonObject=new JSONObject(responsedata);
                    JSONObject jsonObjectdata=jsonObject.getJSONObject("data");
                    username=jsonObjectdata.getString("username");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                Intent intent=new Intent(MainActivity2.this,MainActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity2.this,"注册成功 "+username,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendPostNetRequest(String mUrl, HashMap<String,String> params){
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