package eg.edu.alexu.csd.oop.db.cs24;

import java.sql.SQLException;
import java.util.ArrayList;

public class CommandChecker {
	
	private MyDatabase db;
	
	private static String[] columnsNames;
	private Object[][] dataSet;
	
	public CommandChecker() {
		db = new MyDatabase();		
	}
	
	public void directCommand(String command) throws SQLException {		
		command = command.toLowerCase();
		if(command.contains("create") || command.contains("drop")) {
			
			if(db.executeStructureQuery(command)) {
				
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

	public String[] getColumnsNames() {
		return columnsNames;
	}
	
	public static void setColumnsNames(ArrayList<String> columnNames) {
		columnsNames = new String[columnNames.size()];
		for (int i = 0; i < columnsNames.length; i++) {
			columnsNames[i] = columnNames.get(i);
		}
	}
	
}
