package br.usp.icmc.feature.logic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.usp.icmc.ffsm.CommonPathFSM;
import br.usp.icmc.fsm.common.FiniteStateMachine;
import br.usp.icmc.fsm.common.State;
import br.usp.icmc.fsm.common.Transition;
import br.usp.icmc.reader.FsmModelReader;

public class FSMProperties {

	Map<State,ArrayList<ArrayList<Transition>>> path_map;
	Map<String,ArrayList<CommonPathFSM>> seq_map;
	ArrayList<Transition> no_loop_ft;
	ArrayList<State> found_fc;
	ArrayList<State> nfound_fc;
	String folder;
	FiniteStateMachine fsm;
	//static Logger LOGGER;
	boolean islog, debug;
	String log;
	
	public ArrayList<Transition> getFSMTransitions(){
		return fsm.getTransitions();
	}
	
	public String getlog(){
		return log;
	}
	
	public FSMProperties(String folder, boolean islog, boolean debug, String fsm_path) throws Exception{
		this.folder = folder;
		//this.LOGGER = LOGGER;
		this.islog = islog;
		this.debug = debug;
		log = "";
		File file = new File(fsm_path);
		FsmModelReader reader = new FsmModelReader(file, true);
		fsm = reader.getFsm();
	}
	
	public FSMProperties(String folder){
		this.folder = folder;	
	}
	
