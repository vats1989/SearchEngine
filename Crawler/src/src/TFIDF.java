package src;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TFIDF {

	Map<String, HashMap<String, Integer>> tfMap = new HashMap<String, HashMap<String, Integer>>();
	Map<String, HashMap<String, Double>> tfidfMap = new HashMap<String, HashMap<String, Double>>();
	final String indexPath = "F:\\MyIndexing\\indexing";
	Database db = new Database(true);
	
	TFIDF(boolean flag) throws IOException	{
		if(flag)
			calculateTFIDF(indexPath);
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
	
		TFIDF t = new TFIDF(true);
		String term = "java";
		//Map<String, Double> result = t.getTFIDFValueByTerm(term);
		//DecimalFormat twoDForm = new DecimalFormat("##.##");
		//for(String s : result.keySet())
		//	System.out.println(s+":"+twoDForm.format(result.get(s)));
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
		//System.out.println("1...."+ir.docFreq(new Term("contents", "vatsal")));

		int N = ir.maxDoc();
		String urlPath = "";
		String firstPath = "";
		System.out.println("TF Calculation Starts....");
		for (int docNum = 0; docNum < ir.numDocs(); docNum++) {
			TermFreqVector tfv = ir.getTermFreqVector(docNum, "contents");
			if (tfv == null || tfv.size() == 0) {
				continue;
			}
			String terms[] = tfv.getTerms();
			int termCount = terms.length;
			int freqs[] = tfv.getTermFrequencies();
			//String docName[] = ir.document(docNum).getField("path").stringValue().toString().split("\\\\");
			//String doc = docName[docName.length - 1];
			urlPath = ir.document(docNum).getField("path").stringValue().toString();
			// ir.document(docNum).getField("path").stringValue().substring(ir.document(docNum).getField("path").stringValue().lastIndexOf("\\")+1);
			String tmpurl = "";		
			String data1[] = urlPath.split("articles");
			if(data1.length > 1)	{
				firstPath = data1[0];
				tmpurl = data1[1];
				tmpurl = tmpurl.replaceAll("\\\\", "/");
			}
			
			for (int t = 0; t < termCount; t++) {
				if (!tfMap.containsKey("" + terms[t])) {
					Map<String, Integer> map = new HashMap<String, Integer>();
					map.put(tmpurl, freqs[t]);
					tfMap.put("" + terms[t], (HashMap<String, Integer>) map);
				} else {
					Map<String, Integer> map = tfMap.get("" + terms[t]);
					map.put(tmpurl, freqs[t]);
					tfMap.put("" + terms[t], (HashMap<String, Integer>) map);
				}
			}
		}
		System.out.println("TF Calculation Finished....");
		
		// Calculate idf and then tf-idf of each term per each document
		System.out.println("TF-IDF Calculation Starts....");
		
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
					System.out.println(" URL : "+str);
						jsonObject = new JSONObject();
						jsonObject.put("path", firstPath+"articles"+str.replaceAll("/", "\\\\"));
						jsonObject.put("tfidf", tempMap1.get(str) * idf);
						jsonObject.put("url",str);
						jsonArray.add(jsonObject);
				}
				db.insertScore(key, jsonArray);
			}
		}
		System.out.println("TF-IDF Calculation Finished....");
		System.out.println("Writing TF-IDF to Database Done....");
	}
	
	public Map<String, Double> getTFIDFValueByTerm(String term) {
		return db.getTFIDFFromDB(term);
	}

}
