
package action;

import objects.*;

public class ResetCharPositionAction extends Action {

	public ResetCharPositionAction(StoryCharacter c, String message){
		this.c = c;
		this.message = message;
	}

	@Override
	public String convertToJS() {
		String s = new String();
			
		s += "{speaker:\"" + c.getCharName() + "\", message:\"" + message + "\", commands:\"resetCharacterPositions()\"}";
			
		return s;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Reset Character Positions ";
	}

	@Override
	public String getFunction() {
		// TODO Auto-generated method stub
		return "resetCharacterPositions(); ";
	}
}
