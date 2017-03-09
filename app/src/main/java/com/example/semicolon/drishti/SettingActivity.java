package com.example.semicolon.drishti;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {


    SharedPreferences sharedPreferences;
    String HOST_IP_SP;



    EditText editText;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        editText = (EditText)findViewById(R.id.host_ip);
        button = (Button)findViewById(R.id.host_save);


        sharedPreferences = getSharedPreferences("NETWORK_SHAREDPREF", Context.MODE_PRIVATE);
        HOST_IP_SP = sharedPreferences.getString("HOST_IP_SP", "104.196.153.37");
        Log.d("SettingActivity", HOST_IP_SP);

        editText.setText(HOST_IP_SP);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), editText.getText().toString(), Toast.LENGTH_LONG).show();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("HOST_IP_SP", editText.getText().toString());
                editor.apply();


            }
        });




    }
}
