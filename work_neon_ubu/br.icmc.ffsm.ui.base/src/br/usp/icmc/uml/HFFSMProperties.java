package br.usp.icmc.uml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import br.usp.icmc.parser.FCONSTRAINT;
import br.usp.icmc.parser.SimpleNode;
import br.usp.icmc.reader.UMLModelReader;

public class HFFSMProperties {
	
	Map<CState,ArrayList<ArrayList<CTransition>>> path_map;
	Map<String,ArrayList<CommonPath>> seq_map;
	ArrayList<CTransition> no_loop_ft;
	ArrayList<CState> found_fc;
	ArrayList<CState> nfound_fc;
	String folder;
	
	HFFSM hffsm;
	String prop;
	static String clause;
	static int features;
	static boolean optional;
	static ArrayList<String> all_ids;
	
	boolean islog, debug;
	static String log;
	
	public HFFSMProperties(String folder, boolean islog, boolean debug){
		this.folder = folder;
		this.islog = islog;
		this.debug = debug;
		log = "";
	}
	
	public HFFSMProperties(String folder){
		this.folder = folder;
	}
	
	public HFFSMProperties(HFFSM hffsm){
		this.hffsm = hffsm;
	}
	
	public String getlog(){
		return log;
	}
	
	public Map<CState,ArrayList<ArrayList<CTransition>>> getPathMap(){
		return path_map;
	}
	
	public boolean set_checkHFFSM(String hffsm_path, String prop) throws Exception{
		File file = new File(hffsm_path);
		UMLModelReader reader = new UMLModelReader(file);
		hffsm = reader.getHsm();
		this.prop = prop;
				
		CState fs = check_conditional_state(hffsm.getStruct().getStateSet());
		if(fs != null){
			System.out.println("Invalid conditional state "+ fs + " on "+ hffsm_path);
			return false;
		}
		
		CTransition t = check_transition(hffsm.getTransitions());
		if(t != null){
			System.out.println("Invalid transition "+ t + " on "+ hffsm_path);
			return false;
		}
		
		return true;
	}	
	
	public String getZ3(String ex){
		//String ex = "((S&!E)||A)";
		Reader r = new BufferedReader(new StringReader(ex));
		String z3 = "";
		//new FCONSTRAINT(r);
		FCONSTRAINT.ReInit(r);
		try {
			SimpleNode root = FCONSTRAINT.Parse();
			if (root != null) {
				//root.dump("-");
				z3 = root.getZ3();
				//System.out.println(z3);
			}				
		} catch (Exception e) {
			System.out.println("Syntactical Error.");
			System.out.println(e.getMessage());
			//FCONSTRAINT.ReInit(System.in);			
		} catch (Error e) {
			System.out.println("Lexical Error.");
			System.out.println(e.getMessage());
			//break;
		}		
		return z3;
	}	
	
	public CState check_conditional_state(ArrayList<CState> l) 
			throws IOException, InterruptedException{
				
		String header = prop;		
		String clause = "";
		ArrayList<CState> checked = new ArrayList<CState>();
		checked.addAll(l);
		
		for(CState fs: l){
			String ex = hffsm.getStruct().getFComposition(fs);
			ex = ex.toLowerCase();
			ex = ex.concat(";");
			String z3 = getZ3(ex);
			if(z3.equals("")){
				checked.remove(fs);
				continue;
			}
			clause = clause.concat("(push)\n");
			clause = clause.concat("(assert "+z3+")\n");		
			clause = clause.concat("(check-sat)\n");
			clause = clause.concat("(pop)\n");
		}
		
		String prop_aux = header.concat(clause);
		// print stm2 file and execute
		String fpath = "./"+folder+"/f_cds.smt2";
		FileHandler fh = new FileHandler();
		fh.print_file(prop_aux, fpath);
		
		//System.out.println(prop_aux);
		
		String[] commands = {"./hffsm/z3","./"+folder+"/f_cds.smt2"};
		String result = fh.getProcessOutput(commands);		
		String[] outs = result.split("\n");
		//System.out.println(result);
		
		for(int i=0; i<outs.length; i++){
			if(outs[i].equals("unsat") || outs[i].contains("error")){
				return checked.get(i);
			}
		}
		// Ok there is no invalid conditional state
		return null;	
	}
		
	public CTransition check_transition(ArrayList<CTransition> l) 
			throws IOException, InterruptedException{
				
		String header = prop;
		String clause = "";
		ArrayList<CTransition> checked = new ArrayList<CTransition>();
		checked.addAll(l);
		
		for(CTransition ft: l){
			String ex = hffsm.getStruct().getTComposition(ft);
			ex = ex.toLowerCase();
			ex = ex.concat(";");
			String z3 = getZ3(ex);
			if(z3.equals("")){
				checked.remove(ft);
				continue;
			}
			clause = clause.concat("(push)\n");
			clause = clause.concat("(assert "+z3+")\n");		
			clause = clause.concat("(check-sat)\n");
			clause = clause.concat("(pop)\n");
		}
		
		String prop_aux = header.concat(clause);
		// print stm2 file and execute
		String fpath = "./"+folder+"/f_cts.smt2";
		FileHandler fh = new FileHandler();
		fh.print_file(prop_aux, fpath);
		
		//System.out.println(prop_aux);
				
		String[] commands = {"./hffsm/z3","./"+folder+"/f_cts.smt2"};
		String result = fh.getProcessOutput(commands);		
		String[] outs = result.split("\n");
		//System.out.println(result);
						
		for(int i=0; i<outs.length; i++){
			if(outs[i].equals("unsat") || outs[i].contains("error")){
				return checked.get(i);
			}
		}
		// Ok there is no invalid conditional transition
		return null;	
	}
	
