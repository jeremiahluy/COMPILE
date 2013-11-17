
import java.util.ArrayList;



public class LexicalAnalyzer {

	ArrayList<Token> tokenList;
	String currToken;
	int currState;
	private boolean print_Lex = false;

	public LexicalAnalyzer(){
		tokenList = new ArrayList<Token>();
	}
	
	public void end(){
		tokenList.add(new Token("$", Token.END_TOKEN));
	}
	
	public void analyze(char[] charArr){
		startState(charArr, 0);
	}

	private void startState(char[] charArr, int i) {

		currToken = "";
		//System.out.println("");
		if(i < charArr.length){
			if(charArr[i] == '"')
				strinState(charArr, i+1);
			else if(charArr[i] == ' ' || charArr[i] == '\t' )
				startState(charArr, i + 1);
			else if(charArr[i] == '[')
				linkState(charArr, i);
			else if(Character.isDigit(charArr[i]))
				digitState(charArr, i);
			else identState(charArr, i);
		}
	}
	
	int deciCount;
	private void digitState(char[] charArr, int i){
		
		if(i<charArr.length){
			if(Character.isDigit(charArr[i])){
				currToken += charArr[i];
				digitState(charArr, i+1);
			}
			else if(charArr[i] == '.' && deciCount == 0){
				deciCount = 1;
				currToken += charArr[i];
				digitState(charArr, i+1);
			}
			else{
				saveDigit(currToken);
				System.out.println("Saving Token: DIGIT " + currToken);
				startState(charArr, i);
			}
		}
		else{
			if(currToken != ""){
				saveDigit(currToken);
				System.out.println("Saving Token: DIGIT " + currToken);
				}
			return;
			
		}
		
	}

	private void linkState(char[] charArr, int i) {
		currState = 4;

		if(i < charArr.length){
			if(charArr[i] != ']'){
				currToken += charArr[i];
				linkState(charArr, i+1);
			}
			else if(charArr[i] == ']') {
				currToken += charArr[i];
				saveToken(currToken);
				startState(charArr, i+1);
			}

		}
		else {
			if(currToken != ""){
				saveToken(currToken);
			}
			return;
		}



	}

	private void strinState(char[] charArr, int i) {

		if(i < charArr.length){
			if(charArr[i] == '\\' && charArr.length >= i + 1) {
				currToken += charArr[i]  + "" +  charArr[i + 1];
				//System.out.println("Character: " + charArr[i] + "" + charArr[i + 1]);
				strinState(charArr, i + 2);
			} 
			else  if(charArr[i] == '"') {
				saveToken("\"" + currToken + "\"");
				startState(charArr, i + 1);
			}else {
				currToken += charArr[i];

				strinState(charArr, i + 1);
			}
		}

		else {
			if(currToken != ""){
				saveToken(currToken);
			}

			return;
		}
	}

	private void identState(char[] charArr, int i) {


		if(i < charArr.length){

			if(charArr[i] == '{' || charArr[i] == '}' || charArr[i] == ';' ||
					charArr[i] == ']' || charArr[i] == '=' ||
					charArr[i] == '(' || charArr[i] == ')' || charArr[i] == ':' ||
					charArr[i] == ',') {

				if(currToken != ""){
					saveToken(currToken);
					currToken = "";
				}

				currToken += charArr[i];
				saveToken(currToken);
				currToken = "";
				identState(charArr, i + 1);

			} 

			else if(charArr[i] == '[' || charArr[i] == '"'){
				if(currToken != ""){
					saveToken(currToken);

				}
				startState(charArr, i);
			} else{
				if(charArr[i] == ' '){
					if(currToken != ""){
						saveToken(currToken);
						currToken = "";
					}
					identState(charArr, i + 1);
				}
				else{
					currToken += charArr[i];
					identState(charArr, i+1);
				}
			}
		} else {
			if(currToken != ""){
				saveToken(currToken);
			}
			return;
		}
	}



	private void saveToken(String s){
		Token t = new Token(s, Keyword.getTokenType(s));
		tokenList.add(t);
	}

	private void saveDigit(String s){
		Token t = new Token(s, Token.DIGIT);
		tokenList.add(t);
	}
	
	public ArrayList<Token> getTokenList(){
		return tokenList;
	}

}