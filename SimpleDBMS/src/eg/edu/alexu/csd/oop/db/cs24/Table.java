package eg.edu.alexu.csd.oop.db.cs24;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Table {
	
	private String name;
	private ArrayList<Column<?>> columns;
	
	@SuppressWarnings("unchecked")
	public Table(String name, HashMap<String, String> columns) {
		this.name = name;
		this.columns = new ArrayList<Column<?>>();
		
		Set<?> set = columns.entrySet();
		Iterator<?> iter = set.iterator();
		
		while (iter.hasNext()) {
			Map.Entry<String, String> m = (Map.Entry<String, String>) iter.next();
			if(m.getValue().equals("int")) {
				this.columns.add(new Column<Integer>(m.getKey()));
			}else {
				this.columns.add(new Column<String>(m.getKey()));
			}
		}
	}
	
	public String getName() {
		return name;
	}
	
	@SuppressWarnings("unchecked")
	public void addRecord(HashMap<String, String> record) {
		Set<?> set = record.entrySet();
		Iterator<?> iter = set.iterator();
		
		try {
			while (iter.hasNext()) {
				Map.Entry<String, String> m = (Map.Entry<String, String>) iter.next();
				Column<?> col = getColumnByName(m.getKey());
				if(col.getType().getSimpleName().equals("Integer")) {
					((Column<Integer>)col).add(Integer.parseInt(m.getValue()));
				}else {
					((Column<String>)col).add(m.getValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private Column<?> getColumnByName(String name) {
		for (Column<?> column : this.columns) {
			if(column.getName().equals(name)) {
				return column;
			}
		}
		return null;
	}
	
}
