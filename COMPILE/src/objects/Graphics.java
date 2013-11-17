package objects;

public class Graphics {
	private String cgName;
	private String cgURL;
	private int cgIndex;
	
	public Graphics(String name, String url, int index){
		cgName = name;
		cgURL = url;
		cgIndex = index;
	}

	public String getGraphicsURL() {
		return cgURL;
	}

	public void setGraphicsURL(String bgURL) {
		this.cgURL = bgURL;
	}

	public String getGraphicsName() {
		return cgName;
	}

	public void setGraphicsName(String bgName) {
		this.cgName = bgName;
	}

	public int getGraphicsIndex() {
		return cgIndex;
	}

	public void setGraphicsIndex(int bgIndex) {
		this.cgIndex = bgIndex;
	}
	
	public String toString(){
		return "CG: " + cgName + " URL: " + cgURL + " Index: " + cgIndex;
	}
}
