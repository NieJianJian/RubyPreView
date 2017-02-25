package com.ruby.preview.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ruby.preview.R;
import com.ruby.preview.utils.GetFileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    //    private Button mDeleteBtn, mSelectBtn;
    private ListView mListView;
    private final static String QQPATH = File.separatorChar + "Tencent" + File.separatorChar + "QQfile_recv";
    private List<String> mFileList, mFileNameList;
    private ArrayAdapter<String> mAdapter;
    private String[] urlBuff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFileNameList = new ArrayList<>();
        mFileList = new ArrayList<>();
        mListView = (ListView) findViewById(R.id.main_listview);

        checkPermission();

    }


    private void checkPermission() {
        /*ContextCompat.checkSelfPermission() 检查是否已经授权*/
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // context, 权限名, 请求code
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            // 执行相关操作
            loadFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 执行相关操作
                    loadFile();
                } else {
                    // 因为没有权限无法操作的提示或者处理
                    Toast.makeText(MainActivity.this, "无法获得权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void loadFile() {
        mFileList = GetFileUtil.getFileList(QQPATH, ".xls", true);
        if (mFileList.size() > 0) {
            for (String s : mFileList) {
                String name = s.split("\\/")[s.split("\\/").length - 1];
                mFileNameList.add(name);
            }
            urlBuff = mFileNameList.toArray(new String[mFileList.size()]);
//                        String[] urlBuff = (String[]) mFileNameList.toArray(new String[0]);
            mAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, urlBuff);
            mListView.setAdapter(mAdapter);
        } else {
            mListView.setVisibility(View.GONE);
        }
    }

    public void fansClick(View view) {
        Intent intent = new Intent(MainActivity.this, FansActivity.class);
        startActivity(intent);
    }

    public void previewClick(View view) {
        Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
        startActivity(intent);
    }

    public void deleteClick(View view) {
        for (String path : mFileList) {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        }
        mListView.setVisibility(View.GONE);
    }

}
