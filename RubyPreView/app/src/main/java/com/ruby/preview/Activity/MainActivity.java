package com.ruby.preview.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
