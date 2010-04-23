package no.ctryti.dagensatuio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

public class HomeActivity extends Activity {

	private static final int REFRESH_ID = 1;
	private static final int CLEAR_DB_ID = 2;
	private static final String TAG = "HomeActivity";
	
	private RefreshDbTask mRefreshDbTask;
	
	private String months[], weekdays[];
	private DatabaseAdapter mDbAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mDbAdapter = new DatabaseAdapter(this); 
		
		setContentView(R.layout.home_activity);
		
		
		ArrayList<DinnerItem> items = mDbAdapter.getItems("Frederikke kaf\u00e9", null);
		
		/* populate the screen! (if there is anything to display */
		if(items.size() != 0) {
			
			months = getResources().getStringArray(R.array.months);
			weekdays = getResources().getStringArray(R.array.weekdays);
			
			SeparatedListAdapter adapter = new SeparatedListAdapter(this);
			
			TextView top_tv = (TextView)findViewById(R.id.home_top);
			
			//RelativeLayout bottom_row = (RelativeLayout)findViewById(R.id.home_bottom);
			TextView bottom_tv = (TextView)findViewById(R.id.middle_label);
			
			ListView list = (ListView)findViewById(R.id.home_list);
			
			top_tv.setText(createPeriodString(items.get(0).getPeriod()));
			bottom_tv.setText(items.get(0).getPlace());
			
			/* Split the items into their respective days, add them */
			ArrayList<ArrayList<DinnerItem>> separatedItems = new ArrayList<ArrayList<DinnerItem>>();
			for(int i = 0; i < weekdays.length; i++) {
				separatedItems.add(new ArrayList<DinnerItem>());
			}			
			for(DinnerItem item : items) {
				separatedItems.get(item.getDay()-1).add(item);
			}			
			for(int i = 0; i < weekdays.length; i++)
				adapter.addSection(weekdays[i], new DinnerItemAdapter(this, separatedItems.get(i)));
			
			System.out.println(list);
			list.setAdapter(adapter);
		
			 /*Grid of days */
			GridView days = (GridView) findViewById(R.id.days_list);
			days.setAdapter(new ImageAdapter(this, R.layout.days_item, Arrays.asList(weekdays)));
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
			mRefreshDbTask = new RefreshDbTask(mDbAdapter);
			mRefreshDbTask.execute();
			
			break;
		case CLEAR_DB_ID:
			mDbAdapter.reCreateDatabase();
			break;
		}
		return true;
	}
	
	/* props to Jeff Sharkey (http://jsharkey.org/blog/2008/08/18/separating-lists-with-headers-in-android-09/) for this adapter */
	public class SeparatedListAdapter extends BaseAdapter {

		public final Map<String,Adapter> sections = new LinkedHashMap<String,Adapter>();
		public final ArrayAdapter<String> headers;
		public final static int TYPE_SECTION_HEADER = 0;

		public SeparatedListAdapter(Context context) {
			headers = new ArrayAdapter<String>(context, R.layout.list_header);
		}

		public void addSection(String section, Adapter adapter) {
			this.headers.add(section);
			this.sections.put(section, adapter);
		}

		public Object getItem(int position) {
			for(Object section : this.sections.keySet()) {
				Adapter adapter = sections.get(section);
				int size = adapter.getCount() + 1;

				// check if position inside this section
				if(position == 0) return section;
				if(position < size) return adapter.getItem(position - 1);

				// otherwise jump into next section
				position -= size;
			}
			return null;
		}

		public int getCount() {
			// total together all sections, plus one for each section header
			int total = 0;
			for(Adapter adapter : this.sections.values())
				total += adapter.getCount() + 1;
			return total;
		}

		public int getViewTypeCount() {
			// assume that headers count as one, then total all sections
			int total = 1;
			for(Adapter adapter : this.sections.values())
				total += adapter.getViewTypeCount();
			return total;
		}

		public int getItemViewType(int position) {
			int type = 1;
			for(Object section : this.sections.keySet()) {
				Adapter adapter = sections.get(section);
				int size = adapter.getCount() + 1;

				// check if position inside this section
				if(position == 0) return TYPE_SECTION_HEADER;
				if(position < size) return type + adapter.getItemViewType(position - 1);

				// otherwise jump into next section
				position -= size;
				type += adapter.getViewTypeCount();
			}
			return -1;
		}

		public boolean areAllItemsSelectable() {
			return false;
		}

		public boolean isEnabled(int position) {
			//return (getItemViewType(position) != TYPE_SECTION_HEADER);
			return false;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int sectionnum = 0;
			for(Object section : this.sections.keySet()) {
				Adapter adapter = sections.get(section);
				int size = adapter.getCount() + 1;

				// check if position inside this section
				if(position == 0) return headers.getView(sectionnum, convertView, parent);
				if(position < size) return adapter.getView(position - 1, convertView, parent);

				// otherwise jump into next section
				position -= size;
				sectionnum++;
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}
	protected class ImageAdapter extends BaseAdapter {
		private Context mCtx;
		private int mRowResID;
		private List<String> mList;

		public ImageAdapter(Context ctx, int rowResID, List<String> list){
			this.mCtx = ctx;
			this.mRowResID = rowResID;
			this.mList = list;
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
		public View getView(int position, View convertView, ViewGroup parent) {
			View v;
			String day = mList.get(position);
			if(convertView==null){
				v = View.inflate(mCtx, R.layout.days_item, null);
				TextView txt = (TextView) v.findViewById(R.id.day_text);
				txt.setText(day);
				//b1.setOnTouchListener(new ButtonListener(mCtx, day));

			} else {
				v = convertView;
			}
			return v;
		}

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
		public boolean isEnabled(int position) {
			return false;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			DinnerItem item = mList.get(position);
			View row = convertView; 
			ViewHolder holder = null;
			
			if(row == null) {	
				row = View.inflate(mCtx, R.layout.custom_list_row, null);
				holder = new ViewHolder(row);
				row.setTag(holder);
			} else {
				holder = (ViewHolder)row.getTag();
			}
			holder.getTypeView().setText(item.getType());
			holder.getDescView().setText(item.getDescription());
			
			return row;
		}
	}
	
	class ViewHolder {
		View base;
		TextView type;
		TextView desc;
				
		ViewHolder(View base) {
			this.base = base;
		}
		public TextView getTypeView() {
			if(type == null)
				type = (TextView)base.findViewById(R.id.type);
			return type;
		}
		public TextView getDescView() {
			if(desc == null)
				desc = (TextView)base.findViewById(R.id.desc);
			return desc;
		}
		public View getBase() {
			return base;
		}
	}
}
