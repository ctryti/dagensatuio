package no.ctryti.dagensatuio;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

public class RefreshDbTask extends AsyncTask<Void, Integer, Void>  {

	private static final String TAG = "RefreshDBTask";

	private DatabaseAdapter mDbAdapter;
	
	public RefreshDbTask(DatabaseAdapter dbAdapter) {
		mDbAdapter = dbAdapter;
	}
	
	@Override
	protected void onPreExecute() {
		mDbAdapter.open();
	}

	@Override
	protected void onPostExecute(Void result) {
		mDbAdapter.close();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
	
	}

	@Override
	protected Void doInBackground(Void... params) {

		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response;
		HttpEntity entity;
		HttpGet get;
		ArrayList<DinnerItem> dinnerItems = new ArrayList<DinnerItem>();

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
				v.put(DatabaseAdapter.DAY, t.getDay());
				v.put(DatabaseAdapter.DESC, t.getDescription());
				v.put(DatabaseAdapter.PLACE, t.getPlace());
				v.put(DatabaseAdapter.TYPE, t.getType());
				v.put(DatabaseAdapter.PERIOD, t.getPeriod());
				int gluten = t.isGluten() ? 1 : 0;
				int laktose = t.isLaktose() ? 1 : 0;
				v.put(DatabaseAdapter.GLUTEN, gluten);
				v.put(DatabaseAdapter.LAKTOSE, laktose);
				mDbAdapter.insert(v);

			}
			Log.i(TAG, "Finished adding " + place.getName());
					
		}
		return null;
	}
}
