package Transformation;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;


public class Transformer_PP {
	
	static class MyThread implements Runnable {
		int i = 0;
		
		public MyThread(int i)
		   {
		      this.i = i;
		   }
		
		@Override
		public void run() {
			try {
				exec(i);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args){
	    for(int i=0; i<26; i++){
	    	MyThread t = new MyThread(i);
	    	new Thread(t).start();
	    }
	  }
	
	public static void exec(int i) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("Resources/temp/" + (i+1) + ".txt"));
        FileOutputStream fos = new FileOutputStream("Resources/temp/" + (i+1) + "_clean.txt");
        String line;
        String input = "";
        int lineNumber = 1;
        
	    try {
	        while ((line = br.readLine()) != null) {
	        	System.out.println("Thread: " + Thread.currentThread().getName() + " Line: " + lineNumber);
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
	        		input += s0[0] + "<description>" + s2[0] + "</description>" + '\n';
//	        		System.err.println(s0[0] + "<description>" + s2[0] + "</description>");
	        	} else if (line.matches(".*<release>.+</literal>.*")) {
	        		String[] s0 = line.split("<r");
	        		String[] s1 = line.split(">");
	        		String[] s2 = s1[1].split("</");
	        		input += s0[0] + "<release>" + s2[0] + "</release>" + '\n';
//	        		System.err.println(s0[0] + "<release>" + s2[0] + "</release>");
	        	} else if (line.matches(".*<platform>.+</literal>.*")) {
	        		String[] s0 = line.split("<p");
	        		String[] s1 = line.split(">");
	        		String[] s2 = s1[1].split("</");
	        		input += s0[0] + "<platform>" + s2[0] + "</platform>" + '\n';
//	        		System.err.println(s0[0] + "<platform>" + s2[0] + "</platform>");
	        	} else if (line.matches(".*<developer>.+</literal>.*")) {
	        		String[] s0 = line.split("<d");
	        		String[] s1 = line.split(">");
	        		String[] s2 = s1[1].split("</");
	        		input += s0[0] + "<developer>" + s2[0] + "</developer>" + '\n';
//	        		System.err.println(s0[0] + "<developer>" + s2[0] + "</developer>");
	        	} else if (line.matches(".*<genid>.+</literal>.*")) {
	        		String[] s0 = line.split("<g");
	        		String[] s1 = line.split(">");
	        		String[] s2 = s1[1].split("</");
	        		input += s0[0] + "<genid>" + s2[0] + "</genid>" + '\n';
//	        		System.err.println(s0[0] + "<genid>" + s2[0] + "</genid>");
	        	} else if (line.matches(".*<genre>.+</literal>.*")) {
	        		String[] s0 = line.split("<g");
	        		String[] s1 = line.split(">");
	        		String[] s2 = s1[1].split("</");
	        		input += s0[0] + "<genre>" + s2[0] + "</genre>" + '\n';
//	        		System.err.println(s0[0] + "<genre>" + s2[0] + "</genre>");
	        	} else if (line.matches(".*<gendesc>.+</literal>.*")) {
	        		String[] s0 = line.split("<g");
	        		String[] s1 = line.split(">");
	        		String[] s2 = s1[1].split("</");
	        		input += s0[0] + "<gendesc>" + s2[0] + "</gendesc>" + '\n';
//	        		System.err.println(s0[0] + "<gendesc>" + s2[0] + "</gendesc>");
	        	} else if (line.matches(".*<publisher>.+</literal>.*")) {
	        		String[] s0 = line.split("<p");
	        		String[] s1 = line.split(">");
	        		String[] s2 = s1[1].split("</");
	        		input += s0[0] + "<publisher>" + s2[0] + "</publisher>" + '\n';
//	        		System.err.println(s0[0] + "<publisher>" + s2[0] + "</publisher>");
	        	} else if (line.matches(".*<modes>.+</literal>.*")) {
	        		String[] s0 = line.split("<m");
	        		String[] s1 = line.split(">");
	        		String[] s2 = s1[1].split("</");
	        		input += s0[0] + "<modes>" + s2[0] + "</modes>" + '\n';
//	        		System.err.println(s0[0] + "<modes>" + s2[0] + "</modes>");
	        	} else if (line.matches(".*<computingmedia>.+</literal>.*")) {
	        		String[] s0 = line.split("<c");
	        		String[] s1 = line.split(">");
	        		String[] s2 = s1[1].split("</");
	        		input += s0[0] + "<computingmedia>" + s2[0] + "</computingmedia>" + '\n';
//	        		System.err.println(s0[0] + "<computingmedia>" + s2[0] + "</computingmedia>");
	        	} else if (line.matches(".*</binding>.*")) continue;
	        	else {
//	        		System.err.println(line);
	        		input += line + '\n';
	        	}
		        fos.write(input.getBytes());
	        	lineNumber += 1;
	        }
	        
	    } catch (Exception e) {
	        System.out.println("Problem reading file.");
	    } finally {
	    	br.close();
	    	fos.close();
	    }
	}
}
