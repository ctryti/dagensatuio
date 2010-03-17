package no.ctryti.dagensatuio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;

import no.ctryti.dagensatuio.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SimpleCursorAdapter;

public class PlacesActivity extends ListActivity {

	private static final String TAG = "PlacesActivity";

	private static final int REFRESH_ID = 1;
	private static final int CLEAR_DB_ID = 2;
	
	private boolean isUpdating;
	
	private Cursor mCursor;
	private DatabaseAdapter mDbAdapter;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarVisibility(false);
		//setContentView(R.layout.main);

		mDbAdapter = new DatabaseAdapter(this);
		mDbAdapter.open();
		
		isUpdating = false;
		
		updateView();
		registerForContextMenu(getListView());
	}
	
	private void updateView() {
		Log.i(TAG, "Refresheing mCursor");
		mCursor = mDbAdapter.fetchAllPlaces();
		//setListAdapter(new SimpleCursorAdapter(this, R.layout.menu_item_row, mCursor, new String[] {DatabaseAdapter.KEY_PLACE}, new int[]{R.id.text1} ));
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
			if(!isUpdating) {
				new RefreshDbTask(this).execute();
				isUpdating = true;
			}
			break;
		case CLEAR_DB_ID:
			if(!isUpdating) {
				mDbAdapter.reCreateDatabase();
				updateView();
			}
			break;
		}
		return true;
	}

	public class RefreshDbTask extends AsyncTask<Void, Integer, Void>  {

		private static final String TAG = "RefreshDBTask";
		private Activity mCtx;
		
		private static final int DONE = 1;
		
		public RefreshDbTask(Activity ctx) {
			mCtx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			mCtx.setProgressBarVisibility(true);
			mCtx.setProgressBarIndeterminateVisibility(true);
			mCtx.setProgress(0);
			mCtx.setTitle("Loading: " + Settings.Place.values()[0].getName());
		}

		@Override
		protected void onPostExecute(Void result) {
			mCtx.setProgress(10000);
			mCtx.setTitle(R.string.app_name);
			isUpdating = false;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			switch (values[0]) {
			case DONE:
				mCtx.setTitle("Done!");
				break;
			default:
				if (values[1] != Settings.Place.values().length) {
					mCtx.setTitle("Updating: " + Settings.Place.values()[values[1]].getName());
				}
				mCtx.setProgress(values[0]-1);
				break;
			}
			updateView();
		}

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse response;
			HttpEntity entity;
			HttpGet get;
			ArrayList<DinnerItem> dinnerItems = new ArrayList<DinnerItem>();

			int progresStatus = 0;
			int total = Settings.Place.values().length;

			/* Each of the enum Place contains a name and URI. */
			for (Settings.Place place : Settings.Place.values()) {
				try {
					get = new HttpGet(place.getURI());
					response = client.execute(get);
					entity = response.getEntity();
					dinnerItems.clear();
					dinnerItems.addAll(SiOParser.parse(entity.getContent(), place.getName()));
				} catch (ClientProtocolException e) {
					Log.w(TAG, "ClientProtocolException: " + place.getName());
				} catch (IOException e) {
					Log.w(TAG, "IOException: " + place.getName());
				}
				ContentValues v = new ContentValues();
				for (DinnerItem t : dinnerItems) {
					v.clear();
					v.put(DatabaseAdapter.KEY_DAY, t.getDay());
					v.put(DatabaseAdapter.KEY_DESC, t.getDescription());
					v.put(DatabaseAdapter.KEY_PLACE, t.getPlace());
					v.put(DatabaseAdapter.KEY_TYPE, t.getType());
					v.put(DatabaseAdapter.KEY_PERIOD, t.getPeriod());
					int gluten = t.isGluten() ? 1 : 0;
					int laktose = t.isLaktose() ? 1 : 0;
					v.put(DatabaseAdapter.KEY_GLUTEN, gluten);
					v.put(DatabaseAdapter.KEY_LAKTOSE, laktose);
					mDbAdapter.insert(v);

				}
				Log.i(TAG, "Finished adding " + place.getName());
				progresStatus++;
				publishProgress((int) (((double) progresStatus / total) * 10000), progresStatus);

			}
			publishProgress(DONE);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return null;
		}
	}
}