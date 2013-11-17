package action;

import objects.Chapter;
import objects.ChapterChoice;

public class BranchAction extends Action {
	
	private ChapterChoice chapter;
	
	public BranchAction(ChapterChoice c) {
		this.chapter = c;
	}
	
	@Override
	public String convertToJS() {
		String s = new String();
		s += "{speaker:\"\", message:\"\", commands:\"addBranch(\\\"" + chapter.getMessage() + "\\\", \\\"" + chapter.getC().toLowerCase() + "\\\"); ";
		s += "\"}";
		return s;
	}

	@Override
	public String toString() {
		return "BRANCH HERE";
	}

	@Override
	public String getFunction() {
		return "addBranch(\\\"" + chapter.getMessage().substring(1, chapter.getMessage().length() - 1) + "\\\", \\\"" + chapter.getC().toLowerCase() + "\\\"); ";
	}

}
