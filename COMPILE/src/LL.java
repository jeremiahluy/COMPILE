import java.util.ArrayList;
import java.util.Stack;


public class LL {
	
	private Stack<String> errorList = new Stack<String>();
	
	public void displayErrors() {
		for(int i = 0; i < errorList.size(); i++)
			System.out.println(errorList.get(i));
	}
	
	/*----------BACKGROUND START----------*/
	
	public void bg_start(ArrayList<Token> tokenList, int index){
		if(tokenList.get(index).getType() == Token.BACKGROUND_DEFINITION){
			bg_openbrace(tokenList, index + 1);
		}
		else{
			System.out.println("start");
			reject();
		}
	}
	
	public void bg_openbrace(ArrayList<Token> tokenList, int index){
		if(tokenList.get(index).getType() == Token.L_BRACE ){
			bg_var(tokenList, index + 1);
		}
		else{
			System.out.println("start");
			reject();
		}
	}
	
	public void bg_var(ArrayList<Token> tokenList, int index){
		if(tokenList.get(index).getType() == Token.VARIABLE) {
			bg_ass(tokenList, index + 1);
		} else if(tokenList.get(index).getType() == Token.R_BRACE ) {
			char_start(tokenList, index + 1);
		} else {
			System.out.println("bg");
			reject();
		}

	}
	public void bg_ass(ArrayList<Token> tokenList, int index){
		if(tokenList.get(index).getType() == Token.ASSIGNMENT_OPERATION){
			bg_url(tokenList, index + 1);
		} else {
			System.out.println("bg");
			reject();
		}
	
	}
	
	public void bg_url(ArrayList<Token> tokenList, int index){
		if(tokenList.get(index).getType() == Token.URL){
			bg_eos(tokenList, index + 1);
		} else {
			System.out.println("bg");
			reject();
		}
	}
	
	public void bg_eos(ArrayList<Token> tokenList, int index){
		if(tokenList.get(index).getType() == Token.END_OF_STATEMENT){
			bg_var(tokenList, index + 1);
		} else {
			System.out.println("bg");
			reject();
		}
	}
	

	/*----------BACKGROUND END----------*/
	/*----------CHARACTER START----------*/

	
	public void char_start(ArrayList<Token> tokenList, int index){
		if(tokenList.get(index).getType() == Token.CHARACTER_DEFINITION){
			char_openbrace(tokenList, index + 1);
		} else {
			System.out.println("start2");
			reject();
		}
	}
	
	public void char_openbrace(ArrayList<Token> tokenList, int index){
		if(tokenList.get(index).getType() == Token.L_BRACE){
			char_def_start(tokenList, index + 1);
		} else {
			System.out.println("start2");
			reject();
		}
	} 
	
	public void char_def_start(ArrayList<Token> tokenList, int index){
		if(tokenList.get(index).getType() == Token.VARIABLE){
			char_def_openbrace(tokenList, index + 1);
			
		} else if(tokenList.get(index).getType() == Token.R_BRACE){
			begin(tokenList, index + 1);
			
		} else {
			System.out.println("character");
			reject();
		}
		
	}
	
	public void char_def_openbrace(ArrayList<Token> tokenList, int index){
		if(tokenList.get(index).getType() == Token.L_BRACE){
			emo_var(tokenList, index + 1);
			
		} else {
			System.out.println("character_openbrace");
			reject();
		}
		
	}


	/*----------CHARACTER END----------*/
	/*----------EMOTION START----------*/
	
	public void emo_var(ArrayList<Token> tokenList, int index){
		if(tokenList.get(index).getType() == Token.VARIABLE){
			emo_ass(tokenList, index + 1);
			
		} else if(tokenList.get(index).getType() == Token.R_BRACE ) {
			char_def_start(tokenList, index + 1);
			
		} else {
			System.out.println("character2 emo var");
			reject();
		}
	}

	public void emo_ass(ArrayList<Token> tokenList, int index){
		if(tokenList.get(index).getType() == Token.ASSIGNMENT_OPERATION){
			emo_url(tokenList, index + 1);
		} else {
			System.out.println("character2 emo ass");
			reject();
		}
	}	

	public void emo_url(ArrayList<Token> tokenList, int index){
		if(tokenList.get(index).getType() == Token.URL){
			emo_eos(tokenList, index + 1);
		} else {
			System.out.println("character2 emo url");
			reject();
		}
	}
	

