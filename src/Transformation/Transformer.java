package Transformation;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;


public class Transformer {

	public static void main(String[] args) throws IOException {
		run();
	}
	
	public static void run() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("Resources/dbpedia.txt"));
        FileOutputStream fos = new FileOutputStream("Resources/dbpedia_clean.txt");
        String line;
        String input = "";
        int lineNumber = 1;
        
	    try {
	        while ((line = br.readLine()) != null) {
	        	System.out.println(lineNumber);
	        	if (line.matches(".*<id>.+</id>.*")) {
	        		String[] s0 = line.split("<i");
	        		String[] s1 = line.split(">");
	        		String[] s2 = s1[1].split("</");
	        		input += s0[0] + "<id>" + s2[0] + "</id>";
	        		System.err.println(s0[0] + "<id>" + s2[0] + "</id>");
	        	} else if (line.matches(".*<description>.+</literal>.*")) {
	        		String[] s0 = line.split("<d");
	        		String[] s1 = line.split(">");
	        		String[] s2 = s1[1].split("</");
	        		input += s0[0] + "<description>" + s2[0] + "</description>";
	        		System.err.println(s0[0] + "<description>" + s2[0] + "</description>");
	        	} else if (line.matches(".*<release>.+</literal>.*")) {
	        		String[] s0 = line.split("<r");
	        		String[] s1 = line.split(">");
	        		String[] s2 = s1[1].split("</");
	        		input += s0[0] + "<release>" + s2[0] + "</release>";
	        		System.err.println(s0[0] + "<release>" + s2[0] + "</release>");
	        	} else if (line.matches(".*<platform>.+</literal>.*")) {
	        		String[] s0 = line.split("<p");
	        		String[] s1 = line.split(">");
	        		String[] s2 = s1[1].split("</");
	        		input += s0[0] + "<platform>" + s2[0] + "</platform>";
	        		System.err.println(s0[0] + "<platform>" + s2[0] + "</platform>");
	        	} else if (line.matches(".*<developer>.+</literal>.*")) {
	        		String[] s0 = line.split("<d");
	        		String[] s1 = line.split(">");
	        		String[] s2 = s1[1].split("</");
	        		input += s0[0] + "<developer>" + s2[0] + "</developer>";
	        		System.err.println(s0[0] + "<developer>" + s2[0] + "</developer>");
	        	} else if (line.matches(".*<genid>.+</literal>.*")) {
	        		String[] s0 = line.split("<g");
	        		String[] s1 = line.split(">");
	        		String[] s2 = s1[1].split("</");
	        		input += s0[0] + "<genid>" + s2[0] + "</genid>";
	        		System.err.println(s0[0] + "<genid>" + s2[0] + "</genid>");
	        	} else if (line.matches(".*<genre>.+</literal>.*")) {
	        		String[] s0 = line.split("<g");
	        		String[] s1 = line.split(">");
	        		String[] s2 = s1[1].split("</");
	        		input += s0[0] + "<genre>" + s2[0] + "</genre>";
	        		System.err.println(s0[0] + "<genre>" + s2[0] + "</genre>");
	        	} else if (line.matches(".*<gendesc>.+</literal>.*")) {
	        		String[] s0 = line.split("<g");
	        		String[] s1 = line.split(">");
	        		String[] s2 = s1[1].split("</");
	        		input += s0[0] + "<gendesc>" + s2[0] + "</gendesc>";
	        		System.err.println(s0[0] + "<gendesc>" + s2[0] + "</gendesc>");
	        	} else if (line.matches(".*<publisher>.+</literal>.*")) {
	        		String[] s0 = line.split("<p");
	        		String[] s1 = line.split(">");
	        		String[] s2 = s1[1].split("</");
	        		input += s0[0] + "<publisher>" + s2[0] + "</publisher>";
	        		System.err.println(s0[0] + "<publisher>" + s2[0] + "</publisher>");
	        	} else if (line.matches(".*<modes>.+</literal>.*")) {
	        		String[] s0 = line.split("<m");
	        		String[] s1 = line.split(">");
	        		String[] s2 = s1[1].split("</");
	        		input += s0[0] + "<modes>" + s2[0] + "</modes>";
	        		System.err.println(s0[0] + "<modes>" + s2[0] + "</modes>");
	        	} else if (line.matches(".*<computingmedia>.+</literal>.*")) {
	        		String[] s0 = line.split("<c");
	        		String[] s1 = line.split(">");
	        		String[] s2 = s1[1].split("</");
	        		input += s0[0] + "<computingmedia>" + s2[0] + "</computingmedia>";
	        		System.err.println(s0[0] + "<computingmedia>" + s2[0] + "</computingmedia>");
	        	} else if (line.matches(".*</binding>.*")) continue;
	        	else {
	        		System.err.println(line);
	        		input += line;
	        	}
	        	lineNumber += 1;
	        }
	        
	        fos.write(input.getBytes());
	        
	    } catch (Exception e) {
	        System.out.println("Problem reading file.");
	    } finally {
	    	br.close();
	    	fos.close();
	    }
	}
}
