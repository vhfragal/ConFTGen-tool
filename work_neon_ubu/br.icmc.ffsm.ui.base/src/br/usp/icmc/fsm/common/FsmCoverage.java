package br.usp.icmc.fsm.common;

import java.util.ArrayList;

public class FsmCoverage 
{
	FiniteStateMachine fsm;
	public FsmCoverage(FiniteStateMachine fsm) {
		this.fsm = fsm;
	}
	
	public float transitionCoverage(ArrayList<String> testset)
	{
		float total = fsm.getNumberOfTransitions();
		ArrayList<Transition> covered = new ArrayList<Transition>();
		
		for(String test : testset)
		{
			ArrayList<Transition> current = fsm.reachedTransitionsWithSequence(fsm.getInitialState(), test);
			
			if(current.size() != 0)
			{
				Transition t = current.get(current.size() - 1); //get the last
				String x = t.getInput();
				String alpha = TestSequence.getPrefixFrom(test, x);
				if(! covered.contains(t) && testset.contains(alpha))
					covered.add(t);				
			}
		}
		
		return covered.size() / total;
	}
	
	public float transitionCoverage2(ArrayList<String> testset)
	{
		float total = fsm.getNumberOfTransitions();
		ArrayList<Transition> covered = new ArrayList<Transition>();
		
		for(String test : testset)
		{
			ArrayList<Transition> current = fsm.reachedTransitionsWithSequence(fsm.getInitialState(), test);
			
		/*	if(current.size() != 0)
			{
				Transition t = current.get(current.size() - 1); //get the last
				String x = t.getInput();
				String alpha = TestSequence.getPrefixFrom(test, x);
				if(! covered.contains(t) && testset.contains(alpha))
					covered.add(t);				
			}*/
			for(Transition t : current){
				String x = t.getInput();
				String alpha = TestSequence.getPrefixFrom(test, x);
				if(! covered.contains(t) && testset.contains(alpha))
					covered.add(t);
			}
		}
		
		return covered.size() / total;
	}

	public ArrayList<Transition> getNonCoveredtransitions(ArrayList<String> testset)
	{
		float total = fsm.getNumberOfTransitions();
		ArrayList<Transition> covered = new ArrayList<Transition>();
		ArrayList<Transition> uncovered = new ArrayList<Transition>();
		
		for(String test : testset)
		{
			ArrayList<Transition> current = fsm.reachedTransitionsWithSequence(fsm.getInitialState(), test);
			
			if(current.size() != 0)
			{
				Transition t = current.get(current.size() - 1); //get the last
				String x = t.getInput();
				String alpha = TestSequence.getPrefixFrom(test, x);
				if(! covered.contains(t) && testset.contains(alpha))
					covered.add(t);				
			}
			
			/*for(Transition t : current){
				String x = t.getInput();
				String alpha = TestSequence.getPrefixFrom(test, x);
				if(! covered.contains(t) && testset.contains(alpha))
					covered.add(t);
			}*/
		}
		for(Transition t : fsm.getTransitions())
		{
			if(! covered.contains(t))
				uncovered.add(t);
		}
		
		return uncovered;
	}
	
	public ArrayList<Transition> getNonCoveredtransitions2(ArrayList<String> testset)
	{
		float total = fsm.getNumberOfTransitions();
		ArrayList<Transition> covered = new ArrayList<Transition>();
		ArrayList<Transition> uncovered = new ArrayList<Transition>();
		
		for(String test : testset)
		{
			ArrayList<Transition> current = fsm.reachedTransitionsWithSequence(fsm.getInitialState(), test);
			if(test.equals("SM,F,CP,SM,CM")){
				System.out.println("current " + current);
			}
			if(current.size() != 0)
			{
				Transition t = current.get(current.size() - 1); //get the last
				String x = t.getInput();
				String alpha = TestSequence.getPrefixFrom(test, x);
				if(! covered.contains(t) && testset.contains(alpha))
					covered.add(t);	
				//for(Transition t : current){
				//	covered.add(t);
				//}
			}
		}
		for(Transition t : fsm.getTransitions())
		{
			if(! covered.contains(t))
				uncovered.add(t);
		}
		
		return uncovered;
	}

	public ArrayList<Transition> getCoveredtransitions(ArrayList<String> testset)
	{
		ArrayList<Transition> covered = new ArrayList<Transition>();
		
		for(String test : testset)
		{
			ArrayList<Transition> current = fsm.reachedTransitionsWithSequence(fsm.getInitialState(), test);

			for(Transition t : current)
			{
				if(! covered.contains(t))
					covered.add(t);				
			}
		}
		
		return covered;
	}	
	
	public boolean isInitializedTransitionCoverage(ArrayList<String> testset)
	{
		float tcoverage = transitionCoverage(testset);
		
		if(tcoverage == 1 && testset.contains("EPSILON"))
			return true;
		
		return false;
	}
	
	public boolean isInitializedTransitionCoverage3(ArrayList<String> testset)
	{
		float tcoverage = transitionCoverage2(testset);
		
		if(tcoverage == 1 && testset.contains("EPSILON"))
			return true;
		
		return false;
	}
	
	public boolean isInitializedTransitionCoverage2(ArrayList<String> testset, FsmCoverage coverage)
	{
		
		int total = fsm.getNumberOfTransitions();
		ArrayList<Transition> tr_co1 = coverage.getCoveredtransitions(testset);
		
		if(tr_co1.size()==total && testset.contains("EPSILON")){
			return true;
		}		
		
		return false;
	}
}
