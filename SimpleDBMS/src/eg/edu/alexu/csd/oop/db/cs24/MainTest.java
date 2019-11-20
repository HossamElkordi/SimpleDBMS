package eg.edu.alexu.csd.oop.db.cs24;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class MainTest {
static Parser a=Parser.getInstace();

	public static void main(String[] args) {
		ConditionParser cp = ConditionParser.getInstance();
		ArrayList<String> condition = new ArrayList<String>();
		ArrayList<String> reps = new ArrayList<String>();
		condition.add("x"); condition.add(">"); condition.add("3"); condition.add("or"); condition.add("y"); condition.add("="); condition.add("7");
		reps.add("1"); reps.add("7");
		System.out.println(cp.evaluate(condition, reps));
	}

}
