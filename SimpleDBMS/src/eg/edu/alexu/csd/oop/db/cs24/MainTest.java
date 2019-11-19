package eg.edu.alexu.csd.oop.db.cs24;

import java.util.ArrayList;
import java.util.Iterator;

public class MainTest {

	public static void main(String[] args) {
		ArrayList<String> a = ConditionParser.getInstance().split("x>0 and y<5");
		for (Iterator<String> iterator = a.iterator(); iterator.hasNext();) {
			System.out.println((String) iterator.next());
			
		}
	}

}
