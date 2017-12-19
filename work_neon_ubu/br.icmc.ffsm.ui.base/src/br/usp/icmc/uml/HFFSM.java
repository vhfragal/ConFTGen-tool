package br.usp.icmc.uml;

import java.util.ArrayList;
import java.util.Map;


public class HFFSM{
	//protected HashSet<String> inputAlphabet, outputAlphabet;
	protected ArrayList<String>   F; //features
	protected ArrayList<String>   A; //products	
	protected StateStructure      S;
	protected ArrayList<String>   I; //inputSet
	protected ArrayList<String>   O; //outputSet
	protected ArrayList<CTransition> T;
	//protected CState initialState = null;
	Map<CState,ArrayList<ArrayList<CTransition>>> path_map;
	//Map<String,ArrayList<CommonPath>> seq_map;
	ArrayList<CTransition> no_loop_ft;
	ArrayList<CState> found_fc;
	ArrayList<CState> nfound_fc;
	
	boolean islog, debug;
	static String log;
	String prop;
	
	public HFFSM(CState root, ArrayList<CState> stateSet, ArrayList<CTransition> transitionSet) {
		S = new StateStructure(root, stateSet);		
		T = transitionSet;
		log = "";
		I = new ArrayList<String>();
		O = new ArrayList<String>();
		setInOut();		
		setScope();
	}	
	
	private void setScope(){
		for(CTransition t : T){
			ArrayList<CState> anc1 = t.getSource().getAncestors();
			ArrayList<CState> anc2 = t.getTarget().getAncestors();
			int i = 0;
			for(CState s1 : anc1){
				if(!s1.equals(anc2.get(i))){
					t.setScope(anc2.get(i-1));
					break;
				}
				i++;
			}
		}
	}
	
	private void setInOut(){
		for(CTransition t : T){
			String in = t.getInput();
			String out = t.getOutput();
			if(!I.contains(in)){
				I.add(in);
			}
			if(!O.contains(out)){
				O.add(out);
			}
		}
	}
	
	public ArrayList<String> getInputAlphabet(){
		return I;
	}
	
	public ArrayList<String> getOutputAlphabet(){
		return O;
	}
	
	public String getlog(){
		return log;
	}
	
	public Map<CState,ArrayList<ArrayList<CTransition>>> getPathMap(){
		return path_map;
	}
		
	public CTransition checkR(){
		ArrayList<CState> R = S.getR();
		for(CTransition t : T){			
			if(!R.contains(t.getSource()) || !R.contains(t.getTarget())){
				return t;
			}
		}
		return null;
	}
	
	public CTransition checkOrthogonal(){		
		for(CTransition t : T){	
			if(S.isOrthogonal(t.getSource(), t.getTarget())){
				return t;
			}
		}
		return null;
	}
	
	public CTransition checkOrthogonalGuard(){		
		for(CTransition t : T){
			ArrayList<CState> G = t.getGuard();
			for(CState s : G){
				if(!S.isOrthogonal(t.getSource(), s)){
					return t;
				}
			}			
		}
		return null;
	}
	
