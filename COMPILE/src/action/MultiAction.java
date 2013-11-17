package action;

import java.util.ArrayList;

public class MultiAction extends Action {
	private ArrayList<Action> actionList;
	
	public MultiAction(){
		actionList = new ArrayList<Action>();
	}
	
	public void addAction(Action a) {
		actionList.add(a);
	}
	
	public ArrayList<Action> getActionList() {
		return actionList;
	}

	public void setActionList(ArrayList<Action> actionList) {
		this.actionList = actionList;
	}

	@Override
	public String convertToJS() {
		String s = new String();
		String character = new String(), message = new String(), code = new String();
		for(int i = 0; i < actionList.size(); i++){
			Action a = actionList.get(i);
			if(a instanceof MessageAction){
				character = a.getC().getCharName();
				message = a.getMessage();
			} else {
				code = a.getFunction() + code;
			}
		}
		
		s = "{speaker:\"" + character + "\", message:\"" + message + "\", commands:\"" + code + "\"}";
		return s;
	}

	
		
	@Override
	public String toString() {
		return null;
	}

	@Override
	public String getFunction() {
		// TODO Auto-generated method stub
		return null;
	}
}
