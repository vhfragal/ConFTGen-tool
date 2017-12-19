package sat;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.junit.Test;

import br.usp.icmc.feature.logic.FFSMProperties;
import br.usp.icmc.fsm.common.FiniteStateMachine;
import br.usp.icmc.fsm.common.State;
import br.usp.icmc.fsm.common.TestSequence;
import br.usp.icmc.fsm.common.Transition;
import br.usp.icmc.fsm.constructor.CharacterizationSetConstructor;
import br.usp.icmc.fsm.constructor.HarmonizedIdentifiersConstructor;
import br.usp.icmc.fsm.constructor.TestingTreeSetsConstructor;
import br.usp.icmc.reader.FsmModelReader;

public class FFSM_Experiments 
{
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	/* random everything :)
	 * random feature model structure
	 * random number of states, inputs, outputs, conditions
	 * random target states
	 */
	//most transitions should be part of the core and then a few are specific
	String[] features = {"A","B","C","D","E","F","G","H","I","J",
			"A1","B1","C1","D1","E1","F1","G1","H1","I1","J1",
			"true","true","true","true","true","true","true","true","true","true",
			"true","true","true","true","true","true","true","true","true","true",
			"true","true","true","true","true","true","true","true","true","true",
			"true","true","true","true","true","true","true","true","true","true"};
	String[] only_features = {"A","B","C","D","E","F","G","H","I","J",
			"A1","B1","C1","D1","E1","F1","G1","H1","I1","J1"};
	String[] inputs = {"a","b","c","d","e","f","g","h","i","j",
			"a1","b1","c1","d1","e1","f1","g1","h1","i1","j1",
			"a3","b3","c3","d3","e3","f3","g3","h3","i3","j3",
			"a4","b4","c4","d4","e4","f4","g4","h4","i4","j4",
			"a2","b2","c2","d2","e2","f2","g2","h2","i2","j2"};
			
	boolean islog;
	boolean isplog;
	boolean debug;
	boolean pop_file;
	
	String path;		
	int iterations;
	
	int max_states;
	int min_states;
	int max_inputs;
	int min_inputs;
	int max_outputs;
	int min_outputs;
		
	String exp_folder;
	String folder;
	String ffsm_folder; 
	String fsm_folder;
	String fm_folder;	
	String ffsm_state_suite_folder; 
	String ffsm_transition_suite_folder; 
	String ffsm_full_suite_folder;
	String fsm_pruned_state_suite_folder; 
	String fsm_pruned_transition_suite_folder; 
	String fsm_pruned_full_suite_folder;
	String fsm_derived_full_suite_folder;
	String lpath;
		
	@Test
	public void test_FSM_HSI()
	{
		System.out.println("-----------------------------------------");
		System.out.println("TEST FSM HSI Generation");
		try {			
			//String folder = "./experiments/hsi/";
			//String fsm_name = "fsm_6";			
			
			String folder = "./derived/";			
			String fsm_name = "fsm_derived_6";
			String fsm_file = folder+fsm_name+".txt";
			
			File file = new File(fsm_file);
			FsmModelReader reader = new FsmModelReader(file, true);
			FiniteStateMachine fsm = reader.getFsm();
			
			TestingTreeSetsConstructor cov = new TestingTreeSetsConstructor(fsm);
			cov.construct();
			System.out.println("STATE COVER SET "+TestSequence.getNoPrefixes(cov.getStateCover()));
			System.out.println("TRANSITION COVER SET ");
			ArrayList<String> tcover = TestSequence.getNoPrefixes(cov.getTransitionCover());
			//tcover = TestSequence.getNoPrefixes(tcover);			
			for(String o : tcover){
				System.out.println(o);
			}
			
			CharacterizationSetConstructor csc = new CharacterizationSetConstructor(fsm);
			System.out.println("W SET "+csc.getWset());
			
			System.out.println("HSI sets");
			HarmonizedIdentifiersConstructor h = new HarmonizedIdentifiersConstructor(fsm, csc.getWset());
			for(State s : fsm.getStates()){
				System.out.println("State "+s+" "+h.getHi(s));				
			}
			
			//generate test suite
			ArrayList<String> suite = new ArrayList<String>(); 
			for(State s : fsm.getStates()){				
				for(String sequence : cov.getTransitionCover()){
					ArrayList<Transition> list = fsm.reachedTransitionsWithSequence(fsm.getInitialState(), sequence);
					if(list.size() > 0){
						State reach = list.get(list.size()-1).getOut();
						//System.out.println("LIST "+list+" "+reach);
						if(s.equals(reach)){
							for(String i : h.getHi(s)){
								String test = sequence+","+i;
								suite.add(test);
							}
						}
					}
				}				
			}
			System.out.println("HSI SUITE ");
			suite = TestSequence.getNoPrefixes(suite);
			suite = TestSequence.orderSet(suite);
			for(String o : suite){
				System.out.println(o);
			}
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			fail();
		}
	}	
	
	
	@Test
	public void test_dots_coverage()	{
		
		boolean islog = false;
		boolean debug = true;
		
		//String folder = "./agm/";
		//String ffsm_name = "ffsm_agm2.txt";
		//String fm_name = "fm_agm.xml";
		
		String folder = "./cas/";
		String ffsm_name = "cas.txt";
		String fm_name = "model.xml";
		
		FFSMProperties p = new FFSMProperties(folder, islog, debug);
		String header = p.read_XML_FeatureModel(folder+fm_name);
		
		String file = folder+ffsm_name;
		try {
			//check states and transitions
			if(!p.set_checkFFSM(file, header)){
				System.out.println("Invalid FFSM!!!");
				return;
			}	
			//get transition cover
			System.out.println("=========");
			//find state cover set in innitially connected property
			//then add transitions with paths
			//if(p.is_initially_connected()){
			if(p.is_initially_connected()){
				
			}
			//print conditional state tree			
	    	String dot_name = "ffsm_agm2_state";	    	
	    	p.gen_state_tree(folder, dot_name, true);
	    	//print conditional transition tree			
	    	String dot_name2 = "ffsm_agm2_transition";
	    	try {
				p.find_transition_cover_set();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	p.gen_transition_tree(folder, dot_name2, true);
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
	
}
