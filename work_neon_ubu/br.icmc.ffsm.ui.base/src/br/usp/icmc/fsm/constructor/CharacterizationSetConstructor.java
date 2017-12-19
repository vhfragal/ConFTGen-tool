package br.usp.icmc.fsm.constructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import br.usp.icmc.fsm.common.CostTuple;
import br.usp.icmc.fsm.common.FiniteStateMachine;
import br.usp.icmc.fsm.common.HashList;
import br.usp.icmc.fsm.common.Pair;
import br.usp.icmc.fsm.common.State;
import br.usp.icmc.fsm.common.TestSequence;
import br.usp.icmc.fsm.common.TestSet;

public class CharacterizationSetConstructor 
{
	FiniteStateMachine fsm;
	ArrayList<String> wset;
	
	public void setFsm(FiniteStateMachine fsm) {
		this.fsm = fsm;
	}
	
	public CharacterizationSetConstructor() 
	{
	}
	
	public CharacterizationSetConstructor(FiniteStateMachine fsm) 
	{
		this.fsm = fsm;
		build();
	}

	public void build() 
	{
		wset = new ArrayList<String>();
		
		for(State si : fsm.getStates())
		{
			for(State sj : fsm.getStates())
			{
				if(si != sj)
				{
					String dseq = distiguishSeqN(si, sj);
					if(! wset.contains(dseq))
						wset.add(dseq);
				}
			}
		}
	}
	
	private String distiguishSeq(State si, State sj) 
	{
		int k = 1;
		int n = fsm.getNumberOfStates();
		
		while(k <= (n*(n-1)/2))
		{
			for(String x : getInputSeqWithLength(k))
			{
				if(fsm.separe(x, si, sj))
					return x;
			}
			k++;
		}
		
		if((k) > (n*(n-1)/2))
			System.out.println("ERROR: no minimal machine");
			
		return null;
	}

	private String distiguishSeqN(State si, State sj) 
	{
		int n = fsm.getNumberOfStates();
		ArrayList<String> queue = new ArrayList<String>();
		queue.add(TestSequence.EPSILON);
		
		while(! queue.isEmpty())
		{
			String seq = queue.remove(0);
			if(fsm.separe(seq, si, sj))
				return seq;
			
			if(TestSequence.lenght(seq) <= (n*(n-1)/2))
			{
				for(String x : fsm.getInputAlphabet())
				{
					String newseq = TestSequence.concat(seq, x);
					if(fsm.isDefinedSeq(newseq, si) && fsm.isDefinedSeq(newseq, sj))
					{
						queue.add(newseq);
					}
				}
			}
			else
			{
				System.out.println("ERROR: no minimal machine");
				break;
			}
		}
		return null;
	}
	
	public String getDistinguishSequence(State si, State sj, ArrayList<String> T)
	{
		return distiguishSeqN(si, sj);
	}
	
	
	public void initInputs(int k)
	{
		
	}
	
	public boolean hasNextInput()
	{
		return true;
	}
	
	public String nextInput()
	{
		return null;
	}
	
	public ArrayList<String> getInputSeqWithLength(int k)
	{
		ArrayList<String> ret = new ArrayList<String>();		
		HashSet<String> Li = fsm.getInputAlphabet();

		for(String in : Li)
			ret.add(in);
		
		for (int i = 2; i <= k; i++) 
		{
			ArrayList<String> temp = new ArrayList<String>();
			for(String seq : ret)
				for(String in : Li)
					temp.add(seq + "," + in);
				
			ret = temp;	
		}
		return ret;
	}	
		
	public ArrayList<String> getWset() 
	{
		return wset;
	}

