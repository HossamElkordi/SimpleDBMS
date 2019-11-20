package eg.edu.alexu.csd.oop.db.cs24;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class MainTest {
static Parser a=Parser.getInstace();

	public static void main(String[] args) {
		Map<String,Object> z=a.updateQueryParser("UPDATE table_name\n" +
                "SET column1 = value1, column2 = value2, ...\n" +
        "WHERE condition;");
        System.out.println("d");


	}

}
