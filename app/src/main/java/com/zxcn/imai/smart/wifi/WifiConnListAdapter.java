
package com.zxcn.imai.smart.wifi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxcn.imai.smart.R;

import java.util.ArrayList;

public class WifiConnListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<WifiElement> mArr;
	private String ss_id;

	public WifiConnListAdapter(Context context, ArrayList<WifiElement> list) {
		this.inflater = LayoutInflater.from(context);
		this.mArr = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mArr.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mArr.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.widget_wifi_conn_lv, null);
		TextView ssid = (TextView) view.findViewById(R.id.wifi_conn_name);
		TextView wifi_conn_status = (TextView) view.findViewById(R.id.wifi_conn_status);
		TextView wpe = (TextView) view.findViewById(R.id.wifi_conn_wpe);
		ImageView level = (ImageView) view.findViewById(R.id.wifi_conn_level);
		ImageView wifi_conn_look = (ImageView) view.findViewById(R.id.wifi_conn_look);
		String ss_id= (String) SharedPreferencesUtils.getParam(inflater.getContext(),"is_conn","");
		if (mArr.get(position).getSsid().equals(ss_id)){
			wifi_conn_status.setText("已连接");
		}else {
			wifi_conn_status.setText("");
		}

		ssid.setText(mArr.get(position).getSsid());
//		wpe.setText( mArr.get(position).getCapabilities());
		wpe.setVisibility(View.GONE);
		if (mArr.get(position).getCapabilities().equals("[WPS][ESS]")){
			wifi_conn_look.setVisibility(View.GONE);
		}else {
			wifi_conn_look.setVisibility(View.VISIBLE);
		}
		return view;
	}

	/**
	 * 绝对值
	 *
	 * @param num
	 * @return
	 */
	private int abs(int num) {
		return num * (1 - ((num >>> 31) << 1));
	}

	public void setIsConnected(String ssid) {
		this.ss_id=ssid;
		notifyDataSetChanged();
	}

	public void refreshclearData() {
		mArr.clear();
		notifyDataSetChanged();
	}

	public void setData(ArrayList<WifiElement> allNetWorkList) {
		mArr.clear();
		mArr.addAll(allNetWorkList);
		notifyDataSetChanged();
	}
}