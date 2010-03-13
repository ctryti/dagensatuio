package no.ctryti.dagensatuio;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SimpleCursorAdapter;

public class PlacesActivity extends ListActivity {

	private static final String TAG = "PlacesActivity";
	
	public static final int REFRESH_ID = 1;
	public static final int CLEAR_DB_ID = 2;
	
	private Cursor mCursor;
	private DatabaseAdapter mDbAdapter;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_PROGRESS);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarVisibility(false);
		setContentView(R.layout.main);
		
		updateView();
		registerForContextMenu(getListView());
	}
	private void updateView() {
		Log.i(TAG, "Refresheing mCursor");
		mDbAdapter = new DatabaseAdapter(this);
		mDbAdapter.open();
		mCursor = mDbAdapter.fetchAll();
		setListAdapter(new SimpleCursorAdapter(this, R.layout.menu_item_row, mCursor, new String[] {DatabaseAdapter.KEY_PLACE}, new int[]{R.id.text1} ));
		
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
			new RefreshDbTask(this).execute();
			break;
		case CLEAR_DB_ID:
			mDbAdapter.reCreateDatabase();
			break;
		}
		return true;
	}

}