	public boolean is_deterministic(){	
		try {			
			for(State s : fsm.getStates()){
				ArrayList<Transition> stx1 = (ArrayList<Transition>) s.getOut().clone();
				ArrayList<Transition> stx2 = (ArrayList<Transition>) s.getOut().clone();				
				for(Transition ft: stx1){					
					stx2.remove(ft);					
					for(Transition ft2 : stx2){
						if(ft.getInput().equals(ft2.getInput())){
							if(!ft.getOut().equals(ft2.getOut())){
								return false;
							}
						}						
					}
				}				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return true;
	}
	
	public boolean is_complete(){
		try {			
			for(State s : fsm.getStates()){
				for(String in : fsm.getInputAlphabet()){
					boolean in_found = false;
					for(Transition ft: s.getOut()){							
						if(ft.getInput().equals(in)){
							in_found = true;							
						}						
					}
					if(!in_found){
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return true;
	}
	
	public boolean is_initially_connected(){
		
		boolean init_con = true;
		try	{			
			log = "";
			if(islog) log = log.concat("States "+fsm.getStates()+"\n");		
			if(islog) log = log.concat("Transitions "+fsm.getTransitions()+"\n");		
			if(islog) log = log.concat("Inputs "+fsm.getInputAlphabet()+"\n");		
			if(islog) log = log.concat("Outputs "+fsm.getOutputAlphabet()+"\n");		
										
			//find paths
			find_all_paths();
			if(islog) print_paths(path_map);
			
			//check reachability 
			boolean epath = check_valid_paths();						
			if(!epath){
				return false;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}		
		return init_con;
	}
	
	public boolean is_minimal(){
		
		boolean minimal = true;
		try	{			
			log = "";
			if(islog) log = log.concat("States "+fsm.getStates()+"\n");		
			if(islog) log = log.concat("Transitions "+fsm.getTransitions()+"\n");		
			if(islog) log = log.concat("Inputs "+fsm.getInputAlphabet()+"\n");		
			if(islog) log = log.concat("Outputs "+fsm.getOutputAlphabet()+"\n");	
		
			ArrayList<State> fs_aux = (ArrayList<State>) fsm.getStates().clone();
			for(State fs1 : fsm.getStates()){
				fs_aux.remove(fs1);
				for(State fs2 : fs_aux){					
					boolean stilltrue = find_and_check_disting_seq(fs1, fs2, fsm.getNumberOfStates());
					if(!stilltrue){
						return false;
					}										
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}		
		return minimal;
	}
	
	public boolean find_and_check_disting_seq(State fs1, State fs2, int n) 
			throws IOException, InterruptedException{
							
		ArrayList<Transition> current_out1 = fs1.getOut();
		ArrayList<Transition> current_out2 = fs2.getOut();				
		seq_map = new HashMap<String,ArrayList<CommonPathFSM>>();	
		boolean found_input = false;
				
		//process distinguish seq size 1
		for(String in : fsm.getInputAlphabet()){			
			seq_map.put(in, new ArrayList<CommonPathFSM>());
			for(Transition co1 : current_out1){
				for(Transition co2 : current_out2){
					//check the same input
					if(co1.getInput().equals(in) 
							&& co2.getInput().equals(in)){												
						CommonPathFSM cnew = new CommonPathFSM(fs1, fs2, n);							
						//1-Can be distinguished? (identification)
						//2-Max size path is less than n-1?							
						if(cnew.addCommon(co1, co2)){
							seq_map.get(in).add(cnew);
							found_input = true;
						}						
					}					
				}
			}	
		}	
		if(!found_input){			
			if(islog) log = log.concat("Could not find a input for "+ fs1 + " and "+fs2+"\n");	
			if(debug) System.out.println("Could not find a input for "+ fs1 + " and "+fs2);
			return false;
		}
		
		//print
		print_common_pairs(fs1, fs2);
		
		//check input		
		ArrayList<String> alp = new ArrayList<String>();
		for(String s : fsm.getInputAlphabet()){
			alp.add(s);
		}				
		for(int i=1; i<=alp.size(); i++){			
			if(islog) log = log.concat("CHECKING"+"\n");
			if(debug) System.out.println("CHECKING");
			for(String incheck : alp){				
				if(islog) log = log.concat(incheck+"\n");
				if(debug) System.out.println(incheck);
				ArrayList<String> inputset = check_disting(fs1, fs2, seq_map, incheck);
				if(inputset != null){					
					if(islog) log = log.concat("\nState pair "+fs1+" and "+fs2+" OK for inputset "+inputset+"\n"+"\n");
					if(debug) System.out.println("\nState pair "+fs1+" and "+fs2+" OK for inputset "+inputset+"\n");
					return true;
				}
			}			
		}				
		
		//recursive call to n-1
		for(String in : fsm.getInputAlphabet()){
			ArrayList<CommonPathFSM> caux = (ArrayList<CommonPathFSM>) seq_map.get(in).clone();
			for(CommonPathFSM cp : caux){
				State a1 = cp.get1().get(cp.get1().size()-1).getOut();
				State a2 = cp.get2().get(cp.get2().size()-1).getOut();				
				//if both do not lead to the same state, and both are not self loops
				if(!a1.equals(a2) && !(fs1.equals(a1) && (fs2.equals(a2)))){
					seq_map.get(in).clear();					
					if(islog) log = log.concat(" WHAT? "+ fs1+" -> "+a1 + " "+fs2+" -> "+" "+a2+ " input "+in +"\n");
					if(debug) System.out.println(" WHAT? "+ fs1+" -> "+a1 + " "+fs2+" -> "+" "+a2+ " input "+in);
					boolean got = rec_common(a1, a2, cp, in);
					if(got){						
						if(islog) log = log.concat("GOT "+ fs1+" "+fs2 + " "+a1+" "+" "+a2+ " "+in+"\n");
						if(debug) System.out.println("GOT "+ fs1+" "+fs2 + " "+a1+" "+" "+a2+ " "+in);
						return true;
					}
				}				
			}
		}
		
		if(islog) log = log.concat("Could no find a seq. for "+ fs1 + " and "+fs2+"\n");
		if(debug) System.out.println("Could no find a seq. for "+ fs1 + " and "+fs2);
		//could not find a distinguishing sequence...
		return false;
	}
	
	public boolean rec_common(State fs1, State fs2, CommonPathFSM cp, String lastinput) 
			throws IOException, InterruptedException{
		ArrayList<Transition> current_out1 = fs1.getOut();
		ArrayList<Transition> current_out2 = fs2.getOut();
		boolean found_input = false;
		
		if(islog) log = log.concat(" TEST0 " + fs1 + " " +fs2+"\n");
		if(debug) System.out.println(" TEST0 " + fs1 + " " +fs2+"\n");
		if(islog || debug) print_common_pairs(fs1, fs2);
		
		for(String in : fsm.getInputAlphabet()){			
			for(Transition co1 : current_out1){
				for(Transition co2 : current_out2){
					//check the same input
					if(co1.getInput().equals(in) 
							&& co2.getInput().equals(in) && !cp.getDistinguish()){							
						CommonPathFSM cnew = new CommonPathFSM(cp.getS1(), cp.getS2(), 
								cp.getN(), cp.get1(), cp.get2());						
						if(cnew.addCommon(co1, co2)){							
							seq_map.get(lastinput).add(cnew);
							found_input = true;							
						}						
					}					
				}
			}	
		}	
		if(!found_input){
			if(islog) log = log.concat("\nNo input available!"+"\n");
			if(debug) System.out.println("\nNo input available!");
			return false;
		}
		if(islog) log = log.concat(" TEST1 " + fs1 + " " +fs2+"\n");		
		if(debug) System.out.println(" TEST1 " + fs1 + " " +fs2);
		if(islog || debug) print_common_pairs(fs1, fs2);
			
		//check input				
		ArrayList<String> alp = new ArrayList<String>();
		for(String s : fsm.getInputAlphabet()){
			alp.add(s);
		}				
		for(int i=1; i<=alp.size(); i++){			
			if(islog) log = log.concat("CHECKING 2"+"\n");
			if(debug) System.out.println("CHECKING 2");
			for(String incheck : alp){				
				if(islog) log = log.concat(incheck+"\n");
				if(debug) System.out.println(incheck);
				ArrayList<String> inputset = check_disting(fs1, fs2, seq_map, incheck);
				if(inputset != null){					
					if(islog) log = log.concat("\nState pair "+fs1+" and "+fs2+" OK for inputset "+inputset+"\n"+"\n");
					if(debug) System.out.println("\nState pair "+fs1+" and "+fs2+" OK for inputset "+inputset+"\n");
					return true;
				}
			}			
		}			 
		
		ArrayList<CommonPathFSM> caux = seq_map.get(lastinput);
		for(CommonPathFSM p : caux){
			State a1 = p.get1().get(p.get1().size()-1).getOut();
			State a2 = p.get2().get(p.get2().size()-1).getOut();
			String in = p.get1().get(p.get1().size()-1).getInput();
			State a11 = p.get1().get(p.get1().size()-1).getIn();
			State a22 = p.get2().get(p.get2().size()-1).getIn();				
			if(!a1.equals(a2) && !(a11.equals(a1) && (a22.equals(a2)))){				
				if(islog) log = log.concat("\n States \n"+a1+" "+a2+" "+a11+" "+a22+ " "+in+"\n");
				if(islog) log = log.concat("\nGoing recursive\n"+in+"\n");
				boolean got = rec_common(a1, a2, p, in);
				if(got){
					return true;
				}
			}				
		}		
		
		return false;
	}
	
	public ArrayList<String> check_disting(State fs1, State fs2, 
			Map<String,ArrayList<CommonPathFSM>> map, String inputcheck) 
			throws IOException, InterruptedException{
						
		ArrayList<String> inputset = new ArrayList<String>();
		ArrayList<CommonPathFSM> caux = map.get(inputcheck);
		for(CommonPathFSM cp : caux){				
			if(cp.getDistinguish()){	
				String inaux = "";					
				for(Transition t : cp.get1()){
					inaux = inaux.concat(t.getInput() + "+");						
				}						
				inputset.add(inaux);
			}				
		}				
		if(inputset.size() < 1){
			return null;
		}else return inputset;
	}	
		
	public void find_all_paths(){
		no_loop_ft = (ArrayList<Transition>) fsm.getTransitions().clone();
		for(Transition ft : fsm.getTransitions()){
			if(ft.getIn().equals(ft.getOut())){
				no_loop_ft.remove(ft);
			}
		}
					
		State current = fsm.getInitialState();
		ArrayList<Transition> current_out = current.getOut();
					
		found_fc = new ArrayList<State>();
		nfound_fc = (ArrayList<State>) fsm.getStates().clone();
		nfound_fc.remove(current);
		found_fc.add(current);
					
		path_map = new HashMap<State,ArrayList<ArrayList<Transition>>>();
		
		for(State s : nfound_fc){
			path_map.put(s, new ArrayList<ArrayList<Transition>>());
		}			
		//process initial c. state
		for(Transition ct : current_out){
			if(no_loop_ft.contains(ct)){
				if(!found_fc.contains(ct.getOut())){
					nfound_fc.remove(ct.getOut());
					found_fc.add(ct.getOut());
				}					
				ArrayList<Transition> current_path = new ArrayList<Transition>();
				current_path.add(ct);					
				ArrayList<ArrayList<Transition>> c_paths = new ArrayList<ArrayList<Transition>>();					
				c_paths.add(current_path);
				path_map.put(ct.getOut(), c_paths);
			}				
		}						
		ArrayList<State> found_aux = (ArrayList<State>) found_fc.clone();
		for(State cs : found_aux){
			if(!cs.equals(fsm.getInitialState())){
				rec_find_path(cs);
			}				
		}
	}
	
	public void rec_find_path(State current){
		ArrayList<Transition> current_out = current.getOut();		
		for(Transition ct : current_out){
			if(no_loop_ft.contains(ct)){				
				ArrayList<ArrayList<Transition>> c_paths = path_map.get(ct.getOut());
				ArrayList<ArrayList<Transition>> lc_paths = path_map.get(current);				
				prepath: for(ArrayList<Transition> inc_path : lc_paths){					
					//if this c. state was found before
					for(Transition c : inc_path){
						if(c.getOut().equals(ct.getOut())){
							continue prepath;
						}
					}
					ArrayList<Transition> new_path = new ArrayList<Transition>();
					State last = inc_path.get(inc_path.size()-1).getIn();
					if(!last.equals(ct.getOut()) && c_paths != null){						
						new_path.addAll(inc_path);
						new_path.add(ct);
						if(!c_paths.contains(new_path)){
							c_paths.add(new_path);							
						}						
					}					
				}				
				path_map.put(ct.getOut(), c_paths);				
				if(!found_fc.contains(ct.getOut())){
					nfound_fc.remove(ct.getOut());
					found_fc.add(ct.getOut());
					rec_find_path(ct.getOut());
				}				
			}			
		}		
	}
	
	public boolean check_valid_paths(){		
		if(islog) log = log.concat("\nChecking states...\n");
		for(State s: path_map.keySet()){	
			if(path_map.get(s) != null){
				if(path_map.get(s).size() < 1){
					return false;
				}else{					
					if(islog) log = log.concat("State "+s +" OK"+"\n");
				}
			}			
		}
		return true;
	}
	
	public void print_paths(Map<State,ArrayList<ArrayList<Transition>>> path_map){	
		if(islog) log = log.concat("\n Printing state paths"+"\n");	
		if(debug) System.out.println("\n Printing state paths");
		for(State s: path_map.keySet()){			
			if(islog) log = log.concat("State "+s+"\n");
			if(debug) System.out.println("State "+s);
			int count = 0;
			if(path_map.get(s) != null){
				for(ArrayList<Transition> path : path_map.get(s)){
					count++;					
					if(islog) log = log.concat("Path "+count+": "+path+"\n");
					if(debug) System.out.println("Path "+count+": "+path);
				}
			}			
		}
	}
	
	public void print_common_pairs(State fs1, State fs2){		
		if(islog) log = log.concat("State pair "+ fs1 + " and " + fs2+"\n");
		if(debug) System.out.println("State pair "+ fs1 + " and " + fs2);
		for(String in : fsm.getInputAlphabet()){			
			if(islog) log = log.concat("  Input "+in+"\n");
			if(debug) System.out.println("  Input "+in);
			ArrayList<CommonPathFSM> caux = seq_map.get(in);
			for(CommonPathFSM cp : caux){				
				if(islog) log = log.concat("     Pair "+cp.get1()+ " "+ cp.get2()+"\n");
				if(debug) System.out.println("     Pair "+cp.get1()+ " "+ cp.get2());
			}
		}
	}
	
}
