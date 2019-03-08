package com.xseec.eds.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.activity.SettingActivity;
import com.xseec.eds.model.Custom;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.util.UserLevelHelper;
import com.xseec.eds.util.ViewHelper;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private Activity context;
    private List<Custom> customList;

    public CustomAdapter(Activity context, List<Custom> settingList){
        this.context=context;
        this.customList=settingList;
    }
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from( context ).inflate( R.layout.item_setting,parent ,false);
        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {
        Custom custom=customList.get( position );
        holder.imageRes.setImageResource( custom.getImageRes() );
        holder.textName.setText( custom.getNameRes() );
    }

    @Override
    public int getItemCount() {
        return customList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageRes;
        TextView textName;
        public ViewHolder(View itemView) {
            super( itemView );
            imageRes=itemView.findViewById( R.id.image_res );
            textName=itemView.findViewById( R.id.text_name );
            itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UserLevelHelper.checkCustom()){
                        SettingActivity.start( context,customList.get( getAdapterPosition() ) );
                    }else{
                        String info=context.getString( R.string.setting_limit, WAServicer.getUser().getLevelState() );
                        ViewHelper.singleAlertDialog( context,info,null );
                    }
                }
            } );
        }
    }
}
