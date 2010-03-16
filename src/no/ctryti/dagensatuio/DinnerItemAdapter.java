package no.ctryti.dagensatuio;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DinnerItemAdapter extends BaseAdapter {

	private Context mCtx;
	private List<DinnerItem> mItemList;
	private int mRowResId;
	
	public DinnerItemAdapter(Context ctx, int rowResId, List<DinnerItem> itemList) {
		mCtx = ctx;
		mItemList = itemList;
		mRowResId = rowResId;
	}
	
	@Override
	public int getCount() {
		return mItemList.size();
	}

	@Override
	public Object getItem(int position) {
		return mItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DinnerItem item = mItemList.get(position);
		LayoutInflater inflate = LayoutInflater.from(mCtx);
		View v = inflate.inflate(mRowResId, parent, false);
		TextView type = (TextView)v.findViewById(R.id.type);
		if(type != null)
			type.setText(item.getType());
		TextView desc = (TextView)v.findViewById(R.id.desc);
		if(desc != null)
			desc.setText(item.getDescription());
		return v;
	}

}
