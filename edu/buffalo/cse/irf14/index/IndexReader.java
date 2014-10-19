/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
@SuppressWarnings("unchecked")
/**
 * @author nikhillo
 * Class that emulates reading data back from a written index
 */
public class IndexReader {
	
	 private static HashMap<String, Integer> termDictionary = new HashMap<String, Integer>();
	 private static HashMap<Integer, List<TermDocumentFreq>> termIndex = new HashMap<Integer, List<TermDocumentFreq>>();
	 
	 private static HashMap<String, Integer> authorDictionary = new HashMap<String, Integer>();
	 private static HashMap<Integer, List<TermDocumentFreq>> authorIndex = new HashMap<Integer, List<TermDocumentFreq>>();
	 
	 private static HashMap<String, Integer> placeDictionary = new HashMap<String, Integer>();
	 private static HashMap<Integer, List<TermDocumentFreq>> placeIndex = new HashMap<Integer, List<TermDocumentFreq>>();
	 
	 private static HashMap<String, Integer> categoryDictionary = new HashMap<String, Integer>();
	 private static HashMap<Integer, List<TermDocumentFreq>> categoryIndex = new HashMap<Integer, List<TermDocumentFreq>>();
	
	private static HashMap<String, Integer> dictionary = new HashMap<String, Integer>();
	private static HashMap<Integer, List<TermDocumentFreq>> index = new HashMap<Integer, List<TermDocumentFreq>>();
	
