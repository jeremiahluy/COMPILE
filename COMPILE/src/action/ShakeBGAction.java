

package action;

import objects.*;

public class ShakeBGAction extends Action {
	public ShakeBGAction(Background bg) {
		this.bg = bg;
	}

	@Override
	public String convertToJS() {
		String s = new String();
		
		s += "{speaker:\"\", message:\"\", commands:\"shakeScreen()\"}";
		
		return s;
	}

	@Override
	public String toString() {
		return "Shake Screen";
	}

	@Override
	public String getFunction() {
		// TODO Auto-generated method stub
		return "shakeScreen(); ";
	}
}
