package eg.edu.alexu.csd.oop.db.cs24;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionParser {

	private static ConditionParser cp;

	Pattern mainPattern=Pattern.compile("");

	private ConditionParser() {

	}

	public static ConditionParser getInstance() {
		if(cp == null) {
			cp = new ConditionParser();
		}
		return cp;
	}
	private boolean isCompartor(Character checked){
		if(checked=='>'||checked=='<'||checked=='='){return true;}
		else return false;
	}
	public ArrayList<String> noregexparser(String input){
		ArrayList<String> output=new ArrayList<>();
		int i=0,istart;
		while(i<input.length()) {
			while (input.charAt(i) == ' ') {
				i++;
			}
			if (Character.isDigit(input.charAt(i)) || Character.isLetter(input.charAt(i))) {
				istart = i;
				while (!isCompartor(input.charAt(i)) && input.charAt(i) != ' ') {

					if (i == input.length() - 1) {
						output.add(input.substring(istart));
						i++;
						break;
					}
					i++;
				}
				if(i<input.length()) output.add(input.substring(istart, i));
			} else {
				istart = i;
				while (isCompartor(input.charAt(i))) {
					if (i == input.length() - 1) {
						output.add(input.substring(istart, i));
						i++;
						break;
					}
					i++;

				}
				if(i<input.length()) output.add(input.substring(istart, i));
			}

		}
		if(conditionChecker(output))return output;
		return null;
	}

	private boolean smallCondtioncheck(ArrayList<String> a){
		if(a.get(1).equals("<")||a.get(1).equals(">")||a.get(1).equals("==")||a.get(1).equals("<=")||a.get(1).equals(">=")){
			if(a.get(0)==null||a.get(2)==null){return false;}
			return true;
		}
		return false;
	}
	ArrayList<String> sublist(ArrayList<String> input,int start,int finish){
		ArrayList<String> op=new ArrayList<String>();
		while(start<finish){
			op.add(input.get(start));
			start++;
		}
		return op;
	}

	boolean conditionChecker(ArrayList<String> a){
		if(a.size()<3)return false;
		int i=0;
		while(i<a.size()){
			if(!smallCondtioncheck(sublist(a,i,i+3)))return false;
			if(i+3<a.size()&&!a.get(i+3).equals("and")&&!a.get(i+3).equals("or")){return false;}
			i=i+4;

		}
		return true;

	}
	
	public boolean evaluate(ArrayList<String> condition, ArrayList<String> replacements) {
		
		return false;
	}

}
