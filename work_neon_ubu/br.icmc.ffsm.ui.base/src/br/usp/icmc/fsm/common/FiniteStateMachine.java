package br.usp.icmc.fsm.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

public class FiniteStateMachine 
{
	protected HashSet<String> inputAlphabet, outputAlphabet;
	protected ArrayList<State> states;
	protected ArrayList<Transition> transitions;
	protected State initialState = null;
	
	public FiniteStateMachine() {
		states = new ArrayList<State>();
		transitions = new ArrayList<Transition>();
		inputAlphabet = new HashSet<String>();
		outputAlphabet = new HashSet<String>();
	}
	
	public State getInitialState() {
		return initialState;
	}
	
	public void setInitialState(State initialState) {
		this.initialState = initialState;
	}
	
	public int getNumberOfStates() {
		return states.size();
	}
	
	public int getNumberOfTransitions() {
		return transitions.size();
	}
	
	public void addState(State ns) {
		if(!states.contains(ns)){
			states.add(ns);
		}		
	}
	
	public void addTransition(Transition nt) {
		transitions.add(nt);
		inputAlphabet.add(nt.getInput());
		outputAlphabet.add(nt.getOutput());
	}

	public ArrayList<State> getStates() {
		return states;
	}
	
	public ArrayList<Transition> getTransitions() {
		return transitions;
	}

	public HashSet<String> getOutputAlphabet() {
		return outputAlphabet;
	}
	
	public HashSet<String> getInputAlphabet() {
		return inputAlphabet;
	}
	
	public State nextState(State current, String inputsymbol) 
	{
		if(inputsymbol.equals("EPSILON"))
			return current;
		
		for(Transition ot : current.getOut()) 
		{
			if(ot.getInput().equals(inputsymbol))
				return ot.getOut();
		}
		
		return null;
	}
	
	public String nextStateOut(State current, String inputsymbol) 
	{
		if(inputsymbol.equals("EPSILON"))
			return "";
		
		for(Transition ot : current.getOut()) 
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
		
		State curr = current;
		if(symbols.length > 0){
			String out = nextStateOut(curr, symbols[0]);
			curr = nextState(curr, symbols[0]);
			for(int i=1; i< symbols.length; i++){				
				if(curr != null){
					out = out + "," + nextStateOut(curr, symbols[i]);
					curr = nextState(curr, symbols[i]);
				}
			}
			return out;
		}else return "";	
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

	public String getIOSequence(String s) 
	{
		String in[] = s.split(",");
		String ret = "";
		State current = getInitialState();
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
	
	public void print_file(String out, String path) throws IOException {
		File file3 = new File(path);
		if (!file3.exists()) {
			file3.createNewFile();
		}
		FileWriter w = new FileWriter(file3);
		w.write(out);
		w.close();
	}
	
	public String getProcessOutput(String[] commands) throws IOException, InterruptedException
    {
        ProcessBuilder processBuilder = new ProcessBuilder(commands);

        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        StringBuilder processOutput = new StringBuilder();

        try (BufferedReader processOutputReader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));)
        {
            String readLine;

            while ((readLine = processOutputReader.readLine()) != null)
            {
                processOutput.append(readLine + System.lineSeparator());
            }

            process.waitFor();
        }

        return processOutput.toString().trim();
    }
}
