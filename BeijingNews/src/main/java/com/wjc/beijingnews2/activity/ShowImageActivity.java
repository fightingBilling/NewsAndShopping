package com.wjc.beijingnews2.activity;

import android.app.Activity;
import android.os.Bundle;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wjc.beijingnews2.R;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ShowImageActivity extends Activity {

    private String url;
    private PhotoView iv_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        url = getIntent().getStringExtra("url");
        iv_photo = (PhotoView)findViewById(R.id.iv_photo);

        final PhotoViewAttacher attacher = new PhotoViewAttacher(iv_photo);
        Picasso.with(this)
                .load(url)
                .into(iv_photo, new Callback() {
                    @Override
                    public void onSuccess() {
                        attacher.update();
                    }

                    @Override
                    public void onError() {

                    }
                });

    }
}
