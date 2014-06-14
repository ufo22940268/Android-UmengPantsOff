package me.biubiubiu.umengpantsoff;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;



public class MyActivity extends Activity implements AdapterView.OnItemClickListener {

    private List<AppItem> mAppItems;
    private AppsAdapter mAdapter;
    private ListView mListView;
    private View mProgressView;
    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        mAdapter = new AppsAdapter();
        mListView = (ListView)findViewById(R.id.list);
        mProgressView = findViewById(R.id.progress);
        mEmptyView = findViewById(R.id.empty);

        getListView().setAdapter(mAdapter);
        getListView().setOnItemClickListener(this);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    private ListView getListView() {
        return mListView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getListView().setItemChecked(position, true);
        AppItem appItem = mAppItems.get(position);
        Toast.makeText(this, "友盟渠道名为" + appItem.umengChannel, Toast.LENGTH_SHORT).show();
    }

    class AppItem {

        public String name;
        public Drawable applicationIcon;
        public String umengChannel;

        @Override
        public String toString() {
            return name + "\t" + umengChannel;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetPackageTask().execute();
    }

    class GetPackageTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            PackageManager packageManager = MyActivity.this.getPackageManager();
            List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
            mAppItems = new ArrayList<AppItem>();
            for (ApplicationInfo app : installedApplications) {
                AppItem appItem = new AppItem();
                appItem.name = (String) packageManager.getApplicationLabel(app);
                appItem.applicationIcon = packageManager.getApplicationIcon(app);
                if (app.metaData != null && app.metaData.get("UMENG_CHANNEL") != null) {
                    appItem.umengChannel = app.metaData.getString("UMENG_CHANNEL");
                    mAppItems.add(appItem);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mAdapter.notifyDataSetChanged();
            mProgressView.setVisibility(View.GONE);

            if (mAppItems.size() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mListView.setVisibility(View.VISIBLE);
            }
        }
    }

    class AppsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mAppItems == null) {
                return 0;
            } else {
                return mAppItems.size();
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.list_item_app, parent, false);
            }

            AppItem item = mAppItems.get(position);
            ((ImageView)view.findViewById(R.id.icon)).setImageDrawable(item.applicationIcon);
            ((TextView)view.findViewById(R.id.label)).setText(item.name);
            return view;
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.my, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

}
