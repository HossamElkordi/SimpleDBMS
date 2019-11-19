package eg.edu.alexu.csd.oop.db.cs24;

import java.sql.SQLException;

public class MyDatabase implements Database {

	public String createDatabase(String databaseName, boolean dropIfExists) {
		return null;
	}
	
	public boolean executeStructureQuery(String query) throws SQLException {
		return false;
	}

	public Object[][] executeQuery(String query) throws SQLException {
		return null;
	}

	public int executeUpdateQuery(String query) throws SQLException {
		return 0;
	}
	
}
