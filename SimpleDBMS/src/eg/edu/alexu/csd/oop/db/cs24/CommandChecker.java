package eg.edu.alexu.csd.oop.db.cs24;

import java.sql.SQLException;
import java.util.ArrayList;

public class CommandChecker {
	
	private MyDatabase db;
	
	private static Object[][] columnsNames;
	private Object[][] dataSet;
	
	public CommandChecker() {
		db = new MyDatabase();		
	}
	
	public void directCommand(String command) throws SQLException {		
		command = command.toLowerCase();
		if(command.contains("create") || command.contains("drop")) {
			
			if(db.executeStructureQuery(command)) {
				// columnsNames should have been set from the parser
				dataSet = new Object[0][0];
			}
			
		}else if(command.contains("select")) {
			
			dataSet = db.executeQuery(command);
			
		}else if(command.contains("insert") || command.contains("update") || command.contains("delete")) {
			
			db.executeUpdateQuery(command);

		}
	}

	public Object[][] getDataSet() {
		return dataSet;
	}

	public Object[][] getColumnsNames() {
		return columnsNames;
	}
	
	public static void setColumnsNames(ArrayList<String> columnNames) {
		columnsNames = new Object[1][columnNames.size()];
		for (int i = 0; i < columnsNames.length; i++) {
			columnsNames[0][i] = columnNames.get(i);
		}
	}
	
}