	public String read_XML_FeatureModel(String featuremodel){
		try {
			File fXmlFile = new File(featuremodel);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
					
			doc.getDocumentElement().normalize();
			log = "";
	
			if(islog) log = log.concat("Root element :" + doc.getDocumentElement().getNodeName()+"\n");
			
			if (doc.hasChildNodes()) {	
				features = 0;
				clause = "";
				
				Node root = getRoot(doc.getChildNodes().item(0).getChildNodes()).item(0).getParentNode();
				//if(islog)printNote(root.getChildNodes());	
				
				all_ids = new ArrayList<String>();				
				createTree(root.getChildNodes(), "and", "root");				
						
				if(islog) log = log.concat("Features "+features+"\n");
				String aux_clause = "(define-sort Feature () Bool)\n";
				for(String id : all_ids){
					id = id.toLowerCase();
					aux_clause = aux_clause.concat("(declare-const "+id+" Feature)\n");
				}				
				clause = clause.concat("))\n\n");
				
				String body = clause;
				clause = aux_clause;
				clause = clause.concat(body);
									
				if(islog) log = log.concat(clause+"\n");
			}			
			
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
		return clause;
	}
	
	public NodeList getRoot(NodeList nodeList){
		
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node tempNode = nodeList.item(count);	
			// make sure it's element node.
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
				if(tempNode.getNodeName().equals("struct")){					
					return tempNode.getChildNodes();
				}
			}
		}		
		return null;
	}
	
	private static ArrayList<String> createTree(NodeList nodeList, String parent_type, String parent_id) {
		ArrayList<String> ids = new ArrayList<String>();
	    
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node tempNode = nodeList.item(count);			
			
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {	
				String id = handleNode(tempNode);
				id = id.toLowerCase();
				parent_id = parent_id.toLowerCase();
				ids.add(id);
				
				if((parent_type.equals("and") || parent_type.equals("feature")) && !parent_id.equals("root")){						
					if(optional){
						clause = clause.concat("   (=> "+id+" "+parent_id+")\n");
					}else{
						clause = clause.concat("   (= "+id+" "+parent_id+")\n");
					}
				}
				
				if(tempNode.getNodeName().equals("and")){					
					if(parent_id.equals("root")){
						clause = clause.concat("\n(assert "+id+")\n");
						clause = clause.concat("(assert (and\n");
					}
					createTree(tempNode.getChildNodes(), "and", id);
				}
				if(tempNode.getNodeName().equals("alt")){					
					
					ArrayList<String> child_ids = createTree(tempNode.getChildNodes(), "alt", id);
					ArrayList<String> child_ids2 = (ArrayList<String>) child_ids.clone();
					clause = clause.concat("   (= (or");
					for(String d1: child_ids){
						clause = clause.concat(" "+d1+" ");
					}
					clause = clause.concat(") "+id+")\n");
					for(String d1: child_ids){
						child_ids2.remove(d1);
						for(String d2: child_ids2){
							clause = clause.concat("   (not (and "+d1+" "+d2+"))\n");
						}
					}															
				}
				if(tempNode.getNodeName().equals("or")){
					ArrayList<String> child_ids = createTree(tempNode.getChildNodes(), "or", id);
					clause = clause.concat("   (= (or");
					for(String d1: child_ids){
						clause = clause.concat(" "+d1+" ");
					}					
					clause = clause.concat(") "+id+")\n");
				}
				if(tempNode.getNodeName().equals("feature")){
											
					createTree(tempNode.getChildNodes(), "feature", id);	
				}
							
			}	
		}
	    return ids;
	}
	
	public static String handleNode(Node tempNode){
		features++;
		optional = true;
		NamedNodeMap nodeMap = tempNode.getAttributes();
		String feature_id = "";
		for (int i = 0; i < nodeMap.getLength(); i++) {	
			Node node = nodeMap.item(i);
			if(node.getNodeName().equals("name")){
				String aux = node.getNodeValue();
				feature_id = aux.substring(aux.lastIndexOf("[")+1,aux.lastIndexOf("]"));						 
			}
			if(node.getNodeName().equals("mandatory")){
				if(node.getNodeValue().equals("true")){
					optional = false;
				}
			}
		}			
		all_ids.add(feature_id);
		return feature_id;
	}
	
	private static void printNote(NodeList nodeList) {

	    for (int count = 0; count < nodeList.getLength(); count++) {
			Node tempNode = nodeList.item(count);	
			// make sure it's element node.
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {				
				// get node name and value				
				log = log.concat("\nNode Name =" + tempNode.getNodeName() + " [OPEN]"+"\n");				
				if (tempNode.hasAttributes()) {	
					// get attributes names and values
					NamedNodeMap nodeMap = tempNode.getAttributes();	
					for (int i = 0; i < nodeMap.getLength(); i++) {	
						Node node = nodeMap.item(i);							
						log = log.concat("attr name : " + node.getNodeName()+"\n");
						log = log.concat("attr value : " + node.getNodeValue()+"\n");
					}	
				}	
				if (tempNode.hasChildNodes()) {	
					// loop again if has child nodes
					printNote(tempNode.getChildNodes());	
				}					
				log = log.concat("Node Name =" + tempNode.getNodeName() + " [CLOSE]"+"\n");
			}	
		}
	}	
	
	public boolean is_deterministic(){
		
		boolean deterministic = true;
		try	{
			log = "";
			String clause = "";	
			ArrayList<ArrayList<CTransition>> checked = new ArrayList<ArrayList<CTransition>>(); 
			
			deter:for(CState fst : hffsm.getStruct().getR()){				
				ArrayList<CTransition> stx1 = (ArrayList<CTransition>) fst.getLeave().clone();
				ArrayList<CTransition> stx2 = new ArrayList<CTransition>();
				for(CState s : fst.getAncestors()){
					stx2.addAll(s.getLeave());
				}			
				for(CTransition ft: stx1){					
					stx2.remove(ft);					
					for(CTransition ft2 : stx2){
						if(ft.getTarget().equals(ft2.getTarget())){
							continue;
						}
						if(ft.getInput().equals(ft2.getInput())){
							if(islog) log = log.concat("Checking transitions "+ft+" and "+ft2+"\n");
							if(debug) System.out.println("Checking transitions "+ft+" and "+ft2);
							String ex1 = hffsm.getStruct().getTComposition(ft);
							ex1 = ex1.toLowerCase();
							ex1 = ex1.concat(";");
							String z3_1 = getZ3(ex1);
							String ex2 = hffsm.getStruct().getTComposition(ft2);
							ex2 = ex2.toLowerCase();
							ex2 = ex2.concat(";");
							String z3_2 = getZ3(ex2);
							if(z3_1.equals("")) z3_1 = "true";
							if(z3_2.equals("")) z3_2 = "true";
							clause = clause.concat("(push)\n");				
							clause = clause.concat("(assert (and \n");
							clause = clause.concat("    (and "+z3_1+")\n");
							clause = clause.concat("    (and "+z3_2+")\n");
							clause = clause.concat("))\n");
							clause = clause.concat("(check-sat)\n");
							clause = clause.concat("(pop)\n");
							ArrayList<CTransition> see = new ArrayList<CTransition>();
							see.add(ft);
							see.add(ft2);
							checked.add(see);
						}						
					}					
				}
			}
			if(!clause.equals("")){
				String prop_aux = prop.concat(clause);
				// print stm2 file and execute
				//String path = "./"+folder+"/f_deter_"+count+".smt2";
				String path = "./"+folder+"/f_deter.smt2";
				FileHandler fh = new FileHandler();
				fh.print_file(prop_aux, path);
				
				//String[] commands = {"./hffsm/z3","./"+folder+"/f_deter_"+count+".smt2"};
				String[] commands = {"./hffsm/z3","./"+folder+"/f_deter.smt2"};
				String result = fh.getProcessOutput(commands);							
				String[] outs = result.split("\n");
							
				for(int i=0; i<outs.length; i++){
					if(outs[i].equals("sat")){
						System.out.println("Invalid pair of transitions "+checked.get(i));
						return false;
					}
				}
			}			
		}
		catch(Exception ex)		{
			ex.printStackTrace();
			return false;
		}
		return deterministic;
	}	
		
	public boolean is_initially_connected(){
		
		boolean init_con = true;
		try	{			
			log = "";
			ArrayList<CState> states = hffsm.getStruct().getR();
			if(islog) log = log.concat("Conditional States "+states+"\n");		
			//if(islog) log = log.concat("Transitions "+hffsm.getCTransitions()+"\n");		
			if(islog) log = log.concat("Conditional Inputs "+hffsm.getInputAlphabet()+"\n");		
			if(islog) log = log.concat("Outputs "+hffsm.getOutputAlphabet()+"\n");
						
			//find paths
			find_all_paths();
			if(islog) print_paths(path_map);
			//print_paths(path_map);
			if(islog) log = log.concat("\n\nConditional States "+states+"\n\n");
			
			//check valid paths
			boolean epath = check_valid_paths(prop);
			if(!epath){
				System.out.println("At least one state has no path that reach it");
				return false;
			}
			if(islog) log = log.concat("\nRemoving invalid paths\n"+"\n");	
			if(islog) print_paths(path_map);
			//print_paths(path_map);
			
			//check reachability of products
			boolean cpath = check_product_coverage(prop);
			if(!cpath){
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
			ArrayList<CState> states = hffsm.getStruct().getStateSet();
			if(islog) log = log.concat("Conditional States "+states+"\n");		
			//if(islog) log = log.concat("Transitions "+hffsm.getCTransitions()+"\n");		
			if(islog) log = log.concat("Conditional Inputs "+hffsm.getInputAlphabet()+"\n");		
			if(islog) log = log.concat("Outputs "+hffsm.getOutputAlphabet()+"\n");
			
			String[] outs = check_state_pairs();
			
			int count = 0;
			ArrayList<CState> fs_aux = (ArrayList<CState>) states.clone();
			for(CState fs1 : states){
				fs_aux.remove(fs1);
				for(CState fs2 : fs_aux){
					if(outs[count].equals("sat")){
						boolean stilltrue = find_and_check_disting_seq(prop, fs1, fs2, states.size());
						if(!stilltrue){
							return false;
						}
					}
					count++;
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
	
	public boolean find_and_check_disting_seq(String header, CState fs1, CState fs2, int n) 
			throws IOException, InterruptedException{
							
		ArrayList<CTransition> current_out1 = fs1.getLeave();
		ArrayList<CTransition> current_out2 = fs2.getLeave();				
		seq_map = new HashMap<String,ArrayList<CommonPath>>();	
		boolean found_input = false;
						
		//process distinguish seq size 1
		String[] outs = check_common_pair(header, current_out1, current_out2, fs1, fs2);
		
		int count = 0;
		for(String in : hffsm.getInputAlphabet()){			
			seq_map.put(in, new ArrayList<CommonPath>());
			for(CTransition co1 : current_out1){
				for(CTransition co2 : current_out2){
					//check the same input
					if(co1.getInput().equals(in) 
							&& co2.getInput().equals(in)){	
						//Is there a product for both transitions?
						if(outs[count].equals("sat")){
							CommonPath cnew = new CommonPath(fs1, fs2, n);
							//They...
							//1-Can be distinguished? (identification)
							//2-Max size path is less than n-1?
							//3-Is there a valid path (products) for both transitions?
							if(cnew.addCommon(co1, co2)){
								seq_map.get(in).add(cnew);
								found_input = true;
							}
						}
						count++;
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
		if(islog || debug) print_common_pairs(fs1, fs2);	
		
		//check input		
		ArrayList<String> alp = new ArrayList<String>();
		for(String s : hffsm.getInputAlphabet()){
			alp.add(s);
		}			
		for(int i=1; i<=alp.size(); i++){			
			ArrayList<ArrayList<String>> inchecklist = find_inset(i,alp.size(),alp);			
			if(islog) log = log.concat("CHECKING"+"\n");
			if(debug) System.out.println("CHECKING");
			for(ArrayList<String> incheck : inchecklist){				
				if(islog) log = log.concat(incheck+"\n");
				if(debug) System.out.println(incheck);
				ArrayList<String> inputset = check_disting_old(header, fs1, fs2, seq_map, incheck);
				if(inputset != null){					
					if(islog) log = log.concat("\nState pair "+fs1+" and "+fs2+" OK for inputset "+inputset+"\n"+"\n");
					if(debug) System.out.println("\nState pair "+fs1+" and "+fs2+" OK for inputset "+inputset+"\n");
					return true;
				}
			}			
		}	
					
		
		//recursive call to n-1
		for(String in : hffsm.getInputAlphabet()){
			ArrayList<CommonPath> caux = (ArrayList<CommonPath>) seq_map.get(in).clone();
			for(CommonPath cp : caux){
				CState a1 = cp.get1().get(cp.get1().size()-1).getTarget();
				CState a2 = cp.get2().get(cp.get2().size()-1).getTarget();				
				//if both do not lead to the same state, and both are not self loops
				if(!a1.equals(a2) && !(fs1.equals(a1) && (fs2.equals(a2)))){
					seq_map.get(in).clear();					
					if(islog) log = log.concat(" WHAT? "+ fs1+" -> "+a1 + " "+fs2+" -> "+" "+a2+ " input "+in +"\n");
					if(debug) System.out.println(" WHAT? "+ fs1+" -> "+a1 + " "+fs2+" -> "+" "+a2+ " input "+in);
					boolean got = rec_common(header, a1, a2, cp, in);
					if(got){						
						if(islog) log = log.concat("GOT "+ fs1+" "+fs2 + " "+a1+" "+" "+a2+ " "+in+"\n");
						if(debug) System.out.println("GOT "+ fs1+" "+fs2 + " "+a1+" "+" "+a2+ " "+in);
						return true;
					}
				}				
			}
		}
				
		if(islog) log = log.concat("Could not find a seq. for "+ fs1 + " and "+fs2+"\n");
		if(debug) System.out.println("Could not find a seq. for "+ fs1 + " and "+fs2);
		//could not find a distinguishing sequence...
		return false;
	}
		
	public ArrayList<ArrayList<String>> find_inset(int size, int max, ArrayList<String> alp){		
		ArrayList<ArrayList<String>> inchecklist = new ArrayList<ArrayList<String>>();
		for(int i=size; i<=max; i++){
			for(String in1 : alp){
				ArrayList<String> inset = new ArrayList<String>();
				inset.add(in1);
				if(inset.size() < i){	
					ArrayList<String> inset2 = (ArrayList<String>) inset.clone();
					for(String in2 : alp){						
						if(!in1.equals(in2)){							
							inset2.add(in2);
							if(inset2.size() >= i){
								inchecklist.add(inset2);
								inset2 = (ArrayList<String>) inset.clone();
							}
						}						
					}
				}else inchecklist.add(inset);				
			}
		}
		return inchecklist;
	}
	
	public boolean rec_common(String header, CState fs1, CState fs2, CommonPath cp, String lastinput) 
			throws IOException, InterruptedException{
		ArrayList<CTransition> current_out1 = fs1.getLeave();
		ArrayList<CTransition> current_out2 = fs2.getLeave();
		boolean found_input = false;
				
		if(islog) log = log.concat(" TEST0 " + fs1 + " " +fs2+"\n");
		if(debug) System.out.println(" TEST0 " + fs1 + " " +fs2+"\n");
		if(islog || debug) print_common_pairs(fs1, fs2);
		
		String[] outs = check_common_valid_pair(header, current_out1, current_out2, fs1, fs2, cp);
				
		int count = 0;
		for(String in : hffsm.getInputAlphabet()){			
			for(CTransition co1 : current_out1){
				for(CTransition co2 : current_out2){
					//check the same input
					if(co1.getInput().equals(in) 
							&& co2.getInput().equals(in)){	
						//Is there a product for both transitions?
						if(outs[count].equals("sat") && !cp.getDistinguish()){						
							CommonPath cnew = new CommonPath(cp.getS1(), cp.getS2(), cp.getN(), cp.get1(), cp.get2());							
							if(cnew.addCommon(co1, co2)){									
								seq_map.get(lastinput).add(cnew);
								found_input = true;								
							}
						}
						count++;
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
		for(String s : hffsm.getInputAlphabet()){
			alp.add(s);
		}
		for(int i=1; i<=alp.size(); i++){			
			ArrayList<ArrayList<String>> inchecklist = find_inset(i,alp.size(),alp);			
			if(islog) log = log.concat("CHECKING 2"+"\n");
			if(debug) System.out.println("CHECKING 2");
			for(ArrayList<String> incheck : inchecklist){				
				if(islog) log = log.concat(incheck+"\n");
				if(debug) System.out.println(incheck);
				ArrayList<String> inputset = check_disting_old(header, fs1, fs2, seq_map, incheck);
				if(inputset != null){					
					if(islog) log = log.concat("\nState pair "+fs1+" and "+fs2+" OK for inputset "+inputset+"\n"+"\n");
					if(debug) System.out.println("\nState pair "+fs1+" and "+fs2+" OK for inputset "+inputset+"\n");
					return true;
				}
			}			
		}	
		
		
		ArrayList<CommonPath> caux = seq_map.get(lastinput);
		for(CommonPath p : caux){
			CState a1 = p.get1().get(p.get1().size()-1).getTarget();
			CState a2 = p.get2().get(p.get2().size()-1).getTarget();
			String in = p.get1().get(p.get1().size()-1).getInput();
			CState a11 = p.get1().get(p.get1().size()-1).getSource();
			CState a22 = p.get2().get(p.get2().size()-1).getSource();				
			if(!a1.equals(a2) && !(a11.equals(a1) && (a22.equals(a2)))){				
				if(islog) log = log.concat("\n States \n"+a1+" "+a2+" "+a11+" "+a22+ " "+in+"\n");
				if(islog) log = log.concat("\nGoing recursive\n"+in+"\n");
				if(debug) System.out.println("\n States \n"+a1+" "+a2+" "+a11+" "+a22+ " "+in+"\n");
				if(debug) System.out.println("\nGoing recursive\n"+in+"\n");
				boolean got = rec_common(header, a1, a2, p, in);
				if(got){
					return true;
				}
			}				
		}		
		
		return false;
	}
	
	private ArrayList<String> check_disting(String header, ArrayList<String> alp, CState fs1, CState fs2, 
			Map<String,ArrayList<CommonPath>> map) 
			throws IOException, InterruptedException{
				
		String clause = "";
		ArrayList<ArrayList<String>> inputsetlist = new ArrayList<ArrayList<String>>();
		
		for(int i=1; i<=alp.size(); i++){			
			ArrayList<ArrayList<String>> inchecklist = find_inset(i,alp.size(),alp);
			for(ArrayList<String> incheck : inchecklist){	
				if(islog) log = log.concat(incheck+"\n");
				if(debug) System.out.println(incheck);
				
				ArrayList<String> inputset = new ArrayList<String>();
				String ex1 = hffsm.getStruct().getFComposition(fs1);
				ex1 = ex1.toLowerCase();
				ex1 = ex1.concat(";");
				String z3_1 = getZ3(ex1);
				String ex2 = hffsm.getStruct().getFComposition(fs2);
				ex2 = ex2.toLowerCase();
				ex2 = ex2.concat(";");
				String z3_2 = getZ3(ex2);
				if(z3_1.equals("")) z3_1 = "true";
				if(z3_2.equals("")) z3_2 = "true";
				clause = clause.concat("(push)\n");
				clause = clause.concat("(assert (and "+z3_1+" "+z3_2+"))\n");
				clause = clause.concat("(assert (and \n");
				for(String in : incheck){			
					ArrayList<CommonPath> caux = map.get(in);
					for(CommonPath cp : caux){				
						if(cp.getDistinguish()){	
							String inaux = "";
							clause = clause.concat("    (not (and ");
							for(CTransition t : cp.get1()){
								inaux = inaux.concat(t.getInput()+ "+");
								String ex = hffsm.getStruct().getTComposition(t);
								ex = ex.toLowerCase();
								ex = ex.concat(";");
								String z3 = getZ3(ex);								
								if(z3.equals("")) z3 = " true";
								clause = clause.concat(z3+" ");
							}
							for(CTransition t : cp.get2()){
								String ex = hffsm.getStruct().getTComposition(t);
								ex = ex.toLowerCase();
								ex = ex.concat(";");
								String z3 = getZ3(ex);								
								if(z3.equals("")) z3 = " true";
								clause = clause.concat(z3+" ");
							}
							clause = clause.concat("))\n");	
							inputset.add(inaux);
						}				
					}
				}				
				clause = clause.concat("))\n");
				clause = clause.concat("(check-sat)\n");
				clause = clause.concat("(pop)\n");
				inputsetlist.add(inputset);				
			}
		}		
		if(!clause.equals("")){
			String prop_aux = header.concat(clause);
			// print stm2 file and execute
			String fpath = "./"+folder+"/f_dpair.smt2";
			FileHandler fh = new FileHandler();
			fh.print_file(prop_aux, fpath);
			
			String[] commands = {"./hffsm/z3","./"+folder+"/f_dpair.smt2"};
			String result = fh.getProcessOutput(commands);		
			String[] outs = result.split("\n");
			for(int i=0; i<inputsetlist.size(); i++){
				if(outs[i].equals("unsat")){			
					return inputsetlist.get(i);
				}
			}
		}		
		return null;
	}
	
	public ArrayList<String> check_disting_old(String header, CState fs1, CState fs2, 
			Map<String,ArrayList<CommonPath>> map, ArrayList<String> inputcheck) 
			throws IOException, InterruptedException{
				
		String clause = "";
		ArrayList<String> inputset = new ArrayList<String>();
		String ex1 = hffsm.getStruct().getFComposition(fs1);
		ex1 = ex1.toLowerCase();
		ex1 = ex1.concat(";");
		String z3_1 = getZ3(ex1);
		String ex2 = hffsm.getStruct().getFComposition(fs2);
		ex2 = ex2.toLowerCase();
		ex2 = ex2.concat(";");
		String z3_2 = getZ3(ex2);
		if(z3_1.equals("")) z3_1 = "true";
		if(z3_2.equals("")) z3_2 = "true";
		clause = clause.concat("(assert (and "+z3_1+" "+z3_2+"))\n");
		clause = clause.concat("(assert (and \n");
		for(String in : inputcheck){			
			ArrayList<CommonPath> caux = map.get(in);
			for(CommonPath cp : caux){				
				if(cp.getDistinguish()){	
					String inaux = "";
					clause = clause.concat("    (not (and ");
					for(CTransition t : cp.get1()){
						inaux = inaux.concat(t.getInput() + "+");
						String ex = hffsm.getStruct().getTComposition(t);
						ex = ex.toLowerCase();
						ex = ex.concat(";");
						String z3 = getZ3(ex);								
						if(z3.equals("")) z3 = " true";
						clause = clause.concat(z3+" ");
					}
					for(CTransition t : cp.get2()){
						String ex = hffsm.getStruct().getTComposition(t);
						ex = ex.toLowerCase();
						ex = ex.concat(";");
						String z3 = getZ3(ex);								
						if(z3.equals("")) z3 = " true";
						clause = clause.concat(z3+" ");
					}
					clause = clause.concat("))\n");	
					inputset.add(inaux);
				}				
			}
		}				
		clause = clause.concat("))\n");
		clause = clause.concat("(check-sat)");
		String prop_aux = header.concat(clause);
		// print stm2 file and execute
		String fpath = "./"+folder+"/f_dpair.smt2";
		FileHandler fh = new FileHandler();
		fh.print_file(prop_aux, fpath);
		
		String[] commands = {"./ffsm/z3","./"+folder+"/f_dpair.smt2"};
		String result = fh.getProcessOutput(commands);		
		String[] outs = result.split("\n");
						
		if(outs[0].equals("unsat")){			
			return inputset;						
		}else{
			return null;
		}		
	}	
	
	public String[] check_state_pairs() 
			throws IOException, InterruptedException{
				
		String clause = "";		
		ArrayList<CState> states = hffsm.getStruct().getStateSet();
		ArrayList<CState> fs_aux = (ArrayList<CState>) states.clone();
		for(CState fs1 : states){
			fs_aux.remove(fs1);
			for(CState fs2 : fs_aux){
				String ex1 = hffsm.getStruct().getFComposition(fs1);
				ex1 = ex1.toLowerCase();
				ex1 = ex1.concat(";");
				String z3_1 = getZ3(ex1);
				String ex2 = hffsm.getStruct().getFComposition(fs2);
				ex2 = ex2.toLowerCase();
				ex2 = ex2.concat(";");
				String z3_2 = getZ3(ex2);
				if(z3_1.equals("")) z3_1 = "true";
				if(z3_2.equals("")) z3_2 = "true";
				clause = clause.concat("(push)\n");
				clause = clause.concat("(assert (and "+z3_1+" "+z3_2+"))\n");		
				clause = clause.concat("(check-sat)\n");
				clause = clause.concat("(pop)\n");			
			}
		}	
		if(!clause.equals("")){
			String prop_aux = prop.concat(clause);
			// print stm2 file and execute
			String fpath = "./"+folder+"/f_cscpair.smt2";
			FileHandler fh = new FileHandler();
			fh.print_file(prop_aux, fpath);
			
			String[] commands = {"./hffsm/z3","./"+folder+"/f_cscpair.smt2"};
			String result = fh.getProcessOutput(commands);		
			String[] outs = result.split("\n");
			return outs;
		}
		return new String[0];
	}
		
	private String[] check_common_pair(String header, ArrayList<CTransition> current_out1,
			ArrayList<CTransition> current_out2, CState fs1, CState fs2) 
			throws IOException, InterruptedException{
			
		String clause = "";
		for(String in : hffsm.getInputAlphabet()){
			for(CTransition co1 : current_out1){
				for(CTransition co2 : current_out2){
					//check the same input
					if(co1.getInput().equals(in) 
							&& co2.getInput().equals(in)){		
						String ex1 = hffsm.getStruct().getFComposition(fs1);
						ex1 = ex1.toLowerCase();
						ex1 = ex1.concat(";");
						String z3_1 = getZ3(ex1);
						
						String ex2 = hffsm.getStruct().getFComposition(fs2);
						ex2 = ex2.toLowerCase();
						ex2 = ex2.concat(";");
						String z3_2 = getZ3(ex2);
						if(z3_1.equals("")) z3_1 = "true";
						if(z3_2.equals("")) z3_2 = "true";
						
						clause = clause.concat("(push)\n");
						clause = clause.concat("(assert (and "+z3_1+" "+z3_2+"))\n");
						clause = clause.concat("(assert (and ");
						
						String ex = hffsm.getStruct().getTComposition(co1);
						ex = ex.toLowerCase();
						ex = ex.concat(";");
						String z3 = getZ3(ex);								
						if(z3.equals("")) z3 = "true";
						clause = clause.concat(z3+" ");	
						
						ex = hffsm.getStruct().getTComposition(co2);
						ex = ex.toLowerCase();
						ex = ex.concat(";");
						z3 = getZ3(ex);								
						if(z3.equals("")) z3 = "true";
						clause = clause.concat(z3);
						clause = clause.concat("))\n");
						clause = clause.concat("(check-sat)\n");
						clause = clause.concat("(pop)\n");						
					}					
				}
			}	
		}	
		String prop_aux = header.concat(clause);
		// print stm2 file and execute
		String fpath = "./"+folder+"/f_ccpair.smt2";
		FileHandler fh = new FileHandler();
		fh.print_file(prop_aux, fpath);
		
		String[] commands = {"./hffsm/z3","./"+folder+"/f_ccpair.smt2"};
		String result = fh.getProcessOutput(commands);		
		String[] outs = result.split("\n");
			
		return outs;
	}
	
	private String[] check_common_valid_pair(String header, ArrayList<CTransition> current_out1,
			ArrayList<CTransition> current_out2, CState fs1, CState fs2, CommonPath cp) 
			throws IOException, InterruptedException{
			
		String clause = "";
		for(String in : hffsm.getInputAlphabet()){
			for(CTransition co1 : current_out1){
				for(CTransition co2 : current_out2){
					//check the same input
					if(co1.getInput().equals(in) 
							&& co2.getInput().equals(in)){	
						String ex1 = hffsm.getStruct().getFComposition(fs1);
						ex1 = ex1.toLowerCase();
						ex1 = ex1.concat(";");
						String z3_1 = getZ3(ex1);
						String ex2 = hffsm.getStruct().getFComposition(fs2);
						ex2 = ex2.toLowerCase();
						ex2 = ex2.concat(";");
						String z3_2 = getZ3(ex2);
						if(z3_1.equals("")) z3_1 = "true";
						if(z3_2.equals("")) z3_2 = "true";
						clause = clause.concat("(push)\n");
						clause = clause.concat("(assert (and "+z3_1+" "+z3_2+"))\n");
						clause = clause.concat("(assert (and ");
						for(CTransition t : cp.get1()){
							String ex = hffsm.getStruct().getTComposition(t);
							ex = ex.toLowerCase();
							ex = ex.concat(";");
							String z3 = getZ3(ex);								
							if(z3.equals("")) z3 = " true";
							clause = clause.concat(z3+" ");
						}
						for(CTransition t : cp.get2()){
							String ex = hffsm.getStruct().getTComposition(t);
							ex = ex.toLowerCase();
							ex = ex.concat(";");
							String z3 = getZ3(ex);								
							if(z3.equals("")) z3 = " true";
							clause = clause.concat(z3+" ");
						}
						String ex = hffsm.getStruct().getTComposition(co1);
						ex = ex.toLowerCase();
						ex = ex.concat(";");
						String z3 = getZ3(ex);								
						if(z3.equals("")) z3 = "true";
						clause = clause.concat(z3+" ");	
						
						ex = hffsm.getStruct().getTComposition(co2);
						ex = ex.toLowerCase();
						ex = ex.concat(";");
						z3 = getZ3(ex);								
						if(z3.equals("")) z3 = "true";
						clause = clause.concat(z3);
						clause = clause.concat("))\n");
						clause = clause.concat("(check-sat)\n");
						clause = clause.concat("(pop)\n");						
					}					
				}
			}	
		}	
		String prop_aux = header.concat(clause);
		// print stm2 file and execute
		String fpath = "./"+folder+"/f_ccpairpath.smt2";
		FileHandler fh = new FileHandler();
		fh.print_file(prop_aux, fpath);
		
		String[] commands = {"./hffsm/z3","./"+folder+"/f_ccpairpath.smt2"};
		String result = fh.getProcessOutput(commands);		
		String[] outs = result.split("\n");
			
		return outs;
	}
	
	public boolean check_product_coverage(String header) throws IOException, InterruptedException{
		
		boolean init_con = true;
		String clause = "";
		
		for(CState s: path_map.keySet()){
			if(path_map.get(s) != null){
				String ex = hffsm.getStruct().getFComposition(s);
				ex = ex.toLowerCase();
				ex = ex.concat(";");
				String z3 = getZ3(ex);
				if(z3.equals("")) z3 = "true";
				clause = clause.concat("(push)\n");
				clause = clause.concat("(assert "+z3+")\n");
				clause = clause.concat("(assert (and \n");
				for(ArrayList<CTransition> path : path_map.get(s)){
					clause = clause.concat("    (not (and ");
					for(CTransition t : path){
						ex = hffsm.getStruct().getTComposition(t);
						ex = ex.toLowerCase();
						ex = ex.concat(";");
						z3 = getZ3(ex);
						if(z3.equals("")) z3 = " true ";
						clause = clause.concat(z3);
					}
					clause = clause.concat("))\n");
				}
				clause = clause.concat("))\n");
				clause = clause.concat("(check-sat)\n");
				clause = clause.concat("(pop)\n");
			}			
		}
		if(!clause.equals("")){
			String prop_aux = header.concat(clause);
			// print stm2 file and execute
			String fpath = "./"+folder+"/f_cpath.smt2";
			FileHandler fh = new FileHandler();
			fh.print_file(prop_aux, fpath);
			
			String[] commands = {"./hffsm/z3","./"+folder+"/f_cpath.smt2"};
			String result = fh.getProcessOutput(commands);				
			String[] outs = result.split("\n");
			int count = 0;
			for(CState s: path_map.keySet()){
				if(path_map.get(s) != null){
					if(outs[count].equals("sat")){					
						if(islog) log = log.concat("Conditional state "+s +" cannot be reached by all products"+"\n");
						System.out.println("Conditional state "+s +" cannot be reached by all products"+"\n");
						return false;						
					}else{					
						if(islog) log = log.concat("Conditional state "+s +" OK"+"\n");
					}
					count++;
				}
			}
		}
		return init_con;
	}
		
	public boolean check_valid_paths(String header) throws IOException, InterruptedException{
		
		boolean init_con = true;
		String clauseAll = "";
		
		for(CState s: path_map.keySet()){	
			if(path_map.get(s) != null){				
				for(ArrayList<CTransition> path : path_map.get(s)){	
					String ex = hffsm.getStruct().getFComposition(s);
					ex = ex.toLowerCase();
					ex = ex.concat(";");
					String z3 = getZ3(ex);
					if(z3.equals("")) z3 = "true";
					String clause = "";
					clause = clause.concat("(push)\n");
					clause = clause.concat("(assert "+z3+")\n");
					clause = clause.concat("(assert (and ");
					for(CTransition t : path){
						ex = hffsm.getStruct().getTComposition(t);
						ex = ex.toLowerCase();
						ex = ex.concat(";");
						z3 = getZ3(ex);
						if(z3.equals("")) z3 = " true ";
						clause = clause.concat(z3);
					}
					clause = clause.concat("))\n");
					clause = clause.concat("(check-sat)\n");
					clause = clause.concat("(pop)\n");
					//System.out.println("Path "+path+"\n Clause\n"+clause);
					clauseAll = clauseAll.concat(clause);
				}				
			}			
		}
		if(!clauseAll.equals("")){
			String prop_aux = header.concat(clauseAll);
			// print stm2 file and execute
			String fpath = "./"+folder+"/f_vpath.smt2";
			FileHandler fh = new FileHandler();
			fh.print_file(prop_aux, fpath);
			
			String[] commands = {"./hffsm/z3","./"+folder+"/f_vpath.smt2"};
			String result = fh.getProcessOutput(commands);					
			String[] outs = result.split("\n");
			//System.out.println("Result "+result);
			int count = 0;
			for(CState s: path_map.keySet()){				
				if(path_map.get(s) != null){					
					ArrayList<ArrayList<CTransition>> aux_paths = 
							(ArrayList<ArrayList<CTransition>>) path_map.get(s).clone();
					for(ArrayList<CTransition> path : aux_paths){	
						//System.out.println("Count +"+count +" State "+s+" path "+path+ " on "+outs[count]);
						if(outs[count].equals("unsat")){
							//System.out.println("Remove "+path);
							path_map.get(s).remove(path);								
						}
						count++;
					}
					if(path_map.get(s).size() < 1){
						return false;
					}
				}
			}
		}		
		return init_con;
	}

	public void find_all_paths(){
		ArrayList<CTransition> T = hffsm.getTransitions();
		StateStructure S = hffsm.getStruct();
		no_loop_ft = (ArrayList<CTransition>) T.clone();
		for(CTransition ft : T){
			if(ft.getSource().equals(ft.getTarget())){
				no_loop_ft.remove(ft);
			}
		}
		CState root = S.getRoot();
		ArrayList<CState> R = S.getR();		
		ArrayList<CState> ddesc = S.getDefaultDesc(root);		
		ArrayList<CState> d = (ArrayList<CState>) ddesc.clone();
		for(CState s : d){
			if(!R.contains(s)){
				ddesc.remove(s);
			}
		}			
		ArrayList<CTransition> current_out = new ArrayList<CTransition>();
		for(CState s : ddesc){
			ArrayList<CTransition> leave = s.getLeave();
			current_out.addAll(leave);
			for(CTransition ft : leave){
				if(ft.getSource().equals(ft.getTarget())){
					current_out.remove(ft);
				}
			}
		}		 		
		found_fc = new ArrayList<CState>();
		nfound_fc = (ArrayList<CState>) R.clone();		
		nfound_fc.removeAll(ddesc);
		found_fc.addAll(ddesc);
					
		path_map = new HashMap<CState,ArrayList<ArrayList<CTransition>>>();
		
		for(CState s : nfound_fc){
			path_map.put(s, new ArrayList<ArrayList<CTransition>>());
		}			
		//process initial c. state
		for(CTransition ct : current_out){			
			if(no_loop_ft.contains(ct)){				
				ArrayList<CState> enter = getEnterSet(S,ct);
				//System.out.println(ct+" enter "+enter);
				ArrayList<CState> d1 = (ArrayList<CState>) enter.clone();				
				for(CState s : d1){
					if(!R.contains(s)){
						enter.remove(s);
					}
				}				
				if(!found_fc.contains(ct.getTarget())){					
					nfound_fc.removeAll(enter);
					found_fc.addAll(enter);					
				}					
				ArrayList<CTransition> current_path = new ArrayList<CTransition>();
				current_path.add(ct);
				for(CState s: enter){
					ArrayList<ArrayList<CTransition>> c_paths = path_map.get(s);					
					c_paths.add(current_path);
					path_map.put(s, c_paths);
				}				
			}				
		}						
		ArrayList<CState> found_aux = (ArrayList<CState>) found_fc.clone();
		for(CState cs : found_aux){
			if(!ddesc.contains(cs)){
				rec_find_path(cs);
			}				
		}
	}
	private ArrayList<CState> getEnterSet(StateStructure S, CTransition ct){
		ArrayList<CState> ddesc1 = S.getDefaultDesc(ct.getTarget());		
		ArrayList<CState> enter = new ArrayList<CState>();
		enter.addAll(ddesc1);
		enter.addAll(ct.getTarget().getAncestors());
		ArrayList<CState> pair = new ArrayList<CState>();
		pair.add(ct.getTarget());
		pair.add(ct.getSource());
		enter.removeAll(S.getAncestors(pair));	
		ArrayList<CState> ddesc = S.getDefaultDesc(S.getRoot());
		enter.removeAll(ddesc);
		return enter;
	}
	
	public void rec_find_path(CState current){		
		StateStructure S = hffsm.getStruct();
		ArrayList<CState> R = S.getR();
		ArrayList<CState> ddesc = S.getDefaultDesc(current);
		ddesc.add(current);		
		ArrayList<CState> d = (ArrayList<CState>) ddesc.clone();
		for(CState s : d){
			if(!R.contains(s)){
				ddesc.remove(s);
			}
		}
		
		ArrayList<CTransition> current_out = new ArrayList<CTransition>();
		for(CState s : ddesc){
			current_out.addAll(s.getLeave());
		}
		for(CTransition ct : current_out){
			if(no_loop_ft.contains(ct)){		
				ArrayList<CState> enter = getEnterSet(S,ct);
				ArrayList<CState> d1 = (ArrayList<CState>) enter.clone();
				for(CState s : d1){
					if(!R.contains(s)){
						enter.remove(s);
					}
				}
				ArrayList<ArrayList<CTransition>> c_paths = path_map.get(ct.getTarget());
				ArrayList<ArrayList<CTransition>> lc_paths = path_map.get(current);	
				
				prepath: for(ArrayList<CTransition> inc_path : lc_paths){					
					//if this c. state was found before
					for(CTransition c : inc_path){
						if(c.getTarget().equals(ct.getTarget())){
							continue prepath;
						}
					}
					ArrayList<CTransition> new_path = new ArrayList<CTransition>();
					CState last = inc_path.get(inc_path.size()-1).getSource();
					if(!last.equals(ct.getTarget()) && c_paths != null){						
						new_path.addAll(inc_path);
						new_path.add(ct);
						if(!c_paths.contains(new_path)){
							c_paths.add(new_path);							
						}						
					}					
				}	
				for(CState s: enter){
					path_map.put(s, c_paths);
				}				
				if(!found_fc.contains(ct.getTarget())){					
					nfound_fc.removeAll(enter);
					found_fc.addAll(enter);
					for(CState s : enter){
						if(!found_fc.contains(s)){	
							rec_find_path(s);
						}						
					}
				}				
			}			
		}		
	}	
	
	public void print_paths(Map<CState,ArrayList<ArrayList<CTransition>>> path_map){			
		System.out.println("\nPrinting conditional state paths");
		for(CState s: path_map.keySet()){
			System.out.println(s);
			int count = 0;
			if(path_map.get(s) != null){
				for(ArrayList<CTransition> path : path_map.get(s)){
					count++;
					System.out.println("Path "+count+": "+path+"\n");
				}
			}			
		}
	}
	
	public void print_common_pairs(CState fs1, CState fs2){				
		if(islog) log = log.concat("Conditional State pair "+ fs1 + " and " + fs2+"\n");	
		if(debug) System.out.println("Conditional State pair "+ fs1 + " and " + fs2);
		for(String in : hffsm.getInputAlphabet()){			
			if(islog) log = log.concat("  Input "+in+"\n");
			if(debug) System.out.println("  Input "+in);
			ArrayList<CommonPath> caux = seq_map.get(in);
			for(CommonPath cp : caux){				
				if(islog) log = log.concat("     Pair "+cp.get1()+ " "+ cp.get2()+"\n");
				if(debug) System.out.println("     Pair "+cp.get1()+ " "+ cp.get2());
			}
		}
	}
	
}
