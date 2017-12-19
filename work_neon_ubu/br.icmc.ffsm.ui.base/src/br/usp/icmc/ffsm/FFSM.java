package br.usp.icmc.ffsm;

import java.util.ArrayList;
import java.util.HashSet;

public class FFSM {
	protected HashSet<String> inputAlphabet, outputAlphabet;
	private ArrayList<FState> fstates;
	private ArrayList<FTransition> ftransitions;	
	protected FState finitialState = null;

	public FFSM() {		
		ftransitions = new ArrayList<FTransition>();
		inputAlphabet = new HashSet<String>();
		outputAlphabet = new HashSet<String>();		
		fstates = new ArrayList<FState>();
	}	
	
	public int getNumberOfFStates() {
		return fstates.size();
	}
	
	public int getNumberOfFTransitions() {
		return ftransitions.size();
	}
	
	public FState getFInitialState() {
		return finitialState;
	}
	
	public void setFInitialState(FState finitialState) {
		this.finitialState = finitialState;
	}
	
	public HashSet<String> getOutputAlphabet() {
		return outputAlphabet;
	}
	
	public HashSet<String> getInputAlphabet() {
		return inputAlphabet;
	}
	
	public void addFState(FState fst){
		int exist = 0;
		for(FState fs : fstates){
			if(fs.getStateName().equals(fst.getStateName())){				
				exist = 1;
				break;
			}
		}
		if(exist == 0){
			fstates.add(fst);
		}		
	}
	
	public void addFTransition(FTransition nt) {
		ftransitions.add(nt);
		inputAlphabet.add(nt.getCInput().getIn());
		outputAlphabet.add(nt.getOutput());	
		nt.getSource().getOut().add(nt);
		nt.getTarget().getIn().add(nt);
	}
		
	public ArrayList<FState> getFStates() {
		return fstates;
	}
	
	public ArrayList<FTransition> getFTransitions() {
		return ftransitions;
	}
	/*
	
	public FState nextState(FState current, CIn cinput) 
	{
		String inputsymbol = cinput.getIn();
		String condition = cinput.getCond();
		if(inputsymbol.equals("EPSILON"))
			return current;
		
		for(FTransition ot : current.getOut()) 
		{
			if(ot.getInput().equals(inputsymbol))
				return ot.getOut();
		}
		
		return null;
	}
	
	public String nextStateOut(FState current, CIn cinput) 
	{
		String inputsymbol = cinput.getIn();
		String condition = cinput.getCond();
		if(inputsymbol.equals("EPSILON"))
			return "";
		
		for(FTransition ot : current.getOut()) 
		{
			if(ot.getInput().equals(inputsymbol))
				return ot.getOutput();
		}
		
		return null;
	}
	
	public State nextStateWithSequence(State current, String sequence) 
	{
		String symbols[] = sequence.split(",");
		if(sequence.equals("") || sequence.equals("EPSILON"))		//epsilon
			return current;
		
		State curr = current;
		for(String symb : symbols) {
			if(! symb.equals("EPSILON"))
				curr = nextState(curr, symb);
		}
			
		return curr;
	}

	public ArrayList<Transition> reachedTransitionsWithSequence(State current, String sequence) 
	{
		ArrayList<Transition> ret = new ArrayList<Transition>();
		
		String symbols[] = sequence.split(",");
		if(sequence.equals("") || sequence.equals("EPSILON"))		//epsilon
			return ret;
		
		State curr = current;
		for(String symb : symbols) 
		{
			if(! symb.equals("EPSILON"))
			{
				for(Transition ot : curr.getOut()) 
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
	
	public String nextOutput(State current, String sequence) 
	{
		String symbols[] = sequence.split(",");
		if(sequence.equals("") || sequence.equals("EPSILON"))		//epsilon
			return "";
		
		String out = "";
		State curr = current;
		for(String symb : symbols) {
			if(out.equals(""))
				out = nextStateOut(curr, symb);
			else
				out = out + "," + nextStateOut(curr, symb);
			
			curr = nextState(curr, symb);
		}
			
		return out;
	}

	public boolean isDefinedSeq(String x, State state)
	{
		if(x.equals("EPSILON"))
			return true;
		
		String ins[] = x.split(",");
		State current = state;
		for (int i = 0; i < ins.length; i++) 
		{	
			State next = nextState(current, ins[i]);
			if(next == null)
				return false;
			
			current = next;
		}		
		return true;
	}	
	
	public boolean separe(String x, State si, State sj) 
	{
		if(x.equals("EPSILON"))
			return false;
		
		if((! isDefinedSeq(x, si)) || (! isDefinedSeq(x, sj)))
			return false;
		
		//separe
		String in[] = x.split(",");
		State current_si = si;
		State current_sj = sj;
		for (int i = 0; i < in.length; i++) 
		{
			if(! nextOutput(current_si, in[i]).equals(nextOutput(current_sj, in[i])))
                                    return true;
			
			current_si = nextState(current_si, in[i]);
			current_sj = nextState(current_sj, in[i]);			
		}
		
		return false;
	}
*/

}
