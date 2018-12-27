package com.loslink.myview;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.loslink.myview.widget.StitchImagesView;

public class StitchImagesActivity extends Activity {

    private StitchImagesView stitchImagesView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);

        stitchImagesView=findViewById(R.id.stitchImagesView);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stitchImagesView.onDestroy();
    }
}
