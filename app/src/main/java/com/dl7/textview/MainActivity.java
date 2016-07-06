package com.dl7.textview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.dl7.textview.animtext.ScaleTextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    String[] mTestStrs = new String[]{
            "Stupid is as stupid does.",
            "Have you given any thought to your future?",
            "I was messed up for a long time.",
            "我猜着了开头，但我猜不中这结局",
            "过程和结局哪个更重要?"
    };

    private ScaleTextView mView;
    private Button mBtnStart;
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mView = (ScaleTextView) findViewById(R.id.text_view);
        mBtnStart = (Button) findViewById(R.id.btn_start);
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimer.schedule(new TimerTask() {
                    int index;
                    int length = mTestStrs.length;

                    @Override
                    public void run() {
                        index++;
                        index %= length;
                        mView.post(new Runnable() {
                            @Override
                            public void run() {
                                mView.setAnimationText(mTestStrs[index]);
                            }
                        });
                    }
                }, 500, 3000);
            }
        });

        mTimer = new Timer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }
}
