package br.usp.icmc.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class TestSuiteReader 
{
	private File file;
	private ArrayList<String> testSuite;
	private boolean comma = false;
	
	public ArrayList<String> getTestSuite() 
	{
		return testSuite;
	}
	
	public TestSuiteReader(File file, boolean comma) 
	{
		this.comma = comma;
		this.file = file;
		read();
	}

	private void read() 
	{
		testSuite = new ArrayList<String>();
		
		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			while((line = reader.readLine()) != null)
			{
				line = line.trim();
				if(! line.equals(""))
				{
					if(! comma)
						addTest(line);
					else
						addTestComma(line);
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}		
	}

	private void addTestComma(String line) 
	{
		testSuite.add(line);
	}

	private void addTest(String line) 
	{
		//System.out.println(line);
		char inputs[] = line.toCharArray();
		String seq = String.valueOf(inputs[0]);
		for (int i = 1; i < inputs.length; i++) 
		{
			seq += "," + inputs[i];
		}
		//System.out.println(seq);
		testSuite.add(seq);
	}
}
