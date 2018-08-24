package com.xseec.eds.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.databinding.ItemCardSubBinding;
import com.xseec.eds.model.Tags.Tag;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class TabBaseFragment extends Fragment implements View.OnClickListener {

    @InjectView(R.id.layout_container)
    LinearLayout layoutContainer;

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
            Tag tag = new Tag(item);
            addSubItem(layout, tag);
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
}
