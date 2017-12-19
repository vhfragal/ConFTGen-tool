package br.usp.icmc.uml;

import java.util.ArrayList;
import java.util.HashMap;



import br.usp.icmc.uml.CState.StateType;


public class StateStructure{
	protected CState root;
	protected ArrayList<CState> stateSet;
	protected ArrayList<CState> RSet;
	protected HashMap<CState, ArrayList<Configuration>> configMap;
			
	public StateStructure(CState root, ArrayList<CState> stateSet) {
		this.stateSet = stateSet;
		this.root = root;
		setRelations();
	}	
	
	public void setRelations(){
		//descendants
		setDescendants();
		setAncestors();
		setR();
		//identifyConfigurations();
	}
	private void setR(){
		RSet = new ArrayList<CState>();
		RSet.addAll(stateSet);
		RSet.remove(root);
		for(CState s : stateSet){
			if(s.getType().equals(StateType.region)){
				RSet.remove(s);
			}
		}
	}
	public ArrayList<CState> getR() {
		return RSet;
	}
	/*
	private void identifyConfigurations(){		
		//ArrayList<CState> cList = new ArrayList<CState>();
		//cList.add(root);
		ArrayList<CState> sList = new ArrayList<CState>();
		ArrayList<Configuration> aConf = new ArrayList<Configuration>();
		//Configuration conf = new Configuration();
		findLeaf(root,sList, aConf);
	}
	
	private void findLeaf(CState r, ArrayList<CState> sList, ArrayList<Configuration> aConf){
		//for(CState r : cList){			
			sList.add(r);
			if(r.getType().equals(StateType.simple)){
				Configuration c = new Configuration(sList);
				aConf.add(c);
				return;
			}
			if(r.getType().equals(StateType.compositeOr)){			
				for(CState sub : r.getSubstates()){
					ArrayList<CState> sList2 = new ArrayList<CState>();
					sList2.addAll(sList);
					findLeaf(sub,sList2,aConf);
				}			
			}
			if(r.getType().equals(StateType.compositeAnd)){
				for(CState reg : r.getSubstates()){
					sList.add(reg);
				}
							
			}
		//}		
	}
	
	private void selectAnd(ArrayList<CState> regs){
		ArrayList<CState> sel = new ArrayList<CState>();
		int reg_c = 0;
		for(CState reg : regs){
			reg_c++;
			for(CState st : reg.getSubstates()){
				
			}
		}
	}
	*/
	private void setAncestors(){
		for(CState state : stateSet){			
			for(CState sub : state.getDescendants()){
				ArrayList<CState> anc = sub.getAncestors();
				if(anc == null){
					anc = new ArrayList<CState>();
				}
				if(!anc.contains(state)) anc.add(state);
				sub.setAncestors(anc);
			}			
		}	
	}
	
	private void setDescendants(){
		for(CState state : stateSet){
			ArrayList<CState> desc = new ArrayList<CState>();		
			rec_desc(desc,state);		
			state.setDescendants(desc);
		}		
	}
	private void rec_desc(ArrayList<CState> desc, CState state){
		if(!desc.contains(state)) desc.add(state);
		if(state.getSubstates() != null && state.getSubstates().size()>0){
			desc.addAll(state.getSubstates());
			for(CState sub : state.getSubstates()){
				sub.setSuperState(state); //set father
				rec_desc(desc,sub);			
			}
		}		
	}
	
	public ArrayList<CState> getAncestors(ArrayList<CState> states){
		ArrayList<CState> anc = new ArrayList<CState>();
		for(CState s : states){
			for(CState a : s.getAncestors()){
				if(!anc.contains(a)) anc.add(a);
			}			
		}
		ArrayList<CState> anc2 = new ArrayList<CState>();
		anc2.addAll(anc);
		for(CState a : anc2){
			for(CState s : states){
				if(!s.getAncestors().contains(a)){
					anc.remove(a);
					break;
				}				
			}
		}
		return anc;
	}
	
	public ArrayList<CState> getDescendants(ArrayList<CState> states){
		ArrayList<CState> desc = new ArrayList<CState>();
		for(CState s : states){
			for(CState d : s.getDescendants()){
				if(!desc.contains(d)) desc.add(d);				
			}
		}
		return desc;
	}
	
