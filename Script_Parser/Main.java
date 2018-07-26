import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder; 

public class Main {
	public static void main(String[] args) {
//		Gson gson = new GsonBuilder().create();
//        gson.toJson("Hello", System.out);
//        gson.toJson(123, System.out);
//		File file = new File("src/romeo_juliet.txt");
//		System.out.println(file.exists());
//		Scanner sc;
//		try {
//			sc = new Scanner(file);
//			while (sc.hasNextLine()){
//				System.out.println(sc.nextLine());
//			}
//			sc.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		Script s = new Script("src/macbeth.txt");
		Gson gson = new Gson();
		System.out.println(gson.toJson(s));
	}
}