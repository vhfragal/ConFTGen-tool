package br.usp.icmc.fsm.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.usp.icmc.ffsm.Cond_in_seq;

public class TestSequence 
{
	public static String EPSILON = "EPSILON";
	
	public static int lenght(String sequence)
	{
		if(sequence.equals(EPSILON))
			return 0;
		else
		{
			return sequence.split(",").length;
		}
		
	}
	
	public static boolean isPrefixOf(String pref, String sequence)
	{
		if(pref.equals(EPSILON))
			return true;
		else if(sequence.equals(EPSILON))
			return false;
		else
		{
			String prefs[] = pref.split(",");
			String sequences[] = sequence.split(",");
			
			if(prefs.length > sequences.length)
				return false;
			
			for (int i = 0; i < prefs.length; i++) {
				if(! prefs[i].equals(sequences[i]))
					return false;
			}
			return true;
		}
	}
	
	public static boolean isProperPrefixOf(String pref, String sequence)
	{
		if(isPrefixOf(pref, sequence) && !pref.equals(sequence))
			return true;
		else
			return false;
	}	
	
	public static String getSuffixFrom(String seq, String pref)
	{
		if(pref.equals(EPSILON))
			return seq;
		else if(seq.equals(pref))
			return EPSILON;
		else
		{
			String prefs[] = pref.split(",");
			String sequences[] = seq.split(",");
			String ret = EPSILON;
			for(int i = prefs.length; i < sequences.length; i++)
				ret = TestSequence.concat(ret, sequences[i]);
			
			return ret;
		}
	}

	public static String getPrefixFrom(String seq, String sufix)
	{
		if(sufix.equals(EPSILON))
			return seq;
		else if(seq.equals(sufix))
			return TestSequence.EPSILON;
		else
		{
			String sufixes[] = sufix.split(",");
			String sequences[] = seq.split(",");
			String ret = EPSILON;
			for(int i = 0; i < (sequences.length - sufixes.length); i++)
				ret = TestSequence.concat(ret, sequences[i]);
			
			return ret;
		}
	}
	
	
	public static String concat(String a, String b)
	{
		if(a.equals(EPSILON))
			return b;
		
		if(b.equals(EPSILON))
			return a;
		
		return a + "," + b;
	}

	public static boolean isSufixOf(String sufix, String sequence) 
	{
		if(sufix.equals(EPSILON))
			return true;
		else if(sequence.equals(EPSILON))
			return false;
		else
		{
			String sufixes[] = sufix.split(",");
			String sequences[] = sequence.split(",");
			
			if(sufixes.length > sequences.length)
				return false;
			
			for(int i = sequences.length - 1, j = sufixes.length - 1; j >= 0; i--, j--)
			{
				if(! sequences[i].equals(sufixes[j]))
					return false;
			}
			
			return true;
		}
	}

	public static boolean isProperSufixOf(String sufix, String sequence)
	{
		if(isSufixOf(sufix, sequence) && !sufix.equals(sequence))
			return true;
		else
			return false;
	}
	
	public static ArrayList<String> getAllPrefixesFrom(String seq)
	{
		ArrayList<String> ret = new ArrayList<String>();
		if(seq.equals(EPSILON))
		{
			ret.add(EPSILON);
		}
		else
		{
			ret.add(EPSILON);
			String symbols[] = seq.split(",");
			String pref = symbols[0];
			ret.add(pref);
			for(int i = 1; i < symbols.length; i++)
			{
				pref = pref + "," + symbols[i];
				ret.add(pref);
			}
		}
		return ret;
	}

	public static ArrayList<String> getAllSuffixesFrom(String seq)
	{
		ArrayList<String> ret = new ArrayList<String>();
		if(seq.equals("EPSILON"))
		{
			ret.add("EPSILON");
		}
		else
		{
			ret.add("EPSILON");
			String symbols[] = seq.split(",");
			String pref = symbols[symbols.length - 1];
			ret.add(pref);
			for(int i = symbols.length - 2; i >= 0; i--)
			{
				pref = symbols[i] + "," + pref;
				ret.add(pref);
			}
		}
		
		return ret;
	}	
	
	public static ArrayList<String> getCommonSuffixesFrom(String left, String right) 
	{
		ArrayList<String> ret = new ArrayList<String>();
		
		if(left.equals("EPSILON") || right.equals("EPSILON"))
		{
			return ret;
		}
		
		for(String seq1 : getAllSuffixesFrom(left))
		{
			for(String seq2 : getAllSuffixesFrom(right))
			{
				if(seq1.equals(seq2))
					ret.add(seq1);
			}
		}
		
		return ret;
	}

	public static ArrayList<String> getNoPrefixes(ArrayList<String> finalTestSet) 
	{
		ArrayList<String> noPrefs = new ArrayList<String>();
		for(String test : finalTestSet)
		{
			boolean isPref = false;
			for(String s : finalTestSet)
			{
				if(isProperPrefixOf(test, s))
				{
					isPref = true;
					break;
				}
			}
			if(! isPref)
				noPrefs.add(test);
		}
		return noPrefs;
	}
	
	public static ArrayList<String> orderSet(ArrayList<String> finalTestSet) {
		int max = 100;
		ArrayList<String> order = new ArrayList<String>();
		HashMap<Integer, ArrayList<String>> size_table = new HashMap<Integer, ArrayList<String>>();
		for(int i=1; i<=max; i++){
			ArrayList<String> list = new ArrayList<String>();
			size_table.put(i, list);	
		}		
		for(String test : finalTestSet)	{
			int size = test.length();
			ArrayList<String> list = null;
			if(size_table.get(size) != null){
				list = size_table.get(size);				
			}else{
				list = new ArrayList<String>();
			}
			list.add(test);
			size_table.put(size, list);			
		}
		for(int i=1; i<=max; i++){
			if(size_table.get(i).size() > 0){
				order.addAll(size_table.get(i));
			}
		}
		for(int i : size_table.keySet()){
			if(i > max){
				order.addAll(size_table.get(i));
			}
		}
		return order;
	}
	
	public static ArrayList<Cond_in_seq> orderConditionalSet(ArrayList<Cond_in_seq> finalCTestSet) {
		int max = 100;
		ArrayList<Cond_in_seq> order = new ArrayList<Cond_in_seq>();
		HashMap<Integer, ArrayList<Cond_in_seq>> size_table = new HashMap<Integer, ArrayList<Cond_in_seq>>();
		for(int i=1; i<=max; i++){
			ArrayList<Cond_in_seq> list = new ArrayList<Cond_in_seq>();
			size_table.put(i, list);	
		}		
		for(Cond_in_seq test : finalCTestSet) {
			int size = test.getSequence().get(0).length();
			ArrayList<Cond_in_seq> list = null;
			if(size_table.get(size) != null){
				list = size_table.get(size);				
			}else{
				list = new ArrayList<Cond_in_seq>();
			}
			list.add(test);
			size_table.put(size, list);			
		}
		for(int i=1; i<=max; i++){
			if(size_table.get(i).size() > 0){
				order.addAll(size_table.get(i));
			}
		}
		for(int i : size_table.keySet()){
			if(i > max){
				order.addAll(size_table.get(i));
			}
		}
		return order;
	}
	
}
