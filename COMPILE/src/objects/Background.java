package objects;

public class Background {
	private String bgName;
	private String bgURL;
	private int bgIndex;
	
	public Background(String name, String url, int index){
		bgName = name;
		bgURL = url;
		bgIndex = index;
	}

	public String getBackgroundURL() {
		return bgURL;
	}

	public void setBackgroundURL(String bgURL) {
		this.bgURL = bgURL;
	}

	public String getBackgroundName() {
		return bgName;
	}

	public void setBackgroundName(String bgName) {
		this.bgName = bgName;
	}

	public int getBackgroundIndex() {
		return bgIndex;
	}

	public void setBackgroundIndex(int bgIndex) {
		this.bgIndex = bgIndex;
	}
	
	public String toString(){
		return "Background: " + bgName + " URL: " + bgURL + " Index: " + bgIndex;
	}
}
