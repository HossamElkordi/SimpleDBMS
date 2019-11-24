package eg.edu.alexu.csd.oop.db.cs24;

import java.util.ArrayList;

public class MyCache {
	
	private static MyCache cache;
	
	private ArrayList<Table> cachedTables;
	
	private MyCache() {
		cachedTables = new ArrayList<Table>();
	}

	public static MyCache getInstance() {
		if(cache == null) {
			cache = new MyCache();
		}
		return cache;
	}
	
	public Table retrieveFromCache(String tableName) {
		
		for (int i = 0; i < this.cachedTables.size(); i++) {
			if(cachedTables.get(i).getName().equals(tableName)) {
				Table t = cachedTables.get(i).clone();
				cachedTables.remove(i);
				return t;
			}
		}
		
		return null;
	}
	
	public void addToCache(Table table) {
		if(cachedTables.size() == 5) {
			cachedTables.get(0).writeInFile();
			cachedTables.remove(0);
			cachedTables.add(table);
		}else {
			cachedTables.add(table);
		}
	}
	
	public void clearCache() {
		for (int i = 0; i < this.cachedTables.size(); i++) {
			this.cachedTables.get(i).writeInFile();
		}
		this.cachedTables.clear();
	}
	
}
