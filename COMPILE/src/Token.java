
public class Token {
	private int type;
	private String token;
		
	public static final int VARIABLE = 0,
			KEYWORD = 1,
			MESSAGE = 2,
			ENTRANCE_FUNCTION = 3,		//enters
			EXIT_FUNCTION = 4,			//leaves
			MESSAGE_FUNCTION = 5,		//says
			END_OF_STATEMENT = 6,		//;
			CHAPTER_FUNCTION = 7,		//
			CHOICES_SEPARATOR = 8,		//or
			CHAPTER_TRANSITION = 9,		//Choices
			BACKGROUND_FUNCTION = 10,	//Background
			CHARACTER_DEFINITION = 11,	//Character
			STORY_START = 12,			//Start
			BACKGROUND_DEFINITION = 13,	//background
			EMOTION_FUNCTION = 14,		//feels
			L_BRACE = 15,				//{
			R_BRACE = 16,				//}
			L_SQUARE_BRACE = 17,		//[
			R_SQUARE_BRACE = 18,		//]
			URL = 19,					//[VAR]
			ASSIGNMENT_OPERATION = 20,	//=
			OPEN_PARENTHESIS = 21,		//(
			CLOSE_PARENTHESIS = 22,		//)
			END_OF_FUNCTION = 23,		//:
			CHARACTER = 24,
			EMOTION = 25,
			CG_DEFINITION = 26,			//CG 
			END_TOKEN = 27,				//$
			MULTIACTION = 28,			//,
			DIGIT = 29;					//numbers
	
	public Token(String s, int type){
		this.setToken(s);
		this.setType(type);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
}
