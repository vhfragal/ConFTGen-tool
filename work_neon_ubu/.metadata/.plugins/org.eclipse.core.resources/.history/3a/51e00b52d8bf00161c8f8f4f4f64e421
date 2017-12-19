package br.usp.icmc.fsm.common;

import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ruler 
{
	private static Logger logger = Logger.getAnonymousLogger();
	ArrayList<Pair> C, D;
	Map<String, ArrayList<String>> C2, D2;
	Map<String, ArrayList<String[]>> C3, D3;
	ArrayList<String> T;
	public enum AddedTo {C, D};
	ArrayList<Trigger> queue;
	private FiniteStateMachine fsm;
	
	public void setQueue(ArrayList<Trigger> queue) {
		this.queue = queue;
	}
	
	public static Logger getLogger() {
		return logger;
	}
	
	public ArrayList<Trigger> getQueue() {
		return queue;
	}
	
	public Ruler(ArrayList<Pair> C, ArrayList<Pair> D, ArrayList<String> T) 
	{
		logger.setLevel(Level.OFF);
		this.C = C;
		this.D = D;
		this.T = T;
		
		queue = new ArrayList<Trigger>();
	}
	
	public Ruler(Map<String, ArrayList<String>> C, Map<String, ArrayList<String>> D, ArrayList<String> T) 
	{
		logger.setLevel(Level.OFF);
		this.C2 = C;
		this.D2 = D;
		this.T = T;
		
		queue = new ArrayList<Trigger>();
	}
	
	public Ruler(Map<String, ArrayList<String>> C, Map<String, 
			ArrayList<String>> D, ArrayList<String> T, FiniteStateMachine fsm) 
	{
		logger.setLevel(Level.OFF);
		this.C2 = C;
		this.D2 = D;
		this.T = T;
		this.fsm = fsm;
		
		queue = new ArrayList<Trigger>();
	}
	
	public Ruler(Map<String, ArrayList<String>> C, Map<String, 
			ArrayList<String>> D, ArrayList<String> T, FiniteStateMachine fsm,
			Map<String, ArrayList<String[]>> C3, Map<String, ArrayList<String[]>> D3) 
	{
		logger.setLevel(Level.OFF);
		this.C2 = C;
		this.D2 = D;
		this.C3 = C3;
		this.D3 = D3;
		this.T = T;
		this.fsm = fsm;
		
		queue = new ArrayList<Trigger>();
	}
	
	public boolean in_T (ArrayList<String> T, String alpha, String beta){
		ArrayList<String> alpha_ext = getExtensionsFrom(T, alpha);
		ArrayList<String> beta_ext = getExtensionsFrom(T, beta);

		for (String gamma : getCommonSufix(alpha, beta, alpha_ext,
				beta_ext)) {
			State sa = fsm.nextStateWithSequence(
					fsm.getInitialState(), alpha);
			State sb = fsm.nextStateWithSequence(
					fsm.getInitialState(), beta);
			if (fsm.separe(gamma, sa, sb)) {							
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<String> getCommonSufix(String alpha, String beta,
			ArrayList<String> alphaExt, ArrayList<String> betaExt) {
		ArrayList<String> ret = new ArrayList<String>();

		for (String seq1 : alphaExt) {
			String gamma = TestSequence.getSuffixFrom(seq1, alpha);
			for (String seq2 : betaExt) {
				String gamma2 = TestSequence.getSuffixFrom(seq2, beta);
				if (gamma.equals(gamma2))
					ret.add(gamma);
			}
		}
		return ret;
	}

	public ArrayList<String> getExtensionsFrom(ArrayList<String> T, String alpha) {
		ArrayList<String> ret = new ArrayList<String>();

		for (String seq : T) {
			if (TestSequence.isProperPrefixOf(alpha, seq))
				ret.add(seq);
		}
		return ret;
	}

	public void applyRules(Pair newpair, AddedTo addedTo)
	{
		logger.info("applying rules");
		
		queue.clear();
		queue.add(new Trigger(newpair, addedTo));
		while(queue.size() >= 1)
		{
			Trigger current = queue.remove(0);
			Pair pair = current.getPair();
			
			if(current.getAddedto() == AddedTo.C)
			{
				//System.out.println("added to C");
				rule01(pair);
				rule02(pair);
				rule04(pair);
				rule07(pair);
				rule09(pair);
			}
			else if(current.getAddedto() == AddedTo.D)
			{
				//System.out.println("added to D");
				rule03(pair);
				rule05(pair);
				rule06(pair);
				rule08(pair);
				rule10(pair);
			}
		}
	}	
	
	public void applyRules2(Pair newpair, AddedTo addedTo)
	{
		logger.info("applying rules");
		
		queue.clear();
		queue.add(new Trigger(newpair, addedTo));
		while(queue.size() >= 1)
		{
			Trigger current = queue.remove(0);
			Pair pair = current.getPair();
			
			if(current.getAddedto() == AddedTo.C)
			{
				//System.out.println("added to C");
				rule01(pair);
				//rule02(pair);
				rule04(pair);
				rule07(pair);
				rule09(pair);
			}
			else if(current.getAddedto() == AddedTo.D)
			{
				//System.out.println("added to D");
				rule03(pair);
				rule05(pair);
				rule06(pair);
				rule08(pair);
				rule10(pair);
			}
		}
	}	
	
	public void applyRules5(Pair newpair, AddedTo addedTo)
	{
		logger.info("applying rules");
	//	boolean got = false;
		queue.clear();
		queue.add(new Trigger(newpair, addedTo));
		while(queue.size() >= 1)
		{
			Trigger current = queue.remove(0);
			Pair pair = current.getPair();
			
			if(current.getAddedto() == AddedTo.C)
			{
				//System.out.println("added to C");
				rule01c(pair);
				rule02c(pair);				
				rule04c(pair);				
				rule07c(pair);				
				rule09c(pair);				
			}
			else if(current.getAddedto() == AddedTo.D)
			{
				//System.out.println("added to D");
				rule03c(pair);				
				rule05c(pair);				
				rule06c(pair);				
				rule08c(pair);				
				rule10c(pair);				
			}
		}
	}
		
	public void applyRules3(Pair newpair, AddedTo addedTo)
	{
		logger.info("applying rules");
	//	boolean got = false;
		queue.clear();
		queue.add(new Trigger(newpair, addedTo));
		while(queue.size() >= 1)
		{
			Trigger current = queue.remove(0);
			Pair pair = current.getPair();
			
			if(current.getAddedto() == AddedTo.C)
			{
				//System.out.println("added to C");
				rule01a(pair);
				rule02a(pair);				
				rule04a(pair);				
				rule07a(pair);				
				rule09a(pair);				
			}
			else if(current.getAddedto() == AddedTo.D)
			{
				//System.out.println("added to D");
				rule03a(pair);				
				rule05a(pair);				
				rule06a(pair);				
				rule08a(pair);				
				rule10a(pair);				
			}
		}
	}
	
	public void applyRules3_old1(Pair newpair, AddedTo addedTo)
	{
		logger.info("applying rules");
		
		queue.clear();
		queue.add(new Trigger(newpair, addedTo));
		while(queue.size() >= 1)
		{
			Trigger current = queue.remove(0);
			Pair pair = current.getPair();
			
			if(current.getAddedto() == AddedTo.C)
			{
				//System.out.println("added to C");
				rule01a(pair);
				rule02a(pair);
				rule04a(pair);
				rule07a(pair);
				rule09a(pair);
			}
			else if(current.getAddedto() == AddedTo.D)
			{
				//System.out.println("added to D");
				rule03a(pair);
				rule05a(pair);
				rule06a(pair);
				rule08a(pair);
				rule10a(pair);
			}
		}
	}
	
	public void applyRules3_2(Pair newpair, AddedTo addedTo)
	{
		logger.info("applying rules");
		
		queue.clear();
		queue.add(new Trigger(newpair, addedTo));
		while(queue.size() >= 1)
		{
			Trigger current = queue.remove(0);
			Pair pair = current.getPair();
			
			if(current.getAddedto() == AddedTo.C)
			{
				//System.out.println("added to C");
				rule01a(pair);
				//rule02a(pair);
				rule04a(pair);
				rule07a(pair);
				rule09a(pair);
			}
			else if(current.getAddedto() == AddedTo.D)
			{
				//System.out.println("added to D");
				rule03a(pair);
				rule05a(pair);
				rule06a(pair);
				rule08a(pair);
				rule10a(pair);
			}
		}
	}
	
	public void applyRules4(Pair newpair, AddedTo addedTo)
	{
		logger.info("applying rules");
		
		queue.clear();
		queue.add(new Trigger(newpair, addedTo));
		while(queue.size() >= 1)
		{
			Trigger current = queue.remove(0);
			Pair pair = current.getPair();
			
			if(current.getAddedto() == AddedTo.C)
			{
				//System.out.println("added to C");
				rule01b(pair);
				rule02b(pair);
				rule04b(pair);
				rule07b(pair);
				rule09b(pair);
			}
			else if(current.getAddedto() == AddedTo.D)
			{
				//System.out.println("added to D");
				rule03b(pair);
				rule05b(pair);
				rule06b(pair);
				rule08b(pair);
				rule10b(pair);
			}
		}
	}

	public void rule01(Pair newpair)
	{
		ArrayList<Pair> temp = new ArrayList<Pair>();
		String alpha1 = newpair.getLeft(); 
		String beta1 = newpair.getRight();
		String alpha2 = newpair.getRight();
		String beta2 = newpair.getLeft();

		
		for(Pair pair : C)
		{
			if(pair.getLeft().equals(alpha1))
			{
				String chi = pair.getRight();
				temp.add(new Pair(beta1, chi));
			}
			else if(pair.getRight().equals(alpha1))
			{
				String chi = pair.getLeft();
				temp.add(new Pair(beta1, chi));
			}
			else if(pair.getLeft().equals(alpha2))
			{
				String chi = pair.getRight();
				temp.add(new Pair(beta2, chi));				
			}
			else if(pair.getRight().equals(alpha2))
			{
				String chi = pair.getLeft();
				temp.add(new Pair(beta2, chi));
			}
		}
		for(Pair p : temp)
		{
			if(Pair.add(C, p))
			{
				logger.info("Rule 01 " + p);
				Trigger t = new Trigger(p, AddedTo.C);
				queue.add(t);				
			}
		}
	}

	public void rule02(Pair newpair)
	{
		String alpha = newpair.getLeft();
		String beta = newpair.getRight();
		
		for(String fi1 : T)
		{
			if(TestSequence.isProperPrefixOf(alpha, fi1))
			{
				String fi1_1 = TestSequence.getSuffixFrom(fi1, alpha);
				
				for(String fi2 : T)
				{
					if(TestSequence.isProperPrefixOf(beta, fi2))
					{
						String fi2_2 = TestSequence.getSuffixFrom(fi2, beta);
						if(fi1_1.equals(fi2_2))
						{
							Pair p = new Pair(fi1, fi2);
							if(Pair.add(C, p))
							{
								logger.info("Rule 02 " + p);
								Trigger t = new Trigger(p, AddedTo.C);
								queue.add(t);								
							}
						}
					}
				}
			}
		}
	}
	
	public void rule03(Pair newpair)
	{
		ArrayList<String> gammas = TestSequence.getCommonSuffixesFrom(newpair.getLeft(), newpair.getRight());		
		gammas.remove("EPSILON");
		//System.out.println("pair: " + newpair);
		//System.out.println("gammas: " + gammas);
		for(String gamma : gammas)
		{
			String alpha_ = TestSequence.getPrefixFrom(newpair.getLeft(), gamma);
			String beta_ = TestSequence.getPrefixFrom(newpair.getRight(), gamma);
			//System.out.println("alpha_:" + alpha_);
			//System.out.println("beta_:" + beta_);
			Pair p = new Pair(alpha_, beta_);
			if(Pair.add(D, p))
			{
				logger.info("Rule 03 " + p);
				Trigger t = new Trigger(p, AddedTo.D);
				queue.add(t);
			}
		}
	}
	
	public void rule04(Pair newpair)
	{
		String alpha = newpair.getLeft();
		String beta = newpair.getRight();
		
		for(String chi : T)
		{
			if(Pair.in(new Pair(alpha, chi), D))
			{
				Pair p = new Pair(beta, chi);
				if(Pair.add(D, p))
				{
					logger.info("Rule 04 " + p);
					Trigger t = new Trigger(p, AddedTo.D);
					queue.add(t);					
				}
			}
			
			if(Pair.in(new Pair(beta, chi), D))
			{
				Pair p = new Pair(alpha, chi);
				if(Pair.add(D, p))
				{
					logger.info("Rule 04 " + p);
					Trigger t = new Trigger(p, AddedTo.D);
					queue.add(t);					
				}
			}			
		}
	}	

	public void rule05(Pair newpair)
	{
		String alpha = newpair.getLeft();
		String beta = newpair.getRight();
		
		for(String chi : T)
		{
			if(Pair.in(new Pair(alpha, chi), C))
			{
				Pair p = new Pair(beta, chi);
				if(Pair.add(D, p))
				{
					logger.info("Rule 05 " + p);
					Trigger t = new Trigger(p, AddedTo.D);
					queue.add(t);					
				}				
			}

			if(Pair.in(new Pair(beta, chi), C))
			{
				Pair p = new Pair(alpha, chi);
				if(Pair.add(D, p))
				{
					//logger.info("Rule 05 pair " + newpair);
					logger.info("Rule 05 " + p);
					Trigger t = new Trigger(p, AddedTo.D);
					queue.add(t);					
				}				
			}
		}
	}

	public void rule06(Pair newpair) 
	{
		String alpha = newpair.getLeft(); 
		String beta = newpair.getRight();
		
		if(TestSequence.isProperPrefixOf(beta, alpha))	//
		{
			String temp = alpha;
			alpha = beta;
			beta = temp;
		}
		
		if(TestSequence.isProperPrefixOf(alpha, beta))	//alpha < beta 
		{
			String fi = TestSequence.getSuffixFrom(beta, alpha);
			for(String fi2 : getPotency(fi))
			{
				Pair p = new Pair(alpha, TestSequence.concat(alpha, fi2));
				if(Pair.add(D, p))
				{
					logger.info("Rule 06 " + p);
					Trigger t = new Trigger(p, AddedTo.D);
					queue.add(t);					
				}				
			}
		}
	}

	public ArrayList<String> getPotency(String fi) 
	{
		ArrayList<String> ret = new ArrayList<String>();
		
		String fis[] = fi.split(",");
		for(int k = 2; k <= fis.length; k++)	//k possible divisions
		{
			String sep [] = new String[k];
			boolean canbe = false;
			for(int i = 0; i < k; i++)			
			{
				int seqlen = fis.length / k;
				if(seqlen * k == fis.length)	//the division is possible
				{
					canbe = true;
					sep[i] = "";
					for (int j = i * seqlen; j < (i * seqlen + seqlen); j++) 
					{
						sep[i] += "," + fis[j];
					}
					sep[i] = sep[i].replaceFirst(",", "");
				}
			}
			if(canbe)			//check if sequences are equal
			{
				boolean equals = true;
				for(int i = 1; i < k; i++)
				{
					if(! sep[i].equals(sep[i-1]))
					{
						equals = false;
						break;
					}
				}
				if(equals)
					ret.add(sep[0]);			
			}
		}
		
		return ret;
	}
	
	public void rule07(Pair newpair) 
	{
		String alpha = newpair.getShorter();
		String sequence = newpair.getlonger();
		
		ArrayList<Pair> temp = new ArrayList<Pair>();
		if(TestSequence.isProperPrefixOf(alpha, sequence))
		{
			String betagamma = TestSequence.getSuffixFrom(sequence, alpha);
			for(Pair pair : D)
			{
				if(pair.getLeft().equals(alpha) && TestSequence.isProperPrefixOf(alpha, pair.getRight()))
				{
					String gamma = TestSequence.getSuffixFrom(pair.getRight(), alpha);
					
					if(TestSequence.isProperSufixOf(gamma, betagamma))
					{
						String beta = TestSequence.getPrefixFrom(betagamma, gamma);
						temp.add(new Pair(alpha, TestSequence.concat(alpha, beta)));
					}
				}

				if(pair.getRight().equals(alpha) && TestSequence.isProperPrefixOf(alpha, pair.getLeft()))
				{
					String gamma = TestSequence.getSuffixFrom(pair.getLeft(), alpha);
					
					if(TestSequence.isProperSufixOf(gamma, betagamma))
					{
						String beta = TestSequence.getPrefixFrom(betagamma, gamma);
						temp.add(new Pair(alpha, TestSequence.concat(alpha, beta)));
					}
				}
			}
			for(Pair pair : temp)
			{
				if(Pair.add(D, pair))
				{
					logger.info("Rule 07 " + pair);
					Trigger t = new Trigger(pair, AddedTo.D);
					queue.add(t);					
				}				
			}
		}
	}
	
	public void rule08(Pair newpair) 
	{
		String alpha = newpair.getShorter(); 
		String seq = newpair.getlonger();
		
		if(TestSequence.isProperPrefixOf(alpha, seq))
		{
			String gamma = TestSequence.getSuffixFrom(seq, alpha);
			for(Pair pair : C)
			{
				if(pair.getLeft().equals(alpha) && TestSequence.isProperPrefixOf(alpha, pair.getRight()))
				{
					String betagamma = TestSequence.getSuffixFrom(pair.getRight(), alpha);
					if(TestSequence.isProperSufixOf(gamma, betagamma))
					{
						String beta = TestSequence.getPrefixFrom(betagamma, gamma);
						Pair p = new Pair(alpha, TestSequence.concat(alpha, beta));
						if(Pair.add(D, p))
						{
							logger.info("Rule 08 " + p);
							Trigger t = new Trigger(p, AddedTo.D);
							queue.add(t);					
						}										
					}
				}

				if(pair.getRight().equals(alpha) && TestSequence.isProperPrefixOf(alpha, pair.getLeft()))
				{
					String betagamma = TestSequence.getSuffixFrom(pair.getLeft(), alpha);
					if(TestSequence.isProperSufixOf(gamma, betagamma))
					{
						String beta = TestSequence.getPrefixFrom(betagamma, gamma);
						Pair p = new Pair(alpha, TestSequence.concat(alpha, beta));
						if(Pair.add(D, p))
						{
							logger.info("Rule 08 " + p);
							Trigger t = new Trigger(p, AddedTo.D);
							queue.add(t);					
						}										
					}
				}			
			}
		}
	}
	
	public void rule09(Pair newpair) 
	{
		String alpha = newpair.getShorter(); 
		String seq = newpair.getlonger();

		if(TestSequence.isProperPrefixOf(alpha, seq))
		{
			ArrayList<Pair> temp = new ArrayList<Pair>();
			String gamma = TestSequence.getSuffixFrom(seq, alpha);
			
			for(Pair pair : D)
			{
				if(TestSequence.isProperSufixOf(gamma, pair.getRight()))
				{
					String beta = TestSequence.getPrefixFrom(pair.getRight(), gamma);
					if(beta.equals(pair.getLeft()))
					{
						temp.add(new Pair(alpha, beta));
					}
				}
				
				if(TestSequence.isProperSufixOf(gamma, pair.getLeft()))
				{
					String beta = TestSequence.getPrefixFrom(pair.getLeft(), gamma);
					if(beta.equals(pair.getRight()))
					{
						temp.add(new Pair(alpha, beta));
					}
				}				
			}
			
			for(Pair pair : temp)
			{
				if(Pair.add(D, pair))
				{
					logger.info("Rule 09 " + pair);
					Trigger t = new Trigger(pair, AddedTo.D);
					queue.add(t);					
				}				
			}			
		}		
	}	
	
	public void rule10(Pair newpair) 
	{
		String beta = newpair.getShorter(); 
		String seq = newpair.getlonger();

		if(TestSequence.isProperPrefixOf(beta, seq))
		{
			String gamma = TestSequence.getSuffixFrom(seq, beta);
			
			for(Pair pair : C)
			{
				if(TestSequence.isProperSufixOf(gamma, pair.getRight()))
				{
					String alpha = TestSequence.getPrefixFrom(pair.getRight(), gamma);
					if(alpha.equals(pair.getLeft()))
					{
						Pair p = new Pair(alpha, beta);
						if(Pair.add(D, p))
						{
							logger.info("Rule 10 " + p);
							Trigger t = new Trigger(p, AddedTo.D);
							queue.add(t);					
						}										
					}
				}

				if(TestSequence.isProperSufixOf(gamma, pair.getLeft()))
				{
					String alpha = TestSequence.getPrefixFrom(pair.getLeft(), gamma);
					if(alpha.equals(pair.getRight()))
					{
						Pair p = new Pair(alpha, beta);
						if(Pair.add(D, p))
						{
							logger.info("Rule 10 " + p);
							Trigger t = new Trigger(p, AddedTo.D);
							queue.add(t);					
						}										
					}
				}			
			}
		}				
	}
	
	public void rule01a(Pair newpair)
	{
		ArrayList<Pair> temp = new ArrayList<Pair>();
		String alpha1 = newpair.getLeft(); 
		String beta1 = newpair.getRight();
		String alpha2 = newpair.getRight();
		String beta2 = newpair.getLeft();

		if(C2.get(alpha1)!=null){			
			for(String chi : C2.get(alpha1)){
				temp.add(new Pair(beta1, chi));
			}			
		}else if(C2.get(alpha2)!=null){
			for(String chi : C2.get(alpha2)){
				temp.add(new Pair(beta2, chi));
			}
		}		
		for(Pair p : temp)
		{
			if(HashList.add_pair_hash(C2, p.getLeft(), p.getRight()))
			{ 				
				logger.info("Rule 01 " + p);
				Trigger t = new Trigger(p, AddedTo.C);
				queue.add(t);				
			}
		}
	}

	public void rule02a(Pair newpair)
	{
		String alpha = newpair.getLeft();
		String beta = newpair.getRight();
		
		for(String fi1 : T)
		{
			if(TestSequence.isProperPrefixOf(alpha, fi1))
			{
				String fi1_1 = TestSequence.getSuffixFrom(fi1, alpha);
				
				for(String fi2 : T)
				{
					if(TestSequence.isProperPrefixOf(beta, fi2))
					{
						String fi2_2 = TestSequence.getSuffixFrom(fi2, beta);
						if(fi1_1.equals(fi2_2))
						{
							Pair p = new Pair(fi1, fi2);
							if(HashList.add_pair_hash(C2, p.getLeft(), p.getRight()))
							{								
								logger.info("Rule 02 " + p);
								Trigger t = new Trigger(p, AddedTo.C);
								queue.add(t);								
							}
						}
					}
				}
			}
		}
	}
	
	public void rule03a(Pair newpair)
	{
		ArrayList<String> gammas = TestSequence.getCommonSuffixesFrom(newpair.getLeft(), newpair.getRight());		
		gammas.remove("EPSILON");
		//System.out.println("pair: " + newpair);
		//System.out.println("gammas: " + gammas);
		for(String gamma : gammas)
		{
			String alpha_ = TestSequence.getPrefixFrom(newpair.getLeft(), gamma);
			String beta_ = TestSequence.getPrefixFrom(newpair.getRight(), gamma);
			//System.out.println("alpha_:" + alpha_);
			//System.out.println("beta_:" + beta_);
			Pair p = new Pair(alpha_, beta_);
			//if(in_T(T, alpha_, beta_)){
				if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
				{					
					logger.info("Rule 03 " + p);
					Trigger t = new Trigger(p, AddedTo.D);
					queue.add(t);					
				}
			//}			
		}
	}
	
	public void rule04a(Pair newpair)
	{
		String alpha = newpair.getLeft();
		String beta = newpair.getRight();
		
		for(String chi : T)
		{
			//if(Pair.in(new Pair(alpha, chi), D))
			if(D2.get(alpha)!=null) if(D2.get(alpha).contains(chi))	
			{
				Pair p = new Pair(beta, chi);
				//if(in_T(T, beta, chi)){
					if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
					{						
						logger.info("Rule 04 " + p);
						Trigger t = new Trigger(p, AddedTo.D);
						queue.add(t);						
					}					
				//}				
			}
			
			//if(Pair.in(new Pair(beta, chi), D))
			if(D2.get(beta)!=null) if(D2.get(beta).contains(chi))	
			{
				Pair p = new Pair(alpha, chi);
				//if(in_T(T, alpha, chi)){
					if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
					{						
						logger.info("Rule 04 " + p);
						Trigger t = new Trigger(p, AddedTo.D);
						queue.add(t);						
					}					
				//}
			}			
		}
	}	

	public void rule05a(Pair newpair)
	{
		String alpha = newpair.getLeft();
		String beta = newpair.getRight();
		
		for(String chi : T)
		{
			//if(Pair.in(new Pair(alpha, chi), C))
			if(C2.get(alpha)!=null) if(C2.get(alpha).contains(chi))	
			{
				Pair p = new Pair(beta, chi);
				//if(in_T(T, beta, chi)){
					if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
					{						
						logger.info("Rule 05 " + p);
						Trigger t = new Trigger(p, AddedTo.D);
						queue.add(t);					
					}
				//}								
			}

			//if(Pair.in(new Pair(beta, chi), C))
			if(C2.get(beta)!=null) if(C2.get(beta).contains(chi))	
			{
				Pair p = new Pair(alpha, chi);
				//if(in_T(T, alpha, chi)){
					if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
					{						
						logger.info("Rule 05 " + p);
						Trigger t = new Trigger(p, AddedTo.D);
						queue.add(t);						
					}	
				//}							
			}
		}
	}

	public void rule06a(Pair newpair) 
	{
		String alpha = newpair.getLeft(); 
		String beta = newpair.getRight();
		
		if(TestSequence.isProperPrefixOf(beta, alpha))	//
		{
			String temp = alpha;
			alpha = beta;
			beta = temp;
		}
		
		if(TestSequence.isProperPrefixOf(alpha, beta))	//alpha < beta 
		{
			String fi = TestSequence.getSuffixFrom(beta, alpha);
			for(String fi2 : getPotency(fi))
			{
				Pair p = new Pair(alpha, TestSequence.concat(alpha, fi2));
				//if(in_T(T, alpha, TestSequence.concat(alpha, fi2))){
					if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
					{						
						logger.info("Rule 06 " + p);
						Trigger t = new Trigger(p, AddedTo.D);
						queue.add(t);							
					}
				//}								
			}
		}
	}

		
	public void rule07a(Pair newpair) 
	{
		String alpha = newpair.getShorter();
		String sequence = newpair.getlonger();
		
		ArrayList<Pair> temp = new ArrayList<Pair>();
		if(TestSequence.isProperPrefixOf(alpha, sequence))
		{
			String betagamma = TestSequence.getSuffixFrom(sequence, alpha);
			if(D2.get(alpha)!=null){			
				for(String r : D2.get(alpha)){
					if(TestSequence.isProperPrefixOf(alpha, r)){
						String gamma = TestSequence.getSuffixFrom(r, alpha);
						
						if(TestSequence.isProperSufixOf(gamma, betagamma))
						{
							String beta = TestSequence.getPrefixFrom(betagamma, gamma);
							temp.add(new Pair(alpha, TestSequence.concat(alpha, beta)));
						}
					}
				}			
			}			
			for(Pair p : temp)
			{
				//if(in_T(T, p.getLeft(), p.getRight())){
					if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
					{						
						logger.info("Rule 07 " + p);
						Trigger t = new Trigger(p, AddedTo.D);
						queue.add(t);					
					}					
				//}			
			}
		}
	}
	
	public void rule08a(Pair newpair) 
	{
		String alpha = newpair.getShorter(); 
		String seq = newpair.getlonger();
		
		if(TestSequence.isProperPrefixOf(alpha, seq))
		{
			String gamma = TestSequence.getSuffixFrom(seq, alpha);
			if(C2.get(alpha)!=null){			
				for(String r : C2.get(alpha)){
					if(TestSequence.isProperPrefixOf(alpha, r)){
						String betagamma = TestSequence.getSuffixFrom(r, alpha);
						if(TestSequence.isProperSufixOf(gamma, betagamma))
						{
							String beta = TestSequence.getPrefixFrom(betagamma, gamma);
							Pair p = new Pair(alpha, TestSequence.concat(alpha, beta));
							//if(in_T(T, alpha, TestSequence.concat(alpha, beta))){
								if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
								{									
									logger.info("Rule 08 " + p);
									Trigger t = new Trigger(p, AddedTo.D);
									queue.add(t);									
								}
							//}																	
						}
					}
				}
			}			
		}
	}
	
	public void rule09a(Pair newpair) 
	{
		String alpha = newpair.getShorter(); 
		String seq = newpair.getlonger();

		if(TestSequence.isProperPrefixOf(alpha, seq))
		{
			ArrayList<Pair> temp = new ArrayList<Pair>();
			String gamma = TestSequence.getSuffixFrom(seq, alpha);
			
			for(String a : D2.keySet()){
				if(TestSequence.isProperSufixOf(gamma, a))
				{
					String beta = TestSequence.getPrefixFrom(a, gamma);
					for(String r : D2.get(a)){
						if(beta.equals(r))
						{
							temp.add(new Pair(alpha, beta));
						}
					}
				}				
			}			
			for(Pair p : temp)
			{
				//if(in_T(T, alpha, TestSequence.concat(p.getLeft(), p.getRight()))){
					if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
					{						
						logger.info("Rule 09 " + p);
						Trigger t = new Trigger(p, AddedTo.D);
						queue.add(t);					
					}					
				//}								
			}			
		}		
	}	
	
	public void rule10a(Pair newpair) 
	{
		String beta = newpair.getShorter(); 
		String seq = newpair.getlonger();

		if(TestSequence.isProperPrefixOf(beta, seq))
		{
			String gamma = TestSequence.getSuffixFrom(seq, beta);
						
			for(String a : C2.keySet()){
				if(TestSequence.isProperSufixOf(gamma, a))
				{
					String alpha = TestSequence.getPrefixFrom(a, gamma);
					for(String r : C2.get(a)){
						if(alpha.equals(r))
						{
							Pair p = new Pair(alpha, beta);
							//if(in_T(T, alpha, beta)){
								if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
								{									
									logger.info("Rule 10 " + p);
									Trigger t = new Trigger(p, AddedTo.D);
									queue.add(t);									
								}
							//}								
						}
					}
				}				
			}			
		}				
	}
	
	public void rule01c(Pair newpair)
	{
		ArrayList<Pair> temp = new ArrayList<Pair>();
		String alpha1 = newpair.getLeft(); 
		String beta1 = newpair.getRight();
		String alpha2 = newpair.getRight();
		String beta2 = newpair.getLeft();

		if(C2.get(alpha1)!=null){			
			for(String chi : C2.get(alpha1)){
				temp.add(new Pair(beta1, chi));
			}			
		}else if(C2.get(alpha2)!=null){
			for(String chi : C2.get(alpha2)){
				temp.add(new Pair(beta2, chi));
			}
		}		
		for(Pair p : temp)
		{
			if(HashList.add_pair_hash(C2, p.getLeft(), p.getRight()))
			{ 
				String[] s = new String[2];
				s[0] = p.toString();
				s[1] = "rule 1";				
				if(C3.keySet().contains(newpair.toString())){
					C3.get(newpair.toString()).add(s);
				}else{
					ArrayList<String[]> list = new ArrayList<String[]>();
					list.add(s);
					C3.put(newpair.toString(), list);
				}				
				logger.info("Rule 01 " + p);
				Trigger t = new Trigger(p, AddedTo.C);
				queue.add(t);				
			}
		}
	}

	public void rule02c(Pair newpair)
	{
		String alpha = newpair.getLeft();
		String beta = newpair.getRight();
		
		for(String fi1 : T)
		{
			if(TestSequence.isProperPrefixOf(alpha, fi1))
			{
				String fi1_1 = TestSequence.getSuffixFrom(fi1, alpha);
				
				for(String fi2 : T)
				{
					if(TestSequence.isProperPrefixOf(beta, fi2))
					{
						String fi2_2 = TestSequence.getSuffixFrom(fi2, beta);
						if(fi1_1.equals(fi2_2))
						{
							Pair p = new Pair(fi1, fi2);
							if(HashList.add_pair_hash(C2, p.getLeft(), p.getRight()))
							{
								String[] s = new String[2];
								s[0] = p.toString();
								s[1] = "rule 2";
								if(C3.keySet().contains(newpair.toString())){
									C3.get(newpair.toString()).add(s);
								}else{
									ArrayList<String[]> list = new ArrayList<String[]>();
									list.add(s);
									C3.put(newpair.toString(), list);
								}	
								logger.info("Rule 02 " + p);
								Trigger t = new Trigger(p, AddedTo.C);
								queue.add(t);								
							}
						}
					}
				}
			}
		}
	}
	
	public void rule03c(Pair newpair)
	{
		ArrayList<String> gammas = TestSequence.getCommonSuffixesFrom(newpair.getLeft(), newpair.getRight());		
		gammas.remove("EPSILON");
		//System.out.println("pair: " + newpair);
		//System.out.println("gammas: " + gammas);
		for(String gamma : gammas)
		{
			String alpha_ = TestSequence.getPrefixFrom(newpair.getLeft(), gamma);
			String beta_ = TestSequence.getPrefixFrom(newpair.getRight(), gamma);
			//System.out.println("alpha_:" + alpha_);
			//System.out.println("beta_:" + beta_);
			Pair p = new Pair(alpha_, beta_);
			//if(in_T(T, alpha_, beta_)){
				if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
				{
					String[] s = new String[2];
					s[0] = p.toString();
					s[1] = "rule 3";
					if(D3.keySet().contains(newpair.toString())){
						D3.get(newpair.toString()).add(s);
					}else{
						ArrayList<String[]> list = new ArrayList<String[]>();
						list.add(s);
						D3.put(newpair.toString(), list);
					}	
					System.out.println("RULE 3");
					logger.info("Rule 03 " + p);
					Trigger t = new Trigger(p, AddedTo.D);
					queue.add(t);					
				}
			//}			
		}
	}
	
	public void rule04c(Pair newpair)
	{
		String alpha = newpair.getLeft();
		String beta = newpair.getRight();
		
		for(String chi : T)
		{
			//if(Pair.in(new Pair(alpha, chi), D))
			if(D2.get(alpha)!=null) if(D2.get(alpha).contains(chi))	
			{
				Pair p = new Pair(beta, chi);
				//if(in_T(T, beta, chi)){
					if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
					{
						String[] s = new String[2];
						s[0] = p.toString();
						s[1] = "rule 4a";
						if(D3.keySet().contains(newpair.toString())){
							D3.get(newpair.toString()).add(s);
						}else{
							ArrayList<String[]> list = new ArrayList<String[]>();
							list.add(s);
							D3.put(newpair.toString(), list);
						}
						logger.info("Rule 04 " + p);
						Trigger t = new Trigger(p, AddedTo.D);
						queue.add(t);						
					}					
				//}				
			}
			
			//if(Pair.in(new Pair(beta, chi), D))
			if(D2.get(beta)!=null) if(D2.get(beta).contains(chi))	
			{
				Pair p = new Pair(alpha, chi);
				//if(in_T(T, alpha, chi)){
					if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
					{
						String[] s = new String[2];
						s[0] = p.toString();
						s[1] = "rule 4b";
						if(D3.keySet().contains(newpair.toString())){
							D3.get(newpair.toString()).add(s);
						}else{
							ArrayList<String[]> list = new ArrayList<String[]>();
							list.add(s);
							D3.put(newpair.toString(), list);
						}
						logger.info("Rule 04 " + p);
						Trigger t = new Trigger(p, AddedTo.D);
						queue.add(t);						
					}					
				//}
			}			
		}
	}	

	public void rule05c(Pair newpair)
	{
		String alpha = newpair.getLeft();
		String beta = newpair.getRight();
		
		for(String chi : T)
		{
			//if(Pair.in(new Pair(alpha, chi), C))
			if(C2.get(alpha)!=null) if(C2.get(alpha).contains(chi))	
			{
				Pair p = new Pair(beta, chi);
				//if(in_T(T, beta, chi)){
					if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
					{
						String[] s = new String[2];
						s[0] = p.toString();
						s[1] = "rule 5a";
						if(D3.keySet().contains(newpair.toString())){
							D3.get(newpair.toString()).add(s);
						}else{
							ArrayList<String[]> list = new ArrayList<String[]>();
							list.add(s);
							D3.put(newpair.toString(), list);
						}
						logger.info("Rule 05 " + p);
						Trigger t = new Trigger(p, AddedTo.D);
						queue.add(t);					
					}
				//}								
			}

			//if(Pair.in(new Pair(beta, chi), C))
			if(C2.get(beta)!=null) if(C2.get(beta).contains(chi))	
			{
				Pair p = new Pair(alpha, chi);
				//if(in_T(T, alpha, chi)){
					if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
					{
						String[] s = new String[2];
						s[0] = p.toString();
						s[1] = "rule 5b";
						if(D3.keySet().contains(newpair.toString())){
							D3.get(newpair.toString()).add(s);
						}else{
							ArrayList<String[]> list = new ArrayList<String[]>();
							list.add(s);
							D3.put(newpair.toString(), list);
						}
						logger.info("Rule 05 " + p);
						Trigger t = new Trigger(p, AddedTo.D);
						queue.add(t);						
					}	
				//}							
			}
		}
	}

	public void rule06c(Pair newpair) 
	{
		String alpha = newpair.getLeft(); 
		String beta = newpair.getRight();
		
		if(TestSequence.isProperPrefixOf(beta, alpha))	//
		{
			String temp = alpha;
			alpha = beta;
			beta = temp;
		}
		
		if(TestSequence.isProperPrefixOf(alpha, beta))	//alpha < beta 
		{
			String fi = TestSequence.getSuffixFrom(beta, alpha);
			for(String fi2 : getPotency(fi))
			{
				Pair p = new Pair(alpha, TestSequence.concat(alpha, fi2));
				//if(in_T(T, alpha, TestSequence.concat(alpha, fi2))){
					if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
					{
						String[] s = new String[2];
						s[0] = p.toString();
						s[1] = "rule 6";
						if(D3.keySet().contains(newpair.toString())){
							D3.get(newpair.toString()).add(s);
						}else{
							ArrayList<String[]> list = new ArrayList<String[]>();
							list.add(s);
							D3.put(newpair.toString(), list);
						}
						System.out.println("RULE 6");
						logger.info("Rule 06 " + p);
						Trigger t = new Trigger(p, AddedTo.D);
						queue.add(t);							
					}
				//}								
			}
		}
	}

		
	public void rule07c(Pair newpair) 
	{
		String alpha = newpair.getShorter();
		String sequence = newpair.getlonger();
		
		ArrayList<Pair> temp = new ArrayList<Pair>();
		if(TestSequence.isProperPrefixOf(alpha, sequence))
		{
			String betagamma = TestSequence.getSuffixFrom(sequence, alpha);
			if(D2.get(alpha)!=null){			
				for(String r : D2.get(alpha)){
					if(TestSequence.isProperPrefixOf(alpha, r)){
						String gamma = TestSequence.getSuffixFrom(r, alpha);
						
						if(TestSequence.isProperSufixOf(gamma, betagamma))
						{
							String beta = TestSequence.getPrefixFrom(betagamma, gamma);
							temp.add(new Pair(alpha, TestSequence.concat(alpha, beta)));
						}
					}
				}			
			}			
			for(Pair p : temp)
			{
				//if(in_T(T, p.getLeft(), p.getRight())){
					if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
					{
						String[] s = new String[2];
						s[0] = p.toString();
						s[1] = "rule 7";
						if(D3.keySet().contains(newpair.toString())){
							D3.get(newpair.toString()).add(s);
						}else{
							ArrayList<String[]> list = new ArrayList<String[]>();
							list.add(s);
							D3.put(newpair.toString(), list);
						}
						System.out.println("RULE 7");
						logger.info("Rule 07 " + p);
						Trigger t = new Trigger(p, AddedTo.D);
						queue.add(t);					
					}					
				//}			
			}
		}
	}
	
	public void rule08c(Pair newpair) 
	{
		String alpha = newpair.getShorter(); 
		String seq = newpair.getlonger();
		
		if(TestSequence.isProperPrefixOf(alpha, seq))
		{
			String gamma = TestSequence.getSuffixFrom(seq, alpha);
			if(C2.get(alpha)!=null){			
				for(String r : C2.get(alpha)){
					if(TestSequence.isProperPrefixOf(alpha, r)){
						String betagamma = TestSequence.getSuffixFrom(r, alpha);
						if(TestSequence.isProperSufixOf(gamma, betagamma))
						{
							String beta = TestSequence.getPrefixFrom(betagamma, gamma);
							Pair p = new Pair(alpha, TestSequence.concat(alpha, beta));
							//if(in_T(T, alpha, TestSequence.concat(alpha, beta))){
								if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
								{
									String[] s = new String[2];
									s[0] = p.toString();
									s[1] = "rule 8";
									if(D3.keySet().contains(newpair.toString())){
										D3.get(newpair.toString()).add(s);
									}else{
										ArrayList<String[]> list = new ArrayList<String[]>();
										list.add(s);
										D3.put(newpair.toString(), list);
									}
									System.out.println("RULE 8");
									logger.info("Rule 08 " + p);
									Trigger t = new Trigger(p, AddedTo.D);
									queue.add(t);									
								}
							//}																	
						}
					}
				}
			}			
		}
	}
	
	public void rule09c(Pair newpair) 
	{
		String alpha = newpair.getShorter(); 
		String seq = newpair.getlonger();

		if(TestSequence.isProperPrefixOf(alpha, seq))
		{
			ArrayList<Pair> temp = new ArrayList<Pair>();
			String gamma = TestSequence.getSuffixFrom(seq, alpha);
			
			for(String a : D2.keySet()){
				if(TestSequence.isProperSufixOf(gamma, a))
				{
					String beta = TestSequence.getPrefixFrom(a, gamma);
					for(String r : D2.get(a)){
						if(beta.equals(r))
						{
							temp.add(new Pair(alpha, beta));
						}
					}
				}				
			}			
			for(Pair p : temp)
			{
				//if(in_T(T, alpha, TestSequence.concat(p.getLeft(), p.getRight()))){
					if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
					{
						String[] s = new String[2];
						s[0] = p.toString();
						s[1] = "rule 9";
						if(D3.keySet().contains(newpair.toString())){
							D3.get(newpair.toString()).add(s);
						}else{
							ArrayList<String[]> list = new ArrayList<String[]>();
							list.add(s);
							D3.put(newpair.toString(), list);
						}
						System.out.println("RULE 9");
						logger.info("Rule 09 " + p);
						Trigger t = new Trigger(p, AddedTo.D);
						queue.add(t);					
					}					
				//}								
			}			
		}		
	}	
	
	public void rule10c(Pair newpair) 
	{
		String beta = newpair.getShorter(); 
		String seq = newpair.getlonger();

		if(TestSequence.isProperPrefixOf(beta, seq))
		{
			String gamma = TestSequence.getSuffixFrom(seq, beta);
						
			for(String a : C2.keySet()){
				if(TestSequence.isProperSufixOf(gamma, a))
				{
					String alpha = TestSequence.getPrefixFrom(a, gamma);
					for(String r : C2.get(a)){
						if(alpha.equals(r))
						{
							Pair p = new Pair(alpha, beta);
							//if(in_T(T, alpha, beta)){
								if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
								{
									String[] s = new String[2];
									s[0] = p.toString();
									s[1] = "rule 10";
									if(D3.keySet().contains(newpair.toString())){
										D3.get(newpair.toString()).add(s);
									}else{
										ArrayList<String[]> list = new ArrayList<String[]>();
										list.add(s);
										D3.put(newpair.toString(), list);
									}
									System.out.println("RULE 10");
									logger.info("Rule 10 " + p);
									Trigger t = new Trigger(p, AddedTo.D);
									queue.add(t);									
								}
							//}								
						}
					}
				}				
			}			
		}				
	}
	
	public void rule01b(Pair newpair)
	{
		ArrayList<Pair> temp = new ArrayList<Pair>();
		String alpha1 = newpair.getLeft(); 
		String beta1 = newpair.getRight();
		String alpha2 = newpair.getRight();
		String beta2 = newpair.getLeft();

		if(C2.get(alpha1)!=null){			
			for(String chi : C2.get(alpha1)){
				temp.add(new Pair(beta1, chi));
			}			
		}else if(C2.get(alpha2)!=null){
			for(String chi : C2.get(alpha2)){
				temp.add(new Pair(beta2, chi));
			}
		}		
		for(Pair p : temp)
		{
			if(HashList.add_pair_hash(C2, p.getLeft(), p.getRight()))
			{ 
				logger.info("Rule 01 " + p);
				Trigger t = new Trigger(p, AddedTo.C);
				queue.add(t);				
			}
		}
	}

	public void rule02b(Pair newpair)
	{
		String alpha = newpair.getLeft();
		String beta = newpair.getRight();
		
		for(String fi1 : T)
		{
			if(TestSequence.isProperPrefixOf(alpha, fi1))
			{
				String fi1_1 = TestSequence.getSuffixFrom(fi1, alpha);
				
				for(String fi2 : T)
				{
					if(TestSequence.isProperPrefixOf(beta, fi2))
					{
						String fi2_2 = TestSequence.getSuffixFrom(fi2, beta);
						if(fi1_1.equals(fi2_2))
						{
							Pair p = new Pair(fi1, fi2);
							if(HashList.add_pair_hash(C2, p.getLeft(), p.getRight()))
							{
								logger.info("Rule 02 " + p);
								Trigger t = new Trigger(p, AddedTo.C);
								queue.add(t);								
							}
						}
					}
				}
			}
		}
	}
	
	public void rule03b(Pair newpair)
	{
		ArrayList<String> gammas = TestSequence.getCommonSuffixesFrom(newpair.getLeft(), newpair.getRight());		
		gammas.remove("EPSILON");
		//System.out.println("pair: " + newpair);
		//System.out.println("gammas: " + gammas);
		for(String gamma : gammas)
		{
			String alpha_ = TestSequence.getPrefixFrom(newpair.getLeft(), gamma);
			String beta_ = TestSequence.getPrefixFrom(newpair.getRight(), gamma);
			//System.out.println("alpha_:" + alpha_);
			//System.out.println("beta_:" + beta_);
			Pair p = new Pair(alpha_, beta_);
			if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
			{
				logger.info("Rule 03 " + p);
				Trigger t = new Trigger(p, AddedTo.D);
				queue.add(t);
			}
		}
	}
	
	public void rule04b(Pair newpair)
	{
		String alpha = newpair.getLeft();
		String beta = newpair.getRight();
		
		for(String chi : T)
		{
			//if(Pair.in(new Pair(alpha, chi), D))
			if(D2.get(alpha)!=null) if(D2.get(alpha).contains(chi))	
			{
				Pair p = new Pair(beta, chi);
				if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
				{
					logger.info("Rule 04 " + p);
					Trigger t = new Trigger(p, AddedTo.D);
					queue.add(t);					
				}
			}
			
			//if(Pair.in(new Pair(beta, chi), D))
			if(D2.get(beta)!=null) if(D2.get(beta).contains(chi))	
			{
				Pair p = new Pair(alpha, chi);
				if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
				{
					logger.info("Rule 04 " + p);
					Trigger t = new Trigger(p, AddedTo.D);
					queue.add(t);					
				}
			}			
		}
	}	

	public void rule05b(Pair newpair)
	{
		String alpha = newpair.getLeft();
		String beta = newpair.getRight();
		
		for(String chi : T)
		{
			//if(Pair.in(new Pair(alpha, chi), C))
			if(C2.get(alpha)!=null) if(C2.get(alpha).contains(chi))	
			{
				Pair p = new Pair(beta, chi);
				if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
				{
					logger.info("Rule 05 " + p);
					Trigger t = new Trigger(p, AddedTo.D);
					queue.add(t);					
				}				
			}

			//if(Pair.in(new Pair(beta, chi), C))
			if(C2.get(beta)!=null) if(C2.get(beta).contains(chi))	
			{
				Pair p = new Pair(alpha, chi);
				if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
				{
					//logger.info("Rule 05 pair " + newpair);
					logger.info("Rule 05 " + p);
					Trigger t = new Trigger(p, AddedTo.D);
					queue.add(t);					
				}				
			}
		}
	}

	public void rule06b(Pair newpair) 
	{
		String alpha = newpair.getLeft(); 
		String beta = newpair.getRight();
		
		if(TestSequence.isProperPrefixOf(beta, alpha))	//
		{
			String temp = alpha;
			alpha = beta;
			beta = temp;
		}
		
		if(TestSequence.isProperPrefixOf(alpha, beta))	//alpha < beta 
		{
			String fi = TestSequence.getSuffixFrom(beta, alpha);
			for(String fi2 : getPotency(fi))
			{
				Pair p = new Pair(alpha, TestSequence.concat(alpha, fi2));
				if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
				{
					logger.info("Rule 06 " + p);
					Trigger t = new Trigger(p, AddedTo.D);
					queue.add(t);					
				}				
			}
		}
	}

		
	public void rule07b(Pair newpair) 
	{
		String alpha = newpair.getShorter();
		String sequence = newpair.getlonger();
		
		ArrayList<Pair> temp = new ArrayList<Pair>();
		if(TestSequence.isProperPrefixOf(alpha, sequence))
		{
			String betagamma = TestSequence.getSuffixFrom(sequence, alpha);
			if(D2.get(alpha)!=null){			
				for(String r : D2.get(alpha)){
					if(TestSequence.isProperPrefixOf(alpha, r)){
						String gamma = TestSequence.getSuffixFrom(r, alpha);
						
						if(TestSequence.isProperSufixOf(gamma, betagamma))
						{
							String beta = TestSequence.getPrefixFrom(betagamma, gamma);
							temp.add(new Pair(alpha, TestSequence.concat(alpha, beta)));
						}
					}
				}			
			}			
			for(Pair p : temp)
			{
				if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
				{
					logger.info("Rule 07 " + p);
					Trigger t = new Trigger(p, AddedTo.D);
					queue.add(t);					
				}				
			}
		}
	}
	
	public void rule08b(Pair newpair) 
	{
		String alpha = newpair.getShorter(); 
		String seq = newpair.getlonger();
		
		if(TestSequence.isProperPrefixOf(alpha, seq))
		{
			String gamma = TestSequence.getSuffixFrom(seq, alpha);
			if(C2.get(alpha)!=null){			
				for(String r : C2.get(alpha)){
					if(TestSequence.isProperPrefixOf(alpha, r)){
						String betagamma = TestSequence.getSuffixFrom(r, alpha);
						if(TestSequence.isProperSufixOf(gamma, betagamma))
						{
							String beta = TestSequence.getPrefixFrom(betagamma, gamma);
							Pair p = new Pair(alpha, TestSequence.concat(alpha, beta));
							if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
							{
								logger.info("Rule 08 " + p);
								Trigger t = new Trigger(p, AddedTo.D);
								queue.add(t);					
							}										
						}
					}
				}
			}			
		}
	}
	
	public void rule09b(Pair newpair) 
	{
		String alpha = newpair.getShorter(); 
		String seq = newpair.getlonger();

		if(TestSequence.isProperPrefixOf(alpha, seq))
		{
			ArrayList<Pair> temp = new ArrayList<Pair>();
			String gamma = TestSequence.getSuffixFrom(seq, alpha);
			
			for(String a : D2.keySet()){
				if(TestSequence.isProperSufixOf(gamma, a))
				{
					String beta = TestSequence.getPrefixFrom(a, gamma);
					for(String r : D2.get(a)){
						if(beta.equals(r))
						{
							temp.add(new Pair(alpha, beta));
						}
					}
				}				
			}			
			for(Pair p : temp)
			{
				if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
				{
					logger.info("Rule 09 " + p);
					Trigger t = new Trigger(p, AddedTo.D);
					queue.add(t);					
				}				
			}			
		}		
	}	
	
	public void rule10b(Pair newpair) 
	{
		String beta = newpair.getShorter(); 
		String seq = newpair.getlonger();

		if(TestSequence.isProperPrefixOf(beta, seq))
		{
			String gamma = TestSequence.getSuffixFrom(seq, beta);
						
			for(String a : C2.keySet()){
				if(TestSequence.isProperSufixOf(gamma, a))
				{
					String alpha = TestSequence.getPrefixFrom(a, gamma);
					for(String r : C2.get(a)){
						if(alpha.equals(r))
						{
							Pair p = new Pair(alpha, beta);
							if(HashList.add_pair_hash(D2, p.getLeft(), p.getRight()))
							{
								logger.info("Rule 10 " + p);
								Trigger t = new Trigger(p, AddedTo.D);
								queue.add(t);					
							}	
						}
					}
				}				
			}			
		}				
	}
}