	public CostTuple getDistinguishSequence(String alpha, String beta, ArrayList<String> T, ArrayList<Pair> C) 
	{
		State si = fsm.nextStateWithSequence(fsm.getInitialState(), alpha);
		State sj = fsm.nextStateWithSequence(fsm.getInitialState(), beta);	
		
		int n = fsm.getNumberOfStates();
		ArrayList<CostTuple> queue = new ArrayList<CostTuple>();

		for(String alp : Pair.getPartition(alpha, C))
		{
			for(String bet : Pair.getPartition(beta, C))
			{
				CostTuple tuple = new CostTuple();
				tuple.setGamma(TestSequence.EPSILON);
				tuple.setAlphacost(0);
				tuple.setBetacost(0);
				tuple.setAlpha(alp);
				tuple.setBeta(bet);
				
				queue.add(tuple);				
			}
		}

		CostTuple tuple = new CostTuple();
		tuple.setGamma(TestSequence.EPSILON);
		tuple.setAlphacost(0);
		tuple.setBetacost(0);
		tuple.setAlpha(alpha);
		tuple.setBeta(beta);
		
		queue.add(tuple);	
		
		while(! queue.isEmpty())
		{
			tuple = queue.remove(0);
			
			if(fsm.separe(tuple.getGamma(), si, sj))
				return tuple;
			else if(fsm.nextStateWithSequence(si, tuple.getGamma()) != fsm.nextStateWithSequence(sj, tuple.getGamma()))
			{
				for(String x : fsm.getInputAlphabet())
				{
					String newseq = TestSequence.concat(tuple.getGamma(), x);
					if(fsm.isDefinedSeq(newseq, si) && fsm.isDefinedSeq(newseq, sj) )
					{
						String alphagamma = TestSequence.concat(tuple.getAlpha(), tuple.getGamma());
						String betagamma = TestSequence.concat(tuple.getBeta(), tuple.getGamma());
						
						int alphax_cost = TestSet.calcCost(alphagamma, x, T, tuple.getAlphacost());
						int betax_cost = TestSet.calcCost(betagamma, x, T, tuple.getBetacost());
	
						CostTuple tuple1 = new CostTuple();
						tuple1.setGamma(newseq);
						tuple1.setAlpha(tuple.getAlpha());
						tuple1.setBeta(tuple.getBeta());
						tuple1.setAlphacost(alphax_cost);
						tuple1.setBetacost(betax_cost);
						
						int addcost = alphax_cost + betax_cost;
						int i = 0;
						
						for (; i < queue.size(); i++) 
						{
							CostTuple ti = queue.get(i);
							if(addcost <= (ti.getAlphacost() + ti.getBetacost()))
							{
								break;
							}
						}
						
						queue.add(i, tuple1);
						//queue.add(tuple1);
					}
				}
			}
		}
		return null;
	}
	
	public CostTuple getDistinguishSequence3(String alpha, String beta, ArrayList<String> T, Map<String, ArrayList<String>> C) 
	{
		State si = fsm.nextStateWithSequence(fsm.getInitialState(), alpha);
		State sj = fsm.nextStateWithSequence(fsm.getInitialState(), beta);	
		
		int n = fsm.getNumberOfStates();
		ArrayList<CostTuple> queue = new ArrayList<CostTuple>();
		
		for(String alp : HashList.getPartition(alpha, C))
		{
			for(String bet : HashList.getPartition(beta, C))
			{
				CostTuple tuple = new CostTuple();
				tuple.setGamma(TestSequence.EPSILON);
				tuple.setAlphacost(0);
				tuple.setBetacost(0);
				tuple.setAlpha(alp);
				tuple.setBeta(bet);
				
				queue.add(tuple);				
			}
		}

		CostTuple tuple = new CostTuple();
		tuple.setGamma(TestSequence.EPSILON);
		tuple.setAlphacost(0);
		tuple.setBetacost(0);
		tuple.setAlpha(alpha);
		tuple.setBeta(beta);
		
		queue.add(tuple);	
		
		while(! queue.isEmpty())
		{
			tuple = queue.remove(0);
			String alphagamma = TestSequence.concat(tuple.getAlpha(), tuple.getGamma());
			String betagamma = TestSequence.concat(tuple.getBeta(), tuple.getGamma());
			
			if(fsm.separe(tuple.getGamma(), si, sj) && (T.contains(alphagamma) && T.contains(betagamma)))
				return tuple;
			else if(fsm.nextStateWithSequence(si, tuple.getGamma()) != fsm.nextStateWithSequence(sj, tuple.getGamma()))
			{
				for(String x : fsm.getInputAlphabet())
				{
					String newseq = TestSequence.concat(tuple.getGamma(), x);
					if(fsm.isDefinedSeq(newseq, si) && fsm.isDefinedSeq(newseq, sj) )
					{
						//String alphagamma = TestSequence.concat(tuple.getAlpha(), tuple.getGamma());
						//String betagamma = TestSequence.concat(tuple.getBeta(), tuple.getGamma());
						
						int alphax_cost = TestSet.calcCost(alphagamma, x, T, tuple.getAlphacost());
						int betax_cost = TestSet.calcCost(betagamma, x, T, tuple.getBetacost());
	
						CostTuple tuple1 = new CostTuple();
						tuple1.setGamma(newseq);
						tuple1.setAlpha(tuple.getAlpha());
						tuple1.setBeta(tuple.getBeta());
						tuple1.setAlphacost(alphax_cost);
						tuple1.setBetacost(betax_cost);
						
						int addcost = alphax_cost + betax_cost;
						int i = 0;
						
						for (; i < queue.size(); i++) 
						{
							CostTuple ti = queue.get(i);
							if(addcost <= (ti.getAlphacost() + ti.getBetacost()))
							{
								break;
							}
						}
						
						queue.add(i, tuple1);
						//queue.add(tuple1);
					}
				}
			}
		}
		return null;
	}
	
