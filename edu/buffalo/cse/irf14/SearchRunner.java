package edu.buffalo.cse.irf14;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeMap;

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
	
	private IndexReader indexReader;
	public enum ScoringModel {TFIDF, OKAPI};
	
	private HashMap<String, Integer> queryTermFrequency = new HashMap<String, Integer>();
	private HashMap<String, Integer> queryTermDocumentFrequency = new HashMap<String, Integer>();
	private HashMap<String, HashMap<String, Double>> queryTermDocumentTDIDF = new HashMap<String, HashMap<String, Double>>();

	private TreeMap<Double, List<String>> topResults = new TreeMap<Double, List<String>>();
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
		
		indexReader = new IndexReader(indexDir);
	}
	
	/**
	 * Method to execute given query in the Q mode
	 * @param userQuery : Query to be parsed and executed
	 * @param model : Scoring Model to use for ranking results
	 */
	public void query(String userQuery, ScoringModel model) {
		//TODO: IMPLEMENT THIS METHOD
		Query query = QueryParser.parse(userQuery, "OR");
		List<String> resultPostings = getPostingsList(query.getQueryTree());
		HashMap<String,Double> results = null;
		switch(model)
		{
		case TFIDF:
			results = getTFIDFTopResults(resultPostings);
			break;
		case OKAPI:
			break;
		}
		Set<String> fileIds = results.keySet();
		for (String fileId : fileIds) {
			System.out.println("FileId: "+fileId+"TF-IDF: "+results.get(fileId));
		}
	}
	
	private HashMap<String,Double> getOKAPITopResults(List<String> results)
	{
		
		return null;
	}
	
	private HashMap<String,Double> getTFIDFTopResults(List<String> results)
	{
		Set<String> queryTerms = queryTermFrequency.keySet();

		for (String fileId : results) {
			double finalTfidf = 0.0;
			Double lengthOfQueryVector = 0.0;
			Double lengthOfDocumentVector = 0.0;
					
			for (String queryTerm : queryTerms) {
				double tfidf = (1.0+Math.log10(queryTermFrequency.get(queryTerm).doubleValue()))*Math.log10(IndexReader.documentsLengths.get("documentsCount")/queryTermDocumentFrequency.get(queryTerm));

				lengthOfQueryVector = lengthOfQueryVector+(tfidf*tfidf);

				Double docTfidf = queryTermDocumentTDIDF.get(queryTerm).get(fileId);				
				lengthOfDocumentVector = lengthOfDocumentVector+(docTfidf*docTfidf);
				finalTfidf = finalTfidf+tfidf*docTfidf;
			}
			finalTfidf = finalTfidf/Math.sqrt(lengthOfQueryVector)*Math.sqrt(lengthOfDocumentVector)*IndexReader.documentsLengths.get(fileId);
			List<String> list = topResults.get(finalTfidf);
			if(list == null){
				list = new ArrayList<String>();
			}
			list.add(fileId);
			topResults.put(finalTfidf, list);

		}
		
		Set<Double> finalTfidfs = topResults.descendingKeySet();
		HashMap<String, Double> top10Results = new HashMap<String, Double>();
		int k =10;
		for(Double key: finalTfidfs) {
			List<String> temp = topResults.get(key);
			for (String fileId : temp) {
				top10Results.put(fileId, key);
			}
			if(k >= temp.size()) {
				k = k- temp.size();
			}
			else {
				break;
			}			
		}
		return top10Results;
	}
	
	private List<String> getPostingsList(Tree queryTree) {
		// TODO Auto-generated method stub
		Tree node = queryTree;
		List<String> resultPostings = new ArrayList<String>();
		if(node!=null) {
			List<String> leftPostings = getPostingsList(node.getLeftLeaf());
			List<String> rightPostings = getPostingsList(node.getRightLeaf());
			String nodeValue = node.getNodeValue();
			if(QueryParser.OPERANDS.contains(nodeValue)) {
				ListIterator<String> leftIterator = leftPostings.listIterator();
				ListIterator<String> rightIterator = rightPostings.listIterator();
				String leftDoc = null;
				String rightDoc = null;
				boolean shouldLeftPointerMove = true;
				boolean shouldrightPointerMove = true;	
				resultPostings = new ArrayList<String>();
				if("AND".equals(nodeValue)) {
					//perform AND operation on leftPostings and rightPostings
					while(leftIterator.hasNext() || rightIterator.hasNext())
					{
						if(shouldLeftPointerMove){
							if(leftIterator.hasNext()){
								leftDoc = leftIterator.next();
								shouldLeftPointerMove = false;
							}else break;
						}
						if(shouldrightPointerMove)
						{
							if(rightIterator.hasNext()){
								rightDoc = rightIterator.next();
								shouldrightPointerMove = false;
							}else break;
						}
						Integer leftFileId =  Integer.parseInt(leftDoc);
						Integer rightFileId =  Integer.parseInt(rightDoc);
						if(leftFileId == rightFileId)
						{
							resultPostings.add(leftDoc);
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
						if(shouldLeftPointerMove){
							if(leftIterator.hasNext()){
								leftDoc = leftIterator.next();
								shouldLeftPointerMove = false;
							}else break;
						}
						if(shouldrightPointerMove)
						{
							if(rightIterator.hasNext()){
								rightDoc = rightIterator.next();
								shouldrightPointerMove = false;
							}else break;
						}
						Integer leftFileId =  Integer.parseInt(leftDoc);
						Integer rightFileId =  Integer.parseInt(rightDoc);						
						if(leftFileId == rightFileId)
						{
							resultPostings.add(leftDoc);
							shouldLeftPointerMove = true;
							shouldrightPointerMove = true;
							continue;
						}else if(leftFileId < rightFileId){
							resultPostings.add(leftDoc);
							shouldLeftPointerMove = true;
							continue;
						}else{
							resultPostings.add(rightDoc);
							shouldrightPointerMove = true;
						}
					}
				}else {
					//perform NOT operation on leftPostings and rightPostings
					while(leftIterator.hasNext() || rightIterator.hasNext())
					{
						if(shouldLeftPointerMove){
							if(leftIterator.hasNext()){
								leftDoc = leftIterator.next();
								shouldLeftPointerMove = false;
							}else break;
						}
						if(shouldrightPointerMove)
						{
							if(rightIterator.hasNext()){
								rightDoc = rightIterator.next();
								shouldrightPointerMove = false;
							}else break;
						}
						Integer leftFileId =  Integer.parseInt(leftDoc);
						Integer rightFileId =  Integer.parseInt(rightDoc);
						if(leftFileId == rightFileId)
						{
							shouldLeftPointerMove = true;
							shouldrightPointerMove = true;
							continue;
						}else if(leftFileId < rightFileId){
							resultPostings.add(leftDoc);
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
				String termText = null;
				IndexType indexType;

				if(queryIndexValues.length==1) {
					postings = indexReader.query(queryIndexValues[0],IndexType.TERM);
					//convert MAP<String,Integer> to List<TermDocumentFreq> and return it.
					termText = queryIndexValues[0];
					indexType = IndexType.TERM;
				}else {
					if(queryIndexValues[0].equalsIgnoreCase("AUTHOR"))
					{
						indexType = IndexType.AUTHOR;
					}
					else if(queryIndexValues[0].equalsIgnoreCase("CATEGORY"))
					{
						indexType = IndexType.CATEGORY;
					}
					else if(queryIndexValues[0].equalsIgnoreCase("PLACE"))
					{
						indexType = IndexType.PLACE;
					}
					else
					{
						indexType = IndexType.TERM;
					}
					termText = queryIndexValues[1];

				}
				
				Integer count = queryTermFrequency.get(termText);
				if(count!=null){
					count++;
					queryTermFrequency.put(termText, count);
				}
				else queryTermFrequency.put(termText, 1);
				
				postings = indexReader.query(termText, indexType);
				Integer documentFrequency = queryTermDocumentFrequency.get(termText);
				if(documentFrequency == null){
					queryTermDocumentFrequency.put(termText, postings.size());
				}
				for (TermDocumentFreq termDocumentFreq : postings) {
					double tfidf = (1.0+Math.log10(termDocumentFreq.getFrequency().doubleValue()))*Math.log10(IndexReader.documentsLengths.get("documentsCount")/postings.size());
					HashMap<String, Double> docTfidfMap = queryTermDocumentTDIDF.get(termText);
					if(docTfidfMap == null)
						{
						docTfidfMap = new HashMap<String, Double>();
						}
					docTfidfMap.put(termDocumentFreq.getFileId(), tfidf);
					queryTermDocumentTDIDF.put(termText, docTfidfMap);
					
					resultPostings.add(termDocumentFreq.getFileId());
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
	
	public static void main(String[] args) {
//		try {
//			String query = "(Term:casino AND (Category:zenith AND Category:adobe) OR Place:California) laser takeovers";
//			String query = "(Term:casino AND (Category:zenith AND Category:adobe) OR \"hello world\") laser takeovers";
//			String query = "\"hello computer world\" \"tree parser\" ((first NOT second) AND third)";
//			String query = "\"hello computer world\" Category:\"tree parser\" ((first NOT second) AND third)";
//			String query = "(Love NOT War) AND Category:(movies NOT crime)";
//			String query = "Category:War AND Author:Dutt AND Place:(Baghdad AND Mysore) detainees rebels";
//			String query = "author:\"Patti Domm\" AND american express";
//			String query = "author:(brian OR richard) AND place:(paris OR washington)";
//			String query = "author:minkwoski OR disney";
//			String query = "place:tokyo NOT bank";
//			String query = "french economy employment government policies";
			String query = "author:torday AND (debt OR currency)";
//			String query = "author:miller OR miller";
//			String query = "category:coffee beans";
//			String query = "place:washington AND federal treasury";
//			String query = "place:paris AND government";
//			String query = "blah blah blah";
//			String query = "mitsubishi";
			String desktop = System.getProperty ("user.home") + "/Documents/MSCS/IR/";

		SearchRunner runner = new SearchRunner(desktop, "Macintosh\b HD/Users/Mani/Documents/MSCS/IR/training", 'Q', null);
			runner.query(query, ScoringModel.TFIDF);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
