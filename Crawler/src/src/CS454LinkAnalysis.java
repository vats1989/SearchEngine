package src;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CS454LinkAnalysis {

	List<DocumentModel> allDocs = new ArrayList<DocumentModel>();
	final String indexPath = "F:\\MyIndexing\\indexing";
	Map<String, HashSet<String>> outLinks = new HashMap<String, HashSet<String>>();
	Map<String, HashSet<String>> inLinks  = new HashMap<String, HashSet<String>>();
	int documentNumber;

	
	public static void main(String[] args) throws IOException, ParseException {
		new CS454LinkAnalysis();
	}
	
	public CS454LinkAnalysis() throws IOException {
		getOutGoingLinks();
		getInComingLinks();
	}

	@SuppressWarnings("deprecation")
	private void getOutGoingLinks() throws IOException {
		
		Directory dir = FSDirectory.open(new File(indexPath));
		IndexReader ir = IndexReader.open(dir);
		File storageFile;
		Set<String> set;
		DocumentModel dm;
		int totalDoc = ir.maxDoc();
		setDocumentNumber(ir.maxDoc());
		
		for (int docNum = 0; docNum < ir.numDocs(); docNum++) {
			String doc = ir.document(docNum).getField("path").stringValue().toString();
			//String docName[] = ir.document(docNum).getField("path").stringValue().toString().split("\\\\");
			//String url = ir.document(docNum).getField("url").stringValue().toString();
			
			String data[] = doc.split("articles");
			if(data.length > 1)	{
				String url = data[1];
				url = url.replaceAll("\\\\", "/");
				System.out.println(docNum+" URL : "+doc);
				
				storageFile = new File(doc);
				Document document = Jsoup.parse(storageFile, "ISO-8859-1");
				
				set = new HashSet<String>();			
				dm = new DocumentModel();
				dm.setId(""+docNum+1);
				dm.setPath(doc);
				dm.setUrl(url);
				dm.setDefaultRank((double)1/totalDoc);
				
				Elements links = document.select("a");
				for(Element link : links)	{
					String t = link.attr("abs:href");
					
					if(!t.isEmpty())	{
						System.out.println("5....."+t);
						String temp[] = t.split("articles");
						if(temp.length > 1)	{
							set.add(temp[1]);
							System.out.println("1..."+temp[1]);
						}
					}
				}	
				dm.setOutGoingLink(set);
				outLinks.put(url, (HashSet<String>) set);
				allDocs.add(dm);
				System.out.println("####### "+dm.getOutGoingLink().size());
			}
		}
	}
	/*
	 * print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
	public String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
	*/

	public void getInComingLinks()	{
		Set<String> list;
		for(DocumentModel dm1 : allDocs)	{
			list = new HashSet<String>();
			for(DocumentModel dm2 : allDocs )	{
				if(dm1.getUrl() != dm2.getUrl())
					if(dm2.getOutGoingLink().contains(dm1.getUrl()))
							list.add(dm2.getUrl());
			}
			inLinks.put(dm1.getUrl(), (HashSet<String>) list);
			dm1.setInComingLink(list);
			System.out.println(dm1.getUrl()+":"+list.size());
		}
	}
	
	public List<DocumentModel> getAllLinks() {
		return allDocs;
	}

	public void setAllLinks(List<DocumentModel> allLinks) {
		this.allDocs = allLinks;
	}

	public Map<String, HashSet<String>> getOutLinks() {
		return outLinks;
	}

	public void setOutLinks(Map<String, HashSet<String>> outLinks) {
		this.outLinks = outLinks;
	}

	public Map<String, HashSet<String>> getInLinks() {
		return inLinks;
	}

	public void setInLinks(Map<String, HashSet<String>> inLinks) {
		this.inLinks = inLinks;
	}

	public String getIndexPath() {
		return indexPath;
	}

	public List<DocumentModel> getAllDocs() {
		return allDocs;
	}

	public void setAllDocs(List<DocumentModel> allDocs) {
		this.allDocs = allDocs;
	}

	public int getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(int documentNumber) {
		this.documentNumber = documentNumber;
	}	
}