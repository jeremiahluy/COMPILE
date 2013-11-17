package action;

import java.util.ArrayList;

import objects.Background;
import objects.ChapterChoice;
import objects.Emotion;
import objects.StoryCharacter;

public abstract class Action {
	protected StoryCharacter c;
	protected Background bg;
	protected String message;
	protected Emotion e;
	protected String varName, URL;

	protected ArrayList<ChapterChoice> choiceList = new ArrayList<ChapterChoice>();
	
	public abstract String convertToJS();

	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}	
	public String getVarName() {
		return varName;
	}
	public void setVarName(String name) {
		this.varName = name;
	}	
	public Emotion getE() {
		return e;
	}
	public void setE(Emotion e) {
		this.e = e;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Background getBg() {
		return bg;
	}
	public void setBg(Background bg) {
		this.bg = bg;
	}
	public StoryCharacter getC() {
		return c;
	}
	public void setC(StoryCharacter c) {
		this.c = c;
	}
	public void addChapterChoice(ChapterChoice c){
		choiceList.add(c);
	}
	
	public abstract String toString();
	public abstract String getFunction();
	
}
