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
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.TagsFilter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class TabBaseFragment extends ComFragment implements View.OnClickListener {

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

    protected void addCard(String title, List<String> items) {
        CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.item_card,
                layoutContainer, false);
        LinearLayout layout = getLinearLayout(LinearLayout.VERTICAL);
        addTitle(layout, title);
        for (String item : items) {
            List<Tag> tags = TagsFilter.filterDeviceTagList(tagList, item);
            if (tags != null && tags.size() > 0) {
                addSubItem(layout, tags.get(0));
            }
        }
        cardView.addView(layout);
        layoutContainer.addView(cardView);
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

    protected void addSubItem(ViewGroup parent, Tag tag) {
        View view = getLayoutInflater().inflate(R.layout.item_card_sub, parent, false);
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
        for (int i = 0; i < validTagList.size(); i++) {
            tagList.get(i).setTagValue(validTagList.get(i).getTagValue());
        }
    }
}