	public ArrayList<CostTuple> getDistinguishSequence5(String alpha, String beta, ArrayList<String> T, Map<String, ArrayList<String>> C) 
	{
		State si = fsm.nextStateWithSequence(fsm.getInitialState(), alpha);
		State sj = fsm.nextStateWithSequence(fsm.getInitialState(), beta);	
		
		int n = fsm.getNumberOfStates();
		ArrayList<CostTuple> queue = new ArrayList<CostTuple>();		
		
		CostTuple tuple = new CostTuple();
		tuple.setGamma(TestSequence.EPSILON);
		tuple.setAlphacost(0);
		tuple.setBetacost(0);
		tuple.setAlpha(alpha);
		tuple.setBeta(beta);
		
		queue.add(tuple);
		int min_cost = 0;
		boolean not_found = true;
		ArrayList<CostTuple> all_min_cost = new ArrayList<CostTuple>();
		
		while(! queue.isEmpty())
		{
			tuple = queue.remove(0);
			System.out.println(" ");
			System.out.println("****************START***********");
			System.out.println("	TUPLE1 " + tuple.getAlpha() + " + "+ tuple.getGamma());
			System.out.println("	TUPLE2 " + tuple.getBeta() + " + "+ tuple.getGamma());
			if(fsm.separe(tuple.getGamma(), si, sj)){				
				System.out.println(" ");
				if(not_found){
					min_cost = tuple.getAlphacost() + tuple.getBetacost();
				}
				not_found = false;
				int this_cost = tuple.getAlphacost() + tuple.getBetacost();
				System.out.println("COST 	min	this	alpha	beta");
				System.out.println("COST 	" + min_cost + "	" + this_cost + "	" 
						+ tuple.getAlphacost() + "	" + tuple.getBetacost());
				if(this_cost > min_cost){
					if((this_cost - min_cost) >= 2){
						return all_min_cost;
					}else continue;
				}else{
					System.out.println("SEPARE");
					System.out.println("	SET1 " + tuple.getAlpha() + " + "+ tuple.getGamma());
					System.out.println("	SET2 " + tuple.getBeta() + " + "+ tuple.getGamma());
					System.out.println("****************END***************");
					System.out.println(" ");
					all_min_cost.add(tuple);
				}				
			}				
			else if(fsm.nextStateWithSequence(si, tuple.getGamma()) != fsm.nextStateWithSequence(sj, tuple.getGamma()))
			{
				for(String x : fsm.getInputAlphabet())
				{
					String newseq = TestSequence.concat(tuple.getGamma(), x);
					if(fsm.isDefinedSeq(newseq, si) && fsm.isDefinedSeq(newseq, sj) )
					{
						String alphagamma = TestSequence.concat(tuple.getAlpha(), tuple.getGamma());
						String betagamma = TestSequence.concat(tuple.getBeta(), tuple.getGamma());
						
						int alphax_cost = TestSet.calcCost(alphagamma, x, T, tuple.getAlphacost());
						int betax_cost = TestSet.calcCost(betagamma, x, T, tuple.getBetacost());
	
						CostTuple tuple1 = new CostTuple();
						tuple1.setGamma(newseq);
						tuple1.setAlpha(tuple.getAlpha());
						tuple1.setBeta(tuple.getBeta());
						tuple1.setAlphacost(alphax_cost);
						tuple1.setBetacost(betax_cost);
						
						int addcost = alphax_cost + betax_cost;
						int i = 0;
						
						for (; i < queue.size(); i++) 
						{
							CostTuple ti = queue.get(i);
							if(addcost <= (ti.getAlphacost() + ti.getBetacost()))
							{
								break;
							}
						}						
						queue.add(i, tuple1);						
					}
				}
			}
		}
		return all_min_cost;
	}
	
