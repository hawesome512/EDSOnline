package com.xseec.eds.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.activity.UserManageActivity;
import com.xseec.eds.fragment.UserListFragment;
import com.xseec.eds.model.servlet.Phone;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.ViewHelper;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Activity context;
    private List<Phone> phoneList;
    private UserAdapter.ViewHolder oldHolder;
    private DeleteListener deleteListener;

    public UserAdapter(Activity context, List<Phone> phoneList,DeleteListener listener) {
        this.context = context;
        this.phoneList = phoneList;
        this.deleteListener=listener;
    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserAdapter.ViewHolder holder, int position) {
        Phone phone = phoneList.get(position);
        holder.accountImage.setImageResource(phone.getUserType().getIconRes());
        holder.nameText.setText(phone.getName());
        holder.phoneText.setText(Generator.getPhoneShow(phone.getId()));
        holder.levelText.setText(phone.getUserType().getNameRes());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldHolder != null) {
                    //原先展开的Item收回
                    oldHolder.levelText.setEnabled(false);
                    oldHolder.modifyImage.setVisibility(View.GONE);
                    oldHolder.deleteImage.setVisibility(View.GONE);
                    if (oldHolder == holder) {
                        oldHolder = null;
                        return;
                    }
                }
                holder.levelText.setEnabled(true);
                holder.modifyImage.setVisibility(View.VISIBLE);
                holder.deleteImage.setVisibility(View.VISIBLE);
                oldHolder = holder;
            }
        });
    }

    @Override
    public int getItemCount() {
        return phoneList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView accountImage;
        private TextView nameText;
        private TextView phoneText;
        private TextView levelText;
        private ImageView modifyImage;
        private ImageView deleteImage;

        public ViewHolder(View itemView) {
            super(itemView);
            accountImage = itemView.findViewById(R.id.image_account);
            nameText = itemView.findViewById(R.id.text_name);
            phoneText = itemView.findViewById(R.id.text_phone);
            levelText = itemView.findViewById(R.id.text_level);
            modifyImage = itemView.findViewById(R.id.image_modify);
            deleteImage = itemView.findViewById(R.id.image_delete);
            //nj--修改用户
            modifyImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserManageActivity.start(context, UserListFragment.REQUEST_CREATE
                            , phoneList.get(getAdapterPosition()));
                }
            });

            //nj--删除用户
            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String phone = phoneList.get(getAdapterPosition()).getId();
                    String info = context.getString(R.string.user_delete_confirm, phone);
                    ViewHelper.checkExit(context, info, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            phoneList.remove(getAdapterPosition());
                            notifyDataSetChanged();
                            deleteListener.onDelete();
                        }
                    });
                }
            });
        }
    }

    public interface DeleteListener{
        void onDelete();
    }
}
