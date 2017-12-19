package br.usp.icmc.fsm.common;

import java.util.ArrayList;

public class Pair 
{
	private String left, right;
	
	public Pair(String left, String right) {
		this.left = left;
		this.right = right;
	}
	
	public String getLeft() {
		return left;
	}
	
	public String getRight() {
		return right;
	}
	
	public String getShorter() 
	{
		if(TestSequence.lenght(left) <= TestSequence.lenght(right))
			return left;
		
		return right;
	}

	public String getlonger() 
	{
		if(TestSequence.lenght(left) > TestSequence.lenght(right))
			return left;
		
		return right;		
	}	
	
	@Override
	public String toString() {
		return "("+left+";"+right+")";
	}
	
	public static boolean add(ArrayList<Pair> pairs, Pair pair)
	{
		if(! Pair.in(pair, pairs))
		{
			pairs.add(pair);
			return true;
		}
		return false;
	}

	public static boolean in(Pair pair, ArrayList<Pair> pairs)
	{
		for(Pair p : pairs)
		{
			if(p.equals(pair))
				return true;
		}
		return false;
	}	
	
	private boolean equals(Pair pair) 
	{
		if( left.equals(pair.getLeft()) && right.equals(pair.getRight()) )
			return true;

		if( right.equals(pair.getLeft()) && left.equals(pair.getRight()) )
			return true;
		
		return false;
	}	
	
	public static ArrayList<String> getPartition(String alpha, ArrayList<Pair> C)
	{
		ArrayList<String> ret = new ArrayList<String>();
		
		for(Pair p : C)
		{
			if(p.contains(alpha))
			{
				ret.add(p.getNot(alpha));
			}
		}
		return ret;
	}

	private String getNot(String alpha) 
	{
		if(left.equals(alpha))
			return right;
		
		return left;
	}

	private boolean contains(String alpha) 
	{
		if(left.equals(alpha) || right.equals(alpha))
			return true;
		
		return false;
	}
}