	public ArrayList<CostTuple> getDistinguishSequence4(String alpha, String beta, ArrayList<String> T, Map<String, ArrayList<String>> C) 
	{
		State si = fsm.nextStateWithSequence(fsm.getInitialState(), alpha);
		State sj = fsm.nextStateWithSequence(fsm.getInitialState(), beta);	
		
		ArrayList<CostTuple> queue = new ArrayList<CostTuple>();
		
		CostTuple tuple = new CostTuple();
		tuple.setGamma(TestSequence.EPSILON);
		tuple.setAlphacost(0);
		tuple.setBetacost(0);
		tuple.setAlpha(alpha);
		tuple.setBeta(beta);
		
		queue.add(tuple);			
		int min_cost = 0;
		boolean not_found = true;		
		ArrayList<CostTuple> eq_tuples = new ArrayList<CostTuple>();
		
		while(! queue.isEmpty())
		{
			tuple = queue.remove(0);
			
			if(fsm.separe(tuple.getGamma(), si, sj)){				
				if(not_found){
					min_cost = tuple.getAlphacost() + tuple.getBetacost();
					not_found = false;					
					eq_tuples.add(tuple);
					//print_trace(tuple, min_cost, min_cost, T);					
					continue;
				}				
				int this_cost = tuple.getAlphacost() + tuple.getBetacost();
				//print_trace(tuple, min_cost, this_cost, T);
				
				if(this_cost == min_cost){									
					eq_tuples.add(tuple);
				}
				if(this_cost < min_cost){
					min_cost = this_cost;					
					eq_tuples.add(0,tuple);
				}				
				
				String[] data = tuple.getGamma().split(",");
				int size_g = data.length;				
				if(size_g >= 3){
					return eq_tuples;
				}else continue;				
			}			
			else if(fsm.nextStateWithSequence(si, tuple.getGamma()) != fsm.nextStateWithSequence(sj, tuple.getGamma()))
			{
				for(String x : fsm.getInputAlphabet())
				{
					String newseq = TestSequence.concat(tuple.getGamma(), x);
					if(fsm.isDefinedSeq(newseq, si) && fsm.isDefinedSeq(newseq, sj) )
					{
						String alphagamma = TestSequence.concat(tuple.getAlpha(), tuple.getGamma());
						String betagamma = TestSequence.concat(tuple.getBeta(), tuple.getGamma());
						
						int alphax_cost = TestSet.calcCost2(alphagamma, x, T, tuple.getAlphacost());
						int betax_cost = TestSet.calcCost2(betagamma, x, T, tuple.getBetacost());
	
						CostTuple tuple1 = new CostTuple();
						tuple1.setGamma(newseq);
						tuple1.setAlpha(tuple.getAlpha());
						tuple1.setBeta(tuple.getBeta());
						tuple1.setAlphacost(alphax_cost);
						tuple1.setBetacost(betax_cost);
						
						int addcost = alphax_cost + betax_cost;
						int i = 0;
						
						for (; i < queue.size(); i++) 
						{
							CostTuple ti = queue.get(i);
							if(addcost <= (ti.getAlphacost() + ti.getBetacost()))
							{
								break;
							}
						}						
						queue.add(i, tuple1);						
					}
				}
			}
		}
		return eq_tuples;
	}
	
