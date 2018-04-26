package com.example.user.annotationtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.annotationdemo.BindId;
import com.example.annotationdemo.BindString;

public class MainActivity extends AppCompatActivity {
    @BindId(0x0000)
    TextView t1;
    @BindId(0x0001)
    TextView t2;
    @BindId(0x0002)
    TextView t3;
    @BindId(0x0003)
    TextView t4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @BindString("Hi annotation")
    private void test(){

    }
}
