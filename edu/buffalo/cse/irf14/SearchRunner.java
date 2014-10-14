package edu.buffalo.cse.irf14;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ListIterator;

import org.hamcrest.core.IsEqual;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.TermDocumentFreq;
import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.Query.Tree;
import edu.buffalo.cse.irf14.query.QueryParser;
import edu.buffalo.cse.irf14.DocumentWithTfIdfWeight;
/**
 * Main class to run the searcher.
 * As before implement all TODO methods unless marked for bonus
 * @author nikhillo
 *
 */
public class SearchRunner {
	private String indexDir, corpusDir;
	private char mode;
	private PrintStream stream;
	
	public enum ScoringModel {TFIDF, OKAPI};
	
	/**
	 * Default (and only public) constuctor
	 * @param indexDir : The directory where the index resides
	 * @param corpusDir : Directory where the (flattened) corpus resides
	 * @param mode : Mode, one of Q or E
	 * @param stream: Stream to write output to
	 */
	public SearchRunner(String indexDir, String corpusDir, 
			char mode, PrintStream stream) {
		//TODO: IMPLEMENT THIS METHOD
		this.corpusDir = corpusDir;
		this.indexDir = indexDir;
		this.mode = mode;
		this.stream = stream;
	}
	
	/**
	 * Method to execute given query in the Q mode
	 * @param userQuery : Query to be parsed and executed
	 * @param model : Scoring Model to use for ranking results
	 */
	public void query(String userQuery, ScoringModel model) {
		//TODO: IMPLEMENT THIS METHOD
		Query query = QueryParser.parse(userQuery, "OR");
		List<DocumentWithTfIdfWeight> resultPostings = getPostingsList(query.getQueryTree());
		
	}
	