	public ArrayList<CostTuple> getDistinguishSequence6(String alpha, String beta, ArrayList<String> T,
			Map<String, ArrayList<String>> C) {
		State si = fsm.nextStateWithSequence(fsm.getInitialState(), alpha);
		State sj = fsm.nextStateWithSequence(fsm.getInitialState(), beta);	
		
		int n = fsm.getNumberOfStates();
		ArrayList<CostTuple> queue = new ArrayList<CostTuple>();
		
		for(String alp : HashList.getPartition(alpha, C))
		{
			for(String bet : HashList.getPartition(beta, C))
			{
				CostTuple tuple = new CostTuple();
				tuple.setGamma(TestSequence.EPSILON);
				tuple.setAlphacost(0);
				tuple.setBetacost(0);
				tuple.setAlpha(alp);
				tuple.setBeta(bet);
				
				queue.add(tuple);				
			}
		}

		CostTuple tuple = new CostTuple();
		tuple.setGamma(TestSequence.EPSILON);
		tuple.setAlphacost(0);
		tuple.setBetacost(0);
		tuple.setAlpha(alpha);
		tuple.setBeta(beta);
		
		queue.add(tuple);			
		int min_cost = 0;
		boolean not_found = true;		
		ArrayList<CostTuple> eq_tuples = new ArrayList<CostTuple>();
		
		while(! queue.isEmpty())
		{
			tuple = queue.remove(0);
			
			if(fsm.separe(tuple.getGamma(), si, sj)){				
				if(not_found){
					min_cost = tuple.getAlphacost() + tuple.getBetacost();
					not_found = false;					
					eq_tuples.add(tuple);
					//print_trace(tuple, min_cost, min_cost, T);
					//if(min_cost == 0) return eq_tuples;
					//else continue;
					continue;
				}				
				int this_cost = tuple.getAlphacost() + tuple.getBetacost();
				//print_trace(tuple, min_cost, this_cost, T);
				/*	
				if(this_cost == 0){
					eq_tuples.clear();
					eq_tuples.add(tuple);
					return eq_tuples;
				}*/
				if(this_cost == min_cost){
					//min_cost = this_cost;					
					eq_tuples.add(tuple);
				}
				if(this_cost < min_cost){
					min_cost = this_cost;
					//eq_tuples.clear();
					eq_tuples.add(0,tuple);
				}				
				
				String[] data = tuple.getGamma().split(",");
				int size_g = data.length;				
				if(size_g >= 3 && eq_tuples.size()>0){
					return eq_tuples;
				}else continue;
				/*
				if(this_cost > min_cost){
					if((this_cost - min_cost) >= 3){
						return eq_tuples;
					}else continue;
				}	*/		
			}			
			else if(fsm.nextStateWithSequence(si, tuple.getGamma()) != fsm.nextStateWithSequence(sj, tuple.getGamma()))
			{
				for(String x : fsm.getInputAlphabet())
				{
					String newseq = TestSequence.concat(tuple.getGamma(), x);
					if(fsm.isDefinedSeq(newseq, si) && fsm.isDefinedSeq(newseq, sj) )
					{
						String alphagamma = TestSequence.concat(tuple.getAlpha(), tuple.getGamma());
						String betagamma = TestSequence.concat(tuple.getBeta(), tuple.getGamma());
						
						int alphax_cost = TestSet.calcCost(alphagamma, x, T, tuple.getAlphacost());
						int betax_cost = TestSet.calcCost(betagamma, x, T, tuple.getBetacost());
	
						CostTuple tuple1 = new CostTuple();
						tuple1.setGamma(newseq);
						tuple1.setAlpha(tuple.getAlpha());
						tuple1.setBeta(tuple.getBeta());
						tuple1.setAlphacost(alphax_cost);
						tuple1.setBetacost(betax_cost);
						
						int addcost = alphax_cost + betax_cost;
						int i = 0;
						
						for (; i < queue.size(); i++) 
						{
							CostTuple ti = queue.get(i);
							if(addcost <= (ti.getAlphacost() + ti.getBetacost()))
							{
								break;
							}
						}						
						queue.add(i, tuple1);						
					}
				}
			}
		}
		return eq_tuples;
	}
	
