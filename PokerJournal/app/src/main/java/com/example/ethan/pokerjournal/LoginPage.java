package com.example.ethan.pokerjournal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPage extends AppCompatActivity
{

    private SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        prefs = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = prefs.edit();

        if(!prefs.getBoolean("HasLoginPage", false)){
            editor.putBoolean("HasLoginPage",false);
            editor.commit();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else {
            editor.putBoolean("HasLoginPage",true);
            editor.commit();
        }
    }

    public void onClickMainPage(View v)
    {
        String truePW = getResources().getString(R.string.password);
        EditText etLoginPW = (EditText) findViewById(R.id.etPassword);
        String inputPW = etLoginPW.getText().toString();


        if (inputPW.equals(truePW))
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show();
        }
    }
}
