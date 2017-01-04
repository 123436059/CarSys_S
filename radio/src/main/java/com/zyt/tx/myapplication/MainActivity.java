package com.zyt.tx.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zyt.tx.myapplication.adapter.DividerItemDecoration;
import com.zyt.tx.myapplication.adapter.RadioListAdapter;
import com.zyt.tx.myapplication.entity.RadioBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {
    @BindView(R.id.btnFm)
    TextView btnFm;
    @BindView(R.id.tvFreq)
    TextView tvFreq;
    @BindView(R.id.tvState)
    TextView tvState;
    @BindView(R.id.btnDirect)
    ImageButton btnDirect;
    @BindView(R.id.btnSearch)
    ImageButton btnSearch;
    @BindView(R.id.itemPrev)
    TextView itemPrev;
    @BindView(R.id.itemNext)
    TextView itemNext;
    @BindView(R.id.itemChange)
    TextView itemChange;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    private RadioListAdapter mAdapter;
    private List<RadioBean> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL_LIST));
        initListItemData();
        recyclerView.setAdapter(mAdapter = new RadioListAdapter(mList));
    }

    private void initListItemData() {
        mList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            RadioBean bean = new RadioBean();
            bean.setContent(85 + (0.5 * i) + "");
            mList.add(bean);
        }
    }

    @OnClick({R.id.btnFm, R.id.tvFreq, R.id.btnDirect, R.id.btnSearch, R.id.itemPrev, R.id.itemNext, R.id.itemChange})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFm:

                break;
            case R.id.tvFreq:

                break;
            case R.id.btnDirect:

                break;
            case R.id.btnSearch:

                break;
            case R.id.itemPrev:

                break;
            case R.id.itemNext:

                break;
            case R.id.itemChange:

                break;
        }
    }
}
