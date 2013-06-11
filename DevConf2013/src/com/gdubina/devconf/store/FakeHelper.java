package com.gdubina.devconf.store;

import com.gdubina.devconf.store.FStore.ChempTable;
import com.gdubina.devconf.store.FStore.ScoreTable;
import com.gdubina.devconf.store.FStore.TeamTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class FakeHelper extends SQLiteOpenHelper{

	public FakeHelper(Context context) {
		super(context, FSchema.DB_NAME, null, FSchema.DB_VERSION);
	}

	public static long CHEMP_ID = 1L;
	
	private static long TEAM_SHAHTER = 1;
	private static long TEAM_DINAMO = 2;
	private static long TEAM_METALIS = 3;
	private static long TEAM_DNEPR = 4;

	@Override
	public void onCreate(SQLiteDatabase db) {
		FSchema.onCreate(db);
		initDb(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		FSchema.onDrop(db);
		onCreate(db);
	} 
	
	private void initDb(SQLiteDatabase db){
		//init chemps
		db.insert(ChempTable.TABLE_NAME, null, createChemp(CHEMP_ID, "Премьер Лига"));
		
		//init teams
		db.insert(TeamTable.TABLE_NAME, null, createTeam(TEAM_SHAHTER, "Шахтер"));
		db.insert(TeamTable.TABLE_NAME, null, createTeam(TEAM_DINAMO, "Динамо"));
		db.insert(TeamTable.TABLE_NAME, null, createTeam(TEAM_METALIS, "Металист"));
		db.insert(TeamTable.TABLE_NAME, null, createTeam(TEAM_DNEPR, "Днепр"));
		
		//init score
		db.insert(ScoreTable.TABLE_NAME, null, createScore(1, CHEMP_ID, TEAM_SHAHTER, TEAM_DINAMO, "1:1"));
		db.insert(ScoreTable.TABLE_NAME, null, createScore(2, CHEMP_ID, TEAM_METALIS, TEAM_DNEPR, "2:2"));
		db.insert(ScoreTable.TABLE_NAME, null, createScore(3, CHEMP_ID, TEAM_SHAHTER, TEAM_METALIS, "3:3"));
		db.insert(ScoreTable.TABLE_NAME, null, createScore(4, CHEMP_ID, TEAM_DINAMO, TEAM_DNEPR, "4:4"));
	}
	
	private static ContentValues createScore(long id, long chempId, long team1, long team2, String score){
		ContentValues v = new ContentValues();
		v.put(ScoreTable.ID, id);
		v.put(ScoreTable.CHEMP_ID, chempId);
		v.put(ScoreTable.TEAM1_ID, team1);
		v.put(ScoreTable.TEAM2_ID, team2);
		v.put(ScoreTable.SCORE, score);
		
		return v;
	}
	
	private static ContentValues createTeam(long id, String title){
		ContentValues v = new ContentValues();
		v.put(TeamTable.ID, id);
		v.put(TeamTable.TITLE, title);
		return v;
	} 
	
	private static ContentValues createChemp(long id, String title){
		ContentValues v = new ContentValues();
		v.put(ChempTable.ID, id);
		v.put(ChempTable.TITLE, title);
		return v;
	}
}
