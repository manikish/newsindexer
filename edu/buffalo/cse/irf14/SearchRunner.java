package edu.buffalo.cse.irf14;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import edu.buffalo.cse.irf14.document.FieldNames;
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
	
	private IndexReader indexReader;
	public enum ScoringModel {TFIDF, OKAPI};
	
	private HashMap<String, Integer> queryTermFrequency = new HashMap<String, Integer>();
	private HashMap<String, Integer> queryTermDocumentFrequency = new HashMap<String, Integer>();
	private HashMap<String, HashMap<String, Double>> queryTermDocumentTDIDF = new HashMap<String, HashMap<String, Double>>();

	
	private double documentsCount;
	private static double timeTaken;
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
		documentsCount = IndexReader.documentsLengths.get("documentsCount");
	}
	
	/**
	 * Method to execute given query in the Q mode
	 * @param userQuery : Query to be parsed and executed
	 * @param model : Scoring Model to use for ranking results
	 * @throws FileNotFoundException 
	 */
	public void query(String userQuery, ScoringModel model) throws FileNotFoundException {
		//TODO: IMPLEMENT THIS METHOD
        timeTaken = System.currentTimeMillis();
		Query query = QueryParser.parse(userQuery, "OR");
		List<String> resultPostings = getPostingsList(query.getQueryTree());
		if(resultPostings != null)
		{
			List<DocumentWithTfIdfWeight> results = null;
			switch(model)
			{
			case TFIDF:
				results = getTFIDFTopResults(resultPostings);
				break;
			case OKAPI:
				results = getOKAPITopResults(resultPostings);
				break;
			}
			
			stream.println(userQuery);
			timeTaken = System.currentTimeMillis()-timeTaken;
			stream.println("Time Taken: "+timeTaken);
			stream.println();

			int rank = 1;
			for (DocumentWithTfIdfWeight documentWithTfIdfWeight : results) {
				stream.println(rank);
				rank++;
				Scanner myScanner = new Scanner(new File(corpusDir+File.separator+documentWithTfIdfWeight.getFileId()));
				StringBuffer title = new StringBuffer();
				StringBuffer snippet = new StringBuffer();
				Set<String> queryTerms = queryTermFrequency.keySet();
				String myLine = new String();
				boolean isTitlePopulated = false;
				while(myScanner.hasNextLine()) {
					if((myLine = myScanner.nextLine()).trim().length()!=0) {
						 if (!isTitlePopulated) {
								title = title.append(myLine);
						 }
						 for (String queryTerm : queryTerms) {
							 int i = 0;
							 if(myLine.contains(queryTerm))
							 {
								 snippet.append(myLine+"...");
								 if(i>2){
									 break;
								 }
								 i++;
							 }
						 }
					}
					else if (title.length() != 0)
					{
						isTitlePopulated = true;
					}
				}
				myScanner.close();
				stream.println("Title:"+title);
				stream.println("Snippet: "+snippet);
				stream.print("Relevancy: ");
				stream.printf("%.5f",documentWithTfIdfWeight.getTfIdf());
				stream.println();
			}
		}
		else{
			stream.println("No Results");
		}
	}
	
	private List<DocumentWithTfIdfWeight> getOKAPITopResults(List<String> results)
	{
	    TreeMap<Double, List<String>> topResults = new TreeMap<Double, List<String>>();
		Set<String> queryTerms = queryTermFrequency.keySet();
		double averageDocLength = IndexReader.documentsLengths.get("averageDocumentLength");
		for (String fileId : results) {
			double okapiScore = 0.0;
			double docLengthByAverageDocLength = IndexReader.documentsLengths.get(fileId)/averageDocLength;
			for (String queryTerm : queryTerms) {
				int tf = queryTermFrequency.get(queryTerm);
				Integer df = queryTermDocumentFrequency.get(queryTerm);
				double logFactor = 1.0;
				if(df!=null){ 
					logFactor = Math.log10(documentsCount/df);
				}
				okapiScore = okapiScore + (logFactor*2.5*tf)/((1.5*(0.25+(0.75*docLengthByAverageDocLength)))+tf);
			}
			
			List<String> list = topResults.get(okapiScore);
			if(list == null){
				list = new ArrayList<String>();
			}
			list.add(fileId);
			topResults.put(okapiScore, list);
		}
		
		Set<Double> finalTfidfs = topResults.descendingKeySet();
		double normalizingFactor = 0.0;
		for (Double double1 : finalTfidfs) {
			normalizingFactor = normalizingFactor+(double1*double1);
		}
		normalizingFactor = Math.sqrt(normalizingFactor);
		List<DocumentWithTfIdfWeight> top10Results = new ArrayList<DocumentWithTfIdfWeight>(); 
		int k =10;
		for(Double key: finalTfidfs) {
			List<String> temp = topResults.get(key);
			if(k >= temp.size()) {
				for (String fileId : temp) {
					DocumentWithTfIdfWeight doc = new DocumentWithTfIdfWeight(fileId, key/normalizingFactor);
					top10Results.add(doc);
				}
				k = k- temp.size();
			}
			else {
				for(int i=0; i<k; i++) {
					DocumentWithTfIdfWeight doc = new DocumentWithTfIdfWeight(temp.get(i), key/normalizingFactor);
					top10Results.add(doc);
				}
				break;
			}			
		}

		return top10Results;
	}
	
	private List<DocumentWithTfIdfWeight> getTFIDFTopResults(List<String> results)
	{
		Set<String> queryTerms = queryTermFrequency.keySet();
	    TreeMap<Double, List<String>> topResults = new TreeMap<Double, List<String>>();

		for (String fileId : results) {
			double finalTfidf = 0.0;
			Double lengthOfQueryVector = 0.0;
			Double lengthOfDocumentVector = 0.0;
					
			for (String queryTerm : queryTerms) {
				Integer df = queryTermDocumentFrequency.get(queryTerm);
				if(df == null){
					continue;
				}
				double tfidf = (1.0+Math.log10(queryTermFrequency.get(queryTerm).doubleValue()))*Math.log10(documentsCount/df);

				lengthOfQueryVector = lengthOfQueryVector+(tfidf*tfidf);

				Double docTfidf = queryTermDocumentTDIDF.get(queryTerm).get(fileId);	
				if(docTfidf==null)
				{
					docTfidf = 0.0;
				}
				lengthOfDocumentVector = lengthOfDocumentVector+(docTfidf*docTfidf);
				finalTfidf = finalTfidf+tfidf*docTfidf;
			}
//			finalTfidf = finalTfidf/(Math.sqrt(lengthOfQueryVector)*Math.sqrt(lengthOfDocumentVector)*IndexReader.documentsLengths.get(fileId));
			finalTfidf = finalTfidf/(Math.sqrt(lengthOfQueryVector)*Math.sqrt(lengthOfDocumentVector));

			List<String> list = topResults.get(finalTfidf);
			if(list == null){
				list = new ArrayList<String>();
			}
			list.add(fileId);
			topResults.put(finalTfidf, list);

		}
		
		Set<Double> finalTfidfs = topResults.descendingKeySet();
		List<DocumentWithTfIdfWeight> top10Results = new ArrayList<DocumentWithTfIdfWeight>(); 
		int k =10;
		for(Double key: finalTfidfs) {
			List<String> temp = topResults.get(key);
			if(k >= temp.size()) {
				for (String fileId : temp) {
					DocumentWithTfIdfWeight doc = new DocumentWithTfIdfWeight(fileId, key);
					top10Results.add(doc);
				}
				k = k- temp.size();
			}
			else {
				for(int i=0; i<k; i++) {
					DocumentWithTfIdfWeight doc = new DocumentWithTfIdfWeight(temp.get(i), key);
					top10Results.add(doc);
				}
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
				if(leftPostings==null){
					if("AND".equals(nodeValue)||"NOT".equals(nodeValue)){
						return null;
					}else{
						return rightPostings;
					}
				}
				if(rightPostings == null){
					if("AND".equals(nodeValue)){
						return null;
					}else 
					{
						return leftPostings;
					}
				}

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
						int leftFileId =  Integer.parseInt(leftDoc);
						int rightFileId =  Integer.parseInt(rightDoc);
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
						int leftFileId =  Integer.parseInt(leftDoc);
						int rightFileId =  Integer.parseInt(rightDoc);						
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
						int leftFileId=0;int rightFileId=0;
						if(shouldLeftPointerMove){
							if(leftIterator.hasNext()){
								leftDoc = leftIterator.next();
								shouldLeftPointerMove = false;
								leftFileId =  Integer.parseInt(leftDoc);
							}else break;
						}
						if(shouldrightPointerMove)
						{
							if(rightIterator.hasNext()){
								rightDoc = rightIterator.next();
								shouldrightPointerMove = false;
								rightFileId =  Integer.parseInt(rightDoc);

							}else break;
						}
						
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
				if(documentFrequency == null && postings!=null){
					queryTermDocumentFrequency.put(termText, postings.size());
				}
				if(postings!=null){
					double documentsCount = IndexReader.documentsLengths.get("documentsCount");
					double postingsSize = postings.size();
					for (TermDocumentFreq termDocumentFreq : postings) {
						double tfidf = (1.0+Math.log10(termDocumentFreq.getFrequency().doubleValue()))*Math.log10(documentsCount/postingsSize);
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
		}
		if(resultPostings.size() == 0){
			return null;
		}
		String[] r=resultPostings.toArray(new String[] {""});
		Arrays.sort(r);
		return Arrays.asList(r);
	}

	
	/**
	 * Method to execute queries in E mode
	 * @param queryFile : The file from which queries are to be read and executed
	 */
	public void query(File queryFile) {
		//TODO: IMPLEMENT THIS METHOD
		class Temp{
			String queryId;
			StringBuffer queryText= new StringBuffer();
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(queryFile));
			String line =null;
			Temp query = null;
			List<Temp> queriesInFile = new ArrayList<Temp>();
			while((line= br.readLine())!=null) {
				if(line.contains("numQueries")) {
					continue;
				}else if(line.startsWith("Q_")) {
					query = new Temp();
					String[] queryVariables = line.split("\\{");
					if(queryVariables.length>1) {
						query.queryId = queryVariables[0];
					}
					if(line.contains("}")) {
						query.queryText.append(line.substring(line.indexOf('{')+1, line.indexOf('}')));
						queriesInFile.add(query);
					}
					else 
						query.queryText.append(line.substring(line.indexOf('{')+1, line.length()));
				}else {
					query.queryText.append(line.substring(0, line.indexOf('}')));
					queriesInFile.add(query);
				}
			}
			stream.println("numResults="+queriesInFile.size());
			for(Temp s: queriesInFile) {
				Query myQuery = QueryParser.parse(s.queryText.toString(), "OR");
				List<String> resultPostings = getPostingsList(myQuery.getQueryTree());
				if(resultPostings != null)
				{
					stream.print(s.queryId+"{");
					List<DocumentWithTfIdfWeight> results = getTFIDFTopResults(resultPostings);					
					for (int i=0;i<results.size();i++) {
						stream.print(results.get(i).getFileId()+"#");
						stream.printf("%.5f",results.get(i).getTfIdf());
						if(i!=results.size()-1) {
							stream.print(", ");
						}
					}
					stream.println("}");
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
