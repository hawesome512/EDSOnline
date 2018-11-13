package com.xseec.eds.adapter;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.model.servlet.Action;
import com.xseec.eds.util.DateHelper;

import java.util.List;

import static com.xseec.eds.util.EDSApplication.getContext;

//nj--action list adapter on 2018/11/2
public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ViewHolder> {

    private List<Action> actionList;
    private Context context;
    private boolean isSingleLine=true;

    public ActionAdapter(Context context,List<Action> actionList) {
        this.context = context;
        this.actionList = actionList;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view= LayoutInflater.from( context ).inflate( R.layout.item_action,parent,false );
        return new ViewHolder(view );
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {
        Action action= actionList.get(position);
        holder.imageAction.setImageResource( action.getImageType() );
        String user= getContext().getString( R.string.nav_account,action.getUser());
        holder.textUser.setText( user);
        holder.textTime.setText( DateHelper.getString( action.getTime() ));
        holder.textAction.setText( action.getActionInfo());
    }

    @Override
    public int getItemCount() {
        return actionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageAction;
        private TextView textUser;
        private TextView textTime;
        private TextView textAction;
        private RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super( itemView );
            imageAction = (ImageView) itemView.findViewById( R.id.image_type );
            textUser = (TextView) itemView.findViewById( R.id.text_user );
            textTime = (TextView) itemView.findViewById( R.id.text_time );
            textAction = (TextView) itemView.findViewById( R.id.text_action );
            relativeLayout = (RelativeLayout) itemView.findViewById( R.id.item_action );
            textAction.setEllipsize( TextUtils.TruncateAt.END );
            textAction.setSingleLine( true );
            itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (textAction.getLayout().getEllipsisCount( textAction.getLineCount() - 1 ) > 0) {
                        isSingleLine = false;
                        relativeLayout.setBackgroundColor( ContextCompat.getColor( getContext(),
                                R.color.colorDivider ) );
                        textAction.setEllipsize( null );
                        textAction.setSingleLine( false );
                    } else {
                        if (!isSingleLine) {
                            isSingleLine = true;
                            relativeLayout.setBackgroundColor( ContextCompat.getColor( getContext(),
                                    R.color.colorWhite ) );
                            textAction.setEllipsize( TextUtils.TruncateAt.END );
                            textAction.setSingleLine( true );
                        }
                    }
                }
            } );
        }
    }
}
