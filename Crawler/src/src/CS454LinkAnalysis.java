package src;

import java.io.*;
import java.util.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.parser.ParseException;

public class CS454LinkAnalysis {

	List<DocumentModel> allDocs = new ArrayList<DocumentModel>();
	final String indexPath = "F:\\MyIndexing\\indexing";
	Map<String, HashSet<String>> outLinks = new HashMap<String, HashSet<String>>();
	Map<String, HashSet<String>> inLinks = new HashMap<String, HashSet<String>>();
	int documentNumber;
	Database transactionDB = new Database();

	public static void main(String[] args) throws IOException, ParseException {
		new CS454LinkAnalysis();
	}

	public CS454LinkAnalysis() throws IOException {
		Date start = new Date();
		getOutGoingLinks();
		getInComingLinks();
		Date end = new Date();
		
		System.out.println(end.getTime() - start.getTime() + " total milliseconds");
	}

	@SuppressWarnings("deprecation")
	private void getOutGoingLinks() throws IOException {

		Directory dir = FSDirectory.open(new File(indexPath));
		IndexReader ir = IndexReader.open(dir);
		FileReader storageFile;
		Set<String> set;
		DocumentModel dm;
		int totalDoc = ir.maxDoc();
		setDocumentNumber(ir.maxDoc());

		for (int docNum = 0; docNum < ir.numDocs(); docNum++) {

			String filePath = ir.document(docNum).getField("path").stringValue().toString();
			String documentUrl = ir.document(docNum).getField("url").stringValue().toString();

			storageFile = new FileReader(filePath);
			List<String> links = extractLinks(storageFile);

			set = new HashSet<String>(links);
			dm = new DocumentModel();
			dm.setId("" + docNum + 1);
			dm.setPath(filePath);
			dm.setUrl(documentUrl);
			dm.setDefaultRank((double) 1 / totalDoc);
			dm.setOutGoingLink(set);

			outLinks.put(documentUrl, (HashSet<String>) set);
			allDocs.add(dm);

			//System.out.println(docNum + " " + documentUrl + "->" + set.size());
		}
	}

	public void getInComingLinks() {
		Set<String> list;
		for (DocumentModel dm1 : allDocs) {
			list = new HashSet<String>();
			for (DocumentModel dm2 : allDocs) {
				if (dm1.getUrl() != dm2.getUrl())
					if (dm2.getOutGoingLink().contains(dm1.getUrl()))
						list.add(dm2.getUrl());
			}
			inLinks.put(dm1.getUrl(), (HashSet<String>) list);
			dm1.setInComingLink(list);
			//System.out.println(list.size() + ":" + dm1.getUrl());
		}
	}

	public List<String> extractLinks(Reader reader) throws IOException {
		final ArrayList<String> list = new ArrayList<String>();
		ParserDelegator parserDelegator = new ParserDelegator();
		ParserCallback parserCallback = new ParserCallback() {
			public void handleText(final char[] data, final int pos) {}

			public void handleStartTag(Tag tag, MutableAttributeSet attribute, int pos) {
				if (tag == Tag.A) {
					String address = (String) attribute
							.getAttribute(Attribute.HREF);
					list.add(address);
				}
			}
			public void handleEndTag(Tag t, final int pos) {}
			public void handleSimpleTag(Tag t, MutableAttributeSet a, final int pos) {}
			public void handleComment(final char[] data, final int pos) {}
			public void handleError(final java.lang.String errMsg, final int pos) {}
		};
		parserDelegator.parse(reader, parserCallback, true);
		return list;
	}

	public List<DocumentModel> getAllLinks() {
		return allDocs;
	}

	public Map<String, HashSet<String>> getOutLinks() {
		return outLinks;
	}

	public Map<String, HashSet<String>> getInLinks() {
		return inLinks;
	}

	public List<DocumentModel> getAllDocs() {
		return allDocs;
	}

	public int getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(int documentNumber) {
		this.documentNumber = documentNumber;
	}
}
