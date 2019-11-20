package eg.edu.alexu.csd.oop.db.cs24;

import java.io.File;
import java.sql.SQLException;

public class MyDatabase implements Database {
	
	private final String dbsPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "Databases";

	public MyDatabase() {
		File dir = new File(dbsPath);
		dir.mkdirs();
	}
	
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
