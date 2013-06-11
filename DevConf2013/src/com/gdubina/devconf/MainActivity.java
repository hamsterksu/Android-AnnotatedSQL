package com.gdubina.devconf;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.ListView;

import com.gdubina.devconf.adapter.ScoreAdapter;
import com.gdubina.devconf.store.FSchema2.ScoreView2;
import com.gdubina.devconf.store.FStore.ScoreView;
import com.gdubina.devconf.store.FStoreProvider;
import com.gdubina.devconf.store.FakeHelper;

public class MainActivity extends FragmentActivity implements LoaderCallbacks<Cursor>{

	private ScoreAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ListView listView = (ListView)findViewById(R.id.list_view);
		listView.setAdapter(adapter = new ScoreAdapter(this));
		getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
		return new CursorLoader(this, FStoreProvider.getContentUri(ScoreView.URI_CONTENT), null,
				ScoreView2.TableScore.CHEMP_ID + " = ?", new String[]{String.valueOf(FakeHelper.CHEMP_ID)}, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);		
	}

}