	public void print_trace(CostTuple tuple, int min_cost, int this_cost, ArrayList<String> T){
		System.out.println(" ");
		System.out.println("COST 	min	this	alpha	beta	a	b	g");
		System.out.println("COST 	" + min_cost + "	" + this_cost + "	" 
				+ tuple.getAlphacost() + "	" + tuple.getBetacost()+"	"+tuple.getAlpha()
				+"	"+tuple.getBeta()+"	"+tuple.getGamma());
		System.out.println("*******	T	*********** ");
		for(String t : TestSequence.getNoPrefixes(T)){
			System.out.println(t);
		}System.out.println("*******	T END	***********");
		
	}
	
	public CostTuple getDistinguishSequence2(String alpha, String beta, ArrayList<String> T, Map<String, ArrayList<String>> C) 
	{
		State si = fsm.nextStateWithSequence(fsm.getInitialState(), alpha);
		State sj = fsm.nextStateWithSequence(fsm.getInitialState(), beta);	
		
		int n = fsm.getNumberOfStates();
		ArrayList<CostTuple> queue = new ArrayList<CostTuple>();
		
		for(String alp : HashList.getPartition(alpha, C))
		{
			for(String bet : HashList.getPartition(beta, C))
			{
				CostTuple tuple = new CostTuple();
				tuple.setGamma(TestSequence.EPSILON);
				tuple.setAlphacost(0);
				tuple.setBetacost(0);
				tuple.setAlpha(alp);
				tuple.setBeta(bet);
				
				queue.add(tuple);				
			}
		}

		CostTuple tuple = new CostTuple();
		tuple.setGamma(TestSequence.EPSILON);
		tuple.setAlphacost(0);
		tuple.setBetacost(0);
		tuple.setAlpha(alpha);
		tuple.setBeta(beta);
		
		queue.add(tuple);	
		ArrayList<String> separe = new ArrayList<String>();
		int min_cost = 0;
		boolean not_found = true;
		CostTuple min_tuple = new CostTuple();
		
		while(! queue.isEmpty())
		{
			tuple = queue.remove(0);
			
			if(fsm.separe(tuple.getGamma(), si, sj))
				return tuple;
			/*if(fsm.separe(tuple.getGamma(), si, sj)){				
				System.out.println(" ");
				if(not_found){
					min_cost = tuple.getAlphacost() + tuple.getBetacost();
					not_found = false;
					min_tuple = tuple;
				}				
				int this_cost = tuple.getAlphacost() + tuple.getBetacost();
				System.out.println("COST 	min	this	alpha	beta	a	b	g");
				System.out.println("COST 	" + min_cost + "	" + this_cost + "	" 
						+ tuple.getAlphacost() + "	" + tuple.getBetacost()+"	"+tuple.getAlpha()
						+"	"+tuple.getBeta()+"	"+tuple.getGamma());
				System.out.println("T SET ");
				for(String t : TestSequence.getNoPrefixes(T)){
					System.out.println(t);
				}System.out.println("T SET END ");
						
				if(this_cost < min_cost){
					min_cost = this_cost;
					min_tuple = tuple;
				}
				if(this_cost == min_cost){
					min_cost = this_cost;
					min_tuple = tuple;
				}
				//if(queue.isEmpty()) return min_tuple;
				//System.out.println("WHA!? ");
				if(this_cost > min_cost){
					if((this_cost - min_cost) >= 2){
						return min_tuple;
					}else continue;
				}			
			}		*/	
			else if(fsm.nextStateWithSequence(si, tuple.getGamma()) != fsm.nextStateWithSequence(sj, tuple.getGamma()))
			{
				for(String x : fsm.getInputAlphabet())
				{
					String newseq = TestSequence.concat(tuple.getGamma(), x);
					if(fsm.isDefinedSeq(newseq, si) && fsm.isDefinedSeq(newseq, sj) )
					{
						String alphagamma = TestSequence.concat(tuple.getAlpha(), tuple.getGamma());
						String betagamma = TestSequence.concat(tuple.getBeta(), tuple.getGamma());
						
						int alphax_cost = TestSet.calcCost(alphagamma, x, T, tuple.getAlphacost());
						int betax_cost = TestSet.calcCost(betagamma, x, T, tuple.getBetacost());
	
						CostTuple tuple1 = new CostTuple();
						tuple1.setGamma(newseq);
						tuple1.setAlpha(tuple.getAlpha());
						tuple1.setBeta(tuple.getBeta());
						tuple1.setAlphacost(alphax_cost);
						tuple1.setBetacost(betax_cost);
						
						int addcost = alphax_cost + betax_cost;
						int i = 0;
						
						for (; i < queue.size(); i++) 
						{
							CostTuple ti = queue.get(i);
							if(addcost <= (ti.getAlphacost() + ti.getBetacost()))
							{
								break;
							}
						}
						
						queue.add(i, tuple1);
						//queue.add(tuple1);
					}
				}
			}
		}
		return min_tuple;
	}
	
