package com.annotatedsql.annotation.test;

import com.annotatedsql.annotation.provider.URI;
import com.annotatedsql.annotation.sql.From;
import com.annotatedsql.annotation.sql.Join;
import com.annotatedsql.annotation.sql.SimpleView;
import com.annotatedsql.annotation.sql.Table;

public class Store {

	@Table(Team.NAME)
	interface Team{
		String NAME = "team";
		String ID = "id";
	}
	
	@Table(Chemp.NAME)
	interface Chemp{
		String NAME = "chemp";
		String ID = "id";
	}
	
	@SimpleView("team_view")
	interface TeamView{
		
		@URI
		String PATH_CONTENT = "post/view";
		
		@URI
		String PATH_CONTENT_ITEM = "post/view/#";
		
		@From(Team.NAME)
		String team = "team";
		
		@Join(srcTable = Chemp.NAME, srcColumn = Chemp.ID, destColumn = Team.ID, destTable = Team.NAME)
		String chemp = "chemp";
	} 
}
