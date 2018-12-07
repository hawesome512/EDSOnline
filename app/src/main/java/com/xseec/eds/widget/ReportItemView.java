package com.xseec.eds.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xseec.eds.R;
//nj--自定义reportItem控件
public class ReportItemView extends CardView {

    private RelativeLayout layout;
    private LinearLayout layoutLeft;
    private LinearLayout layoutCenter;
    private LinearLayout layoutRight;
    private TextView title;
    private TextView titleLeft;
    private TextView titleCenter;
    private TextView titleRight;
    private TextView valueLeft;
    private TextView valueCenter;
    private TextView valueRight;

    private ClickListener clickListener;

    public ReportItemView( Context context,  AttributeSet attrs) {
        super( context, attrs );
        LayoutInflater.from( context ).inflate( R.layout.define_report_view,this );
        layout =findViewById( R.id.layout );
        layoutLeft=findViewById( R.id.item_left );
        layoutCenter=findViewById( R.id.item_center );
        layoutRight=findViewById( R.id.item_right );
        title=findViewById( R.id.text_title );
        titleLeft=findViewById( R.id.item_left_title );
        titleCenter=findViewById( R.id.item_center_title );
        titleRight=findViewById( R.id.item_right_title );
        valueLeft=findViewById( R.id.item_left_value );
        valueCenter=findViewById( R.id.item_center_value );
        valueRight=findViewById( R.id.item_right_value );

        layout.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null){
                    clickListener.Click( v );
                }
            }
        } );

        layoutLeft.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null){
                    clickListener.Click( v );
                }
            }
        } );

        layoutCenter.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null){
                    clickListener.Click( v );
                }
            }
        } );

        layoutRight.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null){
                    clickListener.Click( v );
                }
            }
        } );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw( canvas );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout( changed, l, t, r, b );
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener{
        void Click(View v);
    }

    public void setTitle(String value){ title.setText(value);}

    public void setTitleLeft(String value){ titleLeft.setText( value );}

    public void setTitleCenter(String value){ titleCenter.setText( value );}

    public void setTitleRight(String value){ titleRight.setText( value );}

    public void setValueLeft(String value) {
        valueLeft.setText( value );
    }

    public void setValueCenter(String value) {
        valueCenter.setText( value );
    }

    public void setValueRight(String value) {
        valueRight.setText( value );
    }

    public String getTitle(){ return title.getText().toString(); }
}