	public CostTuple getDistinguishSequence3(String alpha, String beta, 
			ArrayList<String> T, ArrayList<String> T4, 
			Map<String, ArrayList<String>> C) 
	{
		State si = fsm.nextStateWithSequence(fsm.getInitialState(), alpha);
		State sj = fsm.nextStateWithSequence(fsm.getInitialState(), beta);	
		
		int n = fsm.getNumberOfStates();
		ArrayList<CostTuple> queue = new ArrayList<CostTuple>();
		
		for(String alp : HashList.getPartition(alpha, C))
		{
			for(String bet : HashList.getPartition(beta, C))
			{
				CostTuple tuple = new CostTuple();
				tuple.setGamma(TestSequence.EPSILON);
				tuple.setAlphacost(0);
				tuple.setBetacost(0);
				tuple.setAlpha(alp);
				tuple.setBeta(bet);
				
				queue.add(tuple);				
			}
		}

		CostTuple tuple = new CostTuple();
		tuple.setGamma(TestSequence.EPSILON);
		tuple.setAlphacost(0);
		tuple.setBetacost(0);
		tuple.setAlpha(alpha);
		tuple.setBeta(beta);
		
		queue.add(tuple);	
		
		while(! queue.isEmpty())
		{
			tuple = queue.remove(0);
			
			if(fsm.separe(tuple.getGamma(), si, sj) 
					&& T4.contains(TestSequence.concat(tuple.getAlpha(),tuple.getGamma()))
					&& T4.contains(TestSequence.concat(tuple.getBeta(),tuple.getGamma()))){				
				return tuple;
			}
			else if(fsm.nextStateWithSequence(si, tuple.getGamma()) != fsm.nextStateWithSequence(sj, tuple.getGamma()))
			{
				for(String x : fsm.getInputAlphabet())
				{
					String newseq = TestSequence.concat(tuple.getGamma(), x);
					if(fsm.isDefinedSeq(newseq, si) && fsm.isDefinedSeq(newseq, sj) )
					{
						String alphagamma = TestSequence.concat(tuple.getAlpha(), tuple.getGamma());
						String betagamma = TestSequence.concat(tuple.getBeta(), tuple.getGamma());
						
						int alphax_cost = TestSet.calcCost(alphagamma, x, T, tuple.getAlphacost());
						int betax_cost = TestSet.calcCost(betagamma, x, T, tuple.getBetacost());
	
						CostTuple tuple1 = new CostTuple();
						tuple1.setGamma(newseq);
						tuple1.setAlpha(tuple.getAlpha());
						tuple1.setBeta(tuple.getBeta());
						tuple1.setAlphacost(alphax_cost);
						tuple1.setBetacost(betax_cost);
						
						int addcost = alphax_cost + betax_cost;
						int i = 0;
						
						for (; i < queue.size(); i++) 
						{
							CostTuple ti = queue.get(i);
							if(addcost <= (ti.getAlphacost() + ti.getBetacost()))
							{
								break;
							}
						}
						
						queue.add(i, tuple1);
						//queue.add(tuple1);
					}
				}
			}
		}
		return null;
	}
}