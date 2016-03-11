package src;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class SearchTerm {
	public static void main(String[] args) throws IOException, ParseException {
		searchTerm("zone");
	}

	@SuppressWarnings("deprecation")
	public static void searchTerm(String searchString) throws IOException,
			ParseException, org.apache.lucene.queryParser.ParseException {
		System.out.println("Searching for '" + searchString + "'");
		Directory directory = FSDirectory.open(new File(CallIndexFiles.indexPath));
		IndexReader indexReader = IndexReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
		QueryParser queryParser = new QueryParser(Version.LUCENE_35,"contents", analyzer);
		Query query = queryParser.parse(searchString);
		query.setBoost(4);
		TopDocs hits = indexSearcher.search(query, 500);
		ScoreDoc[] scoreDocs = hits.scoreDocs;
		System.out.println(scoreDocs.length);
		System.out.println("score, docId");
		for (int doc = 0; doc < scoreDocs.length; doc++) {
			ScoreDoc sd = scoreDocs[doc];
			float score = sd.score;
			int docId = sd.doc;
			Document dc = indexReader.document(docId);
			System.out.println(dc.getField("path"));
			System.out.println(score + "," + docId);
		}
		Map<Integer, String> map = new HashMap<Integer, String>();
		for (int i = 0; i < indexReader.maxDoc(); i++) {
			Document doc = indexReader.document(i);
			String newStr[] = doc.toString().split("\\\\");

			System.out.println(i
					+ ":"
					+ newStr[newStr.length - 1].substring(0,
							newStr[newStr.length - 1].length() - 2)
							.toLowerCase());
			map.put(i,
					newStr[newStr.length - 1].substring(0,
							newStr[newStr.length - 1].length() - 2)
							.toLowerCase());
		}		
	}
}