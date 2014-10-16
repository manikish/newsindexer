package edu.buffalo.cse.irf14.query;

/**
 * Class that represents a parsed query
 * @author nikhillo
 *
 */
public class Query {
	
	private String toString = new String();
	public String getToString() {
		return toString;
	}
	public void setToString(String temp) {
		toString = temp;
	}
	public static class Tree {
		private String nodeValue;
		private Tree leftLeaf;
		private Tree rightLeaf;
		public Tree() {}
		public Tree(String value) {
			this.nodeValue = value;
		}
		public String getNodeValue() {
			return nodeValue;
		}
		public void setNodeValue(String nodeValue) {
			this.nodeValue = nodeValue;
		}
		public Tree getLeftLeaf() {
			return leftLeaf;
		}
		public void setLeftLeaf(Tree leftLeaf) {
		this.leftLeaf = leftLeaf;
		}
		public Tree getRightLeaf() {
			return rightLeaf;
		}
		public void setRightLeaf(Tree rightLeaf) {
			this.rightLeaf = rightLeaf;
		}
	};
	
	private Tree queryTree;
	public void setQueryTree(Tree queryTree) {
		this.queryTree = queryTree;
	}
	public Tree getQueryTree() {
		return queryTree;
	}
	/**
	 * Method to convert given parsed query into string
	 */
	public String toString() {
		//TODO: YOU MUST IMPLEMENT THIS
		return toString;
	}
}
