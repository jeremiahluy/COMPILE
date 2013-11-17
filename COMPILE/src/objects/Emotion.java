package objects;

public class Emotion {
	private String emoName;
	private String emoURL;
	private String emoID;
	
	public Emotion(String name, String url, String id){
		emoName = name;
		emoURL = url;
		emoID = id;
	}
	
	public String getEmoName() {
		return emoName;
	}
	public void setEmoName(String emoName) {
		this.emoName = emoName;
	}
	public String getEmoURL() {
		return emoURL;
	}
	public void setEmoURL(String emoURL) {
		this.emoURL = emoURL;
	}

	public String getEmoID() {
		return emoID;
	}

	public void setEmoID(String emoID) {
		this.emoID = emoID;
	}

}
