package com.ruby.preview.Activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ruby.preview.R;
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

public class MainActivity extends Activity {

    private final static String QQPATH = File.separatorChar + "Tencent" + File.separatorChar + "QQfile_recv";
    private MyOnclickListener mOnclickListener;
    private Button mSearchBtn, mReadBtn, mPreviewBtn;
    private Button mLastBtn, mNextBtn;
    private WebView mWebView;
    private List<String> mUrlList, mFileList;
    private TextView mStatusTv, mShowPathTv, mUrlCountTv, mCurPageCountTv, mJumpUrlTv;
    private int mPreShowPagerCount;
    private final String TEST_PATH = "http://mp.weixin.qq.com/s?__biz=MzIzMDA0NDE1MQ==" +
            "&tempkey=Mqipdv0LeLOOlORlWSaxcZZEvmTZ9jmhUOMLm1pBcndIq7%2BO5T5tnBuWiP9rS7" +
            "JF154R5NZ4n17lWrKEZmYseef4cy3fuVmsgjJS58zwfvqATecZrYaeKhdHQj8IDEQY8D2jqfQ" +
            "6RSGPmNydFatu6Q%3D%3D&chksm=730222e94475abffee693ffb8712b91a680a49c54a777" +
            "4eace942130620a44f7a1043b8814fa#rd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        mWebView.loadUrl(TEST_PATH);
    }

    private void initView() {
        mOnclickListener = new MyOnclickListener();
        mUrlList = new ArrayList<>();

        mSearchBtn = (Button) findViewById(R.id.main_searchBtn);
        mSearchBtn.setOnClickListener(mOnclickListener);
        mReadBtn = (Button) findViewById(R.id.main_readBtn);
        mReadBtn.setOnClickListener(mOnclickListener);
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
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        mWebView.setWebViewClient(new MyWebViewClient()); // 自动跳转
        mWebView.setWebChromeClient(new WebChromeClient());
    }

    private void readUrl() {
        try {
            InputStream inputStream = new FileInputStream(mFileList.get(0));
            Workbook workbook = Workbook.getWorkbook(inputStream);
            Sheet sheet = workbook.getSheet(0);
            int rows = sheet.getRows();
            int cols = sheet.getColumns();

            Log.i("niejianjian", " -> rows -> " + rows);
            Log.i("niejianjian", " -> cols -> " + cols);

            for (int i = 0; i < rows; i++) {
                StringBuilder builder = new StringBuilder("");
                if (sheet.getRow(i).length == 1) {
                    // getCell(cols,row)
                    String str = sheet.getCell(0, i).getContents();
                    if (str.contains("http")) {
                        builder.append(str);
                        Log.i("niejianjian", " -> builder -> " + builder);
                        mUrlList.add(builder.toString());
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
            /*mCurPageCountTv.setText((1 + mPreShowPagerCount) + " / " + mUrlList.size());*/
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
            }
            mJumpUrlTv.setText(builder);*/

            return false;
        }
    }

    class MyOnclickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.main_searchBtn:
                    mFileList = GetFileUtil.getFileList(QQPATH, ".xls", true);
                    mStatusTv.setText("搜索到 " + mFileList.size() + " 个.xls个文件！");
                    if (mFileList.size() > 0) {
                        mShowPathTv.setText("文件名称为：" + mFileList.get(0).split("\\/")[mFileList.get(0).split("\\/").length - 1]);
                    }
                    break;
                case R.id.main_readBtn:
                    readUrl();
                    if (mUrlList.size() > 0) {
                        mUrlCountTv.setText("共提取 " + mUrlList.size() + " 条链接");
                    }
                    break;
                case R.id.main_previewBtn:
                    if (mUrlList.size() > 0) {
                        mWebView.loadUrl(mUrlList.get(0));
                    } else {
                        Toast.makeText(MainActivity.this, "没有读取到连接啊", Toast.LENGTH_SHORT);
                    }
                    break;
                case R.id.main_lastBtn:
                    if (mPreShowPagerCount == 0) {
                        Toast.makeText(MainActivity.this, "前面已经没有了", Toast.LENGTH_SHORT).show();
                    } else {
                        mPreShowPagerCount--;
                        mWebView.loadUrl(mUrlList.get(mPreShowPagerCount));
                    }
                    break;
                case R.id.main_nextBtn:
                    if (mPreShowPagerCount == mUrlList.size()) {
                        Toast.makeText(MainActivity.this, "后面已经没有了", Toast.LENGTH_SHORT).show();
                        mWebView.loadUrl(mUrlList.get(mUrlList.size() - 1));
                    } else {
                        mPreShowPagerCount++;
                        mWebView.loadUrl(mUrlList.get(mPreShowPagerCount));
                    }
                    break;
            }
        }
    }
}
