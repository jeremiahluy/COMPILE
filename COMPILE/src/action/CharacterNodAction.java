package action;

import objects.*;

public class CharacterNodAction extends Action {

	public CharacterNodAction(StoryCharacter c, String message){
		this.c = c;
		this.message = message;
	}

	@Override
	public String convertToJS() {
		String s = new String();
			
		s += "{speaker:\"" + c.getCharName() + "\", message:\"" + message + "\", commands:\"nodCharacter(" + c.getIndex() + ")\"}";
			
		return s;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Nod Character: " + c.getCharName();
	}

	@Override
	public String getFunction() {
		// TODO Auto-generated method stub
		return "nodCharacter(" + c.getIndex() + "); ";
	}
}

	

