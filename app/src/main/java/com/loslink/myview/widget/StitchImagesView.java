package com.loslink.myview.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.loslink.myview.R;
import com.loslink.myview.model.StitchImageInfo;
import com.loslink.myview.utils.DipToPx;
import com.loslink.myview.utils.StitchImagesAdapter;

import java.util.ArrayList;
import java.util.List;

public class StitchImagesView extends FrameLayout {

    private RecyclerView mRecyclerView;
    private StitchImagesAdapter adapter;
    private RelativeLayout rl_right;
    private Context context;

    public StitchImagesView(Context context) {
        this(context, null);
    }

    public StitchImagesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StitchImagesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        findViews(context);
    }

    private void findViews(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_stitch_view, this);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.rv_list);
        rl_right = (RelativeLayout) view.findViewById(R.id.rl_right);

        initRecyclerView();
    }

    private void initRecyclerView() {
        if (mRecyclerView != null) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            mRecyclerView.setLayoutManager(layoutManager);
            List<StitchImageInfo> list=new ArrayList<>();
            for(int i=0;i<10;i++){
                list.add(new StitchImageInfo());
            }
            adapter = new StitchImagesAdapter(context,list);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    refreshControllerView();
                }
            });

            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshControllerView();
                }
            },300);
            adapter.setRecyclerView(mRecyclerView);
        }
    }

    private void refreshControllerView(){
        int count=mRecyclerView.getChildCount();//缓存
        Log.e("mRecyclerView","count:"+count);
        int dataCount=adapter.getDatas().size();
        View firstChild=mRecyclerView.getChildAt(0);
        int firstPosition=mRecyclerView.getChildAdapterPosition(firstChild);
        for(int k=0;k<dataCount;k++){
            if(adapter.getDatas().get(k).getControllerView()==null){
                continue;
            }
            if(k<firstPosition || k>(mRecyclerView.getChildCount()-1+firstPosition)){
                adapter.getDatas().get(k).getControllerView().setVisibility(View.GONE);
            }else{
                adapter.getDatas().get(k).getControllerView().setVisibility(View.VISIBLE);
            }
        }
        for(int j=0;j<count;j++){
            View child=mRecyclerView.getChildAt(j);
            final int position=mRecyclerView.getChildAdapterPosition(child);
            View view = null;
            RelativeLayout.LayoutParams layoutParams;

            if(position==adapter.getDatas().size()-1){//最后一项不需要添加
                return;
            }
            final StitchImageInfo stitchImageInfo=adapter.getDatas().get(position);
            if(stitchImageInfo.getControllerView()!=null){
                view= stitchImageInfo.getControllerView();
                layoutParams= (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.topMargin=child.getBottom()-DipToPx.dipToPx(context,15);
                view.setLayoutParams(layoutParams);
            }else{
                view = LayoutInflater.from(context).inflate(R.layout.item_stitch_controller, null);
                layoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.topMargin=child.getBottom()-DipToPx.dipToPx(context,15);
                rl_right.addView(view,layoutParams);
                stitchImageInfo.setControllerView(view);
                final View finalView = view;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        StitchImageInfo nextInfo=adapter.getDatas().get(position+1);
                        if(stitchImageInfo.isEditing() && nextInfo.isEditing()){
                            stitchImageInfo.setToCut(true);
                            nextInfo.setToCut(true);
                        }else {
                            adapter.setAllNotCut();
                            adapter.setEditIndex(position);
                            adapter.setEdit(true);
                            stitchImageInfo.setEditing(true);
                            nextInfo.setEditing(true);
                            setControllerViewBg(stitchImageInfo, finalView,position);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
            setControllerViewBg(stitchImageInfo,view,position);
//            Log.e("mRecyclerView","index:"+j+"   position:"+position+"    bottom:"+child.getBottom());
        }
    }

    private void setControllerViewBg(StitchImageInfo info,View view,int position){
        ImageView iv_left=view.findViewById(R.id.iv_left);
        ImageView iv_right=view.findViewById(R.id.iv_right);
        if(info.isEditing() && position != adapter.getEditIndex()+1){
            iv_left.setBackgroundResource(R.mipmap.ic_stitch_yes);
            iv_right.setBackgroundResource(R.mipmap.ic_stitch_no);
        }else{
            iv_left.setBackgroundResource(R.mipmap.ic_stitch_cut);
            iv_right.setBackgroundResource(R.mipmap.ic_stitch_reset);
        }
    }

}
