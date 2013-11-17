package action;

import objects.*;

public class CharacterEnterAction extends Action {
	public CharacterEnterAction(StoryCharacter c){
		this.c = c;
	}

	@Override
	public String convertToJS() {
		String s = new String();
		
		s += "{speaker:\"\", message:\"\", commands:\"addCharacterScroll(\\\"" + c.getCharName() + "\\\")\"}";
		
		return s;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Enter Action: " + c.getCharName();
	}

	@Override
	public String getFunction() {
		// TODO Auto-generated method stub
		return "addCharacterScroll(\\\"" + c.getCharName() + "\\\"); ";
	}
}
