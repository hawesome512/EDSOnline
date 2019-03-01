package com.xseec.eds.widget;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.xseec.eds.R;

public class CaptchaTimeCount extends CountDownTimer {
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    private TextView validateBtn;

    public CaptchaTimeCount(long millisInFuture, long countDownInterval,TextView button) {
        super( millisInFuture, countDownInterval );
        this.validateBtn=button;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        validateBtn.setEnabled( false );
        validateBtn.setText( millisUntilFinished/1000+"s" );
    }

    @Override
    public void onFinish() {
        validateBtn.setEnabled( true );
        validateBtn.setText( R.string.login_code_gain );
    }

    public void onReset(){
        cancel();
        validateBtn.setEnabled( true );
        validateBtn.setText( R.string.login_code_gain );
    }
}
