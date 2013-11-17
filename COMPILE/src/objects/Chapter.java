package objects;

import java.util.ArrayList;
import action.*;

public class Chapter {
	private String chapName;
	private ArrayList<ChapterChoice> chapChoices;
	private ArrayList<Emotion> usedEmotes;
	private ArrayList<Background> usedBGs;
	private ArrayList<StoryCharacter> usedChars;
	private ArrayList<Action> actionList;

	public Chapter(String s){
		chapName = s;
		chapChoices = new ArrayList<ChapterChoice>();
		usedEmotes = new ArrayList<Emotion>();
		usedBGs = new ArrayList<Background>();
		usedChars = new ArrayList<StoryCharacter>();
		actionList = new ArrayList<Action>();

	}

	public String convertToJS(){
		String s = new String();
		boolean flag = false;
		
		s += "function reloadKeys_" + chapName.toLowerCase() + "() {\n";
		s += "\tsprites = [";
		for(Emotion e : usedEmotes){
			if(flag)
				s += ", ";
			s += "'" + e.getEmoID() + "'";
			flag = true;
		}

		for(Background bg : usedBGs){
			if(flag)
				s += ", ";
			s += "'" + bg.getBackgroundName() + "'";
			flag = true;
		}
		
		s += "];\n";
		
		boolean char_flag = false;
		s += "\tcharacters = [";
		for(StoryCharacter c : usedChars){
			if(char_flag)
				s += ", ";
			s += "'" + c.getCharName() + "'";
			char_flag = true;
		}
		s += "];";
		s += "\n}\n\n";

		s += "function loadChapter_" + chapName.toLowerCase() + "() {\n";
		s += "\tcurrentChapter = [\n";

		for(Action a : actionList)
			s += "\t\t" + a.convertToJS() + ",\n";

		s += "\t];\n}";

		return s;
	}

	public String getChapName() {
		return chapName;
	}

	public ArrayList<ChapterChoice> getChapChoices() {
		return chapChoices;
	}

	public ArrayList<Emotion> getUsedEmotes() {
		return usedEmotes;
	}

	public ArrayList<Background> getUsedBGs() {
		return usedBGs;
	}

	public ArrayList<Action> getActionList() {
		return actionList;
	}

	public void setChapName(String chapName) {
		this.chapName = chapName;
	}

	public void setChapChoices(ArrayList<ChapterChoice> chapChoices) {
		this.chapChoices = chapChoices;
	}

	public void setUsedEmotes(ArrayList<Emotion> usedEmotes) {
		this.usedEmotes = usedEmotes;
	}

	public void setUsedBGs(ArrayList<Background> usedBGs) {
		this.usedBGs = usedBGs;
	}

	public void setActionList(ArrayList<Action> actionList) {
		this.actionList = actionList;
	}
	
	public void setUsedChars(ArrayList<StoryCharacter> usedChars){
		this.usedChars = usedChars;
	}
	
	public void addUsedChars(StoryCharacter character){
		for(StoryCharacter c : usedChars){
			if(c.getCharName().equals(character.getCharName()))
				return;
		}
		usedChars.add(character);
	}
	
	public void addUsedEmotion(Emotion emotes){
		for(Emotion e : usedEmotes){
			if(e.getEmoID().equals(emotes.getEmoID()))
				return;
		}
		usedEmotes.add(emotes);
	}

	public void addUsedBackground(Background background){
		for(Background bg : usedBGs){
			if(bg == background)
				return;
		}
		usedBGs.add(background);
	}

	public void addAction(Action a){
		if(a instanceof MultiAction)
			for(Action action : ((MultiAction)a).getActionList())
				addUsedObjects(action);
		else addUsedObjects(a);
//		addUsedObjects(a);
		actionList.add(a);
		
	}
	
	public void addUsedObjects(Action a){
		if(a.getClass() == EmotionAction.class) {
			addUsedEmotion(a.getE());
			addUsedChars(a.getC());
		} else if(a.getClass() == CharacterEnterAction.class ||
					a.getClass() == CharacterExitAction.class ||
					a.getClass() == MessageAction.class) {
			addUsedChars(a.getC());
		} else if(a.getClass() == ChangeBGAction.class) {
			addUsedBackground(a.getBg());
		}
	}
	
	public void addChapterChoice(ChapterChoice c){
		chapChoices.add(c);
	}
	
}