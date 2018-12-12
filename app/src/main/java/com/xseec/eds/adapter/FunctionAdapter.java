package com.xseec.eds.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.model.Function;

import java.util.List;

/**
 * Created by Administrator on 2018/12/11.
 */

public class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.ViewHolder> {

    private Context context;
    private List<Function> functionList;
    private FunctionListener listener;

    public FunctionAdapter(Context context, List<Function> functionList, FunctionListener
            listener) {
        this.context = context;
        this.functionList = functionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_function, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Function function = functionList.get(position);
        holder.imageFunc.setImageResource(function.getImageRes());
        holder.textFunc.setText(function.getNameRes());
    }

    @Override
    public int getItemCount() {
        return functionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textFunc;
        ImageView imageFunc;

        public ViewHolder(View itemView) {
            super(itemView);
            textFunc = itemView.findViewById(R.id.text_function);
            imageFunc = itemView.findViewById(R.id.image_function);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onFunctionChanged(functionList.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public interface FunctionListener {
        void onFunctionChanged(Function function);
    }
}
