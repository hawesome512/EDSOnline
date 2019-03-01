package com.xseec.eds.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.activity.UserManageActivity;
import com.xseec.eds.adapter.UserAdapter;
import com.xseec.eds.model.User;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.servlet.Account;
import com.xseec.eds.model.servlet.Phone;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class UserListFragment extends BaseFragment {

    public static final int REQUEST_CREATE = 1;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.text_created_user)
    TextView textCreatedUser;
    @InjectView(R.id.text_limit_user)
    TextView textLimitUser;
    @InjectView(R.id.progress_data_log)
    ProgressBar progressDataLog;
    @InjectView(R.id.recycler)
    RecyclerView recycler;
    @InjectView(R.id.fab_add)
    FloatingActionButton fabAdd;


    private UserAdapter adapter;
    private List<Phone> phoneList;
    private Account account;

    public static UserListFragment newInstance() {
        return new UserListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_account_list, container, false );
        ButterKnife.inject( this, view );
        getActivity().setTitle( R.string.nav_user );
        ViewHelper.initToolbar( (AppCompatActivity) getActivity(), toolbar, R.drawable.menu );
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager( getContext() );
        recycler.setLayoutManager( layoutManager );
        queryAccount();
    }

    private void queryAccount() {
        User user = WAServicer.getUser();
        String deviceName = user.getDeviceName();
        WAServiceHelper.sendAccountQueryRequest( deviceName, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                account = WAJsonHelper.getAccount( response );
                phoneList = new ArrayList<>();
                //nj--暂时生成手机列表，后续从后台获取
                String[] phones = account.getPhone().split( ";" );
                for (int i = 0; i < phones.length; i++) {
                    Phone phone = new Phone( phones[i] );
                    phoneList.add( phone );
                }
                refreshViewsInThread( response );
            }
        } );
    }

    private void updateAccount(Account account){
        WAServiceHelper.sendAccountUpdateRequest( account, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                queryAccount();
            }
        } );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CREATE:
                if (resultCode == RESULT_OK) {
                    progressDataLog.setVisibility( View.VISIBLE );
                    account=data.getParcelableExtra( UserManageActivity.EXTRA_ACCOUNT );
                    updateAccount( account );
                }
                break;
        }
    }

    @Override
    protected void onRefreshViews(String jsonData) {
        progressDataLog.setVisibility( View.GONE );
        String createdUserNumber = String.valueOf( phoneList.size() );
        textCreatedUser.setText( getString( R.string.user_created_number, createdUserNumber ) );
        String limitUserNumber = String.valueOf( account.getNumber() );
        textLimitUser.setText( getString( R.string.user_limit_number, limitUserNumber ) );
        adapter = new UserAdapter( getActivity(), account, phoneList );

        //NJ--删除用户
        adapter.setDeleteClickListener( new UserAdapter.onDeleteClickListener() {
            @Override
            public void onDeleteClick() {
                account=adapter.getAccount();
                updateAccount( account );
            }
        } );
        recycler.setAdapter( adapter );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset( this );
    }

    @OnClick(R.id.fab_add)
    public void onFabAddClicked() {
        //NJ--添加用户是判断用户注册量是否已满
        if(account.getNumber()!=phoneList.size()){
            UserManageActivity.start( getActivity(), REQUEST_CREATE, account, null );
        }else {
            ViewHelper.singleAlertDialog( getActivity(),getString( R.string.user_fill ),null );
        }
    }
}
