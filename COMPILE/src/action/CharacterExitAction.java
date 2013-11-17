package action;

import objects.*;

public class CharacterExitAction extends Action {
	public CharacterExitAction(StoryCharacter c){
		this.c = c;
	}

	@Override
	public String convertToJS() {
		String s = new String();
		
		s += "{speaker:\"\", message:\"\", commands:\"removeCharacterScroll(\\\"" + c.getCharName() + "\\\")\"}";
		
		return s;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Exit Action: " + c.getCharName();
	}

	@Override
	public String getFunction() {
		// TODO Auto-generated method stub
		return "removeCharacterScroll(\\\"" + c.getCharName() + "\\\"); ";
	}
}
