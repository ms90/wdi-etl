package Extraction;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;


public class Extractor {
	
	static ArrayList<String> ids = new ArrayList<>();

	public static void main (String args[]) {
		try {
			run();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static void query(String id) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	String fileName = "Resources/output.txt";
    	FileWriter fw = new FileWriter(fileName, true);
		
		String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ " PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>"
				+ " PREFIX dbpprop: <http://dbpedia.org/property/>"
				+ " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ " SELECT ?id ?title ?description ?release ?platform ?developer ?genid ?genre ?gendesc ?publisher ?modes ?computingmedia WHERE { "
				+ " ?g a dbpedia-owl:VideoGame."
				+ " ?g dbpedia-owl:wikiPageID \"" + id + "\"^^xsd:integer."
				+ " ?g dbpedia-owl:wikiPageID ?id."
				+ " ?g rdfs:label ?title."
				+ " FILTER (lang(?title) = 'en')"
				+ " ?g dbpedia-owl:abstract ?description."
				+ " FILTER (lang(?description) = 'en')"
				+ " ?g dbpedia-owl:computingPlatform ?p."
				+ " ?p rdfs:label ?platform."
				+ " FILTER (lang(?platform) = 'en')"
				+ " ?g dbpedia-owl:genre ?gen."
				+ " ?gen rdfs:label ?genre."
				+ " FILTER (lang(?genre) = 'en')"
				+ " ?gen dbpedia-owl:abstract ?gendesc."
				+ " FILTER (lang(?gendesc) = 'en')"
				+ " ?gen dbpedia-owl:wikiPageID ?genid."
				+ " ?g dbpprop:modes ?mod."
				+ " ?mod rdfs:label ?modes."
				+ " FILTER (lang(?modes) = 'en')"
				+ " OPTIONAL {?g dbpedia-owl:releaseDate ?release.}"
				+ " OPTIONAL {?g dbpprop:developer ?developer."
				+ " FILTER (lang(?developer) = 'en')}"
				+ " OPTIONAL {?g dbpedia-owl:publisher ?pub."
				+ " ?pub rdfs:label ?publisher."
				+ " FILTER (lang(?publisher) = 'en')}"
				+ " OPTIONAL {?g dbpedia-owl:computingMedia ?cm."
				+ " ?cm rdfs:label ?computingmedia."
				+ " FILTER (lang(?computingmedia) = 'en')}}";
		
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
		
		ResultSet rs = qexec.execSelect();
		if (rs.hasNext()){
			ResultSetFormatter.outputAsXML(baos, rs);
			String results = baos.toString();
			fw.write(results);
		}
		fw.close();
		qexec.close();
	}

	private static void run() throws IOException {
		getIds();
		for (int i=0; i<ids.size(); i++) {
			System.out.println("Processing " + (i+1) + "/" + ids.size() + " ID: " + ids.get(i));
			try {
				query(ids.get(i));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	private static void getIds() {
		try {
			FileReader fr = new FileReader("Resources/ids.txt");
			BufferedReader br = new BufferedReader(fr);
			String line;
			
			while((line = br.readLine()) != null) {
				ids.add(line);
			}
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

