/**
 * 
 */
package edu.buffalo.cse.irf14.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import edu.buffalo.cse.irf14.query.Query.Tree;

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
		StringBuffer toStringOfThisQuery = new StringBuffer("{");
		Query myQuery = new Query();
		Stack<String> operatorStack = new Stack<String>();
		List<Object> queryStringList = new ArrayList<Object>();
		int queryStringIndex =-1;
		int termsInBraces[] = new int[10];
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
					queryStringIndex = -1;
				}
				toStringOfThisQuery.append("[");
				count++;
				continue;
			}else if(queryStringIndex==-1 && userQuery.charAt(i)!=' '){
				queryStringIndex = i;
				continue;
			}			
			if(userQuery.charAt(i)==' ') {
				String myString =userQuery.substring(queryStringIndex, i);
				if(!quotes && myString.contains(QUOTES))
					quotes = true;
				if(!quotes) {
					if(OPERANDS.contains(myString)) {
						operatorStack.push(myString);
						toStringOfThisQuery.append(myString+" ");
						freeTextQueryTerms = false;
					}
					else if(myString.contains(COLON)){
						queryStringList.add(myString);
						toStringOfThisQuery.append(myString+" ");
						if(count>0) termsInBraces[count-1]+=1;
						freeTextQueryTerms = true;
					}
					else {
						if(freeTextQueryTerms) {
							operatorStack.push(defaultOperator);
							toStringOfThisQuery.append(defaultOperator+" ");
						}
						queryStringList.add(defaultIndex+myString);
						toStringOfThisQuery.append(defaultIndex+myString+" ");
						if(count>0) termsInBraces[count-1]+=1;
						
						freeTextQueryTerms = true;
					}

					queryStringIndex=-1;
					continue;
				}
				else {					
					if(quotedQueryTerm!=null && myString.contains(QUOTES)) {
						quotedQueryTerm = quotedQueryTerm+" "+myString;
						if(quotedQueryTerm.contains(COLON)) {
							String[] temp = quotedQueryTerm.split(QUOTES);
							quotedQueryTerm = new String();
							for(String s:temp)
								quotedQueryTerm += s; 
							queryStringList.add(quotedQueryTerm);
							toStringOfThisQuery.append(quotedQueryTerm+" ");
						}else {
							queryStringList.add(defaultIndex+quotedQueryTerm.substring(1, quotedQueryTerm.length()-1));
							toStringOfThisQuery.append(defaultIndex+quotedQueryTerm.substring(1, quotedQueryTerm.length()-1)+" ");
						}
						if(count>0) termsInBraces[count-1]+=1;
						quotes = false;
						if(freeTextQueryTerms) {
							operatorStack.push(defaultOperator);
							toStringOfThisQuery.append(defaultOperator+" ");
						}
						freeTextQueryTerms = true;
						quotedQueryTerm = null;
					}else {
						quotedQueryTerm = quotedQueryTerm==null?myString: quotedQueryTerm+" "+myString;
					}
					queryStringIndex = -1;
					defaultIndex = "Term:";
				}
				
			}
			else if(userQuery.charAt(i)==')') {
				String myString = null;
				if(quotes && (myString=userQuery.substring(queryStringIndex, i)).contains(QUOTES)) {
					if(myString.contains(COLON)) {
						queryStringList.add(myString);
						toStringOfThisQuery.append(myString+" ");
					}
					else {
						queryStringList.add(defaultIndex+myString);
						toStringOfThisQuery.append(defaultIndex+myString+" ");
					}
					quotes = false;
					if(freeTextQueryTerms) {
						operatorStack.push(defaultOperator);
						toStringOfThisQuery.append(defaultOperator+" ");
					}
				}else {
					String temp = userQuery.substring(queryStringIndex, i).contains(COLON)?userQuery.substring(queryStringIndex, i):
						defaultIndex+userQuery.substring(queryStringIndex, i);
					queryStringList.add(temp);
					toStringOfThisQuery.append(temp+" ");
				}
				
				if(i!=userQuery.length()-1) {
					if(userQuery.charAt(++i)==' ') {
						queryStringIndex=-1;
					}else {
						queryStringIndex=i;
					}
				}
				for(int j=0;j<termsInBraces[count-1];j++) {
					queryStringList.add(constructNode(operatorStack.pop(), 
							(Object)queryStringList.remove(queryStringList.size()-1), 
							(Object)queryStringList.remove(queryStringList.size()-1)));
				}
				
				if(freeTextQueryTerms) {
					operatorStack.push(defaultOperator);
					toStringOfThisQuery.append(defaultOperator+" ");
				}
				termsInBraces[count-1]=0;
				count--;
				if(count!=0 && termsInBraces[count-1]!=0) termsInBraces[count-1]+=1;
				freeTextQueryTerms = true;
				defaultIndex = "Term:";
				toStringOfThisQuery.append("] ");
				continue;
			}
			else if(i==userQuery.length()-1) {
				String myString = userQuery.substring(queryStringIndex, i+1);
				if(freeTextQueryTerms) {
					operatorStack.push(defaultOperator);
					toStringOfThisQuery.append(defaultOperator+" ");
				}
				if(myString.contains(":")) {
					queryStringList.add(myString);
					toStringOfThisQuery.append(myString);
				}
				else {
					queryStringList.add(defaultIndex+myString);
					toStringOfThisQuery.append(defaultIndex+myString);
				}
			}
		}
		if(operatorStack.isEmpty() && queryStringList.size()==1) {
			queryStringList.add(constructNode((String)queryStringList.remove(0), null, null));
		}
		while(!operatorStack.isEmpty() && !queryStringList.isEmpty()){
			queryStringList.add(constructNode(operatorStack.pop(), 
					(Object)queryStringList.remove(queryStringList.size()-1), 
					(Object)queryStringList.remove(queryStringList.size()-1)));
		}
		myQuery.setQueryTree((Tree)queryStringList.get(0));
		myQuery.setToString(toStringOfThisQuery.append("}").toString());
		return myQuery;
	}
	private static Tree constructNode(String operator, Object operand1, Object operand2) {
		// TODO Auto-generated method stub
		Tree node = new Tree();
		Tree leftLeafNode = operand1==null?null:(operand1 instanceof Tree)? (Tree)operand1 : new Tree((String)operand1);
		Tree rightLeafNode = operand2==null?null:(operand2 instanceof Tree)? (Tree)operand2 :new Tree((String)operand2);
		node.setLeftLeaf(leftLeafNode);
		node.setRightLeaf(rightLeafNode);
		node.setNodeValue(operator);
		return node;
	}
}
