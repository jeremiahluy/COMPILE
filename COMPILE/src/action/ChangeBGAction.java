package action;

import objects.*;

public class ChangeBGAction extends Action {
	public ChangeBGAction(Background bg) {
		this.bg = bg;
	}

	@Override
	public String convertToJS() {
		String s = new String();
		
		s += "{speaker:\"\", message:\"\", commands:\"changeBackgroundScroll(\\\"" + bg.getBackgroundName() + "\\\")\"}";
		
		return s;
	}

	@Override
	public String toString() {
		return "Change Background to: " + getBg().getBackgroundName();
	}

	@Override
	public String getFunction() {
		// TODO Auto-generated method stub
		return "changeBackgroundScroll(\\\"" + bg.getBackgroundName() + "\\\"); ";
	}
}
