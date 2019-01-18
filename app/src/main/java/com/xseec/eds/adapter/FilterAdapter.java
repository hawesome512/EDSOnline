package com.xseec.eds.adapter;

import android.content.Context;
import android.support.constraint.Group;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.model.FilterLabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xseec.eds.util.EDSApplication.getContext;

public class FilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private OnItemClick listener;
    //nj--一级、二级列表数据
    private List<FilterLabel> labelList=new ArrayList<>(  );
    private List<FilterLabel> subLabelList=new ArrayList<>(  );
    private Context context;

    private Map<String,FilterLabel> oldCheckLabel= new HashMap<>();

    public FilterAdapter(Context context,List<FilterLabel> totalLabelList ){
        this.context=context;
        for (FilterLabel itemLabel:totalLabelList){
            if (itemLabel.isGroup()){
                labelList.add( itemLabel );
            }else {
                subLabelList.add( itemLabel );
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (labelList.get( viewType ).getState()==FilterLabel.HEADER){
            View view= LayoutInflater.from( context ).inflate( R.layout.item_filter_header,parent ,false);
            return new HeaderViewHolder( view );
        }else {
            View view = LayoutInflater.from( context ).inflate(  R.layout.item_filter_label, parent,false);
            return new LabelViewHolder( view );
        }
    }

    @Override
    public int getItemViewType(int position) {
        return labelList.get( position ).getState();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FilterLabel label = labelList.get(position);
        if (label.getState() == FilterLabel.LABLE) {
            CheckedTextView textView= ((LabelViewHolder)holder).labelText;
            textView.setChecked( label.isChecked() );
            textView.setText( label.getLabel() );
            textView.setTag(label);
            onCheckClick( textView );
        }else {
            ((HeaderViewHolder)holder).headerText.setText(  label.getLabel() );
        }
    }

    private void onCheckClick(final CheckedTextView textView) {
        textView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterLabel tag=(FilterLabel)v.getTag();
                FilterLabel oldLabel=oldCheckLabel.get( tag.getGroupName() );
                removeSubItems( tag );
                if (oldLabel!=null){
                    oldLabel.setChecked( false );
                    if (oldLabel==tag){
                        oldCheckLabel.remove( tag.getGroupName() );
                        if (listener!=null)
                            listener.onChargeCheck(tag);
                        notifyDataSetChanged();
                        return;
                    }
                }
                tag.setChecked( true );
                addSubItems( tag );
                oldCheckLabel.put( tag.getGroupName(),tag );
                if (listener!=null)
                    listener.onChargeCheck(tag);
                notifyDataSetChanged();
            }
        } );
    }


    @Override
    public int getItemCount() {
        return labelList.size();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
       TextView headerText;
        public HeaderViewHolder(View itemView) {
            super( itemView );
            headerText=itemView.findViewById( R.id.item_header );
        }
    }

    public class LabelViewHolder extends RecyclerView.ViewHolder {
        CheckedTextView labelText;
        public LabelViewHolder(View itemView) {
            super( itemView );
            labelText=itemView.findViewById(R.id.item_label );
        }
    }

    public void setOnClickListener(OnItemClick listenner){
        this.listener=listenner;
    }

    public interface OnItemClick{
        void onChargeCheck(FilterLabel item);
    }

    //nj--添加二级列表
    private void addSubItems(FilterLabel item){
        if (subLabelList.size()==0){
            return;
        }
        for (FilterLabel subLabel:subLabelList){
            if (item.isGroup()&&subLabel.getType().equals( item.getValue() )){
                labelList.add( subLabel );
            }
        }
    }

    //nj--移除二级列表
    private void removeSubItems(FilterLabel item){
        if (subLabelList.size()==0||!item.isGroup()){
            return;
        }
        for (int i=labelList.size()-1;i>=0;i--){
            if (!labelList.get( i ).isGroup()){
                labelList.remove( i );
            }
        }
    }

    public Map<String,FilterLabel> getCheckLabel(){
        return oldCheckLabel;
    }

}