	private List<DocumentWithTfIdfWeight> getPostingsList(Tree queryTree) {
		// TODO Auto-generated method stub
		Tree node = queryTree;
		List<DocumentWithTfIdfWeight> resultPostings = new ArrayList<DocumentWithTfIdfWeight>();
		while(node!=null) {
			List<DocumentWithTfIdfWeight> leftPostings = getPostingsList(node.getLeftLeaf());
			List<DocumentWithTfIdfWeight> rightPostings = getPostingsList(node.getRightLeaf());
			String nodeValue = node.getNodeValue();
			if(QueryParser.OPERANDS.contains(nodeValue)) {
				ListIterator<DocumentWithTfIdfWeight> leftIterator = leftPostings.listIterator();
				ListIterator<DocumentWithTfIdfWeight> rightIterator = rightPostings.listIterator();
				DocumentWithTfIdfWeight leftTermDocFreq = new DocumentWithTfIdfWeight(); 
				DocumentWithTfIdfWeight rightTermDocFreq = new DocumentWithTfIdfWeight();
				boolean shouldLeftPointerMove = true;
				boolean shouldrightPointerMove = true;	
				resultPostings = new ArrayList<DocumentWithTfIdfWeight>();
				if("AND".equals(nodeValue)) {
					//perform AND operation on leftPostings and rightPostings
					while(leftIterator.hasNext() || rightIterator.hasNext())
					{
						if(shouldLeftPointerMove)
						{
							leftTermDocFreq = leftIterator.next();
							shouldLeftPointerMove = false;
						}
						if(shouldrightPointerMove)
						{
							rightTermDocFreq = rightIterator.next();
							shouldrightPointerMove = false;
						}
						Integer leftFileId =  Integer.parseInt(leftTermDocFreq.getFileId());
						Integer rightFileId =  Integer.parseInt(rightTermDocFreq.getFileId());
						if(leftFileId == rightFileId)
						{
							DocumentWithTfIdfWeight doc = new DocumentWithTfIdfWeight(leftTermDocFreq.getFileId(),leftTermDocFreq.getTfIdf()+rightTermDocFreq.getTfIdf());
							resultPostings.add(doc);
							shouldLeftPointerMove = true;
							shouldrightPointerMove = true;
							continue;
						}else if(leftFileId < rightFileId){
							shouldLeftPointerMove = true;
							continue;
						}else{
							shouldrightPointerMove = true;
						}
					}
				}else if("OR".equals(nodeValue)) {
					//perform OR operation on leftPostings and rightPostings
					while(leftIterator.hasNext() || rightIterator.hasNext())
					{
						if(shouldLeftPointerMove)
						{
							leftTermDocFreq = leftIterator.next();
							shouldLeftPointerMove = false;
						}
						if(shouldrightPointerMove)
						{
							rightTermDocFreq = rightIterator.next();
							shouldrightPointerMove = false;
						}
						Integer leftFileId =  Integer.parseInt(leftTermDocFreq.getFileId());
						Integer rightFileId =  Integer.parseInt(rightTermDocFreq.getFileId());
						if(leftFileId == rightFileId)
						{
							DocumentWithTfIdfWeight doc = new DocumentWithTfIdfWeight(leftTermDocFreq.getFileId(),leftTermDocFreq.getTfIdf()+rightTermDocFreq.getTfIdf());
							resultPostings.add(doc);
							shouldLeftPointerMove = true;
							shouldrightPointerMove = true;
							continue;
						}else if(leftFileId < rightFileId){
							DocumentWithTfIdfWeight doc = new DocumentWithTfIdfWeight(leftTermDocFreq.getFileId(),leftTermDocFreq.getTfIdf());
							resultPostings.add(doc);
							shouldLeftPointerMove = true;
							continue;
						}else{
							DocumentWithTfIdfWeight doc = new DocumentWithTfIdfWeight(rightTermDocFreq.getFileId(),rightTermDocFreq.getTfIdf());
							resultPostings.add(doc);
							shouldrightPointerMove = true;
						}
					}
				}else {
					//perform NOT operation on leftPostings and rightPostings
					while(leftIterator.hasNext() || rightIterator.hasNext())
					{
						if(shouldLeftPointerMove)
						{
							leftTermDocFreq = leftIterator.next();
							shouldLeftPointerMove = false;
						}
						if(shouldrightPointerMove)
						{
							rightTermDocFreq = rightIterator.next();
							shouldrightPointerMove = false;
						}
						Integer leftFileId =  Integer.parseInt(leftTermDocFreq.getFileId());
						Integer rightFileId =  Integer.parseInt(rightTermDocFreq.getFileId());
						if(leftFileId == rightFileId)
						{
							shouldLeftPointerMove = true;
							shouldrightPointerMove = true;
							continue;
						}else if(leftFileId < rightFileId){
							DocumentWithTfIdfWeight doc = new DocumentWithTfIdfWeight(leftTermDocFreq.getFileId(),leftTermDocFreq.getTfIdf());
							resultPostings.add(doc);
							shouldLeftPointerMove = true;
							continue;
						}else{
							shouldrightPointerMove = true;
						}
					}

				}
			} else {
				String[] queryIndexValues = nodeValue.contains(":")?nodeValue.split(":"): new String[] {nodeValue};
				List<TermDocumentFreq> postings;
				if(queryIndexValues.length==1) {
					IndexReader reader = new IndexReader(indexDir, IndexType.TERM);
					postings = reader.query(queryIndexValues[0]);
					//convert MAP<String,Integer> to List<TermDocumentFreq> and return it.
				}else {
					IndexReader reader;
					if(queryIndexValues[0].equalsIgnoreCase("AUTHOR"))
					{
						reader = new IndexReader(indexDir, IndexType.AUTHOR);
					}
					else if(queryIndexValues[0].equalsIgnoreCase("CATEGORY"))
					{
						reader = new IndexReader(indexDir, IndexType.CATEGORY);
					}
					else if(queryIndexValues[0].equalsIgnoreCase("PLACE"))
					{
						reader = new IndexReader(indexDir, IndexType.PLACE);
					}
					else
					{
						reader = new IndexReader(indexDir, IndexType.TERM);
					}
					postings = reader.query(queryIndexValues[1]);
				}
				for (TermDocumentFreq termDocumentFreq : postings) {
					double tfidf = (1.0+Math.log10(termDocumentFreq.getFrequency().doubleValue()))*Math.log10(100000/postings.size())/termDocumentFreq.getLength();
					DocumentWithTfIdfWeight doc = new DocumentWithTfIdfWeight(termDocumentFreq.getFileId(),tfidf);
					resultPostings.add(doc);
				}
			}
		}
		return resultPostings;
	}

	
	/**
	 * Method to execute queries in E mode
	 * @param queryFile : The file from which queries are to be read and executed
	 */
	public void query(File queryFile) {
		//TODO: IMPLEMENT THIS METHOD
	}
	
	/**
	 * General cleanup method
	 */
	public void close() {
		//TODO : IMPLEMENT THIS METHOD
	}
	
	/**
	 * Method to indicate if wildcard queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean wildcardSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF WILDCARD BONUS ATTEMPTED
		return false;
	}
	
	/**
	 * Method to get substituted query terms for a given term with wildcards
	 * @return A Map containing the original query term as key and list of
	 * possible expansions as values if exist, null otherwise
	 */
	public Map<String, List<String>> getQueryTerms() {
		//TODO:IMPLEMENT THIS METHOD IFF WILDCARD BONUS ATTEMPTED
		return null;
		
	}
	
	/**
	 * Method to indicate if speel correct queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean spellCorrectSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF SPELLCHECK BONUS ATTEMPTED
		return false;
	}
	
	/**
	 * Method to get ordered "full query" substitutions for a given misspelt query
	 * @return : Ordered list of full corrections (null if none present) for the given query
	 */
	public List<String> getCorrections() {
		//TODO: IMPLEMENT THIS METHOD IFF SPELLCHECK EXECUTED
		return null;
	}
}
