package src;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.*;
import org.apache.lucene.store.*;
import org.apache.lucene.util.Version;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.*;
import org.apache.tika.sax.*;
import org.tartarus.snowball.ext.PorterStemmer;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.*;

public class CallIndexFiles {

	static final String docsPath = "C:\\CrawlerFiles\\wiki-small";
	static final String indexPath = "C:\\CrawlerFiles\\indexing";
	static final String FIELD_PATH = "path";
	static final String FIELD_CONTENTS = "contents";
	static PorterStemmer stemmer = new PorterStemmer();
	static Set<String> list = StopWords.getStopWords();

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws SAXException, TikaException,
			ParseException {

		boolean create = true;

		final File docDir = new File(docsPath);
		if (!docDir.exists() || !docDir.canRead()) {
			System.out
					.println("Document directory '"
							+ docDir.getAbsolutePath()
							+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		Date start = new Date();
		try {

			System.out.println("Indexing to directory '" + indexPath + "'...");
			Directory dir = FSDirectory.open(new File(indexPath));
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35,
					analyzer);

			if (create) {
				
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
			
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}
			iwc.setRAMBufferSizeMB(1024.0);

			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDocs(writer, docDir);

			writer.optimize();
			writer.close();

			Date end = new Date();
			System.out.println(end.getTime() - start.getTime()
					+ " total milliseconds");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass()
					+ "\n with message: " + e.getMessage());
		}
	}

	@SuppressWarnings("resource")
	static void indexDocs(IndexWriter writer, File file) throws IOException,
			SAXException, TikaException {
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, new File(file, files[i]));
					}
				}
			} else {
				FileInputStream fis;
				try {
					fis = new FileInputStream(file);
				} catch (FileNotFoundException fnfe) {
					return;
				}
				try {

					
					int maxStringLength = 10 * 1024 * 1024;
					WriteOutContentHandler handler = new WriteOutContentHandler(
							maxStringLength);
					ContentHandler contenthandler = new BodyContentHandler(
							handler);
					Metadata metadata = new Metadata();
					Parser parser = new AutoDetectParser();
					parser.parse(fis, contenthandler, metadata,
							new ParseContext());
					String newString = contenthandler.toString()
							.replaceAll("/[^a-zA-Z 0-9]+/g", " ")
							.replaceAll("\\s+", " ").trim();

					Tokenizer tokenizer = new StandardTokenizer(
							Version.LUCENE_35, new StringReader(
									newString.toLowerCase()));
					final StandardFilter standardFilter = new StandardFilter(
							Version.LUCENE_35, tokenizer);
					final StopFilter stopFilter = new StopFilter(
							Version.LUCENE_35, standardFilter, list);
					final CharTermAttribute charTermAttribute = tokenizer
							.addAttribute(CharTermAttribute.class);
					stopFilter.reset();
					StringBuilder sb = new StringBuilder();

					while (stopFilter.incrementToken()) {
						final String token = charTermAttribute.toString()
								.toString();
						stemmer.setCurrent(token);
						stemmer.stem();
						String word = stemmer.getCurrent();
						if(!list.contains(word))
							sb.append(stemmer.getCurrent()).append(System.getProperty("line.separator"));
					}

					Document doc = new Document();

					Field pathField = new Field("path", file.getPath(),
							Field.Store.YES, Field.Index.NO);
					pathField.setIndexOptions(IndexOptions.DOCS_ONLY);
					doc.add(pathField);

					String docurl = file.getPath();
					String docName[] = docurl.split("\\\\");
					docurl = docName[docName.length - 1];

					Field urlField = new Field("url", docurl, Field.Store.YES,
							Field.Index.NO);
					urlField.setIndexOptions(IndexOptions.DOCS_ONLY);
					doc.add(urlField);

					NumericField modifiedField = new NumericField("modified");
					modifiedField.setLongValue(file.lastModified());
					doc.add(modifiedField);

					doc.add(new Field("contents", new BufferedReader(
							new InputStreamReader(new ByteArrayInputStream(sb
									.toString().getBytes()), "UTF-8")),
							Field.TermVector.YES));

					if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
						System.out.println("adding " + file);
						writer.addDocument(doc);
					} else {
						System.out.println("updating " + file);
						writer.updateDocument(new Term("path", file.getPath()),
								doc);
					}
				} finally {
					fis.close();
				}
			}
		}
	}
}