	public CState getLeastCommonAncestor(ArrayList<CState> states){		
		ArrayList<CState> anc = getAncestors(states);
		CState lca = null;
		for(CState s : anc){
			if(s.getAncestors().containsAll(anc)){
				lca = s;
				break;
			}
		}
		return lca;
	}
	
	public boolean isOrthogonal(CState s1, CState s2){
		ArrayList<CState> list = new ArrayList<CState>();
		list.add(s1);
		list.add(s2);
		CState lca = getLeastCommonAncestor(list);
		if(lca.getType().equals(StateType.compositeAnd)){
			return true;
		}
		return false;
	}
	
	public ArrayList<CState> getDefaultDesc(CState s){
		ArrayList<CState> list = new ArrayList<CState>();
		list.addAll(s.getDescendants());
		list.remove(s);
		recDefDesc(list,s);
		
		return list;
	}
	private void recDefDesc(ArrayList<CState> list, CState s){
		for(CState c : s.getSubstates()){
			if(!c.getDefault()){
				list.removeAll(c.getDescendants());				
			}else recDefDesc(list,c);
		}
	}
	
	public boolean isDefaultDesc(CState state, CState ancestor){
		ArrayList<CState> list = getDefaultDesc(ancestor);
		if(list.contains(state)){
			return true;
		}		
		return false;
	}
	
	public String getFComposition(CState state){
		ArrayList<CState> list = state.getAncestors();
		String cond = "";
		for(CState s : list){
			String f = s.getFCondition();
			f.trim();
			if(!f.equals("")){				
				//f = f.replace("&", " and ");
				//f = f.replace("!", " not ");
				//f = f.replace("||", " or ");				
				cond = cond.concat(f+"&"); 
			}
		}
		if(cond.length()>1){
			cond= cond.substring(0,cond.length()-1);
		}		
		return cond;
	}
	
	public String getTComposition(CTransition transition){
		CState source = transition.getSource();
		CState target = transition.getTarget();
		ArrayList<CState> list = transition.getGuard();
		String f = transition.getFCondition();
		f.trim();
		String cond = "";
		String p1 = getFComposition(source);
		String p2 = getFComposition(target);
		if(!p1.equals("")){
			cond = cond.concat(p1 + "&");
		}		
		if(!f.equals("")){
			cond = cond.concat(f + "&");
		}	
		for(CState g: list){
			String p3 = getFComposition(g);
			if(!p3.equals("")){
				cond = cond.concat(p3 + "&");
			}
		}
		if(!p2.equals("")){
			cond = cond.concat(p2);
		}else if(cond.length()>1){
			cond= cond.substring(0,cond.length()-1);
		}		
		return cond;
	}
	
	public String getTComposition_old(CTransition transition){
		CState source = transition.getSource();
		CState target = transition.getTarget();
		ArrayList<CState> list = transition.getGuard();
		String f = transition.getFCondition();
		f.trim();
		String cond = "(";
		String p1 = getFComposition(source);
		String p2 = getFComposition(target);
		if(!p1.equals("()")){
			cond = cond.concat(p1 + " and ");
		}		
		if(!f.equals("")){			
			f = f.replace("&", " and ");
			f = f.replace("!", "not ");
			f = f.replace("||", " or ");
			cond = cond.concat("("+ f + ") and ");
		}	
		for(CState g: list){
			String p3 = getFComposition(g);
			if(!p3.equals("()")){
				cond = cond.concat(p3 + " and ");
			}
		}
		if(!p2.equals("()")){
			cond = cond.concat(p2);
		}else if(cond.length()>1){
			cond= cond.substring(0,cond.length()-5);
		}
		cond = cond.concat(")");
		return cond;
	}
	
	@Override
	public String toString() {
		return "";
	}
	
	public void setStateSet(ArrayList<CState> stateSet) {
		this.stateSet = stateSet;
	}
	
	public ArrayList<CState> getStateSet() {
		return stateSet;
	}
	
	public CState getRoot() {
		return root;
	}
	
	public void print() {
		System.out.println("*****************");
		System.out.println("State Tree");
		print("|>", root);
		System.out.println("*****************");
	}
	
	private void print(String level, CState n){
		System.out.println(level + n.toString()+ "(type="+n.getType()+ ",default="+n.getDefault()
				+",fcond(" +n.getFCondition()+"),id="+n.getID()+")");
		
		for(CState n1 : n.getSubstates()){
			print("|   "+level, n1);
		}
	}

}
