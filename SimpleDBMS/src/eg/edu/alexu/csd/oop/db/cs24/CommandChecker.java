package eg.edu.alexu.csd.oop.db.cs24;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class CommandChecker {
	
	private MyDatabase db;
	
	private static String[] columnsNames;
	private Object[][] dataSet;
	private int updatedRows = 0;

	public CommandChecker() {
		db = new MyDatabase();		
	}
	
	public void directCommand(String command) throws SQLException {		
		command = command.toLowerCase();
		if(command.contains("create") || command.contains("drop")) {
			
			if(db.executeStructureQuery(command)) {
				
				dataSet = new Object[0][0];
				updatedRows = 0;
			}
			
		}else if(command.contains("select")) {
			
			dataSet = db.executeQuery(command);
			updatedRows = 0;
			
		}else if(command.contains("insert") || command.contains("update") || command.contains("delete")) {
			
			updatedRows = db.executeUpdateQuery(command);

		}else {
			JOptionPane.showMessageDialog(null, "Syntax Error!");
			throw new SQLException("Syntax Error!");
		}
	}

	public int getUpdatedRows() {
		return updatedRows;
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
