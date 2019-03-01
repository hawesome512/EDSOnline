package com.xseec.eds.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.databinding.ItemCardSubBinding;
import com.xseec.eds.model.TagListener;
import com.xseec.eds.model.User;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.TagsFilter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class TabBaseFragment extends ComFragment implements TagListener {

    @InjectView(R.id.layout_container)
    LinearLayout layoutContainer;
    @InjectView(R.id.progress)
    ProgressBar progress;

    public TabBaseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab_monitor, container, false);
        ButterKnife.inject(this, view);
        initLayout();
        return view;
    }

    protected abstract void initLayout();

    @Override
    public void onResume() {
        super.onResume();
    }

    protected View addCard(int titleRes, List<String> items) {
        CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.item_card,
                layoutContainer, false);
        LinearLayout layout = getLinearLayout(LinearLayout.VERTICAL);
        addTitle(layout, getString(titleRes));
        for (int i = 0; i < items.size(); i++) {
            List<Tag> tags = TagsFilter.filterDeviceTagList(tagList, items.get(i));
            if (tags != null && tags.size() > 0) {
                addSubItem(layout, tags.get(0), i == items.size() - 1);
            }
        }
        cardView.addView(layout);
        layoutContainer.addView(cardView);
        return cardView;
    }

    protected LinearLayout getLinearLayout(int orientation) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(orientation);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return layout;
    }

    protected void addTitle(ViewGroup parent, String title) {
        TextView textView = (TextView) getLayoutInflater().inflate(R.layout.item_card_title,
                parent, false);
        textView.setText(title);
        parent.addView(textView);
    }

    protected void addSubItem(ViewGroup parent, final Tag tag, boolean last) {
        View view = getLayoutInflater().inflate(R.layout.item_card_sub, parent, false);
        if (last) {
            view.findViewById(R.id.view_divider).setVisibility(View.GONE);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tagClickEnable( tag ,v)){
                    onTagClick(tag,v);
                }else {
                    User user= WAServicer.getUser();
                    String hintInfo=getString( R.string.device_modify_level,user.getLevelState() );
                    hintUserLevel( hintInfo );
                }
            }
        });
        ItemCardSubBinding binding = DataBindingUtil.bind(view);
        binding.setTag(tag);
        parent.addView(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onRefreshed(List<Tag> validTagList) {
        super.onRefreshed(validTagList);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(View.GONE);
            }
        });
    }
}
