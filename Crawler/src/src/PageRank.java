package src;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class PageRank {

	CS454LinkAnalysis linkAnalysis;

	List<DocumentModel> allDocs;
	
	int totalDocument;
	
	Map<String, HashSet<String>> outLinks;
	
	Map<String, HashSet<String>> inLinks;
	
	Database db;

	PageRank() throws IOException {
	
		linkAnalysis = new CS454LinkAnalysis();
		
		totalDocument = linkAnalysis.getDocumentNumber();
		
		allDocs = linkAnalysis.getAllDocs();
		
		outLinks = linkAnalysis.getOutLinks();
		
		inLinks = linkAnalysis.getInLinks();
		
		db = new Database(false);
		
		this.startRanking();
	}

	public static void main(String[] args) throws IOException {
		new PageRank();
	}

	public void startRanking() {

		Map<String, Double> defaultRankMap = new HashMap<String, Double>();

		Double defaultRank = (double) (1 / totalDocument);

		Double d = 0.85;

		for (DocumentModel dm : allDocs)
			defaultRankMap.put(dm.getUrl(), defaultRank);

		for (int i = 0; i < 10; i++) {

			for (DocumentModel dm : allDocs) {
				
				Double sum = 0.0;
				
				Double rank = 0.0;
				
				if (dm.getInComingLink() != null) {
					
					for (String in : dm.getInComingLink()) {
						
						rank = defaultRankMap.get(in) / outLinks.get(in).size();
						
						defaultRankMap.replace(in, rank);
						
						sum = sum + rank;
					}
					
					rank = ((1 - d) + d * sum);
					
				} else {
					
					rank = rank + defaultRank;
					
				}
				
				dm.setFinalRank(rank);
			}
		}

		for (DocumentModel dm : allDocs) {
			//System.out.println(dm.getFinalRank());
			dm.round();
			dm.createJSON();
			db.insertRank(dm.getData());
		}
		/*
		 * for(String str : incoming.keySet()) { if(incoming.get(str) != null) {
		 * for(String in : incoming.get(str)) { rank =
		 * defaultRankMap.get(in)/outgoing.get(in).size(); sum = sum + rank; }
		 * rank = ((1-d)/totalDocument + d *sum); } }
		 * 
		 * 
		 * for (String s : tfidf.keySet()) { if(incoming.get(s) != null) {
		 * Set<String> in = incoming.get(s); Double scoreValue = map.get(s); for
		 * (String link : in) { scoreValue += (map.get(link) /
		 * outgoing.get(link).size()); } finalMap.put(s, scoreValue); } }
		 * 
		 * for(String k : finalMap.keySet()) { map.put(k, finalMap.get(k)); }
		 * 
		 * } for(String k : finalMap.keySet()) { System.out.println(k + " - " +
		 * finalMap.get(k)); }
		 */
	}
}