package com.newvision.zeus.glassmanager.setting.pair;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.newvision.zeus.glasscore.protocol.entity.GlassDevicesInfo;
import com.newvision.zeus.glassmanager.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class GlassListAdapter extends BaseAdapter {

    private Context mContext;
    private List<GlassDevicesInfo> mGlassList;
    private LayoutInflater mInflater;

    public GlassListAdapter(Context context, List<GlassDevicesInfo> glassList) {
        this.mContext = context;
        this.mGlassList = glassList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mGlassList.size();
    }

    @Override
    public Object getItem(int position) {
        return mGlassList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GlassDevicesInfo msg = mGlassList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.glass_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.glass_sn = (TextView) convertView.findViewById(R.id.glass_sn);
            viewHolder.glass_ip = (TextView) convertView.findViewById(R.id.glass_ip);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.glass_sn.setText(msg.sn);
        viewHolder.glass_ip.setText(msg.ip);
        return convertView;
    }

    private class ViewHolder {
        public TextView glass_sn;
        public TextView glass_ip;
    }


}
