/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenFilter;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo
 * Class that emulates reading data back from a written index
 */
public class IndexReader {
	
	private static HashMap<String, Integer> dictionary = new HashMap<String, Integer>();
	private static HashMap<Integer, List<TermDocumentFreq>> index = new HashMap<Integer, List<TermDocumentFreq>>();
	private IndexType indexType;
	private String indexDir;
	private FieldNames names;
	/**
	 * Default constructor
	 * @param indexDir : The root directory from which the index is to be read.
	 * This will be exactly the same directory as passed on IndexWriter. In case 
	 * you make subdirectories etc., you will have to handle it accordingly.
	 * @param type The {@link IndexType} to read from
	 */
	public IndexReader(String indexDir, IndexType type) {
		//TODO
		this.indexType = type;
		this.indexDir = indexDir;
		switch (type) {
		case TERM:
			dictionary = IndexWriter.termDictionary;
			index      = IndexWriter.termIndex;
			names = FieldNames.CONTENT;
			break;
		case AUTHOR:
			dictionary = IndexWriter.authorDictionary;
			index      = IndexWriter.authorIndex;
			names = FieldNames.AUTHOR;
			break;
		case PLACE:
			dictionary = IndexWriter.placeDictionary;
			index      = IndexWriter.placeIndex;
			names = FieldNames.PLACE;
			break;
		case CATEGORY:
			dictionary = IndexWriter.categoryDictionary;
			index      = IndexWriter.categoryIndex;
			names = FieldNames.CATEGORY;
			break;
		default:
			break;
		}
	}
	
	/**
	 * Get total number of terms from the "key" dictionary associated with this 
	 * index. A postings list is always created against the "key" dictionary
	 * @return The total number of terms
	 */
	public int getTotalKeyTerms() {
		//TODO : YOU MUST IMPLEMENT THIS
		return dictionary.keySet().size();
	}
	
	/**
	 * Get total number of terms from the "value" dictionary associated with this 
	 * index. A postings list is always created with the "value" dictionary
	 * @return The total number of terms
	 */
	public int getTotalValueTerms() {
		//TODO: YOU MUST IMPLEMENT THIS
		return dictionary.values().size();
	}
	
	/**
	 * Method to get the postings for a given term. You can assume that
	 * the raw string that is used to query would be passed through the same
	 * Analyzer as the original field would have been.
	 * @param term : The "analyzed" term to get postings for
	 * @return A Map containing the corresponding fileid as the key and the 
	 * number of occurrences as values if the given term was found, null otherwise.
	 */
	public Map<String, Integer> getPostings(String term) {
		//TODO:YOU MUST IMPLEMENT THIS
		Map<String, Integer> map = new HashMap<String, Integer>();
		Integer id = dictionary.get(term);
		if(id==null)
			return null;
		List<TermDocumentFreq> myList = index.get(id);
		for(TermDocumentFreq termDocItem: myList) {
			map.put(termDocItem.getFileId(), termDocItem.getFrequency());
		}
		return map;
	}
	
	/**
	 * Method to get the top k terms from the index in terms of the total number
	 * of occurrences.
	 * @param k : The number of terms to fetch
	 * @return : An ordered list of results. Must be <=k fr valid k values
	 * null for invalid k values
	 */
	public List<String> getTopK(int k) {
		//TODO YOU MUST IMPLEMENT THIS
		TreeMap<Integer, List<String>> treeMap = new TreeMap<Integer, List<String>>();
		Set<String> keySet = dictionary.keySet();
		for(String key: keySet) {
			Integer frequency = new Integer(0);
			List<TermDocumentFreq> myList = index.get(dictionary.get(key));
			for(TermDocumentFreq term: myList) {
				frequency += term.getFrequency();
			}
			if(treeMap.containsKey(frequency)) {
				List<String> temp = treeMap.get(frequency);
				temp.add(key);
				treeMap.put(frequency, temp);
			}
			else {
				List<String> temp = new ArrayList<String>();
				temp.add(key);
				treeMap.put(frequency, temp);
			}
		}
		Set<Integer> freqKeys = treeMap.descendingKeySet();
		List<String> myList = new ArrayList<String>();
		for(Integer key: freqKeys) {
			List<String> temp = treeMap.get(key);
			if(k >= temp.size()) {
				myList.addAll(temp);
				k = k- temp.size();
			}
			else {
				for(int i=0; i<k; i++) {
					myList.add(temp.get(i));
				}
			}			
		}
		return myList;
	}
	
	/**
	 * Method to implement a simple boolean AND query on the given index
	 * @param terms The ordered set of terms to AND, similar to getPostings()
	 * the terms would be passed through the necessary Analyzer.
	 * @return A Map (if all terms are found) containing FileId as the key 
	 * and number of occurrences as the value, the number of occurrences 
	 * would be the sum of occurrences for each participating term. return null
	 * if the given term list returns no results
	 * BONUS ONLY
	 */
	public Map<String, Integer> query(String...terms) {
		//TODO : BONUS ONLY
		Map<String, Integer> map = new HashMap<String, Integer>();
		List<TermDocumentFreq> myList = new ArrayList<TermDocumentFreq>();
		List<Token> myTokenList = new ArrayList<Token>();
		for(String queryTerm: terms)
			myTokenList.add(new Token(queryTerm));
		TokenStream myStream = new TokenStream((ArrayList<Token>) myTokenList);
		AnalyzerFactory myAnalyzerFactory = AnalyzerFactory.getInstance();
		TokenFilter myFilter = (TokenFilter) myAnalyzerFactory.getAnalyzerForField(names, myStream);
		while(myFilter!=null) {
			myFilter.perform();
			myFilter = myFilter.getNextFilter();
		}
		while(myStream.hasNext()) {
			Token myToken = myStream.next();
			String queryTerm = myToken.getTermText();
			if(dictionary.get(queryTerm)==null) {
				return null;
			}
			List<TermDocumentFreq> postingsList = index.get(dictionary.get(queryTerm));
			if(myList.size()!=0) {
				myList = intersectPostingsForTerms(myList, postingsList);
			}
			else {
				myList.addAll(postingsList);
			}
		}
		if(myList.size()==0)
			return null;
		for(TermDocumentFreq queryTerm: myList) {
			map.put(queryTerm.getFileId(), queryTerm.getFrequency());
		}
		return map;
	}

	private List<TermDocumentFreq> intersectPostingsForTerms(List<TermDocumentFreq> myList,	List<TermDocumentFreq> postingsList) {
		int i=0,j=0;
		List<TermDocumentFreq> result = new ArrayList<TermDocumentFreq>();
		for(int value;i<myList.size()-1 && j<postingsList.size()-1;) {
			value = myList.get(i).getFileId().compareTo(postingsList.get(j).getFileId());
			if(value==0) {
				result.add(new TermDocumentFreq(
						myList.get(i).getFileId(),
						myList.get(i).getFrequency()+ postingsList.get(i).getFrequency()));
				i++; j++;
			}else if(value < 0) {
				i++;
			}
			else {
				j++;
			}
		}
		return result;
	}
}
