package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.MainActivity;

public class IntroActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity); //xml, java 소스 연결

        // "Driver" 텍스트 뷰를 찾고 애니메이션 적용
        TextView driverText = findViewById(R.id.driver_text);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.move_text);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                driverText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        driverText.startAnimation(animation);

        // 애니메이션 종료 후 MainActivity로 이동
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent); //인트로 실행 후 바로 MainActivity로 넘어감.
                finish();
            }
        },2100); //애니메이션 시간에 맞춰 2.1초 후 인트로 실행
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
}
