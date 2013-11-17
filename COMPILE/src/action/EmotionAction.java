package action;

import objects.*;

public class EmotionAction extends Action {
	
	public EmotionAction(StoryCharacter c, Emotion e){
		this.c = c;
		this.e = e;
	}
	
	@Override
	public String convertToJS() {
		String s = new String();
		
		s += "{speaker:\"\", message:\"\", commands:\"changeCharacter(\\\"" + c.getCharName() + "\\\", \\\"" + getE().getEmoID() + "\\\")\"}";
		
		return s;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Change Emotion: " + getC().getCharName() + " " + getE().getEmoName();
	}

	@Override
	public String getFunction() {
		// TODO Auto-generated method stub
		return "changeCharacter(\\\"" + c.getCharName() + "\\\", \\\"" + getE().getEmoID() + "\\\"); ";
	}

}
