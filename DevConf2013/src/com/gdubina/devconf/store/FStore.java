package com.gdubina.devconf.store;

import com.annotatedsql.annotation.provider.Provider;
import com.annotatedsql.annotation.provider.URI;
import com.annotatedsql.annotation.sql.Column;
import com.annotatedsql.annotation.sql.From;
import com.annotatedsql.annotation.sql.Join;
import com.annotatedsql.annotation.sql.PrimaryKey;
import com.annotatedsql.annotation.sql.Schema;
import com.annotatedsql.annotation.sql.SimpleView;
import com.annotatedsql.annotation.sql.Table;



@Schema(className = "FSchema", dbName = "fstore.db", dbVersion = 1)
@Provider(name = "FStoreProvider", authority = "com.gdubina.devconf.AUTHORITY", schemaClass = "FSchema", openHelperClass = "FakeHelper")
public interface FStore {

	@Table(TeamTable.TABLE_NAME)
	public static interface TeamTable {

		@URI
		String URI_CONTENT = "team_table";

		String TABLE_NAME = "team_table";

		@PrimaryKey
		@Column(type = Column.Type.INTEGER)
		String ID = "_id";
		
		@Column(type = Column.Type.TEXT)
		String TITLE = "title";
	}
	

	@Table(ChempTable.TABLE_NAME)
	public static interface ChempTable {

		@URI
		String URI_CONTENT = "chemp_table";

		String TABLE_NAME = "chemp_table";

		@PrimaryKey
		@Column(type = Column.Type.INTEGER)
		String ID = "_id";
		
		@Column(type = Column.Type.TEXT)
		String TITLE = "title";
	}
	
	

	@Table(ScoreTable.TABLE_NAME)
	public static interface ScoreTable {

		@URI
		String URI_CONTENT = "score_table";

		String TABLE_NAME = "score_table";

		@PrimaryKey
		@Column(type = Column.Type.INTEGER)
		String ID = "_id";
		
		@Column(type = Column.Type.INTEGER)
		String CHEMP_ID = "chemp_id";
		
		@Column(type = Column.Type.INTEGER)
		String TEAM1_ID = "team1_id";
		
		@Column(type = Column.Type.INTEGER)
		String TEAM2_ID = "team2_id";
		
		@Column(type = Column.Type.TEXT)
		String SCORE = "score";
	}
	
	
	@SimpleView(ScoreView.VIEW_NAME)
	public static interface ScoreView {

		@URI(type = URI.Type.DIR, onlyQuery = true)
		String URI_CONTENT = "score_view";

		String VIEW_NAME = "score_view";

		@From(ScoreTable.TABLE_NAME)
		String TABLE_SCORE = "table_score";

		@Join(joinTable = TeamTable.TABLE_NAME, joinColumn = TeamTable.ID, onTableAlias = TABLE_SCORE, onColumn = ScoreTable.TEAM1_ID)
		String TABLE_TEAM1 = "table_team1";
		
		@Join(joinTable = TeamTable.TABLE_NAME, joinColumn = TeamTable.ID, onTableAlias = TABLE_SCORE, onColumn = ScoreTable.TEAM2_ID)
		String TABLE_TEAM2 = "table_team2";
		
		@Join(joinTable = ChempTable.TABLE_NAME, joinColumn = ChempTable.ID, onTableAlias = TABLE_SCORE, onColumn = ScoreTable.CHEMP_ID)
		String TABLE_CHEMP = "table_chemp";
	}
	
}