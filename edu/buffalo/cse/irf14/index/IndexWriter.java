/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.TokenFilter;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo
 * Class responsible for writing indexes to disk
 */
public class IndexWriter {
	
	public static String indexDir;
	public static final AnalyzerFactory myAnalyzerFactory = AnalyzerFactory.getInstance();
	public static final IndexerFactory myIndexerFactory = IndexerFactory.getInstance();
	/**
	 * Default constructor
	 * @param indexDir : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		//TODO : YOU MUST IMPLEMENT THIS
		IndexWriter.indexDir = indexDir;
	}
	
	/**
	 * Method to add the given Document to the index
	 * This method should take care of reading the filed values, passing
	 * them through corresponding analyzers and then indexing the results
	 * for each indexable field within the document. 
	 * @param d : The Document to be added
	 * @throws IndexerException : In case any error occurs
	 */
	public void addDocument(Document d) throws IndexerException {
		//TODO : YOU MUST IMPLEMENT THIS
		
		Tokenizer myTokenizer = new Tokenizer();
		TokenStream myStream = null;
		try {
			myStream = myTokenizer.consume(d.getField(FieldNames.CONTENT)[0]);// modify this hardcoded [0]
			TokenFilter myFilter = (TokenFilter)myAnalyzerFactory.getAnalyzerForField(FieldNames.CONTENT, myStream);
			while(myFilter!=null) {
				myFilter.perform();
				myFilter = myFilter.getNextFilter();
			}
			Writer indexer = myIndexerFactory.getClassForIndex(IndexType.TERM);
			indexer.write(myStream, d.getField(FieldNames.FILEID)[0]);//hardcoded 0
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			throw new IndexerException();
		}
	}
	
	/**
	 * Method that indicates that all open resources must be closed
	 * and cleaned and that the entire indexing operation has been completed.
	 * @throws IndexerException : In case any error occurs
	 */
	public void close() throws IndexerException {
		//TODO
	}

	public String getIndexDir() {
		return indexDir;
	}
	
}
