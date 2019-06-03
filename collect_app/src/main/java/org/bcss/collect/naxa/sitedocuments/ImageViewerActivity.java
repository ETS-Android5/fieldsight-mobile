package org.bcss.collect.naxa.sitedocuments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.CircularProgressDrawable;
import android.widget.ImageView;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.common.GlideApp;
import org.bcss.collect.naxa.common.ImageUtils;
import org.odk.collect.android.activities.CollectAbstractActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.bcss.collect.naxa.common.Constant.EXTRA_MESSAGE;

public class ImageViewerActivity extends CollectAbstractActivity {


    @BindView(R.id.iv_image_viewer)
    ImageView ivImageViewer;
    private String url;

    public static void start(Context context, String list) {
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtra(EXTRA_MESSAGE, list);
        context.startActivity(intent);
    }

    public static void startFromFile(Context context, String list) {
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtra(EXTRA_MESSAGE, list);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        ButterKnife.bind(this);
        url = getIntent().getExtras().getString(EXTRA_MESSAGE);

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);


        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();


        GlideApp.with(getApplicationContext())
                .load(new File(url))
                .into(ivImageViewer);
    }
}
