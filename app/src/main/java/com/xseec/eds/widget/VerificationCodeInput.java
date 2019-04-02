package com.xseec.eds.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.xseec.eds.R;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

/**
 * Created by Administrator on 2018/9/11.
 */

public class VerificationCodeInput extends LinearLayout implements TextWatcher, View.OnKeyListener {

    private static final String TYPE_NUMBER = "number";
    private static final String TYPE_TEXT = "text";
    private static final String TYPE_PHONE = "phone";
    private static final String TYPE_PASSWORD = "password";

    private int box = 4;
    private int boxWidth = 80;
    private int boxHeight = 80;
    private int childPadding = 16;
    private int childMargin = 16;
    private String inputType = TYPE_NUMBER;
    private Drawable boxBgFocus = null;
    private Drawable boxBgNormal = null;
    private Listener listener;
    private boolean focused = false;
    private List<EditText> editTextList = new ArrayList<>();
    private int currentPosition = 0;

    public VerificationCodeInput(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable
                .VericationCodeInput);
        box = typedArray.getInt(R.styleable.VericationCodeInput_box, 4);
        childPadding = (int) typedArray.getDimension(R.styleable.VericationCodeInput_boxMargin,
                childPadding);
        childMargin = (int) typedArray.getDimension(R.styleable.VericationCodeInput_boxMargin,
                childMargin);
        boxBgFocus = typedArray.getDrawable(R.styleable.VericationCodeInput_boxBgFocus);
        boxBgNormal = typedArray.getDrawable(R.styleable.VericationCodeInput_boxBgNormal);
        boxWidth = (int) typedArray.getDimension(R.styleable.VericationCodeInput_boxWidth,
                boxWidth);
        boxHeight = (int) typedArray.getDimension(R.styleable.VericationCodeInput_boxHeight,
                boxHeight);

        initViews();
    }

    private void initViews() {
        setOrientation(HORIZONTAL);
        for (int i = 0; i < box; i++) {
            EditText editText = new EditText(getContext());
            LinearLayout.LayoutParams layoutParams = new LayoutParams(boxWidth, boxHeight);
            layoutParams.setMargins(childMargin, childMargin, childMargin, childMargin);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

            editText.setOnKeyListener(this);
            setBg(editText, i == 0 ? true : false);
            editText.setTextColor(Color.BLACK);
            editText.setLayoutParams(layoutParams);
            editText.setGravity(Gravity.CENTER);
            editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});

            editText.setId(i);
            editText.setEms(1);
            editText.addTextChangedListener(this);
            editText.setFocusable(i==0?true:false);
            addView(editText, i);
            editTextList.add(editText);
        }
    }

    @TargetApi(16)
    private void setBg(EditText editText, boolean focused) {
        if (boxBgNormal != null && !focused) {
            editText.setBackground(boxBgNormal);
        } else if (boxBgFocus != null && focused) {
            editText.setBackground(boxBgFocus);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (start == 0 && count >= 1 && currentPosition != editTextList.size() - 1) {
            currentPosition++;
            editTextList.get(currentPosition).requestFocus();
            setBg(editTextList.get(currentPosition), true);
            setBg(editTextList.get(currentPosition - 1), false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            focus();
            checkAndCommit();
        }
    }

    private void focus() {
        int count = getChildCount();
        EditText editText;
        for (int i = 0; i < count; i++) {
            editText = (EditText) getChildAt(i);
            if (i == currentPosition) {
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();

            } else {
                editText.setFocusable(false);
            }
//            if(editText.getText().length()<1){
//                editText.requestFocus();
//                return;
//            }
        }
    }

    private void checkAndCommit() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean full = true;
        for (int i = 0; i < box; i++) {
            EditText editText = (EditText) getChildAt(i);
            String content = editText.getText().toString();
            if (content.length() == 0) {
                full = false;
                break;
            } else {
                stringBuilder.append(content);
            }
        }
        if (full && listener != null) {
            listener.onCompleted(stringBuilder.toString());
        }
    }

    public void setOnCompletedListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        EditText editText = (EditText) v;
        if (keyCode == KeyEvent.KEYCODE_DEL && editText.getText().length() == 0) {
            int action = event.getAction();
            if (currentPosition != 0 && action == KeyEvent.ACTION_DOWN) {
                currentPosition--;
                focus();
                setBg(editTextList.get(currentPosition), true);
                setBg(editText, false);
                editTextList.get(currentPosition).setText("");
            }
        }
        return false;
    }

    public interface Listener {
        void onCompleted(String content);
    }
}
