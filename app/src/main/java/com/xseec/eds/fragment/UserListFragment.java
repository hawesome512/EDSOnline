package com.xseec.eds.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.activity.QRCodeActivity;
import com.xseec.eds.activity.UserManageActivity;
import com.xseec.eds.adapter.UserAdapter;
import com.xseec.eds.model.User;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.servlet.Account;
import com.xseec.eds.model.servlet.Phone;
import com.xseec.eds.model.servlet.ResponseResult;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_account_list, container, false);
        setHasOptionsMenu(true);
        ButterKnife.inject(this, view);
        ViewHelper.initToolbar((AppCompatActivity) getActivity(), toolbar, R.drawable
                .ic_arrow_back_white_24dp);
        getActivity().setTitle(R.string.setting_user);
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
        queryAccount();
    }

    private void queryAccount() {
        String deviceName = WAServicer.getUser().getDeviceName();
        WAServiceHelper.sendAccountQueryRequest(deviceName, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                account = WAJsonHelper.getAccount(response);
                refreshViewsInThread(response);
            }
        });
    }

    @Override
    protected void onRefreshViews(String jsonData) {
        progressDataLog.setVisibility(View.GONE);
        phoneList = account.getPhones();
        String createdUserNumber = String.valueOf(phoneList.size());
        textCreatedUser.setText(getString(R.string.user_created_number, createdUserNumber));
        String limitUserNumber = String.valueOf(account.getNumber());
        textLimitUser.setText(getString(R.string.user_limit_number, limitUserNumber));
        adapter = new UserAdapter(getActivity(), phoneList, new UserAdapter.DeleteListener() {
            @Override
            public void onDelete() {
                updateAccount();
            }
        });
        recycler.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CREATE:
                if (resultCode == RESULT_OK) {
                    Phone phone = data.getParcelableExtra(UserManageActivity.EXTRA_PHONE);
                    boolean additional = true;
                    for (int i = 0; i < phoneList.size(); i++) {
                        if (phoneList.get(i).getId().equals(phone.getId())) {
                            phoneList.set(i, phone);
                            additional = false;
                            break;
                        }
                    }
                    if (additional) {
                        phoneList.add(phone);
                    }
                    adapter.notifyDataSetChanged();
                    updateAccount();
                }
                break;
        }
    }

    private void updateAccount() {
        progressDataLog.setVisibility(View.VISIBLE);
        account.setPhones(phoneList);
        WAServiceHelper.sendAccountUpdateRequest(account, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                ResponseResult result = WAJsonHelper.getServletResult(response);
                if (result.isSuccess()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDataLog.setVisibility(View.GONE);
                            Toast.makeText(getContext(), R.string.saved, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.fab_add)
    public void onFabAddClicked() {
        //NJ--添加用户是判断用户注册量是否已满
        if (account.getNumber() != phoneList.size()) {
            UserManageActivity.start(getActivity(), REQUEST_CREATE, new Phone());
        } else {
            ViewHelper.singleAlertDialog(getActivity(), getString(R.string.user_fill), null);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.user_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_qr:
                QRCodeActivity.start(getContext());
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
