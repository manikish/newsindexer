/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
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
	
	 private HashMap<String, Integer> termDictionary = new HashMap<String, Integer>();
	 private HashMap<Integer, List<TermDocumentFreq>> termIndex = new HashMap<Integer, List<TermDocumentFreq>>();
	 
	 private HashMap<String, Integer> authorDictionary = new HashMap<String, Integer>();
	 private HashMap<Integer, List<TermDocumentFreq>> authorIndex = new HashMap<Integer, List<TermDocumentFreq>>();
	 
	 private HashMap<String, Integer> placeDictionary = new HashMap<String, Integer>();
	 private HashMap<Integer, List<TermDocumentFreq>> placeIndex = new HashMap<Integer, List<TermDocumentFreq>>();
	 
	 private HashMap<String, Integer> categoryDictionary = new HashMap<String, Integer>();
	 private HashMap<Integer, List<TermDocumentFreq>> categoryIndex = new HashMap<Integer, List<TermDocumentFreq>>();
	 
	 private HashMap<String, Integer> documentsLengths = new HashMap<String, Integer>();
	 
	 private static Integer termCount = 0, authorCount = 0, placeCount = 0, categoryCount = 0;
	 
	 private TokenStream myStream;
	 private String fileId;
	 private Integer docLength;
	 private Integer totalDocumentsLength = 0;
	 private Integer documentsCount = 0;
	 
	/**
	 * Default constructor
	 * @param indexDir : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		//TODO : YOU MUST IMPLEMENT THIS
		this.indexDir = indexDir;
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
		try {
			//hardcoded 0
			docLength = 0;
			documentsCount++;
			for (FieldNames fieldName : d.getFieldNames()) {
				myStream = myTokenizer.consume(d.getField(fieldName)[0]);
				fileId = d.getField(FieldNames.FILEID)[0];
				TokenFilter myFilter = (TokenFilter)myAnalyzerFactory.getAnalyzerForField(fieldName, myStream);
				while(myFilter!=null) {
					myFilter.perform();
					myFilter = myFilter.getNextFilter();
				}
				docLength = docLength + myStream.getSize();
				switch (fieldName) {
				case AUTHOR:
					authorCount = write(authorDictionary, authorIndex, authorCount, true);
					break;
				case CATEGORY:
					categoryCount = write(categoryDictionary, categoryIndex, categoryCount, false);
					break;
				case PLACE:
					placeCount = write(placeDictionary, placeIndex, placeCount, false);
					break;
				case FILEID:
					break;
				default:
					termCount = write(termDictionary, termIndex, termCount, false);
					break;
				}
			}		
			documentsLengths.put(fileId, docLength);
			totalDocumentsLength = totalDocumentsLength+docLength;
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			throw new IndexerException();
		}
	}
	
	public Integer write(HashMap<String, Integer> dictionary,HashMap<Integer, List<TermDocumentFreq>> termIndex2, Integer count, boolean isAuthor) {
		// TODO Auto-generated method stub
		myStream.reset();
        while(myStream.hasNext())
        {
        	Token token = myStream.next();
            String tokenText = token.getTermText();
            Integer ind = dictionary.get(tokenText);
			List<TermDocumentFreq> docsList = new CustomArrayList();
        	if(ind==null)
        	{
        		count++;
        		dictionary.put(tokenText,count);
            	TermDocumentFreq termDocItem = new TermDocumentFreq(fileId, 1);
            	docsList.add(termDocItem);
            	termIndex2.put(count, (ArrayList<TermDocumentFreq>) docsList);
        	}else
        	{
                docsList = termIndex2.get(ind);
                if(!docsList.contains(fileId)) {
                	TermDocumentFreq termDocItem = new TermDocumentFreq(fileId, 1);
                	docsList.add(termDocItem);
                	termIndex2.put(ind, (ArrayList<TermDocumentFreq>) docsList);
                }
        	}        	        	
        }
        if(isAuthor){
            myStream.reset();
        	String tokenText = "";
            while(myStream.hasNext())
            {
            	Token token = myStream.next();
                tokenText = tokenText+token.getTermText()+" ";
            }
            tokenText = tokenText.trim();
            Integer ind = dictionary.get(tokenText);
			List<TermDocumentFreq> docsList = new CustomArrayList();
        	if(ind==null)
        	{
        		count++;
        		dictionary.put(tokenText,count);
            	TermDocumentFreq termDocItem = new TermDocumentFreq(fileId, 1);
            	docsList.add(termDocItem);
            	termIndex2.put(count, (ArrayList<TermDocumentFreq>) docsList);
        	}else
        	{
                docsList = termIndex2.get(ind);
                if(!docsList.contains(fileId)) {
                	TermDocumentFreq termDocItem = new TermDocumentFreq(fileId, 1);
                	docsList.add(termDocItem);
                	termIndex2.put(ind, (ArrayList<TermDocumentFreq>) docsList);
                }
        	}        	        	
        }
             return count;
	}
	
	/**
	 * Method that indicates that all open resources must be closed
	 * and cleaned and that the entire indexing operation has been completed.
	 * @throws IndexerException : In case any error occurs
	 */
	public void close() throws IndexerException {
		
		//TODO
		try {
			documentsLengths.put("totalDocumentsLength", totalDocumentsLength);
			documentsLengths.put("documentsCount", documentsCount);
			documentsLengths.put("averageDocumentLength", totalDocumentsLength/documentsCount);
			
			ObjectOutputStream myOOStream = new ObjectOutputStream(new FileOutputStream(indexDir+File.separator+"termIndex"));
			myOOStream.writeObject(termIndex);
			myOOStream.close();
			myOOStream = new ObjectOutputStream(new FileOutputStream(indexDir+File.separator+"termDictionary"));
			myOOStream.writeObject(termDictionary);
			myOOStream.close();
			myOOStream = new ObjectOutputStream(new FileOutputStream(indexDir+File.separator+"authorIndex"));
			myOOStream.writeObject(authorIndex);
			myOOStream.close();
			myOOStream = new ObjectOutputStream(new FileOutputStream(indexDir+File.separator+"authorDictionary"));
			myOOStream.writeObject(authorDictionary);
			myOOStream.close();
			myOOStream = new ObjectOutputStream(new FileOutputStream(indexDir+File.separator+"categoryIndex"));
			myOOStream.writeObject(categoryIndex);
			myOOStream.close();
			myOOStream = new ObjectOutputStream(new FileOutputStream(indexDir+File.separator+"categoryDictionary"));
			myOOStream.writeObject(categoryDictionary);
			myOOStream.close();
			myOOStream = new ObjectOutputStream(new FileOutputStream(indexDir+File.separator+"placeIndex"));
			myOOStream.writeObject(placeIndex);
			myOOStream.close();
			myOOStream = new ObjectOutputStream(new FileOutputStream(indexDir+File.separator+"placeDictionary"));
			myOOStream.writeObject(placeDictionary);
			myOOStream.close();
			myOOStream = new ObjectOutputStream(new FileOutputStream(indexDir+File.separator+"documentsLengths"));
			myOOStream.writeObject(documentsLengths);
			myOOStream.close();
			

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	public String getIndexDir() {
		return indexDir;
	}
	
}