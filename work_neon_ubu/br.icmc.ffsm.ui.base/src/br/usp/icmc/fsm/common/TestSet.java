package br.usp.icmc.fsm.common;

import java.util.ArrayList;

public class TestSet 
{
	static public void addNewTest(ArrayList<String> T, String newTest)
	{
		if(! T.contains(newTest))
			T.add(newTest);
	}
	
	public static void addAllPrefsOf(ArrayList<String> T, String newTest)
	{
		for(String test : TestSequence.getAllPrefixesFrom(newTest))
                {	
                    addNewTest(T, test);
                }
	}
	
	static public ArrayList<String> minus(ArrayList<String> set1, ArrayList<String> set2)
	{
		ArrayList<String> ret = new ArrayList<String>();
		
		for(String s1 : set1)
		{
			if(! set2.contains(s1))
				ret.add(s1);
		}
		
		return ret;
	}

	public static ArrayList<String> minus(ArrayList<String> set1, String seq) 
	{
		ArrayList<String> ret = new ArrayList<String>();
		
		for(String s1 : set1)
		{
			if(! seq.equals(s1))
				ret.add(s1);
		}
		
		return ret;
	}
	
	public static int size(ArrayList<String> set1)
	{
		int tam = set1.size(); //number of resets
		
		for(String test : set1)
			tam += TestSequence.lenght(test);
		
		return tam;
	}

	public static String getLongestSequence(ArrayList<String> T) 
	{
		String longest = T.get(0);
		int size = TestSequence.lenght(longest); 
		for(String s : T)
			if(TestSequence.lenght(s) > size)
			{
				longest = s;
				size = TestSequence.lenght(s);
			}
		
		return longest;
	}
	
	public static int calcCost0(String alpha, String alphax, ArrayList<String> T, int sumalpha)
	{
		if(! T.contains(alphax))
		{
			if(T.contains(alpha))
			{
				if(isMaximal(T, alpha))
				{
					return 1;
				}
				else
					return TestSequence.lenght(alpha) + 1 + 1;
			}
			else
				return sumalpha + 1;
		}
		else
		{
			return 0;
		}
	}
	
	
	public static int calcCost(String alpha, String x, ArrayList<String> T, int sumalpha)
	{
		String alphax = TestSequence.concat(alpha, x);
		if(! T.contains(alphax))
		{
			if(T.contains(alpha))
			{
				if(isMaximal(T, alpha))
				{
					return 1;
				}
				else
					return TestSequence.lenght(alpha) + 1 + 1;
			}
			else
				return sumalpha + 1;
		}
		else
		{
			return 0;
		}
	}
	
	public static int calcCost2(String alpha, String x, ArrayList<String> T, int sumalpha)
	{
		String alphax = TestSequence.concat(alpha, x);
		for(String test:T){
			if(TestSequence.isPrefixOf(alphax, test) || alphax.equals(test)){
				return 0;
			}else if(TestSequence.isPrefixOf(test, alphax)){
				return (TestSequence.lenght(alphax) - TestSequence.lenght(test));
			}
		}
		return TestSequence.lenght(alphax)+1;
	}

	private static boolean isMaximal(ArrayList<String> T, String alpha) 
	{
		for(String test : T)
			if(TestSequence.isProperPrefixOf(alpha, test))
				return false;
		
		return true;
	}
	
	
}
