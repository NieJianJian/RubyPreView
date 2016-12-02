package com.ruby.preview.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ruby.preview.R;

public class MainActivity extends Activity {

    private Button mFansBtn, mPreviewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void fansClick(View view) {
        Intent intent = new Intent(MainActivity.this, FansActivity.class);
        startActivity(intent);
    }

    public void previewClick(View view) {
        Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
        startActivity(intent);
    }

}