	public void emo_eos(ArrayList<Token> tokenList, int index){
		if(tokenList.get(index).getType() == Token.END_OF_STATEMENT) {
			emo_var(tokenList, index + 1);
			
		} else {
			System.out.println("character2 emo eos");
			reject();
		}
	}
	
	/*----------EMOTION END----------*/
	/*----------STORY START----------*/
	
	public void begin(ArrayList<Token> tokenList, int index){
		if(tokenList.get(index).getType() == Token.STORY_START && tokenList.get(index+1).getType() == Token.END_OF_FUNCTION){
			story(tokenList, index+2);
		} else {
			chpt(tokenList, index);
		}
		
	}
	
	public void chpt(ArrayList<Token> tokenList, int index){
		
		if(tokenList.get(index).getType() == Token.VARIABLE && tokenList.get(index+1).getType() == Token.END_OF_FUNCTION){
			story(tokenList, index+2);
			
		}
	}
	
	
	public void story(ArrayList<Token> tokenList, int index){
		
			
				
			if( tokenList.get(index).getType() == Token.END_OF_STATEMENT){
				
				try{
					story(tokenList, index+1);
				}
				catch(IndexOutOfBoundsException e){
					accept();
				}
			
				
			}
			else if(tokenList.get(index).getType() == Token.VARIABLE && tokenList.get(index+1).getType() == Token.END_OF_FUNCTION ){
				begin(tokenList, index);
			}
			
			else if(tokenList.get(index).getType() == Token.VARIABLE){
				
				
				action(tokenList, index+1);
			}
			

			else if( tokenList.get(index).getType() == Token.CHOICES_SEPARATOR && tokenList.get(index+1).getType() == Token.END_OF_FUNCTION){
				choice(tokenList, index+2);
			}
			
			else{
				System.out.println("story");
				reject();
			}
		
	
		
	
		
			
		
		
	}
	
	
	public void choice(ArrayList<Token> tokenList, int index){
	
		if(tokenList.get(index).getType() == Token.OPEN_PARENTHESIS && tokenList.get(index+1).getType() == Token.VARIABLE && tokenList.get(index+2).getType() == Token.CLOSE_PARENTHESIS
			&& tokenList.get(index+3).getType() == Token.MESSAGE && tokenList.get(index+4).getType() == Token.END_OF_STATEMENT  && tokenList.get(index+5).getType() == Token.CHAPTER_TRANSITION &&
			tokenList.get(index+6).getType() == Token.OPEN_PARENTHESIS && tokenList.get(index+7).getType() == Token.VARIABLE && tokenList.get(index+8).getType() == Token.CLOSE_PARENTHESIS
			&& 	tokenList.get(index+9).getType() == Token.MESSAGE && tokenList.get(index+10).getType() == Token.END_OF_STATEMENT 	){
			
		
			choice1(tokenList, index+11);
		}
		else{
			System.out.println("choice");
			reject();
		}
	
		
	}
	
	public void choice1(ArrayList<Token> tokenList, int index){
		
		if(tokenList.get(index).getType() == Token.CHAPTER_TRANSITION &&
		tokenList.get(index+1).getType() == Token.OPEN_PARENTHESIS && tokenList.get(index+2).getType() == Token.VARIABLE && tokenList.get(index+3).getType() == Token.CLOSE_PARENTHESIS
		&& tokenList.get(index+4).getType() == Token.MESSAGE ){
		
			choice(tokenList, index+5);
			
		}
		else{
		
			story(tokenList, index);
			
		}
		
	}
	
	public void action(ArrayList<Token> tokenList, int index){
		if(tokenList.get(index).getType() == Token.ENTRANCE_FUNCTION || tokenList.get(index).getType() == Token.EXIT_FUNCTION ||
				tokenList.get(index).getType() == Token.BACKGROUND_FUNCTION){
			
			
			story(tokenList, index+1);
		}
		else if((tokenList.get(index).getType() == Token.EMOTION_FUNCTION && tokenList.get(index+1).getType() == Token.VARIABLE) || 
				(tokenList.get(index).getType() == Token.MESSAGE_FUNCTION && tokenList.get(index+1).getType() == Token.MESSAGE) ){
		
			story(tokenList, index+2);
		}
		else{
			reject();
		}
		
		
	}
	
	
	
	
	public void reject(){
		System.out.println("Reject");
	}
	public void accept(){
		System.out.println("Accept");
	}
}
