package com.ruby.preview.Activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.ruby.preview.utils.CycleProgressDialog;
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

/**
 * Created by jian on 2016/12/2.
 */
public class PreviewActivity extends Activity {

    private final static String QQPATH = File.separatorChar + "Tencent" + File.separatorChar + "QQfile_recv";
    private MyOnclickListener mOnclickListener;
    private Button mSearchBtn, mPreviewBtn, mGoBottomBtn, mGoTopBtn;
    private Button mLastBtn, mNextBtn;
    private WebView mWebView;
    private ListView mListView;
    private List<String> mUrlList, mFileList, mFileNameList;
    private TextView mStatusTv, mShowPathTv, mUrlCountTv, mCurPageCountTv, mJumpUrlTv;
    private int mPreShowPagerCount;
    private WindowManager wm;
    private final String TEST_PATH = "http://mp.weixin.qq.com/s?__biz=MzAxMzY4MTA5MA==&" +
            "tempkey=RyKgWnDAIn0IenE%2BCHbo8bA1UMK8ySdnZz8LBfCSpDaM2BU0CR2JUYL8X6L1ov0H" +
            "jiCuCCc5mNV9Vh17IkggBG8FGFcL2EbMILSNrpuK%2B1%2BYntcMG0R18I%2BVZZHC%2BPx2y7" +
            "q1k9%2FtDiEkFvGYCnDBPg%3D%3D&chksm=007d3fe9370ab6ff05744b24e0959daf122fb1f" +
            "83642f08c39f9d4450b5a36b7e1952a81419c#rd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        initView();
//        mWebView.loadUrl(TEST_PATH);
    }

    private void initView() {
        mOnclickListener = new MyOnclickListener();
        mUrlList = new ArrayList<>();
        mFileNameList = new ArrayList<>();
        mFileList = new ArrayList<>();
        mListView = (ListView) findViewById(R.id.main_listview);
        mListView.setOnItemClickListener(new MyOnItemClickListener());
        wm = this.getWindowManager();

        mSearchBtn = (Button) findViewById(R.id.main_searchBtn);
        mSearchBtn.setOnClickListener(mOnclickListener);
        mPreviewBtn = (Button) findViewById(R.id.main_previewBtn);
        mPreviewBtn.setOnClickListener(mOnclickListener);
        mLastBtn = (Button) findViewById(R.id.main_lastBtn);
        mLastBtn.setOnClickListener(mOnclickListener);
        mNextBtn = (Button) findViewById(R.id.main_nextBtn);
        mNextBtn.setOnClickListener(mOnclickListener);
        mGoBottomBtn = (Button) findViewById(R.id.main_goBottomBtn);
        mGoBottomBtn.setOnClickListener(mOnclickListener);
        mGoTopBtn = (Button) findViewById(R.id.main_goTopBtn);
        mGoTopBtn.setOnClickListener(mOnclickListener);

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
        dialog = new CycleProgressDialog.Builder(this).message("加载中，Ruby别急...").build();
        getFileList();
    }

    private void readUrl() {
        try {
            InputStream inputStream = new FileInputStream(mFileList.get(mCurPosistion));
            Workbook workbook = Workbook.getWorkbook(inputStream);
            Sheet sheet = workbook.getSheet(0);
            int rows = sheet.getRows();
            int cols = sheet.getColumns();

            Log.i("niejianjian", " -> rows -> " + rows);
            Log.i("niejianjian", " -> cols -> " + cols);

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    String strUrl = sheet.getCell(j, i).getContents();
                    Log.i("niejianjian", " -> builder -> " + strUrl);
                    if (strUrl.contains("http")) {
                        mUrlList.add(strUrl);
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

    CycleProgressDialog dialog = null;

    class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.i("niejianjian", " -> onPageStarted -> ");
            dialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            /*Log.i("niejianjian", " -> onPageFinished -> " + mWebView.getContentHeight() * mWebView.getScale());
            mWebView.setScrollY((int) (mWebView.getContentHeight() * mWebView.getScale()
                    - mWebView.getMeasuredHeight()) + 100);*/
            mCurPageCountTv.setText((1 + mPreShowPagerCount) + " / " + mUrlList.size());
            dialog.cancel();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("niejianjian", " -> request -> url = " + url);
            /*String[] str = url.split("boy");
            SpannableStringBuilder builder = new SpannableStringBuilder();
            for (int i = 0; i < str.length; i++) {
                builder.append(str[i]);
                if (i != str.length - 1) {
                    builder.append(new Font("boy").color(Color.parseColor("#FF0000")));
                }
            }*/
            mJumpUrlTv.setText(url);

            return false;
        }
    }

    private void getFileList() {
        mListView.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.GONE);
        mFileList.clear();
        mFileNameList.clear();
        mFileList = GetFileUtil.getFileList(QQPATH, ".xls", true);
        mStatusTv.setText("搜索到 " + mFileList.size() + " 个.xls个文件！");
        if (mFileList.size() > 0) {
            for (String s : mFileList) {
                String name = s.split("\\/")[s.split("\\/").length - 1];
                mFileNameList.add(name);
            }
            String[] urlBuff = mFileNameList.toArray(new String[mFileList.size()]);
//                        String[] urlBuff = (String[]) mFileNameList.toArray(new String[0]);
            mListView.setAdapter(new ArrayAdapter<String>(PreviewActivity.this, android.R.layout.simple_list_item_1, urlBuff));
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
                        Toast.makeText(PreviewActivity.this, "没有读取到连接啊", Toast.LENGTH_SHORT);
                    }
                    break;
                case R.id.main_lastBtn:
                    if (mPreShowPagerCount == 0) {
                        Toast.makeText(PreviewActivity.this, "前面已经没有了", Toast.LENGTH_SHORT).show();
                    } else {
                        mPreShowPagerCount--;
                        mWebView.loadUrl(mUrlList.get(mPreShowPagerCount));
                    }
                    break;
                case R.id.main_nextBtn:
                    if (mPreShowPagerCount == mUrlList.size()) {
                        Toast.makeText(PreviewActivity.this, "后面已经没有了", Toast.LENGTH_SHORT).show();
                        mWebView.loadUrl(mUrlList.get(mUrlList.size() - 1));
                    } else {
                        mPreShowPagerCount++;
                        mWebView.loadUrl(mUrlList.get(mPreShowPagerCount));
                    }
                    break;
                case R.id.main_goBottomBtn:
//                    Log.i("niejianjian", " -> onPageFinished -> " + mWebView.getContentHeight() * mWebView.getScale());
//                    mWebView.setScrollY((int) (mWebView.getContentHeight() * mWebView.getScale()
//                            - mWebView.getMeasuredHeight() - wm.getDefaultDisplay().getHeight() / 3));
                    mWebView.findAll("阅读原文");
                    break;
                case R.id.main_goTopBtn:
                    mWebView.setScrollY(0);
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
}
