package br.usp.icmc.ffsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PairTable {

	private ArrayList<Cond_in_seq> d_seq;
	private FState s1;
	private FState s2;
	private ArrayList<CommonPath> paths;
	
	private Map<Cond_in_seq,CommonPath> map;
	private Map<String,ArrayList<CommonPath>> seq_map;
	
	public PairTable(FState s1, FState s2){
		this.d_seq = new ArrayList<Cond_in_seq>();
		this.s1 = s1;
		this.s2 = s2;
		this.paths = new ArrayList<CommonPath>();
		this.map = new HashMap<Cond_in_seq,CommonPath>();
		this.seq_map = new HashMap<String,ArrayList<CommonPath>>();
	}
	
	public void resetMap(){
		this.d_seq = new ArrayList<Cond_in_seq>();
		this.paths = new ArrayList<CommonPath>();
		this.map = new HashMap<Cond_in_seq,CommonPath>();
		this.seq_map = new HashMap<String,ArrayList<CommonPath>>();
	}
	
	public void addSequence(Cond_in_seq in, CommonPath path, Map<String,ArrayList<CommonPath>> seq_map){
		map.put(in, path);
		d_seq.add(in);
		this.paths.add(path);
		this.seq_map = seq_map;
	}
	
	public Map<Cond_in_seq,CommonPath> getMap(){
		return map;
	}
	
	public Map<String,ArrayList<CommonPath>> getSeqMap(){
		return seq_map;
	}
	
	public FState getS1() {
		return s1;
	}
	
	public FState getS2() {
		return s2;
	}
	
	public ArrayList<Cond_in_seq> getCSequence() {
		return d_seq;
	}
	
	public ArrayList<CommonPath> getPaths() {
		return paths;
	}
	
	public String toString(){
		String text = "------------------------------\n"+
				"State pair "+s1+" and "+s2+" Sequence(s):\n";
		for(Cond_in_seq s : d_seq){
			text = text.concat(s+"\n");
			CommonPath cp = map.get(s);
			if(cp != null){
				text = text.concat(cp.get1()+ " and "+ cp.get2()+"\n");
			}						
		}
		return text;
	}
}


