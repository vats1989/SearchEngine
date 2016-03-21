package src;

import java.io.*;
import java.util.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;
import org.json.simple.*;

public class TFIDF {

	Map<String, HashMap<String, Integer>> tfMap = new HashMap<String, HashMap<String, Integer>>();
	Map<String, HashMap<String, Double>> tfidfMap = new HashMap<String, HashMap<String, Double>>();
	final String indexPath = "F:\\MyIndexing\\indexing";
	Database db = new Database(true);
	Database transactionDB = new Database();
	
	public TFIDF(boolean flag) throws IOException	{
		if(flag)
			calculateTFIDF(indexPath);
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {	
		TFIDF t = new TFIDF(true);
		//String term = "java";
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Double> sortByValues(Map<String, Double> map) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue())
						.compareTo(((Map.Entry) (o1)).getValue());
			}
		});
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public void calculateTFIDF(String indexPath) throws IOException {
		
		Directory dir = FSDirectory.open(new File(indexPath));
		IndexReader ir = IndexReader.open(dir);

		int N = ir.maxDoc();
		System.out.println("TF Calculation Starts....");
		for (int docNum = 0; docNum < ir.numDocs(); docNum++) {
			
			TermFreqVector tfv = ir.getTermFreqVector(docNum, "contents");
			if (tfv == null || tfv.size() == 0) {
				continue;
			}
			
			String terms[] = tfv.getTerms();
			int termCount = terms.length;
			int freqs[] = tfv.getTermFrequencies();
			String url = ir.document(docNum).getField("url").stringValue().toString();

			for (int t = 0; t < termCount; t++) {
				if (!tfMap.containsKey("" + terms[t])) {
					Map<String, Integer> map = new HashMap<String, Integer>();
					map.put(url, freqs[t]);
					tfMap.put("" + terms[t], (HashMap<String, Integer>) map);
				} else {
					Map<String, Integer> map = tfMap.get("" + terms[t]);
					map.put(url, freqs[t]);
					tfMap.put("" + terms[t], (HashMap<String, Integer>) map);
				}
			}
		}
		
		Map<String, Integer> tempMap1;
		JSONArray jsonArray;
		JSONObject jsonObject;

		for (String key : tfMap.keySet()) {

			if(!key.isEmpty() && key.length() != 0 && !key.trim().equals("") )		{

				Double idf = (double) Math.log10((double) N / tfMap.get(key).size());
				tempMap1 = (HashMap<String, Integer>) tfMap.get(key);
				jsonArray = new JSONArray();
				
				if(key.contains("."))
					key = key.replaceAll(".", "");
				
				for (String str : tempMap1.keySet())	{
						jsonObject = new JSONObject();
						jsonObject.put("tfidf", tempMap1.get(str) * idf);
						jsonObject.put("url",str);
						jsonArray.add(jsonObject);
				}
				db.insertScore(key, jsonArray);
			}
		}
	}
	
	public Map<String, Double> getTFIDFValueByTerm(String term) {
		return db.getTFIDFFromDB(term);
	}
}
