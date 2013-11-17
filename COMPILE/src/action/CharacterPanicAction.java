
package action;

import objects.*;

public class CharacterPanicAction extends Action {

	public CharacterPanicAction(StoryCharacter c, String message){
		this.c = c;
		this.message = message;
	}

	@Override
	public String convertToJS() {
		String s = new String();
			
		s += "{speaker:\"" + c.getCharName() + "\", message:\"" + message + "\", commands:\"panicCharacter(" + c.getIndex() + ")\"}";
			
		return s;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Panic Character: " + c.getCharName();
	}

	@Override
	public String getFunction() {
		// TODO Auto-generated method stub
		return "panicCharacter(" + c.getIndex() + "); ";
	}
}

	
