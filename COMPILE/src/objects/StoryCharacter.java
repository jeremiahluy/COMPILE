package objects;
import java.util.ArrayList;


public class StoryCharacter {
	private String charName;
	private ArrayList<Emotion> emotionList;
	private int index;
	
	public StoryCharacter(String name, int index){
		charName = name;
		emotionList = new ArrayList<Emotion>();
		this.index = index;
	}
	
	public String getCharName() {
		return charName;
	}
	public void setCharName(String charName) {
		this.charName = charName;
	}
	public ArrayList<Emotion> getEmotionList() {
		return emotionList;
	}
	public void setEmotionList(ArrayList<Emotion> emotionList) {
		this.emotionList = emotionList;
	}
	public void addEmotion(Emotion e){
		emotionList.add(e);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Emotion getEmotion(String string) {
		Emotion ret = null;
		for(Emotion e : emotionList)
			if(e.getEmoName().equals(string))
				ret = e;
		return ret;
	}
	
	public String toString(){
		String s = new String();
		s += "Name: " + charName + "\n";
		for(Emotion e : emotionList)
			s += "\t" + e.getEmoName() + ": " + e.getEmoURL() + "\n"; 
		
		return s;
	}
}
