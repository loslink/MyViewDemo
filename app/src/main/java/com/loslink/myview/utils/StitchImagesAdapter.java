package com.loslink.myview.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.loslink.myview.R;
import com.loslink.myview.model.StitchImageInfo;

import java.util.ArrayList;
import java.util.List;



public class StitchImagesAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<StitchImageInfo> imageList = new ArrayList<>();//全部图片

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

//        Glide.with(mContext).load(item.getPath())
//                .centerCrop()
//                .into(imagesViewHolder.iv_image);

    }



    @Override
    public int getItemCount() {
        return imageList.size();
    }


    private class ImagesViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_image;

        public ImagesViewHolder(View view) {
            super(view);
            iv_image = (ImageView) view.findViewById(R.id.item_image);
        }
    }


    public interface OnStatusListenr{
        void onEdit(boolean edit);
        void onDataChange(List<StitchImageInfo> list);
    }

}
