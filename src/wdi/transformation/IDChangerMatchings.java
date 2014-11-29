package wdi.transformation;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class IDChangerMatchings {

	public static void main(String[] args) throws IOException {
		change(read());
	}

	public static HashMap<String, String> read() throws IOException {
		BufferedReader in = new BufferedReader(new FileReader("resources/id_changes.txt"));
        String line;
        int lineNumber = 1;
        HashMap<String, String> map = new HashMap<String, String>();       
        
        try {
	        while ((line = in.readLine()) != null) {
	        	System.out.println("Reading line: " + lineNumber);
	        	String[] s = line.split(";");
	        	map.put(s[0], s[1]);
	        	lineNumber++;
	        }
        } catch (Exception e) {
	        System.out.println(e.getMessage());
	    } finally {
	    	in.close();
	    }
		return map;
	}
	
	public static void change(HashMap<String, String> map) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("resources/xml/matched-1-3.txt"));
		FileOutputStream fos = new FileOutputStream("resources/xml/matched-1-3_new.txt");
		String line;
        int lineNumber = 1;
        String out = "";
        
        try {
        	System.out.println("------------------------------------------");
	        while ((line = br.readLine()) != null) {
	        	System.out.println("Processing line: " + lineNumber);
	        	String[] s = line.split(",");
	        	out = s[0] + "," + map.get(s[1]) + '\n';
	        	lineNumber++;
		        fos.write(out.getBytes());
	        }
        } catch (Exception e) {
	        System.out.println(e.getMessage());
	    } finally {
	    	br.close();
	    	fos.close();
	    }
	}
}