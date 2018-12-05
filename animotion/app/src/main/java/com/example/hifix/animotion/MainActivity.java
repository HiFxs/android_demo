package com.example.hifix.animotion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
private TextView textView;
private ImageView imageView;
private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
textView=(TextView)findViewById(R.id.text);
imageView=(ImageView)findViewById(R.id.test_image);
button=(Button)findViewById(R.id.button);
button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Animation alpha = AnimationUtils.loadAnimation(MainActivity.this, R.anim.alpha);
        textView.startAnimation(alpha);
        Animation al = AnimationUtils.loadAnimation(MainActivity.this, R.anim.alpha);
        imageView.startAnimation(al);
    }
});

    }
}
