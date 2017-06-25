package com.yangsl.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.yangsl.library.zhfab.ZhihuFabLayout;
import com.yangsl.library.zhfab.ZhihuMenuLayout;

public class MainActivity extends AppCompatActivity {

    private ZhihuFabLayout mZhihuFabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mZhihuFabLayout = (ZhihuFabLayout) findViewById(R.id.zhihu_fablayout);
        mZhihuFabLayout.addMenu("Comment", R.drawable.ic_comment_black_24dp);
        mZhihuFabLayout.setOnFabItemClickListener(new ZhihuFabLayout.OnFabItemClickListener() {
            @Override
            public void onFabItemClick(ZhihuMenuLayout view, int pos) {
                Toast.makeText(MainActivity.this, view.getTagText(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
