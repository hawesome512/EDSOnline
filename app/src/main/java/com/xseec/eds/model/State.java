package com.xseec.eds.model;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.view.View;

import com.xseec.eds.R;
import com.xseec.eds.util.EDSApplication;

/**
 * Created by Administrator on 2018/8/7.
 */

public enum State {
    OFF, OFFLINE, ALARM, ON,TRIP;

    private static final String STATE_OFF = "0";
    private static final String STATE_OFFLINE = "-1";
    private static final String STATE_TRIP = "5";
    private static final String STATE_ALARM = "6";
    private static final String STATE_ON = "7";

    public int getStateColorRes() {
        switch (this) {
            case ALARM:
            case TRIP:
                return R.color.colorAlarm;
            case OFFLINE:
                return R.color.colorOffline;
            case ON:
                return R.color.colorOn;
            case OFF:
                return R.color.colorOff;
            default:
                return R.color.colorNormal;
        }
    }

    public void setUnusualAnimator(View view) {
        switch (this) {
            case ALARM:
            case OFFLINE:
            case TRIP:
                AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(EDSApplication
                        .getContext(), R.animator.alpha_animator);
                animatorSet.setTarget(view);
                animatorSet.start();
                break;
            default:
                break;
        }
    }

//    public static State getState(String value) {
//        if (value == null) {
//            return State.OFFLINE;
//        } else if (value.contains(STATE_ALARM)) {
//            return State.ALARM;
//        } else if (value.contains(STATE_OFFLINE)) {
//            return State.OFFLINE;
//        } else if (value.contains(STATE_ON)) {
//            return State.ON;
//        } else {
//            return State.OFF;
//        }
//    }

    public String getStateText() {
        Context context = EDSApplication.getContext();
        switch (this) {
            case ALARM:
                return context.getString(R.string.overview_state_alarm);
            case OFFLINE:
                return context.getString(R.string.overview_state_offline);
            case ON:
                return context.getString(R.string.overview_state_on);
            case OFF:
                return context.getString(R.string.overview_state_off);
            case TRIP:
                return context.getString(R.string.overview_state_trip);
            default:
                return null;
        }
    }
}
