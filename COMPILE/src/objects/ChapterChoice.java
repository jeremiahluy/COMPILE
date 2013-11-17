package objects;

public class ChapterChoice {
	private String c;
	private String message;
	
	public ChapterChoice(String s, String message) {
		this.c = s;
		this.message = message;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
