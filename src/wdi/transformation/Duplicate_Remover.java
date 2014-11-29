package wdi.transformation;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Duplicate_Remover {
	
	public static void main(String[] args) throws IOException {
		writeToFile(removeDuplicates(readFile()));
	}

	private static ArrayList<VideoGame> readFile() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("resources/temp/dbpedia.txt"));
		String line;
		String input = "";
		VideoGame vg = null;
		int counter = 0;
		ArrayList<VideoGame> games = new ArrayList<VideoGame>();
		
		try {
			while ((line = br.readLine()) != null) {
				if (line.matches(".*<result>.*")) {
					input += line + '\n';
					vg = new VideoGame();
					System.out.println("Creating new VideoGame object! --> " + ++counter);
				} else if (line.matches(".*<id>.+</id>.*")) {
					String[] s0 = line.split("<i");
					String[] s1 = line.split(">");
					String[] s2 = s1[1].split("</");
					input += s0[0] + "<id>" + s2[0] + "</id>" + '\n';
					vg.setId(s2[0]);
				} else if (line.matches(".*<title>.+</title>.*")) {
					String[] s0 = line.split("<t");
					String[] s1 = line.split(">");
					String[] s2 = s1[1].split("</");
					input += s0[0] + "<title>" + s2[0] + "</title>" + '\n';
				} else if (line.matches(".*<description>.+</description>.*")) {
					String[] s0 = line.split("<d");
					String[] s1 = line.split(">");
					String[] s2 = s1[1].split("</");
					input += s0[0] + "<description>" + s2[0] + "</description>" + '\n';
				} else if (line.matches(".*<release>.+</release>.*")) {
					String[] s0 = line.split("<r");
					String[] s1 = line.split(">");
					String[] s2 = s1[1].split("</");
					input += s0[0] + "<release>" + s2[0] + "</release>" + '\n';
				} else if (line.matches(".*<platform>.+</platform>.*")) {
					String[] s0 = line.split("<p");
					String[] s1 = line.split(">");
					String[] s2 = s1[1].split("</");
					input += s0[0] + "<platform>" + s2[0] + "</platform>" + '\n';
				} else if (line.matches(".*<developer>.+</developer>.*")) {
					String[] s0 = line.split("<d");
					String[] s1 = line.split(">");
					String[] s2 = s1[1].split("</");
					input += s0[0] + "<developer>" + s2[0] + "</developer>" + '\n';
				} else if (line.matches(".*<genid>.+</genid>.*")) {
					String[] s0 = line.split("<g");
					String[] s1 = line.split(">");
					String[] s2 = s1[1].split("</");
					input += s0[0] + "<genid>" + s2[0] + "</genid>" + '\n';
				} else if (line.matches(".*<genre>.+</genre>.*")) {
					String[] s0 = line.split("<g");
					String[] s1 = line.split(">");
					String[] s2 = s1[1].split("</");
					input += s0[0] + "<genre>" + s2[0] + "</genre>" + '\n';
				} else if (line.matches(".*<gendesc>.+</gendesc>.*")) {
					String[] s0 = line.split("<g");
					String[] s1 = line.split(">");
					String[] s2 = s1[1].split("</");
					input += s0[0] + "<gendesc>" + s2[0] + "</gendesc>" + '\n';
				} else if (line.matches(".*<publisher>.+</publisher>.*")) {
					String[] s0 = line.split("<p");
					String[] s1 = line.split(">");
					String[] s2 = s1[1].split("</");
					input += s0[0] + "<publisher>" + s2[0] + "</publisher>" + '\n';
				} else if (line.matches(".*<modes>.+</modes>.*")) {
					String[] s0 = line.split("<m");
					String[] s1 = line.split(">");
					String[] s2 = s1[1].split("</");
					input += s0[0] + "<modes>" + s2[0] + "</modes>" + '\n';
				} else if (line.matches(".*<computingmedia>.+</computingmedia>.*")) {
					String[] s0 = line.split("<c");
					String[] s1 = line.split(">");
					String[] s2 = s1[1].split("</");
					input += s0[0] + "<computingmedia>" + s2[0] + "</computingmedia>" + '\n';
				} else if (line.matches(".*</result>.*")) {
					input += line + '\n';
					vg.setInput(input);
					games.add(vg);
					input = "";
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			br.close();
		}
		return games;
	}
	
	public static ArrayList<VideoGame> removeDuplicates(ArrayList<VideoGame> games) {
		Map<String, String> map = new HashMap<String, String>();
		ArrayList<VideoGame> noDuplicateGames = new ArrayList<VideoGame>();
		
		System.out.println("--------------------------------------------------------");
		System.out.println("Removing Duplicates...");
		for (VideoGame g : games) {
			map.putIfAbsent(g.getId(), g.getInput());
		}
		for (String key : map.keySet()) {
			noDuplicateGames.add(new VideoGame(key, map.get(key)));
			System.out.println("Adding " + key);
		}
		return noDuplicateGames;
	}
	
	public static void writeToFile(ArrayList<VideoGame> games) throws IOException {
		FileOutputStream fos = new FileOutputStream("Resources/temp/dbpedia_clean.txt");
		String output = "";
		
		System.out.println("--------------------------------------------------------");
		System.out.println("Writing to file...");
		for (VideoGame g : games) {
			System.out.println("Writing game object " + g.getId());
			output = g.getInput();
			fos.write(output.getBytes());
		}
		fos.close();
	}
}
