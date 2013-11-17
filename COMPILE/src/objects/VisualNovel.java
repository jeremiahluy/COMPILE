package objects;

import java.util.ArrayList;

public class VisualNovel {
	private ArrayList<StoryCharacter> charList;
	private ArrayList<Background> bgList;
	private ArrayList<Chapter> chapList;
	private ArrayList<Graphics> cgList;
	
	public VisualNovel(){
		
	}
	
	public String generateJS(){
		String s = new String();
		
		s += convertChapterToJS() + "\n\n";
		s += convertAssetLibrary() + "\n\n";
		s += convertCharacterLibrary() + "\n\n";
		
		return s;
	}
	
	private String convertCharacterLibrary() {
		String s = new String();
		s += "var characterLibrary = [";
		
		for(StoryCharacter c : charList){
			s += "\n\t//" + c.getCharName() + "\n";
			s += "\t{\n";
			s += "\t\tid: \"" + c.getCharName() + "\",\n";
			s += "\t\tsprite: \"" + c.getEmotionList().get(0).getEmoID() + "\"\n";
			s += "\t}," + "\n";
		}
			
		s += "];";
		return s;
	}

	private String convertAssetLibrary() {
		String s = new String();
		
		s += "var assetLibrary = [";
		
		s += convertCharactersToJS() + "\n";
		s += convertBackgroundsToJS() + "\n";
		s += convertGraphicsToJS() + "\n";
		
		s += "];";
		return s;
	}

	private String convertCharactersToJS() {
		String s = new String();
		
		s += "\n//Sprites";
		for(StoryCharacter c : charList) {
			s += "\n\t//" + c.getCharName() + "\n";
			for(Emotion e : c.getEmotionList()) {
				s += "\t{id: '" + e.getEmoID() + "', src: '" + e.getEmoURL().substring(1, e.getEmoURL().length() - 1) + "', type: 1},\n";
			}
		}
		
		return s;
	}

	private String convertBackgroundsToJS() {
		String s = new String();
		s += "\n//Backgrounds\n";
		for(Background bg : bgList) {
			s += "\t{id: '" + bg.getBackgroundName() + "', src: '" + bg.getBackgroundURL().substring(1, bg.getBackgroundURL().length() - 1) + "', type: 2},\n";
		}
		 
		return s;
	}
	
	private String convertGraphicsToJS() {
		String s = new String();
		
		s += "//Graphics\n";
		for(Graphics cg : cgList) {
			s += "\t{id: '" + cg.getGraphicsName() + "', src: '" + cg.getGraphicsURL().substring(1, cg.getGraphicsURL().length() - 1) + "', type: 3},\n";
		}
		return s;
	}
	
	private String convertChapterToJS() {
		String s = new String();
		
		for(Chapter c : chapList) {
			s += "\n" + c.convertToJS() + "\n";
		} 
		
		return s;
	}
	
	public ArrayList<Chapter> getChapList() {
		return chapList;
	}
	
	public ArrayList<StoryCharacter> getCharList() {
		return charList;
	}

	public ArrayList<Background> getBgList() {
		return bgList;
	}

	public void setCharList(ArrayList<StoryCharacter> charList) {
		this.charList = charList;
	}

	public void setBgList(ArrayList<Background> bgList) {
		this.bgList = bgList;
	}

	public void setChapList(ArrayList<Chapter> chapList) {
		this.chapList = chapList;
	}

	public ArrayList<Graphics> getCgList() {
		return cgList;
	}

	public void setCgList(ArrayList<Graphics> cgList) {
		this.cgList = cgList;
	}

}
