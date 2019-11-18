package eg.edu.alexu.csd.oop.db.cs24;

public class Parser {

	private static Parser parser;
	
	private Parser() {
		
	}
	
	public static Parser getInstace() {
		if(parser == null) {
			parser = new Parser();
		}
		return parser;
	}
		
}
