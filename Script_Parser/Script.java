import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;


public class Script {
//	all lines in script
	private ArrayList<Line> lines = new ArrayList<Line>();
//	set of characters in play
	private HashSet<String> characters = new HashSet<String>();
//	script title
	private String title;
//	(Act, Scene) -> (Start line inclusive, Stop line inclusive)
	private HashMap<String, int[]> acts = new HashMap<String, int[]>();
	
	public Script(String file) {
		int act = 0;
		int scene = 0;
		int lineNumber = 0;
		String line;
		String character;
		String characterLine = "";
		File f = new File(file);
		Scanner scanner;
		try {
			scanner = new Scanner(f);
			title = scanner.nextLine();
			for (String s: scanner.nextLine().split(",")){
				characters.add(s.trim().toLowerCase());
			}
//			get act #
			if (scanner.nextLine().toLowerCase().startsWith("act")){
				act++;
			}
			if (scanner.nextLine().toLowerCase().startsWith("scene")) {
				scene++;
				acts.put(Integer.toString(act)+","+Integer.toString(scene), new int[]{lineNumber, -1});
			}
			while (true) {
				line = scanner.nextLine();
				if (characters.contains(line.toLowerCase().trim())) {
					character = line.toLowerCase().trim();
					lines.add(new Line(character));
					break;
				}
			}
			while (scanner.hasNextLine()) {
				line = scanner.nextLine();
				if (line.toLowerCase().startsWith("act")){
					act++;
				} else if (line.toLowerCase().startsWith("scene")) {
					acts.get(Integer.toString(act)+","+Integer.toString(scene))[1] = lineNumber;
					scene++;
					acts.put(Integer.toString(act)+","+Integer.toString(scene), new int[]{lineNumber+1, -1});
				} else if (characters.contains(line.toLowerCase().trim())) {
					lines.get(lines.size()-1).setLine(characterLine);
					characterLine = "";
					character = line.toLowerCase().trim();
					lines.add(new Line(character));
					lineNumber++;
				} else if (line.startsWith("[") || line.trim().equals("")){
					continue;
				} else {
					characterLine += " "+line;
				}
			}
			lines.get(lines.size()-1).setLine(characterLine);
			acts.get(Integer.toString(act)+","+Integer.toString(scene))[1] = lineNumber;
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@Override
    public String toString() {
		String s = "";
		s += title+"\n";
		int line = 0;
		for (Line l: lines) {
			s += Integer.toString(line)+ " "+ l.toString()+"\n";
			line++;
		}
		for (String key: acts.keySet()){
			int[] loc = acts.get(key);
			System.out.println(key + ":" + Integer.toString(loc[0])+","+Integer.toString(loc[1]));
		}
        return s;
    }
}