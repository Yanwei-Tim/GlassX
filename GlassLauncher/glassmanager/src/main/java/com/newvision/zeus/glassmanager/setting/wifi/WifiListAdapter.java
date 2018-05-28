package com.newvision.zeus.glassmanager.setting.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.newvision.zeus.glassmanager.R;

import java.util.List;


/**
 * Created by zhangsong on 3/6/15.
 */
public class WifiListAdapter extends BaseAdapter {

    private Context mContext;
    private List<ScanResult> mScanResults;
    private LayoutInflater mInflater;

    public WifiListAdapter(Context context, List<ScanResult> results) {
        this.mContext = context;
        this.mScanResults = results;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mScanResults.size();
    }

    @Override
    public Object getItem(int position) {
        return mScanResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScanResult scanResult = mScanResults.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.wifi_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.SSIDView = (TextView) convertView.findViewById(R.id.tv_SSID);
            viewHolder.stateView = (TextView) convertView.findViewById(R.id.tv_state);
            viewHolder.stateImage = (ImageView) convertView.findViewById(R.id.img_state);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.SSIDView.setText(scanResult.SSID);
        viewHolder.stateView.setText(WifiHelper.getInstance().parseReadableScanResultCapability(mContext, scanResult));
        viewHolder.stateImage.setImageResource(WifiHelper.getInstance().getWifiItemIconResId(scanResult));
        return convertView;
    }

    private class ViewHolder {
        public TextView SSIDView;
        public TextView stateView;
        public ImageView stateImage;
    }



}
