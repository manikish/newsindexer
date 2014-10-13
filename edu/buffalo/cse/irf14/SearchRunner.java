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
		getPostingsList(query.getQueryTree());
	}
	
	private List<TermDocumentFreq> getPostingsList(Tree queryTree) {
		// TODO Auto-generated method stub
		Tree node = queryTree;
		List<TermDocumentFreq> resultPostings = new ArrayList<TermDocumentFreq>();
		while(node!=null) {
			List<TermDocumentFreq> leftPostings = getPostingsList(node.getLeftLeaf());
			List<TermDocumentFreq> rightPostings = getPostingsList(node.getRightLeaf());
			String nodeValue = node.getNodeValue();
			if(QueryParser.OPERANDS.contains(nodeValue)) {
				ListIterator<TermDocumentFreq> leftIterator = leftPostings.listIterator();
				ListIterator<TermDocumentFreq> rightIterator = rightPostings.listIterator();
				TermDocumentFreq leftTermDocFreq; 
				TermDocumentFreq rightTermDocFreq;
				boolean shouldLeftPointerMove = true;
				boolean shouldrightPointerMove = true;	
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
							resultPostings.add(leftTermDocFreq);
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
							resultPostings.add(leftTermDocFreq);
							shouldLeftPointerMove = true;
							shouldrightPointerMove = true;
							continue;
						}else if(leftFileId < rightFileId){
							resultPostings.add(leftTermDocFreq);
							shouldLeftPointerMove = true;
							continue;
						}else{
							resultPostings.add(rightTermDocFreq);
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
							resultPostings.add(leftTermDocFreq);
							shouldLeftPointerMove = true;
							continue;
						}else{
							shouldrightPointerMove = true;
						}
					}

				}
			} else {
				String[] queryIndexValues = nodeValue.contains(":")?nodeValue.split(":"): new String[] {nodeValue};
				
				if(queryIndexValues.length==1) {
					IndexReader reader = new IndexReader(indexDir, IndexType.TERM);
					Map<String, Integer> postings = reader.query(queryIndexValues[0]);
					//convert MAP<String,Integer> to List<TermDocumentFreq> and return it.
					for(String s: postings.keySet()) {
						TermDocumentFreq term = new TermDocumentFreq(s, postings.get(s));
						resultPostings.add(term);
					}
				}else {
					IndexReader
				}
			}
			return resultPostings;
		}
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
