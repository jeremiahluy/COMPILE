
package action;

import objects.*;

public class MessageAction extends Action {
	public MessageAction(StoryCharacter c, String message){
		this.c = c;
		this.message = message;
	}
	
	public MessageAction(String c, String message){
		this.c = new StoryCharacter(c, 0);
		this.message = message.substring(1, message.length() - 1);
	}
	
	public String convertToJS(){
		String s = new String();
		s += "{speaker:\"" + c.getCharName() + "\", message:\"" + message + "\", commands:\"\"}";
		return s;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Message Action: " + getC().getCharName() + " " + getMessage();
	}

	@Override
	public String getFunction() {
		// TODO Auto-generated method stub
		return null;
	}
}
