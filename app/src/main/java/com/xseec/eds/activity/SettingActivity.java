package com.xseec.eds.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.xseec.eds.R;
import com.xseec.eds.fragment.SetBasicFragment;
import com.xseec.eds.fragment.SetUserListFragment;
import com.xseec.eds.model.Custom;
import com.xseec.eds.util.ViewHelper;

import butterknife.ButterKnife;

public class SettingActivity extends BaseActivity {
    private final static String KEY_CUSTOM = "custom";

    public static void start(Context context, Custom custom){
        Intent intent=new Intent( context,SettingActivity.class );
        intent.putExtra( KEY_CUSTOM,custom );
        context.startActivity( intent );
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail );
        ButterKnife.inject( this );
        initFragment();
    }

    private void initFragment() {
        Custom custom=getIntent().getParcelableExtra( KEY_CUSTOM );
        switch (custom.getCustomType()){
            case AREA:
            case ALIAS:
                replaceFragment( SetBasicFragment.newInstance( custom ) );
                break;
            case USER:
                replaceFragment( SetUserListFragment.newInstance() );
                break;
            case ENERGY:
            case OVERVIEWTAG:
                ViewHelper.singleAlertDialog( this, "/该功能待完善中/取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                } );
                break;
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace( R.id.layout_container,fragment );
        transaction.commit();
    }
}
