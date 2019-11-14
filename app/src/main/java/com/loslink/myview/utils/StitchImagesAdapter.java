package com.loslink.myview.utils;

import android.content.Context;
import android.graphics.RectF;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loslink.myview.R;
import com.loslink.myview.model.StitchHistoryAction;
import com.loslink.myview.model.StitchImageInfo;
import com.loslink.myview.widget.RegionView;

import java.util.ArrayList;
import java.util.List;



public class StitchImagesAdapter extends RecyclerView.Adapter {

    public static final int EDIT_INDEX_EMPTY=-2;
    private Context mContext;
    private List<StitchImageInfo> imageList = new ArrayList<>();//全部图片
    private int editIndex = EDIT_INDEX_EMPTY;//编辑的开始项下标，共两个编辑项，下一个为：editIndex+1
    private boolean isEdit = false;
    private RecyclerView recyclerView;
    private OnStitchImagesListenr onStitchImagesListenr;

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public OnStitchImagesListenr getOnStitchImagesListenr() {
        return onStitchImagesListenr;
    }

    public void setOnStitchImagesListenr(OnStitchImagesListenr onStitchImagesListenr) {
        this.onStitchImagesListenr = onStitchImagesListenr;
    }


    public StitchImagesAdapter(Context context,List<StitchImageInfo> list) {
        mContext = context;
        imageList=list;
    }

    public List<StitchImageInfo> getDatas() {
        return imageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stitch_image, parent, false);
        return new ImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ImagesViewHolder imagesViewHolder = (ImagesViewHolder) holder;
        if(imageList.size()-1 < position){
            return;
        }
        final StitchImageInfo item = imageList.get(position);

        if(position==0){
            imagesViewHolder.regionView.setMode(RegionView.MODE_TOP);
        }else if(position==imageList.size()-1){
            imagesViewHolder.regionView.setMode(RegionView.MODE_BOTTOM);
        }else {
            imagesViewHolder.regionView.setMode(RegionView.MODE_NOMAL);
        }

        if(isEdit && editIndex>=0 && (position==editIndex || position==editIndex+1)){
            imagesViewHolder.regionView.setEdit(true);
            item.setEditing(true);
        }else{
            imagesViewHolder.regionView.setEdit(false);
            item.setEditing(false);
        }
        imagesViewHolder.regionView.setPath("position:"+position);

        imagesViewHolder.regionView.setCropRectF(item.getCurrentCropRectF());

        imagesViewHolder.regionView.setHistoryActions(item.getHistoryActions());

        imagesViewHolder.regionView.setOnRegionViewListenr(new RegionView.OnRegionViewListenr() {
            @Override
            public void onCropRect(RectF cropRectF) {
                item.setCurrentCropRectF(cropRectF);
            }

            @Override
            public void onHistoryAction(List<StitchHistoryAction> historyActions) {
                item.setHistoryActions(historyActions);
                notifyData(recyclerView,position);

            }

            @Override
            public void onScaleCallback(float scale) {
                item.setScaleValue(scale);
            }
        });

        if(item.isToCut()){//最后才剪裁
            item.setToCut(false);
            imagesViewHolder.regionView.cutImage(editIndex);
        }

        if(onStitchImagesListenr!=null){
            onStitchImagesListenr.onLoadHolderPosition(position);
        }
    }

    public void notifyData(final RecyclerView recyclerView, final int position) {
        if (recyclerView.isComputingLayout()) {
            // 延时递归处理。
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyData(recyclerView,position);
                }
            },100);
        } else {
            notifyItemChanged(position);
        }
    }

    public int getEditIndex() {
        return editIndex;
    }

    public void setEditIndex(int editIndex) {
        this.editIndex = editIndex;
    }

    /**
     * 把所有项设置为非剪裁模式
     */
    public void setAllNotCut() {
        for(int i=0;i<imageList.size();i++){
            imageList.get(i).setToCut(false);
        }
    }

    /**
     * 把所有项设置为非编辑中模式
     */
    public void setAllNotEditting() {
        for(int i=0;i<imageList.size();i++){
            imageList.get(i).setEditing(false);
        }
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }


    @Override
    public int getItemCount() {
        return imageList.size();
    }


    private class ImagesViewHolder extends RecyclerView.ViewHolder {

        private RegionView regionView;

        public ImagesViewHolder(View view) {
            super(view);
            regionView = (RegionView) view.findViewById(R.id.item_image);
        }
    }


    public interface OnStitchImagesListenr{
        void onLoadHolderPosition(int position);
    }

}
