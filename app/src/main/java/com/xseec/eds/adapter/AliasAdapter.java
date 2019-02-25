package com.xseec.eds.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.model.Device;
import com.xseec.eds.util.TagsFilter;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/12/7.
 */

public class AliasAdapter extends RecyclerView.Adapter<AliasAdapter.ViewHolder> {
    private Context context;
    private LinkedHashMap<String, String> aliasMap;
    private List<String> dvNames;

    public AliasAdapter(Context context, LinkedHashMap<String, String> map) {
        this.context = context;
        this.aliasMap = map;
        dvNames = TagsFilter.getZoneAndDeviceList(TagsFilter.getDeviceList(null));
        this.aliasMap = new LinkedHashMap<>();
        for (String name : dvNames) {
            String alias;
            if (map.containsKey(name)) {
                alias = map.get(name);
            } else {
                Device device = Device.initWith(name);
                alias = device == null ? name : device.getDeviceAlias();
            }
            this.aliasMap.put(name, alias);
        }
    }

    public LinkedHashMap<String, String> getAliasMap() {
        return aliasMap;
    }

    private void initAliasMap() {
        List<String> tagList = TagsFilter.getDeviceList(null);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alias, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = dvNames.get(position);
        Device device = Device.initWith(name);
        holder.textName.setText(name);
        if (device == null) {
            holder.textName.setBackgroundResource(R.color.colorDivider);
        }
        holder.editAlias.setText(aliasMap.get(name));
    }

    @Override
    public int getItemCount() {
        return aliasMap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textName;
        private EditText editAlias;

        public ViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_name);
            editAlias = itemView.findViewById(R.id.edit_alias);
            editAlias.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    aliasMap.put(dvNames.get(getAdapterPosition()), s.toString());
                }
            });
        }
    }
}
