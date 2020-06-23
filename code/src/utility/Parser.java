package utility;

import java.io.File;

public abstract class Parser {
	protected DBManager dbManager;
	//this class implements methods to set the right tables 
	protected void initializeDB() {
		// clear the DB
		dbManager.resetDB();
		
	}
}
