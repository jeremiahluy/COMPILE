
public class Keyword {
	private static String keywords[] = {"Background",
										"Character",
										"Start",
										"Chapter",
										"background",
										"says",
										"Choices",
										"or",
										"feels",
										"enters",
										"leaves",
										";",
										"{",
										"}",
										"[",
										"]",
										"=",
										"(",
										")",
										":",
										"CG",
										"$",
										","};

	public static boolean isKeyword(String s) {
		for(int i = 0; i < keywords.length; i++)
			if(keywords[i].equals(s)){
				return true;
			}
		return false;
	}
	
	public static int getTokenType(String s){
		if(s.equals(keywords[0]))
			return Token.BACKGROUND_DEFINITION;
		else if(s.equals(keywords[1]))
			return Token.CHARACTER_DEFINITION;
		else if(s.equals(keywords[2]))
			return Token.STORY_START;
		else if(s.equals(keywords[3]))
			return Token.CHAPTER_FUNCTION;
		else if(s.equals(keywords[4]))
			return Token.BACKGROUND_FUNCTION;
		else if(s.equals(keywords[5]))
			return Token.MESSAGE_FUNCTION;
		else if(s.equals(keywords[7]))
			return Token.CHOICES_SEPARATOR;
		else if(s.equals(keywords[6]))
			return Token.CHAPTER_TRANSITION;
		else if(s.equals(keywords[8]))
			return Token.EMOTION_FUNCTION;
		else if(s.equals(keywords[9]))
			return Token.ENTRANCE_FUNCTION;
		else if(s.equals(keywords[10]))
			return Token.EXIT_FUNCTION;
		else if(s.equals(keywords[11]))
			return Token.END_OF_STATEMENT;
		else if(s.equals(keywords[12]))
			return Token.L_BRACE;
		else if(s.equals(keywords[13]))
			return Token.R_BRACE;
		else if(s.equals(keywords[14]))
			return Token.L_SQUARE_BRACE;
		else if(s.equals(keywords[15]))
			return Token.R_SQUARE_BRACE;
		else if(s.equals(keywords[16]))
			return Token.ASSIGNMENT_OPERATION;
		else if(s.equals(keywords[17]))
			return Token.OPEN_PARENTHESIS;
		else if(s.equals(keywords[18]))
			return Token.CLOSE_PARENTHESIS;
		else if(s.equals(keywords[19]))
			return Token.END_OF_FUNCTION;
		else if(s.equals(keywords[20]))
			return Token.CG_DEFINITION;
		else if(s.equals(keywords[21]))
			return Token.END_TOKEN;
		else if(s.equals(keywords[22]))
			return Token.MULTIACTION;
		else if(s.startsWith("\""))
			return Token.MESSAGE;
		else if(s.startsWith("["))
			return Token.URL;
		
		return Token.VARIABLE;
	}
	
	public static boolean isSeparator(char c) {
		if(!Character.isDigit(c) && !Character.isLetter(c))
			return true;
		return false;
	}
}
