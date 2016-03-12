package src;

import java.io.IOException;
import java.util.*;

import org.json.simple.JSONObject;

public class SearchingTerm {

	Map<String, Double> tfidfMap;
	
	TFIDF tfidf;
	
	Database db = new Database(false);
	
	
	String term;
	
	Map<String, Double> newRankMap;

	SearchingTerm(String term) throws IOException {
		
		this.term = term;
		
		tfidf = new TFIDF(false);
		
		tfidfMap = tfidf.getTFIDFValueByTerm(term);
		
		newRankMap = new HashMap<String, Double>();
	}

	public static void main(String[] args) throws IOException {
		
		SearchingTerm st = new SearchingTerm("java");
		
		st.getPageRank();
	}

	public void getPageRank() {
		
		if(tfidfMap != null)	{
		
			for (String s : tfidfMap.keySet()) {
			
				//System.out.println(s);
				
				JSONObject json = db.getPageRank(s);
				
				newRankMap.put(json.get("path").toString(),
						
								Double.parseDouble(json.get("rank").toString())	+ tfidfMap.get(s));
			}
			
			newRankMap = tfidf.sortByValues(newRankMap);
			
			for(String s : newRankMap.keySet())
			
				System.out.println(s +" - "+ newRankMap.get(s));
		} else {
			System.out.println("Search Result Not Found");
		}
		
	}
}