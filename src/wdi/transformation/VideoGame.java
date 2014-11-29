package wdi.transformation;

public class VideoGame {
	
	private String id;
	private String input;
	
	public VideoGame(String id, String input) {
		this.id = id;
		this.input = input;
	}

	public VideoGame() {
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}
}
