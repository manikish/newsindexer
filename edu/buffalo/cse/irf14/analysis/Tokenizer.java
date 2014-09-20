/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;

import edu.buffalo.cse.irf14.document.Parser;


/**
 * @author nikhillo
 * Class that converts a given string into a {@link TokenStream} instance
 */
public class Tokenizer {
	private String delim = Parser.SPACE_SEPERATOR;
	private TokenStream tokenStream;
//	private Boolean isFullStopEnabled = Boolean.FALSE;
	/**
	 * Default constructor. Assumes tokens are whitespace delimited
	 */
	public Tokenizer() {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
	}
	
	/**
	 * Overloaded constructor. Creates the tokenizer with the given delimiter
	 * @param delim : The delimiter to be used
	 */
	public Tokenizer(String delim) {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		this.delim = delim;
	}
	
	/**
	 * Method to convert the given string into a TokenStream instance.
	 * This must only break it into tokens and initialize the stream.
	 * No other processing must be performed. Also the number of tokens
	 * would be determined by the string and the delimiter.
	 * So if the string were "hello world" with a whitespace delimited
	 * tokenizer, you would get two tokens in the stream. But for the same
	 * text used with lets say "~" as a delimiter would return just one
	 * token in the stream.
	 * @param str : The string to be consumed
	 * @return : The converted TokenStream as defined above
	 * @throws TokenizerException : In case any exception occurs during
	 * tokenization
	 */
	public TokenStream consume(String str) throws TokenizerException { //implement try catch block for casting exception
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		if(str!=null && !str.isEmpty()) {
			String[]  tokenArray = str.split(delim);
			ArrayList<Token> tokensArrayList = new ArrayList<Token>();
			if(tokenArray != null) {
				for(String temp: tokenArray) {
					if(!temp.isEmpty()) {
						int index = temp.indexOf(".");
						if(index==-1 || index==temp.length()-1){
							addTokens(tokensArrayList, temp);
						}
						else{
							String[] temp2 = temp.split(".");
							for(String s:temp2)
								if(!s.isEmpty())
									addTokens(tokensArrayList, s);
						}
					}
				}
			}
			tokenStream = new TokenStream(tokensArrayList);
			return tokenStream;
		}
		else {
			throw new TokenizerException();
		}
	}

	private void addTokens(ArrayList<Token> tokensArrayList, String temp) {
		Token myToken = new Token();
		myToken.setIsNoun(Boolean.FALSE);
		if(!StopwordTokenFilter.STOPWORD_LIST.contains(temp.toLowerCase()))
			myToken.setIsNoun(Boolean.TRUE);
		myToken.setTermText(temp);
		char c = temp.charAt(0);
		if(c>=65 && c<=90) myToken.setRetainText(myToken.getIsNoun());
		tokensArrayList.add(myToken);
	}
}

