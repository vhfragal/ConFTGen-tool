package br.usp.icmc.reader;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import br.usp.icmc.uml.CState;
import br.usp.icmc.uml.CState.StateType;
import br.usp.icmc.uml.CTransition;
import br.usp.icmc.uml.HFFSM;

public class UMLModelReader {
	protected File file;
	protected boolean validFile;
	protected HFFSM hsm;
	//protected HashMap<String, CState> cstates;
	
	protected static Document doc;
	protected static CState root;
	protected static ArrayList<CState> states;
	protected static ArrayList<CTransition> transitions;
	
	public UMLModelReader(File file) {
		this.file = file;
		// hsm = new HStateMachine();
		// cstates = new HashMap<String, State>();

		read();
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public HFFSM getHsm() throws Exception {
		// if (hsm.getStates().size() == 0 || hsm.getTransitions().size() == 0)
		// throw new Exception("Empty FSM");

		return hsm;
	}

	protected void addTransition(String line) throws Exception {

	}

	protected void read() {
		validFile = true;

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();
			//simple test
			//basicTest(doc);			

			//print states
			Element elements = doc.getDocumentElement();				
			NodeList nodeList = elements.getChildNodes();
			//int level = 1;
			//printStates(nodeList, level);
			
			//find state tree
			root = null;
			states = new ArrayList<CState>();
			transitions = new ArrayList<CTransition>();
			findStates(nodeList, null);
			findTransitions(nodeList);
			
			hsm = new HFFSM(root, states, transitions);
			
		} catch (Exception e) {
			validFile = false;
			e.printStackTrace();
		}
	}
	
