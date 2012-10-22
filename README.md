Android-AnnotatedSQL
====================

Android library for auto generating SQL schema. 

This project is an eclipse plugin to generate SQL schema by annotations for Android project. It's annotation processor so it will not add some code to your final apk.
It will work during compile process

To use this library in project you should add annotations jar to project class path  and add plugin to eclipse. After that you should turn on annotation preprocessing for your project on the project properties screen and check plugin in factory path section.

To build api jar only please use buil-api.xml

We suppport the following annotations: **@Autoincrement**, **@Column**, **@From**, **@Index**, **@Join**, **@NotNull**, **@PrimaryKey**, **@Schema**, **@SimpleView**, **@Table**

###Top level annotation:
* @Schema - tables container class. 
* @Table - table definition interface
	* @Column - mark field as table column. 
    * @Autoincrement - mark field as autoincrement
    * @PrimaryKey - mark field as Primary Key    
    * @NotNull - filed can't be NULL
* @PrimaryKey - you can specify composite Primary Key for table
* @Index - create index
* @SimpleView - create simple view to join a few tabels
	* @From
    * @Join
    
##Example    
Create tabels and view for sport results application.

* Defile schema

`	@Schema("SqlSchema")
	public class FStore {
	....
`

* Let's define interfaces inside schema class
		
        @Table(TeamTable.TABLE_NAME)
      	public static interface TeamTable{
          
            String TABLE_NAME = "team_table";
    
            @PrimaryKey
            @Column(type = Type.INTEGER)
            String ID = "_id";
            
            @Column(type = Type.TEXT)
            String TITLE = "title";
            
            @Column(type = Type.INTEGER)
            String CHEMP_ID = "chemp_id";
            
            @Column(type = Type.INTEGER)
            String IS_FAV = "is_fav";
        }
    
        @Table(ChempTable.TABLE_NAME)
        public static interface ChempTable{
            
    
            @PrimaryKey
            @Column(type = Type.INTEGER)
            String ID = "_id";
            
            @Column(type = Type.TEXT)
            String TITLE = "title";
            
        }

		@Table(ResultsTable.TABLE_NAME)
		@Index(name = "chemp_index", columns = ResultsTable.CHEMP_ID)
		@PrimaryKey(collumns = {ResultsTable.TEAM_ID, ResultsTable.CHEMP_ID})
		public static interface ResultsTable{
    
			@Column(type = Type.INTEGER)
			String ID = "_id";
        
    		@NotNull
			@Column(type = Type.INTEGER)
			String TEAM_ID = "team_id";
        
			@NotNull
			@Column(type = Type.INTEGER)
			String POINTS = "points";
        
			@NotNull
			@Column(type = Type.INTEGER)
			String CHEMP_ID = "chemp_id";
       		 ...............
    	}
    
      

* Define simple view to join all necessary data for score screen

		@SimpleView(ResultView.VIEW_NAME)
		public static interface ResultView{
		
			@From(ResultsTable.TABLE_NAME)
			String TABLE_RESULT = "table_result";
		
			@Join(srcTable = TeamTable.TABLE_NAME, srcColumn = TeamTable.ID, destTable = ResultView.TABLE_RESULT, destColumn = ResultsTable.TEAM_ID)
			String TABLE_TEAM = "table_team";
		
			@Join(srcTable = ChempTable.TABLE_NAME, srcColumn = ChempTable.ID, destTable = ResultView.TABLE_RESULT, destColumn = ResultsTable.CHEMP_ID)
			String TABLE_CHEMP = "table_chemp";
		}
