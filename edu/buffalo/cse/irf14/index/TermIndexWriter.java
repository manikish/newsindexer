package edu.buffalo.cse.irf14.index;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;

public class TermIndexWriter implements Writer {
    private static HashMap<String, Integer> termDictionary = new HashMap<>();
    private static HashMap<Integer, List<String>> termIndex = new HashMap<Integer, List<String>>();
    private static Integer count = 0;
    
    
	@Override
	public void write(TokenStream stream, String fileId) {
		// TODO Auto-generated method stub
        while(stream.hasNext())
        {
        	Token token = stream.next();
            String tokenText = token.getTermText();
            Integer index = termDictionary.get(tokenText);
        	if(index==null)
        	{
        		count++;
            	termDictionary.put(token.getTermText(),count);
            	List<String> docsList = new ArrayList<String>();
            	docsList.add(fileId);
            	termIndex.put(count, docsList);
        	}else
        	{
                List<String> docsList = termIndex.get(index);
                if(!docsList.contains(fileId))
                {
                	docsList.add(fileId);
                    termIndex.put(index, docsList);
                }
                
        	}
        	        	
        }
	}

}
