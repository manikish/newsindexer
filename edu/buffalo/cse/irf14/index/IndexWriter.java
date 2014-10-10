/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	 public HashMap<String, Integer> termDictionary = new HashMap<String, Integer>();
	 public static HashMap<Integer, HashMap<String, Integer>> termIndex = new HashMap<Integer, HashMap<String, Integer>>();
	 
	 public static HashMap<String, Integer> authorDictionary = new HashMap<String, Integer>();
	 public static HashMap<Integer, HashMap<String, Integer>> authorIndex = new HashMap<Integer, HashMap<String, Integer>>();
	 
	 public static HashMap<String, Integer> placeDictionary = new HashMap<String, Integer>();
	 public static HashMap<Integer, HashMap<String, Integer>> placeIndex = new HashMap<Integer, HashMap<String, Integer>>();
	 
	 public static HashMap<String, Integer> categoryDictionary = new HashMap<String, Integer>();
	 public static HashMap<Integer, HashMap<String, Integer>> categoryIndex = new HashMap<Integer, HashMap<String, Integer>>();
	 
	 private static Integer termCount = 0, authorCount = 0, placeCount = 0, categoryCount = 0;
	 
	 private TokenStream myStream;
	 private String fileId;
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
		try {
			//hardcoded 0
			for (FieldNames fieldName : d.getFieldNames()) {
				myStream = myTokenizer.consume(d.getField(fieldName)[0]);
				fileId = d.getField(FieldNames.FILEID)[0];
				TokenFilter myFilter = (TokenFilter)myAnalyzerFactory.getAnalyzerForField(fieldName, myStream);
				while(myFilter!=null) {
					myFilter.perform();
					myFilter = myFilter.getNextFilter();
				}
				switch (fieldName) {
				case AUTHOR:
					authorCount = write(authorDictionary, authorIndex, authorCount);
					break;
				case CATEGORY:
					categoryCount = write(categoryDictionary, categoryIndex, categoryCount);
					break;
				case PLACE:
					placeCount = write(placeDictionary, placeIndex, placeCount);
					break;
				case FILEID:
					break;
				default:
					termCount = write(termDictionary, termIndex, termCount);
					break;
				}
			}			
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
		}
	}
	
	public Integer write(HashMap<String, Integer> dictionary,HashMap<Integer, HashMap<String, Integer>> index, Integer count) {
		// TODO Auto-generated method stub
		myStream.reset();
        while(myStream.hasNext())
        {
        	Token token = myStream.next();
            String tokenText = token.getTermText();
            Integer ind = dictionary.get(tokenText);
            HashMap<String, Integer> docsList = new HashMap<String, Integer>();
        	if(ind==null)
        	{
        		count++;
        		dictionary.put(tokenText,count);
            	HashMap<String, Integer> freq = new HashMap<String, Integer>();
            	freq.put(fileId, 1);
            	index.put(count, freq);
        	}else
        	{
                docsList = index.get(ind);
                if(!docsList.containsKey(fileId)) {
                	HashMap<String, Integer> freq = new HashMap<String, Integer>();
                	freq.put(tokenText, 1);
                	index.put(count, freq);
                }
                else {
                	docsList.put(fileId, docsList.get(fileId)+1);
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
		ObjectOutputStream oo;
		try {
			oo = new ObjectOutputStream(new FileOutputStream(indexDir+File.separator+"termDictionary"));
			oo.writeObject(termDictionary);
			oo.close();
			oo = new ObjectOutputStream(new FileOutputStream(indexDir+File.separator+"termIndex"));
			oo.writeObject(termIndex);
			oo.close();
			oo = new ObjectOutputStream(new FileOutputStream(indexDir+File.separator+"authorIndex"));
			oo.writeObject(authorIndex);
			oo.close();
			oo = new ObjectOutputStream(new FileOutputStream(indexDir+File.separator+"authorDictionary"));
			oo.writeObject(authorDictionary);
			oo.close();
			oo = new ObjectOutputStream(new FileOutputStream(indexDir+File.separator+"placeIndex"));
			oo.writeObject(placeIndex);
			oo.close();
			oo = new ObjectOutputStream(new FileOutputStream(indexDir+File.separator+"placeDictionary"));
			oo.writeObject(placeDictionary);
			oo.close();
			oo = new ObjectOutputStream(new FileOutputStream(indexDir+File.separator+"categoryDictionary"));
			oo.writeObject(categoryDictionary);
			oo.close();
			oo = new ObjectOutputStream(new FileOutputStream(indexDir+File.separator+"categoryIndex"));
			oo.writeObject(categoryIndex);
			oo.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String getIndexDir() {
		return indexDir;
	}
	
}
