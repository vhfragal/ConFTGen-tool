package br.usp.icmc.ffsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.usp.icmc.uml.CState;
import br.usp.icmc.uml.CTransition;

public class Compose {

	private ArrayList<CTransition> mutlist;
	private CState region;
	private CState nleaf;	
		
	public Compose(CState region, CState nleaf, ArrayList<CTransition> mutlist){
		this.mutlist = new ArrayList<CTransition>();
		this.region = region;
		this.nleaf = nleaf;		
	}
	
	public CState getRegion() {
		return region;
	}
	
	public CState getNleaf() {
		return nleaf;
	}
	
	public ArrayList<CTransition> getList() {
		return mutlist;
	}
	
}


