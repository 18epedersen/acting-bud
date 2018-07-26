public class Line {
	private String character;
	private String line;
	
	public Line(String character, String line) {
		this.character = character;
		this.line = line;
	}
	
	public Line(String character) {
		this.character = character;
	}
	
	public void setLine(String line) {
		this.line = line;
	}
	
	public String getCharacter() {
		return this.character;
	}
	
	public String getLine() {
		return this.line;
	}
	
	@Override
    public String toString() {
        return String.format(character + ": " + line);
    }
}