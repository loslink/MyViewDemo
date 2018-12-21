package com.loslink.myview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.loslink.myview.model.AnimalViewHolder;
import com.loslink.myview.model.ItemAnimal;
import com.loslink.myview.model.ItemCartoon;
import com.loslink.myview.model.ItemDrawable;
import com.loslink.myview.model.ItemScenic;
import com.loslink.myview.utils.LinearDividerItemDecoration;
import com.loslink.myview.utils.RecyclerListAdapter;
import com.loslink.myview.widget.JunkCleanView;
import com.loslink.myview.widget.RegionView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends Activity {

    private RecyclerView mRecyclerView;
    RecyclerListAdapter adapter;
    RelativeLayout rl_right;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        rl_right=findViewById(R.id.rl_right);

        initRecyclerView();
    }

    private void initRecyclerView() {
        if (mRecyclerView != null) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(layoutManager);
            adapter = onCreateAdapter();
            if (adapter != null) {
                mRecyclerView.setAdapter(adapter);
            }
            adapter.addAll(createItemDrawableList());
            RecyclerView.ItemDecoration itemDecoration = createDividerItemDecoration();
            if (itemDecoration != null) {
                mRecyclerView.addItemDecoration(itemDecoration);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                        int count=mRecyclerView.getChildCount();//缓存
                        Log.e("mRecyclerView","count:"+count);
                        for(int j=0;j<count;j++){
                            View child=mRecyclerView.getChildAt(j);
                            int position=mRecyclerView.getChildAdapterPosition(child);

                            Log.e("mRecyclerView","index:"+j+"   position:"+position+"    bottom:"+child.getBottom());
                            ImageView imageView = null;
                            RelativeLayout.LayoutParams layoutParams;
                            if(lastPosition!=position){
                                imageView=new ImageView(RecyclerViewActivity.this);
                                imageView.setImageResource(R.mipmap.ic_junk_ufo);
                                layoutParams=new RelativeLayout.LayoutParams(100, 100);
                                layoutParams.topMargin=child.getBottom();
                                rl_right.addView(imageView,layoutParams);
                            }else{
                                imageView= (ImageView) rl_right.getChildAt(0);
                                layoutParams= (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                                layoutParams.topMargin=child.getBottom();
                                imageView.setLayoutParams(layoutParams);
                            }

                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(RecyclerViewActivity.this,"hahaha",Toast.LENGTH_SHORT).show();
                                }
                            });
                            lastPosition=position;
                        }
                    }
                });

            }

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        }
    }

    private int lastPosition=-1;

    public RecyclerListAdapter onCreateAdapter() {
        return new RecyclerListAdapter() {
            {
                addViewType(ItemAnimal.class, new ViewHolderFactory<ViewHolder>() {
                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent) {
                        return new AnimalViewHolder(parent);
                    }
                });

            }
        };
    }

    private RecyclerView.ItemDecoration createDividerItemDecoration() {
        LinearDividerItemDecoration dividerItemDecoration = new LinearDividerItemDecoration(
                this, LinearDividerItemDecoration.LINEAR_DIVIDER_VERTICAL);

        dividerItemDecoration.registerTypeDrawable(getItemViewType(ItemAnimal.class),
                new LinearDividerItemDecoration.DrawableCreator() {
                    @Override
                    public Drawable create(RecyclerView parent, int adapterPosition) {
                        return getResources().getDrawable(R.drawable.bg_animal_divider);
                    }
                });

        return dividerItemDecoration;
    }

    public int getItemViewType(Class<?> clazz) {
        return adapter.getItemViewType(clazz);
    }
    private List<ItemDrawable> createItemDrawableList() {
        List<ItemDrawable> itemDrawableList = new ArrayList<>();
        // ItemAnimal
        for(int i=0;i<40;i++){
            itemDrawableList.add(new ItemAnimal(R.mipmap.girl, "girl"));
        }

        return itemDrawableList;
    }
}
