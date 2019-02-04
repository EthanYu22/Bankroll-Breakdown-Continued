package com.example.ethan.pokerjournal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginPage extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
    }

    public void onClickMainPage(View v)
    {
        EditText loginPW = (EditText) findViewById(R.id.password);
        String pwd = loginPW.getText().toString();
        String truePW = getResources().getString(R.string.password);

        if(pwd.equals(truePW))
        {
            Intent intent = new Intent(LoginPage.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
