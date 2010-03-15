package no.ctryti.dagensatuio;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HomeActivity extends Activity {

	DatabaseAdapter mDbAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		mDbAdapter = new DatabaseAdapter(this);
		mDbAdapter.open();
		
		ArrayList<DinnerItem> items = mDbAdapter.getAllFromPlace("Frederikke Kafé");  
		
		DinnerItemAdapter adapter = new DinnerItemAdapter(this, R.layout.custom_list_row, items);
		
		ListView lv = (ListView) findViewById(R.id.list);
		
		lv.setAdapter(adapter);
		
		setContentView(R.layout.home_activity);
		
		
	}

	private class DinnerItemAdapter extends BaseAdapter {
		
		private Context mCtx;
		private List<DinnerItem> mList;
		private int mRowResID;
		
		public DinnerItemAdapter(Context ctx, int rowResID, List<DinnerItem> list) {
			mCtx = ctx;
			mRowResID = rowResID;
			mList = list;
		}
		
		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			DinnerItem item = mList.get(arg0);
			LayoutInflater inflater = LayoutInflater.from(mCtx);
			View v = inflater.inflate(mRowResID, arg2);
			TextView type = (TextView)v.findViewById(R.id.type);
			type.setText(item.getType());
			TextView desc = (TextView)v.findViewById(R.id.desc);
			desc.setText(item.getDescription());
			
			return v;
		}
		
	}
	
}
