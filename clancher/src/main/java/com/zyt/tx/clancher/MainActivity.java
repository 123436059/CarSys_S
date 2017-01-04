package com.zyt.tx.clancher;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<ResolveInfo> mApps;

    private static final int REQUEST_WIDGET = 2;
    private static final int REQUEST_CREATE_APPWIDGET = 3;
    private AppWidgetHost mAppWidegetHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GridView mGridView = (GridView) findViewById(R.id.gridView);
        findViewById(R.id.addWidget).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addWidget();
            }
        });

        loadApp();
        mGridView.setAdapter(new GridAdapter());
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ResolveInfo info = (ResolveInfo) adapterView.getAdapter().getItem(i);
                String pkg = info.activityInfo.packageName;
                String cls = info.activityInfo.name;

                ComponentName componentName = new ComponentName(pkg, cls);
                Intent intent = new Intent();
                intent.setComponent(componentName);
                startActivity(intent);
            }
        });
    }

    private void addWidget() {
        mAppWidegetHost = new AppWidgetHost(this,1);
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        int appId = mAppWidegetHost.allocateAppWidgetId();
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appId);
        startActivityForResult(pickIntent,REQUEST_WIDGET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_WIDGET:
                    addAppWidget(data);
                    break;

                case REQUEST_CREATE_APPWIDGET:
                    completeAddAppWidget(data);
                    break;
            }
        }
    }

    private void completeAddAppWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = AppWidgetManager.getInstance(this).getAppWidgetInfo(appWidgetId);
        synchronized (this) {
            AppWidgetHostView mHostView = mAppWidegetHost.createView(this, appWidgetId, appWidgetInfo);
            mHostView.setAppWidget(appWidgetId,appWidgetInfo);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(appWidgetInfo.minWidth, appWidgetInfo.minHeight);
            //TODO 主界面添加view

        }

    }

    private void addAppWidget(Intent data) {
        int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = AppWidgetManager.getInstance(this).getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo != null) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityforResultSafely(intent, REQUEST_CREATE_APPWIDGET);
        } else {
            onActivityResult(REQUEST_CREATE_APPWIDGET,RESULT_OK,data);
        }

    }

    private void startActivityforResultSafely(Intent intent, int requestCreateAppwidget) {
        try {
            startActivityForResult(intent,requestCreateAppwidget);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadApp() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
    }

    class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mApps.size();
        }

        @Override
        public ResolveInfo getItem(int i) {
            return mApps.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ResolveInfo item = getItem(i);
            Log.d("taxi", "position=" + i);
            if (view == null) {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_grid, null);
            }

            ImageView imageView = (ImageView) view.findViewById(R.id.ivContent);
            Drawable drawable = item.activityInfo.loadIcon(getPackageManager());
            imageView.setImageDrawable(drawable);
            return view;
        }
    }
}