	public StateStructure getStruct(){
		return S;
	}
	public ArrayList<CTransition> getTransitions(){
		return T;
	}
	/*
	public CState getInitialState() {
		return initialState;
	}
	
	public void setInitialState(CState initialState) {
		this.initialState = initialState;
	}
	
	public int getNumberOfStates() {
		return states.size();
	}
	
	public int getNumberOfTransitions() {
		return transitions.size();
	}
	
	public void addState(CState ns) {
		if(!states.contains(ns)){
			states.add(ns);
		}		
	}
	
	public void addTransition(CTransition nt) {
		transitions.add(nt);
		inputAlphabet.add(nt.getTrigger());
		outputAlphabet.add(nt.getEffect());
	}

	public ArrayList<CState> getStates() {
		return states;
	}
	
	public ArrayList<CTransition> getTransitions() {
		return transitions;
	}

	public HashSet<String> getOutputAlphabet() {
		return outputAlphabet;
	}
	
	public HashSet<String> getInputAlphabet() {
		return inputAlphabet;
	}
	
	public CState nextState(CState current, String inputsymbol) 
	{
		if(inputsymbol.equals("EPSILON"))
			return current;
		
		for(CTransition ot : current.getOut()) 
		{
			if(ot.getInput().equals(inputsymbol))
				return ot.getOut();
		}
		
		return null;
	}
	
	public String nextStateOut(CState current, String inputsymbol) 
	{
		if(inputsymbol.equals("EPSILON"))
			return "";
		
		for(CTransition ot : current.getOut()) 
		{
			if(ot.getInput().equals(inputsymbol))
				return ot.getOutput();
		}
		
		return null;
	}
	
	public CState nextStateWithSequence(CState current, String sequence) 
	{
		String symbols[] = sequence.split(",");
		if(sequence.equals("") || sequence.equals("EPSILON"))		//epsilon
			return current;
		
		CState curr = current;
		for(String symb : symbols) {
			if(! symb.equals("EPSILON"))
				curr = nextState(curr, symb);
		}
			
		return curr;
	}

	public ArrayList<CTransition> reachedTransitionsWithSequence(CState current, String sequence) 
	{
		ArrayList<CTransition> ret = new ArrayList<CTransition>();
		
		String symbols[] = sequence.split(",");
		if(sequence.equals("") || sequence.equals("EPSILON"))		//epsilon
			return ret;
		
		CState curr = current;
		for(String symb : symbols) 
		{
			if(! symb.equals("EPSILON"))
			{
				for(CTransition ot : curr.getOut()) 
				{
					if(ot.getInput().equals(symb))
					{
						curr = ot.getOut();
						ret.add(ot);
						break;
					}
				}	
			}
		}
			
		return ret;
	}	
	
	public String nextOutput(CState current, String sequence) 
	{
		String symbols[] = sequence.split(",");
		if(sequence.equals("") || sequence.equals("EPSILON"))		//epsilon
			return "";
		
		String out = "";
		CState curr = current;
		for(String symb : symbols) {
			if(out.equals(""))
				out = nextStateOut(curr, symb);
			else
				out = out + "," + nextStateOut(curr, symb);
			
			curr = nextState(curr, symb);
		}
			
		return out;
	}

	public boolean isDefinedSeq(String x, CState state)
	{
		if(x.equals("EPSILON"))
			return true;
		
		String ins[] = x.split(",");
		CState current = state;
		for (int i = 0; i < ins.length; i++) 
		{	
			CState next = nextState(current, ins[i]);
			if(next == null)
				return false;
			
			current = next;
		}		
		return true;
	}	
	
	public boolean separe(String x, CState si, CState sj) 
	{
		if(x.equals("EPSILON"))
			return false;
		
		if((! isDefinedSeq(x, si)) || (! isDefinedSeq(x, sj)))
			return false;
		
		//separe
		String in[] = x.split(",");
		CState current_si = si;
		CState current_sj = sj;
		for (int i = 0; i < in.length; i++) 
		{
			if(! nextOutput(current_si, in[i]).equals(nextOutput(current_sj, in[i])))
                                    return true;
			
			current_si = nextState(current_si, in[i]);
			current_sj = nextState(current_sj, in[i]);	
			//if(current_si.equals(sj)){
			//	return false;
			//}
		}
		
		return false;
	}

	public String getIOSequence(String s) 
	{
		String in[] = s.split(",");
		String ret = "";
		CState current = getInitialState();
		for (int i = 0; i < in.length; i++) 
		{
			if("".equals(ret))
				ret += in[i] + "/";
			else
				ret += "," + in[i] + "/";

			if(nextStateOut(current, in[i]) == null)
				ret += "UNDEFINED";
			else
				ret += nextStateOut(current, in[i]);			
			
			current = nextState(current, in[i]);
		}
		return ret;
	}
	*/
}
