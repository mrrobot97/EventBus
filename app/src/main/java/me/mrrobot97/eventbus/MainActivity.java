package me.mrrobot97.eventbus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button bt;
    Button btAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        bt= (Button) findViewById(R.id.bt);
        btAsync= (Button) findViewById(R.id.btAsync);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new ToastEvent());
            }
        });
        btAsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new ToastEvent());
                    }
                }).start();
            }
        });
    }

    public void onEvent(ToastEvent event){
        Log.d("YJW","onEvent: "+ Thread.currentThread().getName());
    }

    public void onEventMainThread(ToastEvent event){
        Toast.makeText(this, "Haha on MainThread", Toast.LENGTH_SHORT).show();
        Log.d("YJW","onEventMainThread: "+ Thread.currentThread().getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
