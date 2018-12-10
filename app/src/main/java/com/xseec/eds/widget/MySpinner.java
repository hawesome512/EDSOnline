package com.xseec.eds.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;

//nj--使Spinner控件中的Item能够被重复监听 2018/11/21
public class MySpinner extends AppCompatSpinner {

    private int reSelectionItem;

    public MySpinner(Context context) {
        super( context );
    }

    public MySpinner(Context context, AttributeSet attrs) {
        super( context, attrs );
    }

    @Override
    public void setSelection(int position, boolean animate) {
        boolean sameSelection=reSelectionItem==getSelectedItemPosition();
        super.setSelection( position, animate );
        if (sameSelection){
            getOnItemSelectedListener().onItemSelected( this,getSelectedView(),position,
                    getSelectedItemId());
        }
    }

    @Override
    public void setSelection(int position) {
        boolean sameSelection;
        if (reSelectionItem==-1){
            sameSelection=position==getSelectedItemPosition();
        }else{
            sameSelection=reSelectionItem==getSelectedItemPosition();
        }
        super.setSelection( position );
        if (sameSelection){
            getOnItemSelectedListener().onItemSelected(this,getSelectedView(),
                    position,getSelectedItemId());
        }
    }

    public void setReSelectionItem(int reSelectionItem) { this.reSelectionItem = reSelectionItem; }
}
