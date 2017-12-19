package br.usp.icmc.uml;

import java.io.IOException;
import java.util.ArrayList;

public class CommonPath {

	ArrayList<CTransition> common_in1;
	ArrayList<CTransition> common_in2;
	CState s1,s2;
	boolean max = false;
	boolean disting;
	int ncstates;
	
	public CommonPath(CState s1, CState s2, int numberCStates){
		common_in1 = new ArrayList<CTransition>();
		common_in2 = new ArrayList<CTransition>();
		this.s1 = s1;
		this.s2 = s2;
		ncstates = numberCStates;
		disting = false;
	}
	
	public CommonPath(CState s1, CState s2, int numberCStates, 
			ArrayList<CTransition> common_in1, ArrayList<CTransition> common_in2){
		this.common_in1 = new ArrayList<CTransition>();
		this.common_in2 = new ArrayList<CTransition>();
		this.s1 = s1;
		this.s2 = s2;
		ncstates = numberCStates;
		this.common_in1.addAll(common_in1);
		this.common_in2.addAll(common_in2);
		disting = false;
	}
	
	public boolean addCommon(CTransition t1, CTransition t2) 
			throws IOException, InterruptedException{		
			
		if(common_in1.size() < (ncstates-1) && !disting){	
			if(common_in1.size() >= (ncstates-2) && t1.getOutput().equals(t2.getOutput())){
				return false;
			}			
			if(!t1.getOutput().equals(t2.getOutput())){
				disting = true;
			}			
			common_in1.add(t1);
			common_in2.add(t2);				
			return true;
		}else{
			max = true;
			return false;
		}	
	}
	
	public int getN(){
		return ncstates;
	}
	
	public CState getS1(){
		return s1;
	}
	
	public CState getS2(){
		return s2;
	}
	
	public ArrayList<CTransition> get1(){
		return common_in1;
	}
	
	public ArrayList<CTransition> get2(){
		return common_in2;
	}
	
	public boolean getDistinguish(){
		return disting;
	}
	
	public boolean getIsMax(){
		return max;
	}	
}
