package com.gdubina.devconf.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gdubina.devconf.R;
import com.gdubina.devconf.store.FSchema2.ScoreView2;

public class ScoreAdapter extends ResourceCursorAdapter {

	public ScoreAdapter(Context context) {
		super(context, R.layout.score_list_item, null, true);
	}

	@Override
	public void bindView(View v, Context context, Cursor c) {
		Holder h = (Holder) v.getTag();
		
		h.team1.setText(c.getString(columnTeam1Title));
		h.team2.setText(c.getString(columnTeam2Title));
		h.score.setText(c.getString(columnScore));
	}
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = super.newView(context, cursor, parent);
		v.setTag(new Holder((TextView) v.findViewById(R.id.team1), (TextView) v.findViewById(R.id.team2),
				(TextView) v.findViewById(R.id.score)));
		return v;
	}

	int columnTeam1Id;
	int columnTeam2Id;
	
	int columnTeam1Title;
	int columnTeam2Title;
	int columnScore;

	@Override
	public Cursor swapCursor(Cursor newCursor) {
		if (newCursor != null) {

			columnTeam1Id = newCursor.getColumnIndex(ScoreView2.TableTeam1.ID);
			columnTeam2Id = newCursor.getColumnIndex(ScoreView2.TableTeam2.ID);
			
			columnTeam1Title = newCursor.getColumnIndex(ScoreView2.TableTeam1.TITLE);

			columnTeam2Title = newCursor.getColumnIndex(ScoreView2.TableTeam2.TITLE);

			columnScore = newCursor.getColumnIndex(ScoreView2.TableScore.SCORE);
		}

		return super.swapCursor(newCursor);
	}
	
	private class Holder {
		
		final TextView team1;
		final TextView team2;

		final TextView score;

		public Holder(TextView team1, TextView team2, TextView score) {
			super();
			this.team1 = team1;
			this.team2 = team2;
			this.score = score;
		}

	}
}
