package eg.edu.alexu.csd.oop.db.cs24;

public class CommandChecker {
	
	public void directCommand(String command) {		
		command = command.toLowerCase();
		if(command.contains("create") || command.contains("drop")) {
			
		}else if(command.contains("select")) {
			
		}else if(command.contains("insert") || command.contains("update") || command.contains("delete")) {
			
		}
	}
	
}
