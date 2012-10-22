Android-AnnotatedSQL
====================

Android library for auto generating SQL schema

This project is a eclipse plugin to generate SQL schema by annotation for Android project
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
    
    

