package no.ctryti.dagensatuio;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HomeActivity extends Activity {

	
	private static final int REFRESH_ID = 1;
	private static final int CLEAR_DB_ID = 2;
	
	String months[], weekdays[];
	private int currentDay;
	
	
	private static final String TAG = "HomeActivity";
	
	DatabaseAdapter mDbAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbAdapter = new DatabaseAdapter(this); 
		setContentView(R.layout.home_activity);
	
		months = getResources().getStringArray(R.array.months);
		weekdays = getResources().getStringArray(R.array.weekdays);
		
		
		ArrayList<DinnerItem> items = mDbAdapter.getItems("Frederikke kaf\u00e9", null);
		//ArrayList<DinnerItem> items = mDbAdapter.getItems("SV Kafeen", null);
		
		/* populate the screen! (if there is anything to display */
		if(items.size() != 0) {
			currentDay = -1;
			TextView top_tv = (TextView)findViewById(R.id.home_top);
			TextView bottom_tv = (TextView)findViewById(R.id.home_bottom);
			ListView list = (ListView)findViewById(R.id.home_list);
			
			top_tv.setText(createPeriodString(items.get(0).getPeriod()));
			bottom_tv.setText(items.get(0).getPlace());
						
			if(list != null) {
				DinnerItemAdapter adapter = new DinnerItemAdapter(this, items);
				list.setAdapter(adapter);
			}
		}
	}

	private String createPeriodString(String period) {
		
		int year = Integer.parseInt(period.substring(0, 4));
		int week = Integer.parseInt(period.substring(4,6));
		String month;
		Calendar cal = new GregorianCalendar(); 
		cal.setMinimalDaysInFirstWeek(3);
		
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.WEEK_OF_YEAR, week);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		month = months[cal.get(Calendar.MONTH)];
		
		return "Uke "+week+": "+(cal.get(Calendar.DAY_OF_MONTH)-4)+". - "+cal.get(Calendar.DAY_OF_MONTH)+". "+month;
	}
	
	private class DinnerItemAdapter extends BaseAdapter {

		private Context mCtx;
		private List<DinnerItem> mList;
				
		public DinnerItemAdapter(Context ctx,  List<DinnerItem> list) {
			mCtx = ctx;
			mList = list;
		}
		@Override public int getCount() { return mList.size(); }
		@Override public Object getItem(int position) { return mList.get(position); }
		@Override public long getItemId(int position) { return position; }
		
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
