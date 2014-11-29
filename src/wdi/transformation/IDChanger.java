package wdi.transformation;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class IDChanger {

	public static void main(String[] args) {
		try {
			run();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void run() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("resources/xml/thegamesdb_merged.txt"));
        FileOutputStream fos = new FileOutputStream("resources/xml/thegamesdb_redone.txt");
        FileOutputStream out = new FileOutputStream("resources/id_changes.txt");
        String line;
        String input = "";
        String output = "";
        int lineNumber = 1;
        int counter = 0;
        
        try {
	        while ((line = br.readLine()) != null) {
	        	System.out.println("Processing line: " + lineNumber);
	        	if (line.matches(".*<id>.+</id>.*")) {
	        		String[] s0 = line.split("<i");
	        		String[] s1 = line.split(">");
	        		String[] s2 = s1[1].split("</");
	        		String id = "gdb-" + counter;
	        		input = s0[0] + "<id>" + id + "</id>" + '\n';
	        		output = s2[0] + ";" + id + '\n';
		        	counter++;
	        	} else input = line + '\n';
	        	lineNumber++;
		        fos.write(input.getBytes());
		        out.write(output.getBytes());
	        }
        } catch (Exception e) {
	        System.out.println(e.getMessage());
	    } finally {
	    	br.close();
	    	fos.close();
	    	out.close();
	    }
	}
}