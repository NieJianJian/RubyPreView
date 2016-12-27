package com.ruby.preview.Activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ruby.preview.R;
import com.ruby.preview.utils.Font;
import com.ruby.preview.utils.GetFileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class FansActivity extends Activity {

    private final static String QQPATH = File.separatorChar + "Tencent" + File.separatorChar + "QQfile_recv";
    private MyOnclickListener mOnclickListener;
    private Button mSearchBtn, mPreviewBtn;
    private Button mLastBtn, mNextBtn;
    private WebView mWebView;
    private ListView mListView, mResultListView;
    private List<String> mUrlList, mFileList, mFileNameList, mCheckUrlList;
    private TextView mStatusTv, mShowPathTv, mUrlCountTv, mCurPageCountTv, mJumpUrlTv;
    private int mPreShowPagerCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fans);

        initView();
    }

    private void initView() {
        mOnclickListener = new MyOnclickListener();
        mUrlList = new ArrayList<>();
        mFileNameList = new ArrayList<>();
        mFileList = new ArrayList<>();
        mCheckUrlList = new ArrayList<>();

        mListView = (ListView) findViewById(R.id.main_listview);
        mListView.setOnItemClickListener(new MyOnItemClickListener());
        mResultListView = (ListView) findViewById(R.id.main_resultListview);
        mResultListView.setOnItemClickListener(new MyOnItemClickListener1());

        mSearchBtn = (Button) findViewById(R.id.main_searchBtn);
        mSearchBtn.setOnClickListener(mOnclickListener);
        mPreviewBtn = (Button) findViewById(R.id.main_previewBtn);
        mPreviewBtn.setOnClickListener(mOnclickListener);
        mLastBtn = (Button) findViewById(R.id.main_lastBtn);
        mLastBtn.setOnClickListener(mOnclickListener);
        mNextBtn = (Button) findViewById(R.id.main_nextBtn);
        mNextBtn.setOnClickListener(mOnclickListener);

        mStatusTv = (TextView) findViewById(R.id.main_statusTv);
        mStatusTv.setText("请点击按钮搜索.xls文件");
        mShowPathTv = (TextView) findViewById(R.id.main_showPathTv);
        mUrlCountTv = (TextView) findViewById(R.id.main_urlCountTv);
        mCurPageCountTv = (TextView) findViewById(R.id.main_curPageCountTv);
        mJumpUrlTv = (TextView) findViewById(R.id.main_jumpUrlTv);

        mWebView = (WebView) findViewById(R.id.main_webview);
        mWebView.setVisibility(View.GONE);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true); // 防止微信连接中的js失效
        mWebView.setWebViewClient(new MyWebViewClient()); // 自动跳转
        mWebView.setWebChromeClient(new WebChromeClient());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFileList();
    }

    private void readUrl() {
        try {
            InputStream inputStream = new FileInputStream(mFileList.get(mCurPosistion));
            Workbook workbook = Workbook.getWorkbook(inputStream);
            Sheet[] sheetCount = workbook.getSheets();
            if (sheetCount.length > 0) {
                Log.i("niejianjian", " -> sheetCount -> " + sheetCount.length);
                for (int i = 0; i < sheetCount.length; i++) {
                    Sheet sheet = workbook.getSheet(i);
                    int rows = sheet.getRows();
                    int cols = sheet.getColumns();

                    Log.i("niejianjian", " -> rows -> " + rows);
                    Log.i("niejianjian", " -> cols -> " + cols);

                    for (int m = 0; m < rows; m++) {
                        StringBuilder builder = new StringBuilder("");
                        if (sheet.getRow(m).length == 1) {
                            // getCell(cols,row)
                            String str = sheet.getCell(0, m).getContents();
                            if (str.contains("http")) {
                                builder.append(str);
                                Log.i("niejianjian", " -> builder -> " + builder);
                                mUrlList.add(builder.toString());
                            }
                        }
                    }
                }
            }
            Log.i("niejianjian", " -> urlList -> " + mUrlList.size());
            inputStream.close();
            workbook.close();

        } catch (BiffException | IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isJump = false;

    class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.i("niejianjian", " -> onPageStarted -> ");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.i("niejianjian", " -> onPageFinished -> " + mWebView.getContentHeight() * mWebView.getScale());
            mWebView.setScrollY((int) (mWebView.getContentHeight() * mWebView.getScale()
                    - mWebView.getMeasuredHeight()));
            mCurPageCountTv.setText((1 + mPreShowPagerCount) + " / " + mUrlList.size());
            if (isJump) {
                isJump = false;
                if (mPreShowPagerCount == (mUrlList.size() - 1)) {
                    Toast.makeText(FansActivity.this, "后面已经没有了", Toast.LENGTH_SHORT).show();
//                    mWebView.loadUrl(mUrlList.get(mUrlList.size() - 1));
                    if (mCheckUrlList.size() > 0) {
                        String[] urlBuff = mCheckUrlList.toArray(new String[mCheckUrlList.size()]);
                        mResultListView.setVisibility(View.VISIBLE);
                        mWebView.setVisibility(View.GONE);
                        mResultListView.setAdapter(new ArrayAdapter<String>(FansActivity.this, android.R.layout.simple_list_item_1, urlBuff));
                        mCheckUrlList.clear();
                    }
//                        String[] urlBuff = (String[]) mFileNameList.toArray(new String[0]);
                } else {
                    mPreShowPagerCount++;
                    mWebView.loadUrl(mUrlList.get(mPreShowPagerCount));
                }
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("niejianjian", " -> request -> url = " + url);
            String[] str = url.split("boy");
            SpannableStringBuilder builder = new SpannableStringBuilder();
            for (int i = 0; i < str.length; i++) {
                builder.append(str[i]);
                if (i != str.length - 1) {
                    builder.append(new Font("boy").color(Color.parseColor("#FF0000")));
                }
            }
            mCheckUrlList.add(builder.toString());
            mJumpUrlTv.setText(builder);
            isJump = true;

            return false;
        }
    }

    private void getFileList(){
        mListView.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.GONE);
        mResultListView.setVisibility(View.GONE);
        mCheckUrlList.clear();
        mUrlList.clear();
        mFileList.clear();
        mFileNameList.clear();
        mPreShowPagerCount = 0;
        mFileList = GetFileUtil.getFileList(QQPATH, ".xls", true);
        mStatusTv.setText("搜索到 " + mFileList.size() + " 个.xls个文件！");
        if (mFileList.size() > 0) {
            for (String s : mFileList) {
                String name = s.split("\\/")[s.split("\\/").length - 1];
                mFileNameList.add(name);
            }
            String[] urlBuff = mFileNameList.toArray(new String[mFileList.size()]);
//                        String[] urlBuff = (String[]) mFileNameList.toArray(new String[0]);
            Log.i("niejianjian", " -> urlBuff -> " + urlBuff.toString());
            mListView.setAdapter(new ArrayAdapter<String>(FansActivity.this, android.R.layout.simple_list_item_1, urlBuff));
        }
    }

    class MyOnclickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.main_searchBtn:
                    getFileList();
                    break;
                case R.id.main_previewBtn:
                    mListView.setVisibility(View.GONE);
                    mWebView.setVisibility(View.VISIBLE);
                    if (mUrlList.size() > 0) {
                        mWebView.loadUrl(mUrlList.get(0));
                    } else {
                        Toast.makeText(FansActivity.this, "没有读取到连接啊", Toast.LENGTH_SHORT);
                    }
                    break;
                case R.id.main_lastBtn:
                    if (mPreShowPagerCount == 0) {
                        Toast.makeText(FansActivity.this, "前面已经没有了", Toast.LENGTH_SHORT).show();
                    } else {
                        mPreShowPagerCount--;
                        mWebView.loadUrl(mUrlList.get(mPreShowPagerCount));
                    }
                    break;
                case R.id.main_nextBtn:
                    if (mPreShowPagerCount == (mUrlList.size() - 1)) {
                        Toast.makeText(FansActivity.this, "后面已经没有了", Toast.LENGTH_SHORT).show();
//                        mWebView.loadUrl(mUrlList.get(mUrlList.size() - 1));
                    } else {
                        mPreShowPagerCount++;
                        mWebView.loadUrl(mUrlList.get(mPreShowPagerCount));
                    }
                    break;
            }
        }
    }

    private int mCurPosistion;

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            mCurPosistion = i;
            mShowPathTv.setText("Ruby，你选择了文件 ： " + mFileNameList.get(i));

            mUrlList.clear();
            readUrl();
            if (mUrlList.size() > 0) {
                mUrlCountTv.setText("共提取 " + mUrlList.size() + " 条链接");
            }
        }
    }

    class MyOnItemClickListener1 implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Toast.makeText(FansActivity.this, mUrlList.get(i).split("=K")[1], Toast.LENGTH_SHORT).show();
        }
    }
}
