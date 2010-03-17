package no.ctryti.dagensatuio;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HomeActivity extends Activity {

	DatabaseAdapter mDbAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.home_activity);
		
		mDbAdapter = new DatabaseAdapter(this);
		mDbAdapter.open();
		
		ArrayList<DinnerItem> items = mDbAdapter.getAllFromPlace("Frederikke kafé");
		
		System.out.println("Items:");
		for(DinnerItem item : items)
			System.out.println(item.getDescription());
		
		ListView innerList = (ListView)findViewById(R.id.home_list);
	
		if(innerList != null) {
			DinnerItemAdapter adapter = new DinnerItemAdapter(this, R.layout.custom_list_row, items);
			innerList.setAdapter(adapter);
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
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			DinnerItem item = mList.get(arg0);
			//LayoutInflater inflater = LayoutInflater.from(mCtx);
			
			View inflatedView = View.inflate(mCtx, R.layout.custom_list_row, null);
			
			//View v = inflater.inflate(mRowResID, arg2);
			TextView type = (TextView)inflatedView.findViewById(R.id.type);
			type.setText(item.getType());
			TextView desc = (TextView)inflatedView.findViewById(R.id.desc);
			desc.setText(item.getDescription());
			
			return inflatedView;
		}
	}
	
}