	public static HashMap<String, Integer> documentsLengths = new HashMap<String, Integer>();

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
		ObjectInputStream oistream = null;
		try {
			switch (type) {
			case TERM:
				oistream = new ObjectInputStream(new FileInputStream(indexDir+File.separator+"termIndex"));
				index = (HashMap<Integer, List<TermDocumentFreq>>) oistream.readObject();
				oistream = new ObjectInputStream(new FileInputStream(indexDir+File.separator+"termDictionary"));
				dictionary = (HashMap<String, Integer>) oistream.readObject();
				names = FieldNames.CONTENT;
				break;
			case AUTHOR:
				oistream = new ObjectInputStream(new FileInputStream(indexDir+File.separator+"authorIndex"));
				index = (HashMap<Integer, List<TermDocumentFreq>>) oistream.readObject();
				oistream = new ObjectInputStream(new FileInputStream(indexDir+File.separator+"authorDictionary"));
				dictionary = (HashMap<String, Integer>) oistream.readObject();
				names = FieldNames.AUTHOR;
				break;
			case PLACE:
				oistream = new ObjectInputStream(new FileInputStream(indexDir+File.separator+"placeIndex"));
				index = (HashMap<Integer, List<TermDocumentFreq>>) oistream.readObject();
				oistream = new ObjectInputStream(new FileInputStream(indexDir+File.separator+"placeDictionary"));
				dictionary = (HashMap<String, Integer>) oistream.readObject();
				names = FieldNames.PLACE;
				break;
			case CATEGORY:
				oistream = new ObjectInputStream(new FileInputStream(indexDir+File.separator+"categoryIndex"));
				index = (HashMap<Integer, List<TermDocumentFreq>>) oistream.readObject();
				oistream = new ObjectInputStream(new FileInputStream(indexDir+File.separator+"categoryDictionary"));
				dictionary = (HashMap<String, Integer>) oistream.readObject();
				names = FieldNames.CATEGORY;
				break;
			default:
				break;
			}
		}
		catch(IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public IndexReader(String indexDir)
	{
		this.indexDir = indexDir;
		ObjectInputStream oistream = null;
			try {
					oistream = new ObjectInputStream(new FileInputStream(indexDir+File.separator+"termIndex"));
					termIndex = (HashMap<Integer, List<TermDocumentFreq>>) oistream.readObject();
					oistream = new ObjectInputStream(new FileInputStream(indexDir+File.separator+"termDictionary"));
					termDictionary = (HashMap<String, Integer>) oistream.readObject();

					oistream = new ObjectInputStream(new FileInputStream(indexDir+File.separator+"authorIndex"));
					authorIndex = (HashMap<Integer, List<TermDocumentFreq>>) oistream.readObject();
					oistream = new ObjectInputStream(new FileInputStream(indexDir+File.separator+"authorDictionary"));
					authorDictionary = (HashMap<String, Integer>) oistream.readObject();

					oistream = new ObjectInputStream(new FileInputStream(indexDir+File.separator+"placeIndex"));
					placeIndex = (HashMap<Integer, List<TermDocumentFreq>>) oistream.readObject();
					oistream = new ObjectInputStream(new FileInputStream(indexDir+File.separator+"placeDictionary"));
					placeDictionary = (HashMap<String, Integer>) oistream.readObject();

					oistream = new ObjectInputStream(new FileInputStream(indexDir+File.separator+"categoryIndex"));
					categoryIndex = (HashMap<Integer, List<TermDocumentFreq>>) oistream.readObject();
					oistream = new ObjectInputStream(new FileInputStream(indexDir+File.separator+"categoryDictionary"));
					categoryDictionary = (HashMap<String, Integer>) oistream.readObject();
					
					oistream = new ObjectInputStream(new FileInputStream(indexDir+File.separator+"documentsLengths"));
					documentsLengths = (HashMap<String, Integer>) oistream.readObject();
				}
			catch(IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		Set<String> fileId = new HashSet<String>();
		for (int i=0; i<index.size(); i++) {
			List<TermDocumentFreq>l=index.get(i+1);
			for(TermDocumentFreq t:l){
				fileId.add(t.getFileId());
			}
		}
		return fileId.size();
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
		if(k<=0)
		{
			return null;
		}
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

	public List<TermDocumentFreq> query(String term, IndexType type) {
		//TODO : BONUS ONLY
		List<TermDocumentFreq> myList = new ArrayList<TermDocumentFreq>();
		List<Token> myTokenList = new ArrayList<Token>();
		myTokenList.add(new Token(term));
		TokenStream myStream = new TokenStream((ArrayList<Token>) myTokenList);
		AnalyzerFactory myAnalyzerFactory = AnalyzerFactory.getInstance();
		switch(type)
		{
		case TERM:
			names = FieldNames.CONTENT;
			break;
		case PLACE:
			names = FieldNames.PLACE;
			break;
		case AUTHOR:
			names = FieldNames.AUTHOR;
			break;
		case CATEGORY:
			names = FieldNames.CATEGORY;
			break;
		}
		TokenFilter myFilter = (TokenFilter) myAnalyzerFactory.getAnalyzerForField(names, myStream);
		while(myFilter!=null) {
			myFilter.perform();
			myFilter = myFilter.getNextFilter();
		}
		List<TermDocumentFreq> postingsList;
		myStream.reset();
		while(myStream.hasNext()) {
			Token myToken = myStream.next();
			String queryTerm = myToken.getTermText().toLowerCase();
			switch(type)
			{
			case TERM:
				if(termDictionary.get(queryTerm)==null) {
					return null;
				}
				postingsList = termIndex.get(termDictionary.get(queryTerm));
				myList.addAll(postingsList);
				break;
			case PLACE:
				if(placeDictionary.get(queryTerm)==null) {
					return null;
				}
				postingsList = placeIndex.get(placeDictionary.get(queryTerm));
				myList.addAll(postingsList);
				break;

			case CATEGORY:
				if(categoryDictionary.get(queryTerm)==null) {
					return null;
				}
				postingsList = categoryIndex.get(categoryDictionary.get(queryTerm));
				myList.addAll(postingsList);
				break;

			case AUTHOR:
				if(authorDictionary.get(queryTerm)==null) {
					return null;
				}
				postingsList = authorIndex.get(authorDictionary.get(queryTerm));
				myList.addAll(postingsList);
				break;
			}
			
		}
		if(myList.size()==0)
			return null;
		else return myList;
	}

//	private List<TermDocumentFreq> intersectPostingsForTerms(List<TermDocumentFreq> myList,	List<TermDocumentFreq> postingsList) {
//		int i=0,j=0;
//		List<TermDocumentFreq> result = new ArrayList<TermDocumentFreq>();
//		for(int value;i<myList.size()-1 && j<postingsList.size()-1;) {
//			value = myList.get(i).getFileId().compareTo(postingsList.get(j).getFileId());
//			if(value==0) {
//				result.add(new TermDocumentFreq(
//						myList.get(i).getFileId(),
//						myList.get(i).getFrequency()+ postingsList.get(i).getFrequency()));
//				i++; j++;
//			}else if(value < 0) {
//				i++;
//			}
//			else {
//				j++;
//			}
//		}
//		return result;
//	}
}
