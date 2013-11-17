package action;

import objects.StoryCharacter;


public class ResetAllCharAction extends Action {

	public ResetAllCharAction(StoryCharacter c, String message){
		this.c = c;
		this.message = message;
	}

	@Override
	public String convertToJS() {
		String s = new String();
			
		s += "{speaker:\"" + c.getCharName() + "\", message:\"" + message + "\", commands:\"resetAllCharacters()\"}";
			
		return s;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Reset All Character ";
	}

	@Override
	public String getFunction() {
		// TODO Auto-generated method stub
		return "resetAllCharacters(); ";
	}
}