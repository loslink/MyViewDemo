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
import com.loslink.myview.model.StitchHistoryAction;
import com.loslink.myview.model.StitchImageInfo;
import com.loslink.myview.utils.DipToPx;
import com.loslink.myview.utils.StitchImagesAdapter;

import java.util.ArrayList;
import java.util.List;
/**
 * 长拼图
 * @author loslink
 * @time 2018/12/27 15:51
 */
public class StitchImagesView extends FrameLayout {

    private RecyclerView mRecyclerView;
    private StitchImagesAdapter adapter;
    private RelativeLayout rl_right;
    private Context context;
    public static int actionCursor=0;
    private int controllerPosition=StitchImagesAdapter.EDIT_INDEX_EMPTY;
    private List<String> listImagePath;
    private List<StitchImageInfo> imageInfoList=new ArrayList<>();

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

    public List<StitchImageInfo> getImageInfoList() {
        return imageInfoList;
    }

    public List<String> getListImagePath() {
        return listImagePath;
    }

    public void setListImagePath(List<String> listImagePath) {
        this.listImagePath = listImagePath;
        initRecyclerView();
    }


    private void findViews(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_stitch_view, this);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.rv_list);
        rl_right = (RelativeLayout) view.findViewById(R.id.rl_right);

        initRecyclerView();
    }

    private void initRecyclerView() {
        if(listImagePath==null){
            return;
        }
        if (mRecyclerView != null) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            mRecyclerView.setLayoutManager(layoutManager);

            for(int i=0;i<listImagePath.size();i++){
                StitchImageInfo stitchImageInfo=new StitchImageInfo();
                stitchImageInfo.setPath(listImagePath.get(i));
                imageInfoList.add(stitchImageInfo);
            }
            adapter = new StitchImagesAdapter(context,imageInfoList);
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
            },200);
            adapter.setRecyclerView(mRecyclerView);
            adapter.setOnStitchImagesListenr(new StitchImagesAdapter.OnStitchImagesListenr() {
                @Override
                public void onLoadHolderPosition(int position) {
//                    if(controllerPosition == position){
//                        refreshControllerView();
//                    }
                }
            });
        }
    }


    /**
     * 刷新右控制杆显示
     */
    private void refreshControllerView(){
        int count=mRecyclerView.getChildCount();//缓存
        if(count<=0){
            return;
        }
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
            if(position<0 || position > adapter.getDatas().size()-1){
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
                setControllerLister(stitchImageInfo,view,position);
            }

            ImageView iv_right=view.findViewById(R.id.iv_right);
            final StitchImageInfo nextInfo=adapter.getDatas().get(position+1);
            //含有历史动作且非编辑状态 || 编辑状态
            if(hasHistoryByCuter(stitchImageInfo,nextInfo,position) || (adapter.isEdit() && stitchImageInfo.isEditing() && nextInfo.isEditing())){
                iv_right.setVisibility(View.VISIBLE);
            }else {
                iv_right.setVisibility(View.GONE);
            }
            setControllerViewBg(stitchImageInfo,nextInfo,view,position);
//            Log.e("mRecyclerView","index:"+j+"   position:"+position+"    bottom:"+child.getBottom());
        }
    }

    /**
     * 该剪刀是否有剪裁记录
     * @param thatInfo
     * @param nextInfo
     * @param cuterIndex
     * @return
     */
    private boolean hasHistoryByCuter(StitchImageInfo thatInfo,StitchImageInfo nextInfo,int cuterIndex){
        if(((thatInfo.getHistoryActions()!=null
                && thatInfo.getHistoryActions().size()>0)
                || (nextInfo.getHistoryActions()!=null
                && nextInfo.getHistoryActions().size()>0))
                && !thatInfo.isEditing()
                && !nextInfo.isEditing()
                && !adapter.isEdit()
                ){
            if(thatInfo.getHistoryActions()!=null){
                for(int i=0;i<thatInfo.getHistoryActions().size();i++){
                    if(thatInfo.getHistoryActions().get(i).getCuterIndex()==cuterIndex){
                        return true;
                    }
                }
            }

            if(nextInfo.getHistoryActions()!=null){
                for(int i=0;i<nextInfo.getHistoryActions().size();i++){
                    if(nextInfo.getHistoryActions().get(i).getCuterIndex()==cuterIndex){
                        return true;
                    }
                }
            }

            return false;
        }else{
            return false;
        }
    }

    /**
     * 返回剪刀的历史动作
     * tips：两item对比，返回最新的动作
     * @param thatInfo
     * @param nextInfo
     * @param cuterIndex
     */
    private void goBackByHistory(StitchImageInfo thatInfo,StitchImageInfo nextInfo,int cuterIndex){

        StitchHistoryAction thatAction = null,nextAction = null;
        if(thatInfo.getHistoryActions()!=null){
            for(int i=thatInfo.getHistoryActions().size()-1;i>=0;i--){
                if(thatInfo.getHistoryActions().get(i).getCuterIndex()==cuterIndex){//取最新一个
                    thatAction=thatInfo.getHistoryActions().get(i);
                    break;
                }
            }
        }

        if(nextInfo.getHistoryActions()!=null){
            for(int i=nextInfo.getHistoryActions().size()-1;i>=0;i--){
                if(nextInfo.getHistoryActions().get(i).getCuterIndex()==cuterIndex){//取最新一个
                    nextAction=nextInfo.getHistoryActions().get(i);
                    break;
                }
            }
        }

        if(thatAction!=null && nextAction!=null){
            if(thatAction.getActionIndex() > nextAction.getActionIndex()){//thatAction 在前
                thatInfo.getHistoryActions().remove(thatAction);
            }else if(thatAction.getActionIndex() < nextAction.getActionIndex()){//nextAction 在前
                nextInfo.getHistoryActions().remove(nextAction);
            }else{//同时期 同剪刀
                thatInfo.getHistoryActions().remove(thatAction);
                nextInfo.getHistoryActions().remove(nextAction);
            }
        }else if(thatAction!=null){//只存在thatAction
            thatInfo.getHistoryActions().remove(thatAction);
        }else if(nextAction!=null){//只存在nextAction
            nextInfo.getHistoryActions().remove(nextAction);
        }
    }

    /**
     * 设置控制杆的监听
     * @param stitchImageInfo
     * @param controllerView
     * @param editIndex
     */
    private void setControllerLister(final StitchImageInfo stitchImageInfo, View controllerView, final int editIndex){
        final ImageView iv_left=controllerView.findViewById(R.id.iv_left);
        final ImageView iv_right=controllerView.findViewById(R.id.iv_right);
        final StitchImageInfo nextInfo=adapter.getDatas().get(editIndex+1);

        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                if(stitchImageInfo.isEditing() && nextInfo.isEditing()){
                    stitchImageInfo.setToCut(true);
                    nextInfo.setToCut(true);
                    stitchImageInfo.setEditing(false);
                    nextInfo.setEditing(false);
                    adapter.setEdit(false);
//                    adapter.setEditIndex(StitchImagesAdapter.EDIT_INDEX_EMPTY);
                    actionCursor++;//每裁剪一次加1
                }else {
                    adapter.setAllNotCut();
                    adapter.setAllNotEditting();
                    adapter.setEditIndex(editIndex);
                    adapter.setEdit(true);
                    stitchImageInfo.setEditing(true);
                    nextInfo.setEditing(true);
                }
                controllerPosition = editIndex;
                adapter.notifyDataSetChanged();
                iv_left.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshControllerView();
                    }
                },100);
            }
        });

        iv_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stitchImageInfo.isEditing() && nextInfo.isEditing()){//正在编辑的控制杆-取消
                    adapter.setAllNotCut();
                    stitchImageInfo.setEditing(false);
                    nextInfo.setEditing(false);
                    adapter.setEdit(false);
                }else {//正常控制杆-返回上一步
                    goBackByHistory(stitchImageInfo,nextInfo,editIndex);
                }
                adapter.notifyDataSetChanged();
                iv_right.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshControllerView();
                    }
                },100);
            }
        });
    }

    private void setControllerViewBg(StitchImageInfo info,StitchImageInfo nextInfo,View view,int position){
        ImageView iv_left=view.findViewById(R.id.iv_left);
        ImageView iv_right=view.findViewById(R.id.iv_right);
        if(info.isEditing() && nextInfo.isEditing() && adapter.isEdit()){
            iv_left.setBackgroundResource(R.mipmap.ic_stitch_yes);
            iv_right.setBackgroundResource(R.mipmap.ic_stitch_no);
        }else{
            iv_left.setBackgroundResource(R.mipmap.ic_stitch_cut);
            iv_right.setBackgroundResource(R.mipmap.ic_stitch_reset);
        }
    }

    public void onDestroy(){
        actionCursor=0;
    }


}
