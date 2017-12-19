package br.usp.icmc.fsm.common;

import java.util.ArrayList;
import java.util.Map;

public class HashList {
					
		public static boolean add_pair_hash(Map<String, ArrayList<String>> ret, String alpha, String beta){				
			
			if(ret.get(alpha)!=null){
				if(!ret.get(alpha).contains(beta)){
					ret.get(alpha).add(beta);
					return true;
				}else return false;
			}else{
				ArrayList<String> arr = new ArrayList<String>();
				arr.add(beta);
				ret.put(alpha, arr);
				return true;
			}
		}
		
		public static boolean add_triple_hash(Map<String, ArrayList<String[]>> ret,
				String alpha, String beta, String gamma){				
			
			String[] s = new String[2];
			s[0] = beta;
			s[1] = gamma;
			
			if(ret.get(alpha)!=null){
				boolean contain = false;
				for (String[] s_a : ret.get(alpha)){
					if(s_a[0].equals(beta)){
						contain = true;
					}
				}
				if(!contain){
					ret.get(alpha).add(s);
					return true;
				}else return false;
			}else{
				ArrayList<String[]> arr = new ArrayList<String[]>();				
				arr.add(s);
				ret.put(alpha, arr);
				return true;
			}
		}
		
		public static String get_gamma_hash(Map<String, ArrayList<String[]>> ret,
				String alpha, String beta){				
			
			if(ret.get(alpha)!=null){				
				for (String[] s_a : ret.get(alpha)){
					if(s_a[0].equals(beta)){
						return s_a[1];
					}
				}				
			}
			if(ret.get(beta)!=null){				
				for (String[] s_a : ret.get(beta)){
					if(s_a[0].equals(alpha)){
						return s_a[1];
					}
				}				
			}
			return "---";
		}
		
		public static boolean pair_hash_in(Map<String, ArrayList<String>> D, String alpha, String beta){				
			
			if(D.get(alpha)!=null){			
				for(String r : D.get(alpha)){
					if(r.equals(beta)){
						return true;
					}
				}
			}		
			
			return false;
		}
		
		public static ArrayList<String> getPartition(String alpha, Map<String, ArrayList<String>> C){
			
			if(C.get(alpha)!=null){
				return C.get(alpha);
			}
			return new ArrayList<String>();
			
		}

}
