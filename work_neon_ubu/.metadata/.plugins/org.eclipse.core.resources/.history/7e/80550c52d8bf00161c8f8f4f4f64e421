package br.usp.icmc.fsm.common;

import java.util.Iterator;

public class Ntree {
	
	private Node root;
	
	public Ntree() {
	
	}
	
	public void setRoot(Node root) {
		this.root = root;
	}
	
	public Node getRoot() {
		return root;
	}
	
	public void addNode(Node n1, Node n2) {
		n1.addChild(n2);
	}
	
	public void addNode(Node n1, String label, Node n2) {
		n1.addChild(n2, label);
	}
	
	public void removeNode(Node n1, Node n2) {
		n1.remove(n2);
	}
	
	public void print() {
		System.out.println("*****************");
		System.out.println("Testing Tree");
		print("-", root);
		System.out.println("*****************");
	}
	
	private void print(String level, Node n)
	{
		System.out.println(level + n.getState().getLabel() + "("+n.getType()+")");
		//Iterator<String> arclabels = n.getLabels().iterator();
		for(Node n1 : n.getChildren())
		{
			print(level + "----", n1);
		}
	}
}
