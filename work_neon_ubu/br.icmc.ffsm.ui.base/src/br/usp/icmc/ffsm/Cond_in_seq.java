package br.usp.icmc.ffsm;

import java.util.ArrayList;

public class Cond_in_seq {

	private String condition;
	private ArrayList<String> seq;
	
	public Cond_in_seq(ArrayList<String> seq, String cond){
		this.seq = seq;
		this.condition = cond;
	}
	
	//public void concatenate(ArrayList<String> seq, String cond){
	//	this.seq.addAll(seq);		
	//	this.condition = "(and "+this.condition+" "+cond+")";
	//}
	
	public ArrayList<String> getSequence() {
		return seq;
	}
	public String getCond() {
		return condition;
	}
	
	public String toString(){
		return "("+seq+","+condition+")";
	}
	
	public void setSequence(ArrayList<String> seq){
		this.seq = seq;
	}
	
	public void setCondition(String condition){
		this.condition = condition;
	}
}


