package eg.edu.alexu.csd.oop.db.cs24;

import java.util.ArrayList;
import java.util.HashMap;

public class MainTest {
//	static Parser a=Parser.getInstace();

	public static void main(String[] args) {
		String path = System.getProperty("user.dir") + System.getProperty("file.separator") + "Databases" + System.getProperty("file.separator") + "test.xml";
		HashMap<String, String> cols = new HashMap<String, String>();
		cols.put("id", "int");
		cols.put("name", "varchar");
		Table table = new Table("group", cols);
		XML p = XML.getInstace();
		p.SaveTable(table, path);
		HashMap<String, String> record = new HashMap<String, String>();
		record.put("id", "24");
		record.put("name", "Hossam");
		table.addRecord(record, path);
		record.put("id", "10");
		record.put("name", "Ahmes");
		table.addRecord(record, path);
		record.put("id", "22");
		record.put("name", "Pierre");
		table.addRecord(record, path);
		record.put("id", "66");
		record.put("name", "Mina");
		table.addRecord(record, path);
		ArrayList<String> condition = new ArrayList<String>();
		condition.add("id"); condition.add("="); condition.add("10");
		Object[][] ans=table.SelectRecord(condition, path);
		ArrayList<String> condition1 = new ArrayList<String>();
		
//		ConditionParser cp = ConditionParser.getInstance();
//		ArrayList<String> condition = new ArrayList<String>();
//		ArrayList<String> reps = new ArrayList<String>();
//		condition.add("x"); condition.add("="); condition.add("x"); condition.add("or"); condition.add("y"); condition.add(">="); condition.add("7");
//		reps.add("y"); reps.add("7");
//		System.out.println(cp.evaluate(condition, reps));
	}

}
