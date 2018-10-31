package com.xseec.eds.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.entity.LocalMedia;
import com.xseec.eds.R;
import com.xseec.eds.util.PhotoPicker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/24.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private Activity context;
    private List<LocalMedia> localMediaList;
    private final static int MAX = 12;
    private boolean addable = false;

    public PhotoAdapter(Activity context, List<LocalMedia> localMediaList) {
        this.context = context;
        this.localMediaList = localMediaList;
    }

    public void setLocalMediaList(List<LocalMedia> localMedias){
        localMediaList=new ArrayList<>();
        localMediaList.addAll(localMedias);
        notifyDataSetChanged();
    }

    public List<LocalMedia> getLocalMediaList(){
        return localMediaList;
    }

    public void addMediaList(List<LocalMedia> localMedias) {
        localMediaList.addAll(localMedias);
        notifyDataSetChanged();
    }

    public void setAddable(boolean addable) {
        this.addable = addable;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_filter_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (position < localMediaList.size()) {
            //显示图片
            holder.imageRemove.setVisibility(addable?View.VISIBLE:View.INVISIBLE);
            holder.imageRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    localMediaList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, localMediaList.size());
                }
            });
            LocalMedia media = localMediaList.get(position);
            String path = PhotoPicker.getFinalPath(media);
            Glide.with(context).load(path).into(holder.imageContent);
            holder.imageContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoPicker.previewSelectedPhotos(context, localMediaList, position);
                }
            });
        } else {
            //添加图片
            holder.imageRemove.setVisibility(View.INVISIBLE);
            holder.imageContent.setImageResource(R.drawable.ic_add_grey_300_24dp);
            holder.imageContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoPicker.addPhotos(context, MAX - localMediaList.size());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (addable) {
            return localMediaList.size() < MAX ? localMediaList.size() + 1 : MAX;
        }else {
            return localMediaList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageContent;
        ImageView imageRemove;

        public ViewHolder(View itemView) {
            super(itemView);
            imageContent = itemView.findViewById(R.id.image_content);
            imageRemove = itemView.findViewById(R.id.image_remove);
        }
    }
}
