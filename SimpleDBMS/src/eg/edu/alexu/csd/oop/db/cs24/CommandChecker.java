package eg.edu.alexu.csd.oop.db.cs24;

import java.sql.SQLException;

public class CommandChecker {
	
	private MyDatabase db;
	
	private Object[][] columnsNames;
	private Object[][] dataSet;
	
	private int rowNum = 0;
	
	public CommandChecker() {
		db = new MyDatabase();		
	}
	
	public void directCommand(String command) throws SQLException {		
		command = command.toLowerCase();
		if(command.contains("create") || command.contains("drop")) {
			
			if(db.executeStructureQuery(command)) {
				// columnsNames should have been set from the parser
				rowNum = 0;
				dataSet = new Object[rowNum][0];
			}
			
		}else if(command.contains("select")) {
			
			dataSet = db.executeQuery(command);
			
		}else if(command.contains("insert") || command.contains("update") || command.contains("delete")) {
			
			rowNum = db.executeUpdateQuery(command);
				
			
		}
	}

	public Object[][] getDataSet() {
		return dataSet;
	}

	public Object[][] getColumnsNames() {
		return columnsNames;
	}
	
	public void setColumnsNames(Object[][] columnsNames) {
		this.columnsNames = columnsNames;
	}
	
}
