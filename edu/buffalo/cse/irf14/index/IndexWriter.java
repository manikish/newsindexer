/**
 * 
 */
package edu.buffalo.cse.irf14.index;

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
	
	 public static HashMap<String, Integer> termDictionary = new HashMap<String, Integer>();
	 public static HashMap<Integer, List<TermDocumentFreq>> termIndex = new HashMap<Integer, List<TermDocumentFreq>>();
	 
	 public static HashMap<String, Integer> authorDictionary = new HashMap<String, Integer>();
	 public static HashMap<Integer, List<TermDocumentFreq>> authorIndex = new HashMap<Integer, List<TermDocumentFreq>>();
	 
	 public static HashMap<String, Integer> placeDictionary = new HashMap<String, Integer>();
	 public static HashMap<Integer, List<TermDocumentFreq>> placeIndex = new HashMap<Integer, List<TermDocumentFreq>>();
	 
	 public static HashMap<String, Integer> categoryDictionary = new HashMap<String, Integer>();
	 public static HashMap<Integer, List<TermDocumentFreq>> categoryIndex = new HashMap<Integer, List<TermDocumentFreq>>();
	 
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
			throw new IndexerException();
		}
	}
	
	public Integer write(HashMap<String, Integer> dictionary,HashMap<Integer, List<TermDocumentFreq>> index, Integer count) {
		// TODO Auto-generated method stub
        while(myStream.hasNext())
        {
        	Token token = myStream.next();
            String tokenText = token.getTermText();
            Integer ind = dictionary.get(tokenText);
			List<TermDocumentFreq> docsList = new ArrayList<TermDocumentFreq>(){
        		@Override
        		public boolean contains(Object o) {
        			// TODO Auto-generated method stub
        			TermDocumentFreq term = null;        			
        			for(int i=0;i<this.size();i++) {
        				if((term=this.get(i)).getFileId()==o) {
        					term.setFrequency(term.getFrequency()+1);
        					return true;        					
        				}
        			}
        			return false;
        		}
        	};
        	if(ind==null)
        	{
        		count++;
        		dictionary.put(tokenText,count);
            	TermDocumentFreq termDocItem = new TermDocumentFreq();
            	termDocItem.setFileId(fileId);
            	termDocItem.setFrequency(termDocItem.getFrequency()+1);
            	docsList.add(termDocItem);
            	index.put(count, docsList);
        	}else
        	{
                docsList = index.get(ind);
                if(!docsList.contains(fileId)) {
                	TermDocumentFreq termDocItem = new TermDocumentFreq();
                	termDocItem.setFileId(fileId);
                	termDocItem.setFrequency(termDocItem.getFrequency()+1);
                	docsList.add(termDocItem);
                	index.put(ind, docsList);
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
	}

	public String getIndexDir() {
		return indexDir;
	}
	
}
