package br.usp.icmc.fsm.common;

import java.util.ArrayList;

import br.usp.icmc.ffsm.FState;
import br.usp.icmc.ffsm.FTransition;

public class Node {
	private State state;
	private FState fstate;
	private FTransition ft;
	String type;
	private ArrayList<Node> children = new ArrayList<Node>();
	private ArrayList<String> labels = new ArrayList<String>();
	
	public Node(State st, String type) {
		state = st;
		this.type = type;
	}
	
	public String toString(){
		String out = "";
		if(state != null){
			out = out.concat(state+"");
		}
		if(fstate != null){
			out = out.concat(fstate+"");
		}
		if(ft != null){
			out = out.concat(ft+"");
		}
		return out;
	}
	
	public Node(State st) {
		state = st;
	}
	
	public Node(FState fs) {
		fstate = fs;
	}
	
	public Node(FTransition ft) {
		this.ft = ft;
	}
	
	public String getType(){
		return type;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public State getState() {
		return state;
	}
	
	public FState getFState() {
		return fstate;
	}
	
	public FTransition getFTransition() {
		return ft;
	}
	
	public void addChild(Node n) {
		children.add(n);		
	}
	
	public void addChild(Node n, String label) {
		children.add(n);
		labels.add(label);
	}
	
	public ArrayList<Node> getChildren() {
		return children;
	}
	
	public void remove(Node n){
		children.remove(n);
	}
	
	public ArrayList<String> getLabels() {
		return labels;
	}
}
