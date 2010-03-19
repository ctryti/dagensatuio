package no.ctryti.dagensatuio;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HomeActivity extends Activity {

	
	private static final int REFRESH_ID = 1;
	private static final int CLEAR_DB_ID = 2;
	
	private static final String TAG = "HomeActivity";
	
	DatabaseAdapter mDbAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.home_activity);
		
		mDbAdapter = new DatabaseAdapter(this); 
		
		ArrayList<DinnerItem> items = mDbAdapter.getAllFromPlace("Frederikke kafé");
		
		System.out.println("Items:");
		for(DinnerItem item : items)
			Log.i(TAG, item.getDescription());
		
		ListView list = (ListView)findViewById(R.id.home_list);
	
		if(list != null) {
			DinnerItemAdapter adapter = new DinnerItemAdapter(this, R.layout.custom_list_row, items);
			list.setAdapter(adapter);
		}
	}

	private class DinnerItemAdapter extends BaseAdapter {
		
		private Context mCtx;
		private List<DinnerItem> mList;
				
		public DinnerItemAdapter(Context ctx, int rowResID, List<DinnerItem> list) {
			mCtx = ctx;
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
		public View getView(int arg0, View convertView, ViewGroup parent) {
			DinnerItem item = mList.get(arg0);
						
			View row = convertView; 
			if(row == null)	
				row = View.inflate(mCtx, R.layout.custom_list_row, null);
			
			TextView type = (TextView)row.findViewById(R.id.type);
			type.setText(item.getType());
			TextView desc = (TextView)row.findViewById(R.id.desc);
			desc.setText(item.getDescription());
			
			return row;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, REFRESH_ID, Menu.NONE, "Refresh");
		menu.add(Menu.NONE, CLEAR_DB_ID, Menu.NONE, "Clear database");
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {

		case REFRESH_ID:
			new RefreshDbTask(mDbAdapter).execute();
			break;
		case CLEAR_DB_ID:
			mDbAdapter.reCreateDatabase();
			break;
		}
		return true;
	}
	
}
