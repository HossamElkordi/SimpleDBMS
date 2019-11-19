package eg.edu.alexu.csd.oop.db.cs24;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionParser {
	
	private static final String COMPTOSTRING_REGEX = "[A-Za-z]+";
	private static final String COMP_REGEX = "[[\\>\\<\\=]" + "[\\=]]";
	private static final String COMPTOINT_REGEX = "[0-9]+";
	private static final String CONDITION_REGEX = COMPTOSTRING_REGEX + COMP_REGEX + "[" + COMPTOSTRING_REGEX + COMPTOINT_REGEX + "]?";
	private static final String FORMULA_REGEX = CONDITION_REGEX + "[" + COMPTOSTRING_REGEX + CONDITION_REGEX + "]*";
	
	private Pattern stringEx;
	private Pattern intEx;
	private Pattern compEx;
	
	private static ConditionParser cp;
	
	private ConditionParser() {
		stringEx = Pattern.compile(COMPTOSTRING_REGEX);
		intEx = Pattern.compile(COMPTOINT_REGEX);
		compEx = Pattern.compile(COMP_REGEX);
	}

	public static ConditionParser getInstance() {
		if(cp == null) {
			cp = new ConditionParser();
		}
		return cp;
	}
	
	public ArrayList<String> split(String condition) {
		ArrayList<String> cond = new ArrayList<String>();
		
		if(Pattern.matches(FORMULA_REGEX, condition)) {
			
			Matcher matchString = stringEx.matcher(condition);
			Matcher matchInt = intEx.matcher(condition);
			Matcher matchComp = compEx.matcher(condition);
			
			while(true) {
				getOneCondition(cond, matchString, matchInt, matchComp);
				
//				if(condition.contains("or") || condition.contains("and") || condition.contains("not")) {
//					String s = matchString.group().toLowerCase();
//					if(s.equals("or") || s.equals("and") || s.equals("not")) {
//						cond.add(s);
//					}
//				}else {
//					break;
//				}
				
			}
		}
		
		return cond;
	}

	private void getOneCondition(ArrayList<String> cond, Matcher matchString, Matcher matchInt, Matcher matchComp) {
		while(matchString.find()) {
			cond.add(matchString.group());
			break;
		}
		while(matchComp.find()) {
			cond.add(matchComp.group());
			break;
		}
		while(matchInt.find()) {
			cond.add(matchInt.group());
			break;
		}
		while(matchString.find()) {
			cond.add(matchString.group());
			break;
		}
	}
}
