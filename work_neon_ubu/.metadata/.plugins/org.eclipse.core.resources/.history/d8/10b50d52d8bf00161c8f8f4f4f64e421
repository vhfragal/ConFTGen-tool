package br.usp.icmc.ffsm;

import java.io.IOException;
import java.util.ArrayList;

import br.usp.icmc.fsm.common.State;
import br.usp.icmc.fsm.common.Transition;

public class CommonPathFSM {

	ArrayList<Transition> common_in1;
	ArrayList<Transition> common_in2;
	State s1,s2;
	boolean max = false;
	boolean disting;
	int ncstates;
	
	//create new common path
	public CommonPathFSM(State s1, State s2, int numberCStates){
		common_in1 = new ArrayList<Transition>();
		common_in2 = new ArrayList<Transition>();
		this.s1 = s1;
		this.s2 = s2;
		ncstates = numberCStates;
		disting = false;
	}
	
	//increment other common path
	public CommonPathFSM(State s1, State s2, int numberCStates, 
			ArrayList<Transition> common_in1, ArrayList<Transition> common_in2){
		this.common_in1 = new ArrayList<Transition>();
		this.common_in2 = new ArrayList<Transition>();
		this.s1 = s1;
		this.s2 = s2;
		ncstates = numberCStates;
		this.common_in1.addAll(common_in1);
		this.common_in2.addAll(common_in2);
		disting = false;
	}
	
	public boolean addCommon(Transition t1, Transition t2) 
			throws IOException, InterruptedException{		
			
		if(common_in1.size() < (ncstates-1) && !disting){	
			//System.out.println("ooooo "+ common_in1.size()+" "+(ncstates-2)+" "+ !t1.getOutput().equals(t2.getOutput()));
			if(common_in1.size() >= (ncstates-2) && t1.getOutput().equals(t2.getOutput())){
				//System.out.println("YES");
				return false;
			}
			common_in1.add(t1);
			common_in2.add(t2);			
			if(!t1.getOutput().equals(t2.getOutput())){
				disting = true;
			}			
			return true;
		}else{
			max = true;
			return false;
		}	
	}
	
	public int getN(){
		return ncstates;
	}
	
	public State getS1(){
		return s1;
	}
	
	public State getS2(){
		return s2;
	}
	
	public ArrayList<Transition> get1(){
		return common_in1;
	}
	
	public ArrayList<Transition> get2(){
		return common_in2;
	}
	
	public boolean getDistinguish(){
		return disting;
	}
	
	public boolean getIsMax(){
		return max;
	}	
	
}
