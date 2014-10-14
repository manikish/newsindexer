/**
 * 
 */
package edu.buffalo.cse.irf14.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author nikhillo
 * Static parser that converts raw text to Query objects
 */
public class QueryParser {
	
	public static String defaultOperator = "OR";
	public static int count = 0;
	public static String defaultIndex = "Term:";
	public static String OPERANDS = "AND|OR|NOT";
	public static String COLON = ":";
	public static String QUOTES = "\"";
	/**
	 * MEthod to parse the given user query into a Query object
	 * @param userQuery : The query to parse
	 * @param defaultOperator : The default operator to use, one amongst (AND|OR)
	 * @return Query object if successfully parsed, null otherwise
	 */
	public static Query parse(String userQuery, String defaultOperator) { //yet to implement operator stack for free text queries
		//TODO: YOU MUST IMPLEMENT THIS METHOD
		Query myQuery = new Query();
		Stack<String> operatorStack = new Stack<String>();
		List<Object> queryStringList = new ArrayList<Object>();
		int queryStringIndex =-1;
		String quotedQueryTerm = null;
		boolean quotes=false;
		boolean freeTextQueryTerms = false;
		if(!defaultOperator.isEmpty() && !defaultOperator.equals(QueryParser.defaultOperator)) {
			QueryParser.defaultOperator = defaultOperator;
		}
		for(int i=0; i<userQuery.length();i++) {
			if(userQuery.charAt(i)=='(') {
				if(queryStringIndex!=-1 && userQuery.substring(queryStringIndex, i).contains(COLON)) {
					defaultIndex = userQuery.substring(queryStringIndex, i);
				}
				count++;
				continue;
			}else if(queryStringIndex==-1 && userQuery.charAt(i)!=' '){
				queryStringIndex = i;
				continue;
			}			
			if(userQuery.indexOf(i)==' ') {
				String myString =userQuery.substring(queryStringIndex, i);
				if(!quotes && myString.contains(QUOTES))
					quotes = true;
				if(!quotes) {
					if(OPERANDS.contains(myString)) {
						operatorStack.push(myString);
						freeTextQueryTerms = false;
					}
					else if(myString.contains(COLON)){
						queryStringList.add(myString);
					}
					else {
						if(freeTextQueryTerms) {
							operatorStack.push("OR");
						}
						queryStringList.add(defaultIndex+myString);
						defaultIndex = "Term:";
					}
				}
				else {					
					if(quotedQueryTerm!=null && myString.contains(QUOTES)) {
						quotedQueryTerm = quotedQueryTerm+" "+myString;
						queryStringList.add(quotedQueryTerm.substring(queryStringIndex+1, quotedQueryTerm.length()-1));
						quotes = false;
						quotedQueryTerm = null;
					}else {
						quotedQueryTerm = quotedQueryTerm+" "+myString;
					}
				}
				
				queryStringIndex=0;
				continue;
			}
			else if(userQuery.charAt(i)==')') {
				count--;
				queryStringList.add(constructNode(operatorStack.pop(), 
						(String)queryStringList.remove(queryStringList.size()-1), 
						(String)queryStringList.remove(queryStringList.size()-1)));
				continue;
			}
		}
		while(!operatorStack.isEmpty() && !queryStringList.isEmpty()){
			queryStringList.add(constructNode(operatorStack.pop(), 
					(String)queryStringList.remove(queryStringList.size()-1), 
					(String)queryStringList.remove(queryStringList.size()-1)));
		}
		myQuery.setQueryTree((Query.Tree)queryStringList.get(0));
		return myQuery;
	}
	private static Query.Tree constructNode(String operator, String operand1, String operand2) {
		// TODO Auto-generated method stub
		Query.Tree node = new Query.Tree();
		Query.Tree leftLeafNode = new Query.Tree(operand1);
		Query.Tree rightLeafNode = new Query.Tree(operand2);
		node.setLeftLeaf(leftLeafNode);
		node.setRightLeaf(rightLeafNode);
		node.setNodeValue(operator);
		return node;
	}
}