	private static void findTransitions(NodeList nodeList) throws Exception {
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {	
					Element eElement = (Element) node;
					if(node.getNodeName().equals("transition")){
						if (eElement.getAttribute("xmi:type").equals("uml:Transition")) {
							createTransition(node); 
							findTransitions(node.getChildNodes());
							continue;
						}
					}
					findTransitions(node.getChildNodes());
				}
			}
		}
	}
	
	public static CTransition createTransition(Node node) throws Exception{
		Element eElement = (Element) node;
		String ct = eElement.getAttribute("name");
		String id = eElement.getAttribute("xmi:id");
		String source = eElement.getAttribute("source");
		String target = eElement.getAttribute("target");
		//check if it connect two relevant states
		boolean f1 = false;
		boolean f2 = false;
		CState a = null;
		CState b = null;
		for(CState s : states){
			if(s.getID().equals(source)){
				f1 = true;
				a = s;
			}
			if(s.getID().equals(target)){
				f2 = true;
				b = s;
			}
		}
		if(!f1 || !f2) return null;
		String cond = "";
		String guard = "";
		String input = ct.substring(0,ct.indexOf("/"));
		String output = ct.substring(ct.indexOf("/")+1,ct.length());
		if(ct.indexOf("(") > 0){
			cond = ct.substring(ct.indexOf("(")+1, ct.lastIndexOf(")"));
			input = ct.substring(0,ct.indexOf("("));
		}
		if(ct.indexOf("[") > 0){
			guard = ct.substring(ct.indexOf("[")+1, ct.lastIndexOf("]"));
			input = ct.substring(0,ct.indexOf("["));
		}
		String[] glist = guard.split(",");
		ArrayList<CState> sts = new ArrayList<CState>();
		for(String g : glist){
			for(CState s : states){
				if(s.getLabel().equals(g)){
					sts.add(s);
					break;
				}
			}			
		}
			
		CTransition t = new CTransition(input, sts, cond, output, a, b, id);
		transitions.add(t);
		return t;
	}
	
	private static void findStates(NodeList nodeList, CState superState) throws Exception {
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {	
					Element eElement = (Element) node;
					if(node.getNodeName().equals("region")){
						if (eElement.getAttribute("xmi:type").equals("uml:Region")) {
							CState st = createState(node,true);
							//region states 
							if(st.getType().equals(StateType.region)){
								ArrayList<CState> sub = superState.getSubstates();
								sub.add(st);
								superState.setSubstates(sub);
								st.setSuperState(superState);
								findStates(node.getChildNodes(),st);
								continue;
							}
						}
					}
					if(node.getNodeName().equals("subvertex")){
						if (eElement.getAttribute("xmi:type").equals("uml:State")) {							
							CState st = createState(node,false);							
							//simple states 
							if(st.getType().equals(StateType.simple)){
								ArrayList<CState> sub = superState.getSubstates();
								sub.add(st);
								superState.setSubstates(sub);
								st.setSuperState(superState);
							}
							//states with one region 
							if(st.getType().equals(StateType.compositeOr)){
								ArrayList<CState> sub = superState.getSubstates();
								sub.add(st);
								superState.setSubstates(sub);
								st.setSuperState(superState);
								findStates(node.getChildNodes(),st);
								continue;
							}
							//states with several regions
							if(st.getType().equals(StateType.compositeAnd)){
								ArrayList<CState> sub = superState.getSubstates();
								sub.add(st);
								superState.setSubstates(sub);
								st.setSuperState(superState);
								NodeList as = node.getChildNodes();
								for(int e=0; e<eElement.getChildNodes().getLength(); e++){
									if(as.item(e)!=null && as.item(e).getNodeName()!=null){
										if(as.item(e).getNodeName().equals("region")){
											CState st2 = createState(as.item(e), true);
											ArrayList<CState> sub2 = st.getSubstates();
											sub2.add(st2);
											st.setSubstates(sub2);
											st2.setSuperState(st);
											findStates(as.item(e).getChildNodes(),st2);
										}
									}									
								}
								continue;
							}
						}
					}	
					//root 	<packagedElement xmi:type="uml:StateMachine"					
					if(root==null && node.getNodeName().equals("packagedElement")){
						if (eElement.getAttribute("xmi:type").equals("uml:StateMachine")) {
							CState rt = createRoot(node);
							findStates(node.getChildNodes(),rt);
							return;
						}
					}
					findStates(node.getChildNodes(),superState);
				}
			}
		}
	}
	public static CState createState(Node node, boolean isRegion) throws Exception{
		Element eElement = (Element) node;
		String state = eElement.getAttribute("name");
		String id = eElement.getAttribute("xmi:id");
		String cond = "";
		String label = state;
		if(state.indexOf("(") > 0){
			cond = state.substring(state.indexOf("(")+1, state.lastIndexOf(")"));
			label = state.substring(0,state.indexOf("("));
		}	
		Boolean isdefault = false;
		StateType st = null;		
		if(isRegion){
			st = StateType.region;
			isdefault = true;
		}else{
			st = state_type(node);			
			isdefault = check_default(node, id);
		}		
		CState s = new CState(label, cond, st, isdefault, id);
		states.add(s);
		return s;
	}
	
	public static boolean check_default(Node iNode, String id){		
		Element parent = (Element) iNode.getParentNode();	
		NodeList nodeList = parent.getChildNodes();	
		ArrayList<String> pid = new ArrayList<String>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {	
				Element eElement = (Element) node;					
				if(node.getNodeName().equals("subvertex")){
					if (eElement.getAttribute("xmi:type").equals("uml:Pseudostate")) {
						pid.add(eElement.getAttribute("xmi:id"));						
					}
				}
			}
		}
		NodeList nList = doc.getElementsByTagName("transition");
		for (int i = 0; i < nList.getLength(); i++) {
			Node node = nList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {	
				Element eElement = (Element) node;					
				if (eElement.getAttribute("xmi:type").equals("uml:Transition") &&
						pid.contains(eElement.getAttribute("source"))&&
						eElement.getAttribute("target").equals(id)) {
					return true;						
				}
			}
		}
		return false;
	}
	
	public static CState createRoot(Node node) throws Exception{
		Element eElement = (Element) node;
		String state = eElement.getAttribute("name");
		String id = eElement.getAttribute("xmi:id");
		String cond = "";
		String label = state;
		if(state.indexOf("|") > 0){
			cond = state.substring(state.indexOf("|")+1, state.lastIndexOf("|"));
			label = state.substring(0,state.indexOf("|"));
		}
		CState s = new CState(label, cond, state_type(node), true, id);		  
		root = s;
		root.setSuperState(null);
		states.add(s);
		return s;
	}
	
	public static StateType state_type(Node node){
		Element eElement = (Element) node;
		NodeList as = node.getChildNodes();
		int regs = 0;
		for(int e=0; e<eElement.getChildNodes().getLength(); e++){
			if(as.item(e).getNodeName().equals("region")){
				regs++;
			}
		}
		if(regs == 0) return StateType.simple;
		if(regs == 1) return StateType.compositeOr;
		if(regs > 1) return StateType.compositeAnd;
		return null;
	}
	
	/*
	public void basicTest(Document doc){
		System.out.println("Root element :"	+ doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("subvertex");
		System.out.println("----------------------------");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				if (eElement.getAttribute("xmi:type").equals("uml:State")) {
					System.out.println("State: "
							+ eElement.getAttribute("name"));
				}					
			}
		}
	}
	
	
	private static void printStates(NodeList nodeList, int level) {
		level++;
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {					
					String space = getAlign(level);					
					Element eElement = (Element) node;
					//simple states
					if(node.getNodeName().equals("subvertex")){
						if (eElement.getAttribute("xmi:type").equals("uml:State")) {
							System.out.println(space+ eElement.getAttribute("name"));
							if(eElement.getChildNodes().getLength()>1){
								NodeList as = node.getChildNodes();
								boolean found = false;
								for(int e=0; e<eElement.getChildNodes().getLength(); e++){
									if(as.item(e).getNodeName().equals("region")){
										found = true;
										break;
									}
								}
								if(found) level++;
								for(int e=0; e<eElement.getChildNodes().getLength(); e++){
									if(as.item(e)!=null && as.item(e).getNodeName()!=null){
										if(as.item(e).getNodeName().equals("region")){
											String space2 = getAlign(level);
											Element eElement2 = (Element) as.item(e);
											System.out.println(space2+ eElement2.getAttribute("name"));
											printStates(as.item(e).getChildNodes(), level);
										}
									}									
								}
								if(found) continue;
							}
						}
					}	
					//root 	<packagedElement xmi:type="uml:StateMachine"
					if(node.getNodeName().equals("packagedElement")){
						if (eElement.getAttribute("xmi:type").equals("uml:StateMachine")) {
							System.out.println(space+ eElement.getAttribute("name"));							
						}
					}
					printStates(node.getChildNodes(), level);					
				}
			}
		}
	}
	
	public static String getAlign(int level){
		String space = "";
		for(int x=1; x<level; x++){
			space = space.concat("| ");
		}
		if(level > 1) space = space.concat(" |__");
		return space;
	}*/

	public boolean isValidFile() {
		return validFile;
	}
}
