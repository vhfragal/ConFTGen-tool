package br.usp.icmc.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import br.usp.icmc.feature.logic.FFSMProperties;
import br.usp.icmc.ffsm.Compose;
import br.usp.icmc.uml.CState;
import br.usp.icmc.uml.CState.StateType;
import br.usp.icmc.uml.CTransition;
import br.usp.icmc.uml.HFFSM;
import br.usp.icmc.uml.HFFSMProperties;
import parser.yakindu.FCONSTRAINT;
import parser.yakindu.SimpleNode;

public class FFSMModelReaderYAKINDU {
	protected File file;
	protected boolean validFile;
	protected HFFSM hsm;	
	//protected HashMap<String, CState> cstates;
	
	protected static Document doc;
	protected static CState root;
	protected static ArrayList<CState> states;
	protected static ArrayList<CTransition> transitions;
	static HFFSMProperties p;
	static FFSMProperties pp;
	static Reader r;
	
	static String header;
	static String project_path;
	static String inner_path;
	
	static ArrayList<String> ids;
	static ArrayList<Node> nodes;
	protected static ArrayList<CState> leafs_in;
	protected static ArrayList<CState> leafs_out;
	static ArrayList<CTransition> done;
	static ArrayList<CTransition> removed;
	
	public FFSMModelReaderYAKINDU(File file) {
		this.file = file;
		// hsm = new HStateMachine();
		// cstates = new HashMap<String, State>();

		read();
	}
	
	public ArrayList<CTransition> getTransitions(){
		return done;
	}
	
	public void removeIDs(ArrayList<String> ids, String op){
		
		Element elements = doc.getDocumentElement();				
		NodeList nodeList = elements.getChildNodes();
		FFSMModelReaderYAKINDU.ids = ids;
		
		nodes = new ArrayList<Node>();
		
		try {
			//find invalid
			
			System.out.println("IDS");
			for(String i : ids){
				System.out.println(i);
			}
			System.out.println();
			
			findNodes(nodeList);
			
			//update connections
			updateNodes(nodeList, op);
			//remove			
			for(Node n : nodes){				
				Node father = n.getParentNode();
				if(father != null) father.removeChild(n);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	private static void updateNodes(NodeList nodeList, String op) throws Exception {
		
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {	
					Element eElement = (Element) node;					
					if(node.getNodeName().equals("vertices")){
						if (eElement.getAttribute("xsi:type").equals("sgraph:State")) {							
							String inc = eElement.getAttribute("incomingTransitions");
							String[] iss = inc.split(" ");
							ArrayList<String> valid = new ArrayList<String>(); 
							for(String s : iss){
								if(!ids.contains(s)){
									valid.add(s);
								}
							}
							String up = "";
							for(String s : valid){
								up = up.concat(s+" ");
							}
							if(!up.equals(""))up = up.substring(0,up.length()-1);
							// update inc transitions attribute
							NamedNodeMap attr = node.getAttributes();
							Node nodeAttr = attr.getNamedItem("incomingTransitions");
							//System.out.println("UPDATED "+up +" FOR "+inc);
							nodeAttr.setTextContent(up);
						}
					}					 
					updateNodes(node.getChildNodes(),op);
				}
			}
		}		
	}
	private static void addNodes(NodeList nodeList) throws Exception {
		
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {	
					Element eElement = (Element) node;
					System.out.println("ADD NODE REGION "+eElement.getAttribute("xmi:id"));
					nodes.add(node);
					ids.add(eElement.getAttribute("xmi:id"));
					addNodes(node.getChildNodes());
				}
			}
		}
	}
	
	private static void findNodes(NodeList nodeList) throws Exception {
				
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {	
					Element eElement = (Element) node;
					//states
					if(node.getNodeName().equals("vertices")){
						if (eElement.getAttribute("xsi:type").equals("sgraph:State")) {
							//eliminate state
							if(ids.contains(eElement.getAttribute("xmi:id"))){
								nodes.add(node);
								NodeList nList = node.getChildNodes();
								for (int j = 0; j < nList.getLength(); j++) {
									Node n1 = nList.item(j);
									if (n1 != null && n1.getNodeType() == Node.ELEMENT_NODE) {	
										Element eE = (Element) n1;
										ids.add(eE.getAttribute("xmi:id"));
										System.out.println("EXTRA "+eE.getAttribute("xmi:id"));
									}
								}								
								continue;
							}
							//update internal transitions
							String id = eElement.getAttribute("xmi:id");
							for(String s : ids){
								if(s.indexOf(":") > 0){									
									String id2 = s.substring(0,s.indexOf(":"));
									String tr = s.substring(s.indexOf(":")+1, s.length());
									System.out.println("IDS Check "+s+" "+id+" "+id2);
									if(id.equals(id2)){										
										String spec = eElement.getAttribute("specification");
										System.out.println("REPLACE "+tr+ " "+spec);										
										//spec = spec.replaceAll(tr, "");
										String before = spec.substring(0,spec.indexOf(tr));
										String after = spec.substring(spec.indexOf(tr)+tr.length(),spec.length());
										spec = before+after;
										// update inc transitions attribute
										NamedNodeMap attr = node.getAttributes();
										Node nodeAttr = attr.getNamedItem("specification");										
										nodeAttr.setTextContent(spec);
										eElement.setAttribute("specification", spec);
										
										spec = eElement.getAttribute("specification");									
										System.out.println("REPLACE 2 "+nodeAttr.getNodeValue()
											+ " "+spec);
									}
								}
							}													
						}
					}
					//regions
					if(node.getNodeName().equals("regions")){											
						if(ids.contains(eElement.getAttribute("xmi:id"))){
							nodes.add(node);
							addNodes(node.getChildNodes());
							continue;
						}						
					}
					if(node.getNodeName().equals("children")){
						if (eElement.getAttribute("type").equals("Region")) {							
							if(ids.contains(eElement.getAttribute("element"))){
								nodes.add(node);
								ids.add(eElement.getAttribute("xmi:id"));
								System.out.println("EXTRA 1 "+eElement.getAttribute("xmi:id"));
								continue;
							}
						}
						if (eElement.getAttribute("type").equals("State")) {							
							if(ids.contains(eElement.getAttribute("element"))){
								nodes.add(node);
								ids.add(eElement.getAttribute("xmi:id"));
								System.out.println("EXTRA 2 "+eElement.getAttribute("xmi:id"));
								continue;
							}
						}
						if (eElement.getAttribute("type").equals("Entry")) {							
							if(ids.contains(eElement.getAttribute("element"))){
								nodes.add(node);
								ids.add(eElement.getAttribute("xmi:id"));
								System.out.println("EXTRA 3 "+eElement.getAttribute("xmi:id"));
								continue;
							}
						}
					}	
					//transitions
					if(node.getNodeName().equals("outgoingTransitions")){											
						if(ids.contains(eElement.getAttribute("xmi:id"))){
							nodes.add(0,node);
							continue;
						}						
					}
					if(node.getNodeName().equals("edges")){
						if (eElement.getAttribute("type").equals("Transition")) {							
							if(ids.contains(eElement.getAttribute("element"))){
								nodes.add(0,node);
								continue;
							}
							if(ids.contains(eElement.getAttribute("target"))){
								nodes.add(0,node);
								continue;
							}
							if(ids.contains(eElement.getAttribute("source"))){
								nodes.add(0,node);
								continue;
							}
						}
					}	 
					findNodes(node.getChildNodes());
				}
			}
		}		
	}
	
	public String printXML(){
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			//StreamResult result = new StreamResult(new File(filepath));
			StreamResult result = new StreamResult(new StringWriter());
			transformer.transform(source, result);
			String xmlString = result.getWriter().toString();
			
			//xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xmlString;
			return xmlString;
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
		
	private CState composeStates_old(ArrayList<CState> cplist, CState s1, CState s2){		
		String label = "";
		String hcond = "(and ";
		String id = "";
		
		label = label.concat(s1.getLabel()+"*");
		hcond = hcond.concat(s1.getFCondition()+" ");
		id = id.concat(s1.getID()+" ");
		
		label = label.concat(s2.getLabel()+"*");
		hcond = hcond.concat(s2.getFCondition()+" ");
		id = id.concat(s2.getID()+" ");

		label = label.substring(0,label.length()-1);
		hcond = hcond.substring(0,hcond.length()-1);
		hcond = hcond.concat(")");
		//System.out.println(" CST "+s1+" "+s2+" "+cplist);
		for(CState s : cplist){
			if(s.getLabel().equals(label) && 
					s.getFCondition().equals(hcond) &&
					s.getID().equals(id) ){
				System.out.println("STATE EXIST "+s);
				return s;
			}
		}		
		CState comp = new CState(label, hcond, StateType.simple, true, id);	
		cplist.add(comp);
		System.out.println("NEW STATE "+comp);
		return comp;
	}
	
	private boolean containState(ArrayList<CState> list, CState state){
		String label = state.getLabel();
		String hcond = state.getFCondition();
		String id = state.getID();
		for(CState s : list){
			if(s.getLabel().equals(label) && 
					checkZ3Equiv(s.getFCondition(), hcond) &&
					s.getID().equals(id)){	
				return true;
			}
		}
		return false;
	}
	
	private CState composeMStates(ArrayList<CState> cplist, ArrayList<CState> list, String change_cond){		
		String label = "";
		String hcond = "";
		String id = "";
		
		ArrayList<String> vars = new ArrayList<String>();
		for(CState s : list){
			label = label.concat(s.getLabel()+"*");			
			String c = s.getFCondition();
			if(!c.equals("true") && !vars.contains(c)){
				vars.add(c);
			}			
			id = id.concat(s.getID()+" ");
		}		
		if(change_cond != null){
			if(!change_cond.equals("true") && !vars.contains(change_cond)){
				vars.add(change_cond);
			}			
		}		
		label = label.substring(0,label.length()-1);		
		id = id.substring(0,id.length()-1);		
		if(vars.size() <= 0){
			hcond = "true";
		}else if(vars.size() == 1){
			hcond = vars.get(0);
		}else{
			hcond = "(and ";
			for(String v: vars){
				hcond = hcond.concat(v+" ");
			}
			hcond = hcond.substring(0,hcond.length()-1);
			hcond = hcond.concat(")");
		}		
		
		for(CState s : cplist){
			if(s.getLabel().equals(label) && 
					checkZ3Equiv(s.getFCondition(), hcond)){					
				return s;
			}
		}		
		CState comp = new CState(label, hcond, StateType.simple, false, id);	
		cplist.add(comp);
		System.out.println("NEW STATE "+comp+" "+hcond+" "+list);
		return comp;
	}
	
	private CState composeMStates(ArrayList<CState> cplist, ArrayList<CState> list, String change_cond,
			boolean isdefault){		
		String label = "";
		String hcond = "";
		String id = "";
		
		ArrayList<String> vars = new ArrayList<String>();
		for(CState s : list){
			label = label.concat(s.getLabel()+"*");			
			String c = s.getFCondition();
			if(!c.equals("true") && !vars.contains(c)){
				vars.add(c);
			}			
			id = id.concat(s.getID()+" ");
		}		
		if(change_cond != null){
			if(!change_cond.equals("true") && !vars.contains(change_cond)){
				vars.add(change_cond);
			}			
		}		
		label = label.substring(0,label.length()-1);		
		id = id.substring(0,id.length()-1);		
		if(vars.size() <= 0){
			hcond = "true";
		}else if(vars.size() == 1){
			hcond = vars.get(0);
		}else{
			hcond = "(and ";
			for(String v: vars){
				hcond = hcond.concat(v+" ");
			}
			hcond = hcond.substring(0,hcond.length()-1);
			hcond = hcond.concat(")");
		}		
		
		for(CState s : cplist){
			if(s.getLabel().equals(label) && 
					checkZ3Equiv(s.getFCondition(), hcond)){					
				return s;
			}
		}		
		CState comp = new CState(label, hcond, StateType.simple, isdefault, id);	
		cplist.add(comp);
		System.out.println("NEW STATE "+comp+" "+hcond+" "+list);
		return comp;
	}
	
	private CState composeMStates_old(ArrayList<CState> cplist, ArrayList<CState> list, String change_cond,
			boolean isdefault){		
		String label = "";
		String hcond = "(and ";
		String id = "";
		
		for(CState s : list){
			label = label.concat(s.getLabel()+"*");
			//hcond = hcond.concat(reduce_condition(s.getFCondition())+" ");
			hcond = hcond.concat(s.getFCondition()+" ");
			id = id.concat(s.getID()+" ");
		}
		//change_cond = reduce_condition(change_cond);
		if(change_cond != null){
			hcond = hcond.concat(change_cond);
		}else{
			hcond = hcond.substring(0,hcond.length()-1);
		}	
		
		label = label.substring(0,label.length()-1);
		//hcond = hcond.substring(0,hcond.length()-1);
		id = id.substring(0,id.length()-1);
		hcond = hcond.concat(")");
		//hcond = reduce_condition(hcond);
		//System.out.println(" CST "+s1+" "+s2+" "+cplist);
		for(CState s : cplist){
			if(s.getLabel().equals(label) && 
					checkZ3Equiv(s.getFCondition(), hcond)){
					//s.getFCondition().equals(hcond) &&
					//s.getID().equals(id) ){
				//System.out.println("STATE EXIST "+s);
				return s;
			}
		}		
		CState comp = new CState(label, hcond, StateType.simple, isdefault, id);	
		cplist.add(comp);
		System.out.println("NEW STATE "+comp+" "+hcond+" "+list);
		return comp;
	}
	
	private CTransition composeTransition_Old1(ArrayList<CTransition> ctlist, CTransition t,
			CState change_source, CState change_target, String change_cond){		
		String in = t.getInput();
		String cond = t.getFCondition();
		String output = t.getOutput();
		CState source = t.getSource();
		CState target = t.getTarget();		
		if(change_source != null){
			source = change_source;			
		}
		if(change_target != null){
			target = change_target;
		}
		if(change_cond != null){
			if(cond.equals("true")){
				cond = change_cond;
			}else{
				cond = "(and "+cond+" "+change_cond+")";
			}		
		}
			
		for(CTransition f : ctlist){
			if(f.getInput().equals(in) &&
					f.getOutput().equals(output) &&
					f.getFCondition().equals(cond) &&
					
					f.getSource().getLabel().equals(source.getLabel()) && 
					f.getSource().getFCondition().equals(source.getFCondition()) &&
					f.getSource().getID().equals(source.getID()) &&
					
					f.getTarget().getLabel().equals(target.getLabel()) && 
					f.getTarget().getFCondition().equals(target.getFCondition()) &&
					f.getTarget().getID().equals(target.getID())
					){
				System.out.println("EXIST "+source+" -- "+in+" "+cond+" "+output+" "+target);
				return f;
			}
		}	
		
		CTransition tr = new CTransition(in, new ArrayList<CState>(), cond, output, source, target, "");
		ctlist.add(tr);
		System.out.println("NEW TR "+tr);
		return tr;
	}
	
	private CTransition composeTransition_old(ArrayList<CTransition> ctlist, CTransition t,
			CState change_source, CState change_target){		
		String in = t.getInput();
		String cond = t.getFCondition();
		String output = t.getOutput();
		CState source = t.getSource();
		CState target = t.getTarget();		
		if(change_source != null){
			source = change_source;			
		}
		if(change_target != null){
			target = change_target;
		}
			
		for(CTransition f : ctlist){
			if(f.getInput().equals(in) &&
					f.getOutput().equals(output) &&
					f.getFCondition().equals(cond) &&
					
					f.getSource().getLabel().equals(source.getLabel()) && 
					f.getSource().getFCondition().equals(source.getFCondition()) &&
					f.getSource().getID().equals(source.getID()) &&
					
					f.getTarget().getLabel().equals(target.getLabel()) && 
					f.getTarget().getFCondition().equals(target.getFCondition()) &&
					f.getTarget().getID().equals(target.getID())
					){
				System.out.println("EXIST "+source+" -- "+in+" "+cond+" "+output+" "+target);
				return f;
			}
		}	
		
		CTransition tr = new CTransition(in, new ArrayList<CState>(), cond, output, source, target, "");
		ctlist.add(tr);
		System.out.println("NEW TR "+tr);
		return tr;
	}
	
	private CTransition composeTransition2(ArrayList<CTransition> ctlist, CTransition t1, CTransition t2,
			CState change_source, CState change_target){		
		String in = t1.getInput();
		String cond = t1.getFCondition();
		String output = t1.getOutput();
		CState source = t1.getSource();
		CState target = t1.getTarget();
		
		String cond2 = t2.getFCondition();
		String output2 = t2.getOutput();
		
		if(cond.equals("true") && !cond2.equals("true")){
			cond = cond2;
		}else if(!cond.equals("true") && !cond2.equals("true")){
			cond = "(and " +cond+" "+cond2+ ")";
		}
		output = output+"*"+output2; 
		
		if(change_source != null){
			source = change_source;			
		}
		if(change_target != null){
			target = change_target;
		}
			
		for(CTransition f : ctlist){
			if(f.getInput().equals(in) &&
					f.getOutput().equals(output) &&
					f.getFCondition().equals(cond) &&
					
					f.getSource().getLabel().equals(source.getLabel()) && 
					f.getSource().getFCondition().equals(source.getFCondition()) &&
					f.getSource().getID().equals(source.getID()) &&
					
					f.getTarget().getLabel().equals(target.getLabel()) && 
					f.getTarget().getFCondition().equals(target.getFCondition()) &&
					f.getTarget().getID().equals(target.getID())
					){
				System.out.println("EXIST "+t1);
				return f;
			}
		}	
		
		CTransition tr = new CTransition(in, new ArrayList<CState>(), cond, output, source, target, "");
		ctlist.add(tr);
		System.out.println("NEW TR "+tr);
		return tr;
	}
	
	private CTransition composeMTransition(ArrayList<CTransition> ctlist, ArrayList<CTransition> list,
			CState change_source, CState change_target, String change_cond){		
		CTransition t1 = list.remove(0);
		
		String in = t1.getInput();
		String cond = t1.getFCondition();
		String output = t1.getOutput();
		CState source = t1.getSource();
		CState target = t1.getTarget();
		
		for(CTransition t2 : list){
			String cond2 = t2.getFCondition();
			String output2 = t2.getOutput();
			
			if(cond.equals("true") && !cond2.equals("true")){
				cond = cond2;
			}else if(!cond.equals("true") && !cond2.equals("true")){
				cond = "(and " +cond+" "+cond2+ ")";
			}
			output = output+"*"+output2;
		}		 
		
		if(change_source != null){
			source = change_source;			
		}
		if(change_target != null){
			target = change_target;
		}
		
		if(change_cond != null){
			if(cond.equals("true")){
				cond = change_cond;
			}else{
				cond = "(and "+cond+" "+change_cond+")";
			}		
		}
			
		for(CTransition f : ctlist){
			if(f.getInput().equals(in) &&
					f.getOutput().equals(output) &&
					f.getFCondition().equals(cond) &&
					
					f.getSource().getLabel().equals(source.getLabel()) && 
					f.getSource().getFCondition().equals(source.getFCondition()) &&
					f.getSource().getID().equals(source.getID()) &&
					
					f.getTarget().getLabel().equals(target.getLabel()) && 
					f.getTarget().getFCondition().equals(target.getFCondition()) &&
					f.getTarget().getID().equals(target.getID())
					){
				//System.out.println("EXIST "+source+" -- "+in+" "+cond+" "+output+" "+target);
				return f;
			}
		}	
		
		CTransition tr = new CTransition(in, new ArrayList<CState>(), cond, output, source, target, "");
		ctlist.add(tr);
		System.out.println("NEW TR "+tr);
		return tr;
	}
	
	private void orderComp(ArrayList<CState> slit){
		
		if(slit.size() > 1){
			CState[] ord = new CState[leafs_in.size()];
			System.out.println("DATA "+leafs_in+ " -> slit "+slit );
			for(CState l : leafs_in){
				for(CState l2 : slit){					
					//if(l.getSuperState().getDescendants().contains(l2)){
					if(containState(l.getSuperState().getDescendants(),l2)){
						ord[leafs_in.indexOf(l)] = l2;										
					}
				}							
			}
			slit.clear();
			for(CState l : leafs_in){
				if(ord[leafs_in.indexOf(l)] != null){
					slit.add(ord[leafs_in.indexOf(l)]);
				}
			}	
		}	
	}
	
	private void setInitialComposition(CState n, CState comp, ArrayList<CState> cplist, 
			ArrayList<CTransition> ctlist, HashMap<CState,ArrayList<CState>> rmap,
			ArrayList<ArrayList<CState>> rcomposed){
					
		cm:for(ArrayList<CState> set : rcomposed){
			//check valid combination	
			if(set.size() > 1){
				String cond = "(and ";
				for(CState re1 : set){					
					cond = cond.concat(re1.getFCondition()+" ");
				}
				cond = cond.concat(")");
				if(!checkZ3Cond(cond)){
					System.out.println("SKIP 1 composition "+set);
					continue cm;
				}
			}			
			for(CState reg1 : n.getSubstates()){
				ArrayList<CState> qr = new ArrayList<CState>();
				for(CState reg2 : n.getSubstates()){
					if(!reg1.equals(reg2)){
						if(checkZ3Equiv(reg1.getFCondition(),reg2.getFCondition())){
							qr.add(reg2);
						}
					}
				}
				if(set.contains(reg1) && !set.containsAll(qr)){
					System.out.println("SKIP 2 composition "+set);
					continue cm;
				}
			}
			
			String comp_cond = "(and ";
			for(CState region : n.getSubstates()){
				if(set.contains(region)){
					comp_cond = comp_cond.concat(region.getFCondition()+" ");
				}else{
					comp_cond = comp_cond.concat("(not "+region.getFCondition()+") ");
				}
			}
			comp_cond = comp_cond.substring(0,comp_cond.length()-1);
			comp_cond = comp_cond.concat(")");
			
			System.out.println("SET "+set+" cond "+comp_cond+" init "+comp+ " "+leafs_in);
			for(CState r1 : set){
				for(CState s1 : leafs_in){
					if(r1.getDescendants().contains(s1)){
						ArrayList<CState> rlist1 = getRSlist(r1,rmap);
						for(CTransition t1 : s1.getLeave()){
							if(t1.getSource().equals(t1.getTarget())){
								System.out.println("SELF LOOP "+t1);
								ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
								mutlist.add(t1);
								composeMTransition(ctlist, mutlist, comp, comp, comp_cond);								
							}
						}
						for(CTransition t1 : s1.getLeave()){							
							if(r1.getDescendants().contains(t1.getTarget()) &&
									!rlist1.contains(t1.getTarget())){
								continue;
							}
							//boolean found = false;
							ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
							mutlist.add(t1);
							for(CState r2 : set){
								for(CState s2 : leafs_in){
									if(!s1.equals(s2) && r2.getDescendants().contains(s2)){
										ArrayList<CState> rlist2 = getRSlist(r2,rmap);
										System.out.println("TEST "+t1+ " "+s2);
										for(CState x : rlist2){
											System.out.println("-> "+x+ " "+x.getFCondition());
										}
										for(CTransition t2 : s2.getLeave()){
											if(t1.getInput().equals(t2.getInput())){
												System.out.println("SAME INPUT "+t1+ " "+t2);
												if(rlist1.contains(t1.getTarget()) && 
														rlist2.contains(t2.getTarget())){
													System.out.println("SLIT ADD "+t2);
													mutlist.add(t2);
												}
												/*if(r2.getDescendants().contains(t2.getTarget()) &&
														!rlist2.contains(t2.getTarget())){
													continue;
												}												
												if(n.getDescendants().contains(t1.getTarget()) &&
														n.getDescendants().contains(t2.getTarget()) &&
														!leafs_in.contains(t1.getTarget()) &&
														!leafs_in.contains(t2.getTarget())){
													//before - check condition 
													
													mutlist.add(t2);								
													//found = true;									
												}*/
											}
										}
									}
								}
							}
							System.out.println("MULTI "+mutlist);
							ArrayList<CState> slit = new ArrayList<CState>();
							for(CTransition tn : mutlist){
								slit.add(tn.getTarget());					
							}
							System.out.println("SLIT 1 "+slit);
							orderComp(slit);
							if(leafs_in.contains(t1.getTarget())){
								System.out.println("LEAVE THEN ");
								composeMTransition(ctlist, mutlist, comp, comp, comp_cond);
							}else if(r1.getDescendants().contains(t1.getTarget()) || rlist1.contains(t1.getTarget())){
								System.out.println("LEAVE ELSE 1 "+slit);
								CState cp = composeMStates(cplist,slit,comp_cond);
								composeMTransition(ctlist, mutlist, comp, cp, null);
							}else{
								System.out.println("LEAVE ELSE 2 ");
								composeMTransition(ctlist, mutlist, comp, t1.getTarget(), null);
							}							
						}
						for(CTransition t1 : s1.getReach()){
							if(r1.getDescendants().contains(t1.getSource()) &&
									!rlist1.contains(t1.getSource())){
								continue;
							}
							//boolean found = false;
							ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
							mutlist.add(t1);
							for(CState r2 : set){
								for(CState s2 : leafs_in){
									if(!s1.equals(s2) && r2.getDescendants().contains(s2)){
										ArrayList<CState> rlist2 = getRSlist(r2,rmap);
										System.out.println("TEST REACHS "+t1+ " "+s2);
										for(CTransition t2 : s2.getReach()){
											if(t1.getInput().equals(t2.getInput())){
												System.out.println("SAME INPUT "+t1+ " "+t2);
												if(rlist1.contains(t1.getSource()) && 
														rlist2.contains(t2.getSource())){
													mutlist.add(t2);
												}
												/*
												if(r2.getDescendants().contains(t2.getSource()) &&
														!rlist2.contains(t2.getSource())){
													continue;
												}												
												if(n.getDescendants().contains(t1.getSource()) &&
														n.getDescendants().contains(t2.getSource())
														//!leafs_in.contains(t1.getSource()) &&
														//!leafs_in.contains(t2.getSource())
														){
													//before - check condition 
													
													mutlist.add(t2);								
													//found = true;									
												}*/
											}
										}
									}
								}
							}							
							ArrayList<CState> slit = new ArrayList<CState>();
							for(CTransition tn : mutlist){
								slit.add(tn.getSource());					
							}
							orderComp(slit);
							if(leafs_in.contains(t1.getSource())){
								System.out.println("REACH THEN ");
								composeMTransition(ctlist, mutlist, comp, comp, comp_cond);
							}else if(r1.getDescendants().contains(t1.getSource()) || rlist1.contains(t1.getSource())){
								System.out.println("REACH ELSE 1 ");
								CState cp = composeMStates(cplist,slit,comp_cond);
								composeMTransition(ctlist, mutlist, cp, comp, null);
							}else{
								System.out.println("REACH ELSE 2 ");
								composeMTransition(ctlist, mutlist, t1.getSource(), comp, null);
							}
							/*System.out.println("REMOVE T "+t1);
							ArrayList<CTransition> upt = (ArrayList<CTransition>) t1.getTarget().getReach().clone();
							upt.remove(t1);
							t1.getTarget().setReach(upt);
							upt = (ArrayList<CTransition>) t1.getSource().getLeave().clone();
							upt.remove(t1);
							t1.getSource().setLeave(upt);
							*/
						}
					}					
				}
			}
			//
		}
	}
	
	private void setNewComposition(ArrayList<CState> set, String comp_cond,
			CState n, CState comp, ArrayList<CState> cplist, 
			ArrayList<CTransition> ctlist, HashMap<CState,ArrayList<CState>> rmap,
			ArrayList<ArrayList<CState>> rcomposed){
						
		System.out.println("SET "+set+" cond "+comp_cond+" init "+comp+ " "+leafs_in);
		for(CState r1 : set){
			
			for(CState s1 : leafs_in){
				if(r1.getDescendants().contains(s1)){
					ArrayList<CState> rlist1 = getRSlist(r1,rmap);
					for(CTransition t1 : s1.getLeave()){
						if(t1.getSource().equals(t1.getTarget())){
							System.out.println("SELF LOOP "+t1);
							ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
							mutlist.add(t1);
							composeMTransition(ctlist, mutlist, comp, comp, comp_cond);								
						}
					}
					for(CTransition t1 : s1.getLeave()){							
						if(r1.getDescendants().contains(t1.getTarget()) &&
								!rlist1.contains(t1.getTarget())){
							continue;
						}
						//boolean found = false;
						ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
						mutlist.add(t1);
						for(CState r2 : set){
							for(CState s2 : leafs_in){
								if(!s1.equals(s2) && r2.getDescendants().contains(s2)){
									ArrayList<CState> rlist2 = getRSlist(r2,rmap);
									System.out.println("TEST "+t1+ " "+s2);
									for(CState x : rlist2){
										System.out.println("-> "+x+ " "+x.getFCondition());
									}
									for(CTransition t2 : s2.getLeave()){
										if(t1.getInput().equals(t2.getInput())){
											System.out.println("SAME INPUT "+t1+ " "+t2);
											if(rlist1.contains(t1.getTarget()) && 
													rlist2.contains(t2.getTarget())){
												System.out.println("SLIT ADD "+t2);
												mutlist.add(t2);
											}												
										}
									}
								}
							}
						}
						System.out.println("MULTI "+mutlist);
						ArrayList<CState> slit = new ArrayList<CState>();
						for(CTransition tn : mutlist){
							slit.add(tn.getTarget());					
						}
						System.out.println("SLIT 1 "+slit);
						orderComp(slit);
						if(leafs_in.contains(t1.getTarget())){
							System.out.println("LEAVE THEN ");
							composeMTransition(ctlist, mutlist, comp, comp, comp_cond);
						}else if(r1.getDescendants().contains(t1.getTarget()) || rlist1.contains(t1.getTarget())){
							System.out.println("LEAVE ELSE 1 "+slit);
							CState cp = composeMStates(cplist,slit,comp_cond);
							composeMTransition(ctlist, mutlist, comp, cp, null);
						}else{
							System.out.println("LEAVE ELSE 2 ");
							composeMTransition(ctlist, mutlist, comp, t1.getTarget(), null);
						}							
					}
					for(CTransition t1 : s1.getReach()){
						if(r1.getDescendants().contains(t1.getSource()) &&
								!rlist1.contains(t1.getSource())){
							continue;
						}
						//boolean found = false;
						ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
						mutlist.add(t1);
						for(CState r2 : set){
							for(CState s2 : leafs_in){
								if(!s1.equals(s2) && r2.getDescendants().contains(s2)){
									ArrayList<CState> rlist2 = getRSlist(r2,rmap);
									System.out.println("TEST REACHS "+t1+ " "+s2);
									for(CTransition t2 : s2.getReach()){
										if(t1.getInput().equals(t2.getInput())){
											System.out.println("SAME INPUT "+t1+ " "+t2);
											if(rlist1.contains(t1.getSource()) && 
													rlist2.contains(t2.getSource())){
												mutlist.add(t2);
											}												
										}
									}
								}
							}
						}							
						ArrayList<CState> slit = new ArrayList<CState>();
						for(CTransition tn : mutlist){
							slit.add(tn.getSource());					
						}
						orderComp(slit);
						if(leafs_in.contains(t1.getSource())){
							System.out.println("REACH THEN ");
							composeMTransition(ctlist, mutlist, comp, comp, comp_cond);
						}else if(r1.getDescendants().contains(t1.getSource()) || rlist1.contains(t1.getSource())){
							System.out.println("REACH ELSE 1 ");
							CState cp = composeMStates(cplist,slit,comp_cond);
							composeMTransition(ctlist, mutlist, cp, comp, null);
						}else{
							System.out.println("REACH ELSE 2 ");
							composeMTransition(ctlist, mutlist, t1.getSource(), comp, null);
						}						
					}
				}					
			}
		}
	}
	
	private ArrayList<CState> getRSlist(CState region, HashMap<CState,ArrayList<CState>> rmap){
		
		//for(CState re1 : rset){			
		System.out.println("GET RS "+leafs_in+" "+region);
			for(CState s1 : leafs_in){
				if(region.getDescendants().contains(s1)){
					System.out.println("RETURN "+rmap.get(s1));
					return (ArrayList<CState>) rmap.get(s1).clone();
				}
			}
		//}
		return null;
	}
	
	private ArrayList<CState> composeSingleRegion(ArrayList<CState> cplist, ArrayList<CTransition> ctlist,
			String comp_cond, ArrayList<CState> rlist1){
			
		ArrayList<CState> list = new ArrayList<CState>();
		for(CState r1 : rlist1){
			System.out.println("ADD SINGLE "+r1+" "+comp_cond);
			for(CTransition l1 : r1.getLeave()){	
				ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
				mutlist.add(l1);
				//get source state
				ArrayList<CState> slit = new ArrayList<CState>();
				slit.add(r1);
				CState source = null;
				if(r1.getDefault()){
					source = composeMStates(cplist,slit,comp_cond,true);
				}else source = composeMStates(cplist,slit,comp_cond);			
				if(!containState(list,source)) list.add(source);
			
				//get target state
				CState target = null;
				slit.clear();
				if(rlist1.contains(l1.getTarget())){
					slit.add(l1.getTarget());
					if(l1.getTarget().getDefault()){
						target = composeMStates(cplist,slit,comp_cond,true);
					}else target = composeMStates(cplist,slit,comp_cond);
					if(!containState(list,target)) list.add(target);
				}else{
					target = l1.getTarget();
				}			
				System.out.println("LEAVE ");
				composeMTransition(ctlist, mutlist, source, target, null);
			}
			for(CTransition e1 : r1.getReach()){	
				ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
				mutlist.add(e1);
				//get target state
				ArrayList<CState> slit = new ArrayList<CState>();
				slit.add(r1);							
				CState target = null;
				if(r1.getDefault()){
					target = composeMStates(cplist,slit,comp_cond,true);
				}else target = composeMStates(cplist,slit,comp_cond);			
				if(!containState(list,target)) list.add(target);
			
				//get source state
				CState source = null;
				slit.clear();
				if(rlist1.contains(e1.getSource())){
					slit.add(e1.getSource());
					if(e1.getTarget().getDefault()){
						source = composeMStates(cplist,slit,comp_cond,true);
					}else source = composeMStates(cplist,slit,comp_cond);
					if(!containState(list,source)) list.add(source);
				}else{
					source = e1.getSource();
				}			
				System.out.println("REACH ");
				composeMTransition(ctlist, mutlist, source, target, null);
			}
		}		
		return list;
	}
	
	private ArrayList<ArrayList<CTransition>> recursive_find_comp(ArrayList<CState> regions,
			CState region1, CTransition l1,	HashMap<CState,ArrayList<CState>> rmap){
				
		ArrayList<ArrayList<CTransition>> complist = new ArrayList<ArrayList<CTransition>>();			
		ArrayList<CTransition> currentcomp = new ArrayList<CTransition>();
		currentcomp.add(l1);
		complist.add(currentcomp);
		for(CState region2 : regions){	
			if(!region1.equals(region2)){
				ArrayList<ArrayList<CTransition>> complist2 = (ArrayList<ArrayList<CTransition>>) complist.clone();				
				ArrayList<CState> rlist2 = getRSlist(region2,rmap);
				for(CState r2 : rlist2){					
					for(CTransition l2 : r2.getLeave()){						
						if(l1.getInput().equals(l2.getInput())){							
							for(ArrayList<CTransition> curr: complist2){
								ArrayList<CTransition> comp = new ArrayList<CTransition>();
								comp.addAll(curr);
								comp.add(l2);
								if(complist.contains(curr)){
									complist.remove(curr);
								}								
								complist.add(comp);
							}
						}
					}							
				}
			}
		}
		return complist;
	}
	
	private void composeMultiRegion(ArrayList<CState> regions, ArrayList<CState> cplist,
			ArrayList<CTransition> ctlist, String comp_cond, HashMap<CState,ArrayList<CState>> rmap){
				
		ArrayList<ArrayList<CTransition>> tocompose = new ArrayList<ArrayList<CTransition>>();		
		ArrayList<String> inputs = new ArrayList<String>();
		for(CState region1 : regions){
			ArrayList<CState> rlist1 = getRSlist(region1,rmap);
			for(CState r1 : rlist1){								
				for(CTransition l1 : r1.getLeave()){
					if(!inputs.contains(l1.getInput())){
						//one transition per region - composed
						ArrayList<ArrayList<CTransition>> onelist = recursive_find_comp(regions,region1,l1,rmap);
						tocompose.addAll(onelist);
					}				
				}
				for(CTransition l1 : r1.getLeave()){
					inputs.add(l1.getInput());
				}				
			}
		}		
		//compose transition combinations - leave
		composeLeave(tocompose,regions,cplist,ctlist,comp_cond,rmap);
		
		tocompose = new ArrayList<ArrayList<CTransition>>();		
		inputs = new ArrayList<String>();
		for(CState region1 : regions){
			ArrayList<CState> rlist1 = getRSlist(region1,rmap);
			for(CState r1 : rlist1){								
				for(CTransition l1 : r1.getReach()){
					if(!inputs.contains(l1.getInput())){
						//one transition per region - composed
						ArrayList<ArrayList<CTransition>> onelist = recursive_find_comp(regions,region1,l1,rmap);
						tocompose.addAll(onelist);
					}				
				}
				for(CTransition l1 : r1.getReach()){
					inputs.add(l1.getInput());
				}				
			}
		}
		//compose transition combinations - reach
		composeReach(tocompose,regions,cplist,ctlist,comp_cond,rmap);
	}
		
	private void composeLeave(ArrayList<ArrayList<CTransition>> tocompose, ArrayList<CState> regions,
			ArrayList<CState> cplist,
			ArrayList<CTransition> ctlist, String comp_cond, HashMap<CState,ArrayList<CState>> rmap){
		
		for(ArrayList<CTransition> onelist : tocompose){
			ArrayList<CState> slit = new ArrayList<CState>();
			for(CTransition tn : onelist){
				slit.add(tn.getSource());					
			}
			//get source state	
			orderComp(slit);
			CState cp1 = composeMStates(cplist,slit,comp_cond);
			//get target state
			CState cp2 = null;
			slit.clear();
			ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
			for(CTransition one : onelist){
				boolean found = false;
				for(CState region : regions){
					ArrayList<CState> rlist = getRSlist(region,rmap);
					if(rlist.contains(one.getTarget())){
						found = true;
						break;
					}
				}
				if(!found){
					cp2 = one.getTarget();
					slit.add(one.getTarget());
					mutlist.add(one);
					break;
				}
			}
			if(cp2 == null){
				for(CTransition tn : onelist){
					slit.add(tn.getTarget());					
				}
				orderComp(slit);
				cp2 = composeMStates(cplist,slit,comp_cond);
				mutlist = onelist;
			}
			System.out.println("LEAVE MULTI "+mutlist);
			composeMTransition(ctlist, mutlist, cp1, cp2, null);
		}
	}
	
	private void composeReach(ArrayList<ArrayList<CTransition>> tocompose, ArrayList<CState> regions,
			ArrayList<CState> cplist,
			ArrayList<CTransition> ctlist, String comp_cond, HashMap<CState,ArrayList<CState>> rmap){
		
		for(ArrayList<CTransition> onelist : tocompose){
			ArrayList<CState> slit = new ArrayList<CState>();
			for(CTransition tn : onelist){
				slit.add(tn.getTarget());					
			}
			//get target state	
			orderComp(slit);
			CState cp1 = composeMStates(cplist,slit,comp_cond);
			//get source state
			CState cp2 = null;
			slit.clear();
			ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
			for(CTransition one : onelist){
				boolean found = false;
				for(CState region : regions){
					ArrayList<CState> rlist = getRSlist(region,rmap);
					if(rlist.contains(one.getSource())){
						found = true;
						break;
					}
				}
				if(!found){
					cp2 = one.getSource();
					slit.add(one.getSource());
					mutlist.add(one);
					break;
				}
			}
			if(cp2 == null){
				for(CTransition tn : onelist){
					slit.add(tn.getSource());					
				}
				orderComp(slit);
				cp2 = composeMStates(cplist,slit,comp_cond);
				mutlist = onelist;
			}
			System.out.println("REACH MULTI "+mutlist);
			composeMTransition(ctlist, mutlist, cp2, cp1, null);
		}
	}
	
		/*
		for(CTransition l1 : r1.getLeave()){
			if(!leafs_in.contains(l1.getTarget())){
				ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
				mutlist.add(l1);
				for(CTransition l2 : r2.getLeave()){
					if(l1.getInput().equals(l2.getInput())){	
						//if(rlist11.contains(l1.getTarget()) && rlist2.contains(l2.getTarget())){
							//before - check condition 
							
							mutlist.add(l2);								
							//found = true;									
						//}
					}
				}
				ArrayList<CState> slit = new ArrayList<CState>();
				for(CTransition tn : mutlist){
					slit.add(tn.getSource());					
				}
				orderComp(slit);
												
				//get source state		
				CState cp1 = composeMStates(cplist,slit,comp_cond);
				//get target state
				CState cp2 = null;
				slit.clear();
				if(rlist11.contains(l1.getTarget())){
					for(CTransition tn : mutlist){
						slit.add(tn.getTarget());					
					}
					orderComp(slit);
					cp2 = composeMStates(cplist,slit,comp_cond);
				}else{
					cp2 = l1.getTarget();
				}			
				//System.out.println("LEAVE "+mutlist);
				composeMTransition(ctlist, mutlist, cp1, cp2, null);
			}
		}
		
		for(CTransition l1 : r1.getReach()){	
			if(!leafs_in.contains(l1.getSource())){
				ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
				mutlist.add(l1);
				for(CTransition l2 : r2.getReach()){
					if(l1.getInput().equals(l2.getInput())){	
						//if(rlist11.contains(l1.getSource()) && rlist2.contains(l2.getSource())){
							//before - check condition 
							
							mutlist.add(l2);								
							//found = true;									
						//}
					}
				}
				ArrayList<CState> slit = new ArrayList<CState>();
				for(CTransition tn : mutlist){
					slit.add(tn.getTarget());					
				}
				orderComp(slit);
												
				//get target state		
				CState cp1 = composeMStates(cplist,slit,comp_cond);
				//get source state
				CState cp2 = null;
				slit.clear();
				if(rlist11.contains(l1.getSource())){
					for(CTransition tn : mutlist){
						slit.add(tn.getSource());					
					}
					orderComp(slit);
					cp2 = composeMStates(cplist,slit,comp_cond);
				}else{
					cp2 = l1.getSource();
				}	
				//System.out.println("REACH "+mutlist);
				composeMTransition(ctlist, mutlist, cp2, cp1, null);
			}
		}*/
	//}
	
	private void composeMultiRegion_old(CState r1, CState r2, ArrayList<CState> cplist, ArrayList<CTransition> ctlist,
			String comp_cond, ArrayList<CState> rlist11, ArrayList<CState> rlist2){
		
		for(CTransition l1 : r1.getLeave()){
			if(!leafs_in.contains(l1.getTarget())){
				ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
				mutlist.add(l1);
				for(CTransition l2 : r2.getLeave()){
					if(l1.getInput().equals(l2.getInput())){	
						//if(rlist11.contains(l1.getTarget()) && rlist2.contains(l2.getTarget())){
							//before - check condition 
							
							mutlist.add(l2);								
							//found = true;									
						//}
					}
				}
				ArrayList<CState> slit = new ArrayList<CState>();
				for(CTransition tn : mutlist){
					slit.add(tn.getSource());					
				}
				orderComp(slit);
												
				//get source state		
				CState cp1 = composeMStates(cplist,slit,comp_cond);
				//get target state
				CState cp2 = null;
				slit.clear();
				if(rlist11.contains(l1.getTarget())){
					for(CTransition tn : mutlist){
						slit.add(tn.getTarget());					
					}
					orderComp(slit);
					cp2 = composeMStates(cplist,slit,comp_cond);
				}else{
					cp2 = l1.getTarget();
				}			
				//System.out.println("LEAVE "+mutlist);
				composeMTransition(ctlist, mutlist, cp1, cp2, null);
			}
		}
		
		for(CTransition l1 : r1.getReach()){	
			if(!leafs_in.contains(l1.getSource())){
				ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
				mutlist.add(l1);
				for(CTransition l2 : r2.getReach()){
					if(l1.getInput().equals(l2.getInput())){	
						//if(rlist11.contains(l1.getSource()) && rlist2.contains(l2.getSource())){
							//before - check condition 
							
							mutlist.add(l2);								
							//found = true;									
						//}
					}
				}
				ArrayList<CState> slit = new ArrayList<CState>();
				for(CTransition tn : mutlist){
					slit.add(tn.getTarget());					
				}
				orderComp(slit);
												
				//get target state		
				CState cp1 = composeMStates(cplist,slit,comp_cond);
				//get source state
				CState cp2 = null;
				slit.clear();
				if(rlist11.contains(l1.getSource())){
					for(CTransition tn : mutlist){
						slit.add(tn.getSource());					
					}
					orderComp(slit);
					cp2 = composeMStates(cplist,slit,comp_cond);
				}else{
					cp2 = l1.getSource();
				}	
				//System.out.println("REACH "+mutlist);
				composeMTransition(ctlist, mutlist, cp2, cp1, null);
			}
		}
	}
	
	private CState getTransitionLeaf(HashMap<CState,ArrayList<CState>> rmap,
			ArrayList<CState> rlist2, CState region){		
		
		if(region != null){
			if(rlist2.size() <= 0){
				for(CState leaf : leafs_in){
					if(region.getDescendants().contains(leaf)){
						return leaf;
					}
				}
			}
		}				
		ArrayList<CState> rlist = (ArrayList<CState>) rlist2.clone();
		for(CState s : rlist2){
			if(leafs_in.contains(s)){
				rlist.remove(s);
			}
		}	
		for(CState leaf : leafs_in){			
			if(rmap.get(leaf).containsAll(rlist)){
				return leaf;
			}
		}		
		return null;
	}
	
	private boolean isTransitionLeaf(String input, HashMap<CState,ArrayList<CState>> rmap,
			ArrayList<CState> rlist2){		
		//find self leaf 
		CState leaf = null;
		ArrayList<CState> rlist = (ArrayList<CState>) rlist2.clone();
		for(CState s : rlist2){
			if(leafs_in.contains(s)){
				rlist.remove(s);
			}
		}		
		for(CState le : leafs_in){
			System.out.println("TEST LEAF "+rmap.get(le)+ " "+rlist);
			if(rmap.get(le).containsAll(rlist)){
				System.out.println("LEAF "+le);
				leaf = le;
				break;
			}
		}
		if(leaf != null){
			for(CTransition l2 : leaf.getLeave()){
				if(input.equals(l2.getInput()) && l2.getSource().equals(l2.getTarget())){
					return true;
				}
			}			
		}else{
			System.out.println("ERROR FINDING LEAF "+rlist2+" leafs "+leafs_in);
		}
		return false;
	}
	
	private boolean isTransitionLeafOut(String input, HashMap<CState,ArrayList<CState>> rmap,
			ArrayList<CState> rlist2){		
		//find self leaf 
		CState leaf = null;
		ArrayList<CState> rlist = (ArrayList<CState>) rlist2.clone();
		for(CState s : rlist2){
			if(leafs_in.contains(s)){
				rlist.remove(s);
			}
		}		
		for(CState le : leafs_in){
			System.out.println("TEST LEAF "+rmap.get(le)+ " "+rlist);
			if(rmap.get(le).containsAll(rlist)){
				System.out.println("LEAF "+le);
				leaf = le;
				break;
			}
		}
		if(leaf != null){
			for(CTransition l2 : leaf.getLeave()){
				if(input.equals(l2.getInput()) && !rlist2.contains(l2.getTarget())){
					return true;
				}
			}			
		}else{
			System.out.println("ERROR FINDING LEAF "+rlist2+" leafs "+leafs_in);
		}
		return false;
	}
	
	private boolean isTransitionLeaf2(String input, HashMap<CState,ArrayList<CState>> rmap,
			ArrayList<CState> rlist2){		
		//find self leaf 
		CState leaf = null;
		ArrayList<CState> rlist = (ArrayList<CState>) rlist2.clone();
		for(CState s : rlist2){
			if(leafs_in.contains(s)){
				rlist.remove(s);
			}
		}		
		for(CState le : leafs_in){
			System.out.println("TEST LEAF "+rmap.get(le)+ " "+rlist);
			if(rmap.get(le).containsAll(rlist)){
				System.out.println("LEAF "+le);
				leaf = le;
				break;
			}
		}
		if(leaf != null){
			for(CTransition l2 : leaf.getReach()){
				if(input.equals(l2.getInput()) && l2.getSource().equals(l2.getTarget())){
					return true;
				}
			}			
		}else{
			System.out.println("ERROR FINDING LEAF "+rlist2+" leafs "+leafs_in);
		}
		return false;
	}
	
	private boolean isTransitionLeafOut2(String input, HashMap<CState,ArrayList<CState>> rmap,
			ArrayList<CState> rlist2){		
		//find self leaf 
		CState leaf = null;
		ArrayList<CState> rlist = (ArrayList<CState>) rlist2.clone();
		for(CState s : rlist2){
			if(leafs_in.contains(s)){
				rlist.remove(s);
			}
		}		
		for(CState le : leafs_in){
			System.out.println("TEST LEAF "+rmap.get(le)+ " "+rlist);
			if(rmap.get(le).containsAll(rlist)){
				System.out.println("LEAF "+le);
				leaf = le;
				break;
			}
		}
		if(leaf != null){
			for(CTransition l2 : leaf.getReach()){
				if(input.equals(l2.getInput()) && !rlist2.contains(l2.getTarget())){
					return true;
				}
			}			
		}else{
			System.out.println("ERROR FINDING LEAF "+rlist2+" leafs "+leafs_in);
		}
		return false;
	}
	
	private String getType(CTransition l, ArrayList<CState> rlist, boolean isTarget){
		String type = "";
		CState compare = null;
		if(isTarget){
			compare = l.getTarget();
		}else compare = l.getSource();
		//if(rlist.contains(compare)){
		if(containState(rlist,compare)){
			//target is normal
			type = "normal";
		}else{
			//target is outside
			type = "out";
		}
		return type;
	}
		
	private String getType1(CState init, CTransition l, ArrayList<CState> rlist){
		String type = "";
		if(l.getTarget().equals(init)){
			//target is init
			type = "init";
		}else if(rlist.contains(l.getTarget())){
			//target is normal
			type = "normal";
		}else{
			//target is outside
			type = "out";
		}
		return type;
	}
	private String getType2(String in, CState init, CTransition l, 
			ArrayList<CState> rlist, HashMap<CState,ArrayList<CState>> rmap){
		String type = "";
		if(l.getTarget().equals(init) && isTransitionLeaf(in, rmap, rlist)){
			//target is loop
			type = "loop";
		}else if(rlist.contains(l.getTarget())){
			//target is normal
			type = "normal";
		}else if(isTransitionLeafOut(in, rmap, rlist)){
			//target is outside
			type = "out";
		}else type = "error";
		return type;
	}
	private String getType11(CState init, CTransition l, ArrayList<CState> rlist){
		String type = "";
		if(l.getSource().equals(init)){
			//target is init
			type = "init";
		}else if(rlist.contains(l.getSource())){
			//target is normal
			type = "normal";
		}else{
			//target is outside
			type = "out";
		}
		return type;
	}
	private String getType22(String in, CState init, CTransition l, 
			ArrayList<CState> rlist, HashMap<CState,ArrayList<CState>> rmap){
		String type = "";
		if(l.getSource().equals(init) && isTransitionLeaf2(in, rmap, rlist)){
			//target is loop
			type = "loop";
		}else if(rlist.contains(l.getSource())){
			//target is normal
			type = "normal";
		}else if(isTransitionLeafOut2(in, rmap, rlist)){
			//target is outside
			type = "out";
		}else type = "error";
		return type;
	}
		
	private ArrayList<CState> newComposeLeave(CState r, CState icomp, ArrayList<CState> rlist1, ArrayList<CState> rlist2,
			ArrayList<CState> cplist,
			ArrayList<CTransition> ctlist, String comp_cond, HashMap<CState,ArrayList<CState>> rmap){
		
		System.out.println("NEW COMPOSE LEAVE 1 "+r+" "+icomp+" "+r.getLeave());	
		ArrayList<CState> list = new ArrayList<CState>();
		if(leafs_in.contains(r)){
			return list;
		}
		for(CTransition l1 : r.getLeave()){
			if(leafs_in.contains(l1.getTarget())){
				continue;
			}
			HashMap<CTransition, String> tmap = new HashMap<CTransition, String>();
			String type1 = getType1(icomp,l1,rlist1);
			//tmap.put(l1, type1);
			String type2 = "";			
			for(CTransition l2 : icomp.getLeave()){
				if(l1.getInput().equals(l2.getInput())){
					type2 = getType2(l1.getInput(),icomp,l2,rlist2,rmap);
					tmap.put(l2, type2);
				}		
			}			
			//get source state
			ArrayList<CState> slit = new ArrayList<CState>();
			slit.add(r);
			slit.add(getTransitionLeaf(rmap,rlist2,null));
			System.out.println("SLIT B4 "+slit);
			orderComp(slit);
			System.out.println("SLIT AF "+slit);
			CState source = composeMStates(cplist,slit,comp_cond);
			//if(!containState(cplist,source)){
			//	return list;
			//}
			list.add(source);
			//get target state
			slit = new ArrayList<CState>();
			ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
			mutlist.add(l1);
			if(type1.equals("out")){				
				System.out.println("LEAVE INIT MULTI THEN 1 ");
				//composeMTransition(ctlist, mutlist, source, l1.getTarget(), null);
			}else{
				if(type1.equals("normal")){
					slit.add(l1.getTarget());					
				}
				for(CTransition t : tmap.keySet()){
					if(tmap.get(t).equals("out")){						
						//mutlist.clear();						
						//mutlist.add(t);
						System.out.println("LEAVE INIT MULTI OUT 1");
						//composeMTransition(ctlist, mutlist, source, t.getTarget(), null);
						//continue;
					}else if(tmap.get(t).equals("normal")){
						ArrayList<CState> slit2 = (ArrayList<CState>) slit.clone();
						ArrayList<CTransition> mutlist2 = (ArrayList<CTransition>) mutlist.clone();
						slit2.add(t.getTarget());
						mutlist2.add(t);
						orderComp(slit2);
						CState target = composeMStates(cplist,slit2,comp_cond);
						list.add(target);
						System.out.println("LEAVE INIT MULTI 2 "+mutlist2);
						composeMTransition(ctlist, mutlist2, source, target, null);
					}else if(tmap.get(t).equals("loop")){
						ArrayList<CTransition> mutlist2 = (ArrayList<CTransition>) mutlist.clone();
						mutlist2.add(t);						
						System.out.println("LEAVE INIT MULTI 3"+mutlist2);
						composeMTransition(ctlist, mutlist2, source, l1.getTarget(), null);
					}					
				}
			}	
		}
		return list;
	}
	private ArrayList<CState> newComposeLeave2(CState r1, CState r2, CState icomp,
			ArrayList<CState> rlist1, ArrayList<CState> rlist2,
			ArrayList<CState> cplist,
			ArrayList<CTransition> ctlist, String comp_cond, HashMap<CState,ArrayList<CState>> rmap){
		
		System.out.println("NEW COMPOSE LEAVE 2 "+r1+" "+r2+" "+r1.getLeave()+ " "+r2.getLeave());
		ArrayList<CState> list = new ArrayList<CState>();
		for(CTransition l1 : r1.getLeave()){
			//if(leafs_in.contains(l1.getTarget())){
			//	continue;
			//}
			HashMap<CTransition, String> tmap = new HashMap<CTransition, String>();
			String type1 = getType1(icomp,l1,rlist1);			
			String type2 = "";			
			for(CTransition l2 : r2.getLeave()){
				if(l1.getInput().equals(l2.getInput())){
					type2 = getType1(icomp,l2,rlist2);
					tmap.put(l2, type2);
				}		
			}			
			//get source state
			ArrayList<CState> slit = new ArrayList<CState>();
			slit.add(r1);
			slit.add(r2);
			orderComp(slit);
			CState source = composeMStates(cplist,slit,comp_cond);
			//if(!containState(cplist,source)){
			//	return list;
			//}
			list.add(source);
			//get target state
			slit = new ArrayList<CState>();
			ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
			mutlist.add(l1);
			if(type1.equals("out")){				
				System.out.println("LEAVE INIT MULTI THEN 11");
				//composeMTransition(ctlist, mutlist, source, l1.getTarget(), null);
			}else{
				if(type1.equals("normal")){
					slit.add(l1.getTarget());					
				}
				for(CTransition t : tmap.keySet()){
					if(tmap.get(t).equals("out")){
						//mutlist.clear();
						//mutlist.add(t);
						System.out.println("LEAVE INIT MULTI OUT 11");
						//composeMTransition(ctlist, mutlist, source, t.getTarget(), null);
						//continue;
					}else if(tmap.get(t).equals("normal")){
						ArrayList<CState> slit2 = (ArrayList<CState>) slit.clone();
						ArrayList<CTransition> mutlist2 = (ArrayList<CTransition>) mutlist.clone();
						slit2.add(t.getTarget());
						mutlist2.add(t);
						orderComp(slit2);
						CState target = composeMStates(cplist,slit2,comp_cond);
						list.add(target);
						System.out.println("LEAVE INIT MULTI 2 "+mutlist2);
						composeMTransition(ctlist, mutlist2, source, target, null);
					}else if(tmap.get(t).equals("init")){
						ArrayList<CTransition> mutlist2 = (ArrayList<CTransition>) mutlist.clone();
						mutlist2.add(t);						
						System.out.println("LEAVE INIT MULTI 3"+mutlist2);
						composeMTransition(ctlist, mutlist2, source, l1.getTarget(), null);
					}					
				}
			}	
		}
		return list;
	}
	private ArrayList<CState> newComposeReach(CState r, CState icomp, ArrayList<CState> rlist1, ArrayList<CState> rlist2,
			ArrayList<CState> cplist,
			ArrayList<CTransition> ctlist, String comp_cond, HashMap<CState,ArrayList<CState>> rmap){
		
		System.out.println("NEW COMPOSE REACH 1 "+r+" "+icomp+" "+r.getReach());
		ArrayList<CState> list = new ArrayList<CState>();
		if(leafs_in.contains(r)){
			return list;
		}
		for(CTransition l1 : r.getReach()){	
			if(leafs_in.contains(l1.getSource())){
				continue;
			}
			HashMap<CTransition, String> tmap = new HashMap<CTransition, String>();
			String type1 = getType11(icomp,l1,rlist1);			
			String type2 = "";			
			for(CTransition l2 : icomp.getReach()){
				if(l1.getInput().equals(l2.getInput())){
					type2 = getType22(l1.getInput(),icomp,l2,rlist2,rmap);
					tmap.put(l2, type2);
				}		
			}			
			//get target state
			ArrayList<CState> slit = new ArrayList<CState>();
			slit.add(r);
			slit.add(getTransitionLeaf(rmap,rlist2,null));
			orderComp(slit);
			CState target = composeMStates(cplist,slit,comp_cond);
			//if(!containState(cplist,target)){
			//	return list;
			//}
			list.add(target);
			//get source state
			slit = new ArrayList<CState>();
			ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
			mutlist.add(l1);
			if(type1.equals("out")){
				System.out.println("REACH INIT MULTI THEN 1");
				//composeMTransition(ctlist, mutlist, l1.getSource(), target, null);
			}else{
				if(type1.equals("normal")){
					slit.add(l1.getSource());					
				}
				for(CTransition t : tmap.keySet()){
					if(tmap.get(t).equals("out")){						
						//mutlist.clear();						
						//mutlist.add(t);
						System.out.println("REACH INIT MULTI OUT 1");
						//composeMTransition(ctlist, mutlist, t.getSource(), target, null);
						//continue;
					}else if(tmap.get(t).equals("normal")){
						ArrayList<CState> slit2 = (ArrayList<CState>) slit.clone();
						ArrayList<CTransition> mutlist2 = (ArrayList<CTransition>) mutlist.clone();
						slit2.add(t.getSource());
						mutlist2.add(t);
						orderComp(slit2);
						CState source = composeMStates(cplist,slit2,comp_cond);
						list.add(source);
						System.out.println("REACH INIT MULTI 2 "+mutlist2);
						composeMTransition(ctlist, mutlist2, source, target, null);
					}else if(tmap.get(t).equals("loop")){
						ArrayList<CTransition> mutlist2 = (ArrayList<CTransition>) mutlist.clone();
						mutlist2.add(t);						
						System.out.println("REACH INIT MULTI 3"+mutlist2);
						composeMTransition(ctlist, mutlist2, l1.getSource(), target, null);
					}					
				}
			}	
		}
		return list;
	}
	private ArrayList<CState> newComposeReach2(CState r1, CState r2, CState icomp, ArrayList<CState> rlist1, ArrayList<CState> rlist2,
			ArrayList<CState> cplist,
			ArrayList<CTransition> ctlist, String comp_cond, HashMap<CState,ArrayList<CState>> rmap){
		
		System.out.println("NEW COMPOSE REACH 2 "+r1+" "+r2+" "+r1.getReach()+ " "+r2.getReach());
		ArrayList<CState> list = new ArrayList<CState>();
		for(CTransition l1 : r1.getReach()){
			if(leafs_in.contains(l1.getSource())){
				continue;
			}
			HashMap<CTransition, String> tmap = new HashMap<CTransition, String>();
			String type1 = getType11(icomp,l1,rlist1);			
			String type2 = "";			
			for(CTransition l2 : r2.getReach()){
				if(l1.getInput().equals(l2.getInput())){
					type2 = getType11(icomp,l2,rlist2);
					tmap.put(l2, type2);
				}		
			}			
			//get target state
			ArrayList<CState> slit = new ArrayList<CState>();
			slit.add(r1);
			slit.add(r2);
			orderComp(slit);
			CState target = composeMStates(cplist,slit,null);
			//if(!containState(cplist,target)){
			//	return list;
			//}
			list.add(target);
			//get source state
			slit = new ArrayList<CState>();
			ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
			mutlist.add(l1);
			if(type1.equals("out")){				
				System.out.println("REACH INIT MULTI THEN 11");
				//composeMTransition(ctlist, mutlist, l1.getSource(), target, null);
			}else{
				if(type1.equals("normal")){
					slit.add(l1.getSource());					
				}
				for(CTransition t : tmap.keySet()){
					if(tmap.get(t).equals("out")){						
						//mutlist.clear();						
						//mutlist.add(t);
						System.out.println("REACH INIT MULTI OUT 11");
						//composeMTransition(ctlist, mutlist, t.getSource(), target, null);
						//continue;
					}else if(tmap.get(t).equals("normal")){
						ArrayList<CState> slit2 = (ArrayList<CState>) slit.clone();
						ArrayList<CTransition> mutlist2 = (ArrayList<CTransition>) mutlist.clone();
						slit2.add(t.getSource());
						mutlist2.add(t);
						orderComp(slit2);
						CState source = composeMStates(cplist,slit2,comp_cond);
						list.add(source);
						System.out.println("REACH INIT MULTI 22 "+mutlist2);
						composeMTransition(ctlist, mutlist2, source, target, null);
					}else if(tmap.get(t).equals("loop")){
						ArrayList<CTransition> mutlist2 = (ArrayList<CTransition>) mutlist.clone();
						mutlist2.add(t);						
						System.out.println("REACH INIT MULTI 33"+mutlist2);
						composeMTransition(ctlist, mutlist2, l1.getSource(), target, null);
					}					
				}
			}	
		}
		return list;
	}
	
	private void newComposeReach_old(CState r1, CState r2, ArrayList<CState> rlist2, ArrayList<CState> cplist,
			ArrayList<CTransition> ctlist, String comp_cond){
		for(CTransition e1 : r1.getReach()){
			ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
			ArrayList<CState> slit = new ArrayList<CState>();
			mutlist.add(e1);
			for(CTransition l2 : r2.getReach()){
				if(e1.getInput().equals(l2.getInput()) && rlist2.contains(l2.getSource())){
					mutlist.add(l2);
				}
			}
			//get target state	
			slit.add(e1.getTarget());				
			CState target = composeMStates(cplist,slit,comp_cond);
			//get source state
			slit = new ArrayList<CState>();
			for(CTransition l : mutlist){
				slit.add(l.getSource());					
			}
			orderComp(slit);
			CState source = composeMStates(cplist,slit,comp_cond);
			System.out.println("REACH INIT MULTI "+mutlist);
			composeMTransition(ctlist, mutlist, source, target, null);
		}
	}
	
	
	private ArrayList<CState> composeRegionPair(ArrayList<CState> rlist11, ArrayList<CState> rlist2,
			ArrayList<CState> cplist, ArrayList<CTransition> ctlist, CState icomp,
			String comp_cond, HashMap<CState,ArrayList<CState>> rmap){
	
		ArrayList<CState> list = new ArrayList<CState>();		
		ArrayList<CState> out = new ArrayList<CState>();		
			
		for(CState r1 : rlist11){			
			//compose with init composition
			System.out.println("PAIR "+r1+ " "+rlist11);
			list.addAll(newComposeLeave(r1,icomp,rlist11,rlist2,cplist,ctlist,comp_cond,rmap));
			list.addAll(newComposeReach(r1,icomp,rlist11,rlist2,cplist,ctlist,comp_cond,rmap));
			//rlist11.add(getTransitionLeaf(rmap,rlist1));
			for(CState r2 : rlist2){
				if(!leafs_in.contains(r1) || !leafs_in.contains(r2)){
					System.out.println("PAIR 2 "+r2+ " "+rlist2);
					list.addAll(newComposeLeave2(r1,r2,icomp,rlist11,rlist2,cplist,ctlist,comp_cond,rmap));
					list.addAll(newComposeReach2(r1,r2,icomp,rlist11,rlist2,cplist,ctlist,comp_cond,rmap));	
				}							
			}
		}	
		//compose right with init composition
		//for(CState r2 : rlist2){			
		//	list.addAll(newComposeLeave(r2,icomp,rlist2,rlist11,cplist,ctlist,comp_cond,rmap));
		//	list.addAll(newComposeReach(r2,icomp,rlist2,rlist11,cplist,ctlist,comp_cond,rmap));
		//}	
		
		for(CState l : list){
			if(!containState(out,l)){
				out.add(l);
			}
		}
		return out;
	}
	
	/*for(ArrayList<CTransition> onelist : tocompose){
		ArrayList<CState> slit = new ArrayList<CState>();
		for(CTransition tn : onelist){
			slit.add(tn.getTarget());					
		}
		//get target state	
		orderComp(slit);
		CState cp1 = composeMStates(cplist,slit,comp_cond);
		//get source state
		CState cp2 = null;
		slit.clear();
		ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
		for(CTransition one : onelist){
			boolean found = false;
			for(CState region : regions){
				ArrayList<CState> rlist = getRSlist(region,rmap);
				if(rlist.contains(one.getSource())){
					found = true;
					break;
				}
			}
			if(!found){
				cp2 = one.getSource();
				slit.add(one.getSource());
				mutlist.add(one);
				break;
			}
		}
		if(cp2 == null){
			for(CTransition tn : onelist){
				slit.add(tn.getSource());					
			}
			orderComp(slit);
			cp2 = composeMStates(cplist,slit,comp_cond);
			mutlist = onelist;
		}
		System.out.println("REACH MULTI "+mutlist);
		composeMTransition(ctlist, mutlist, cp2, cp1, null);
	}*/
		
	private void setComposition_old(CState n, CState comp, ArrayList<CState> cplist, 
			ArrayList<CTransition> ctlist, HashMap<CState,ArrayList<CState>> rmap,
			ArrayList<ArrayList<CState>> rcomposed){
		
		System.out.println("SET COMPOSITION "+rcomposed);
		cm:for(ArrayList<CState> set : rcomposed){			
			//check valid combination
			if(set.size() > 1){
				String cond = "(and ";
				for(CState re1 : set){					
					cond = cond.concat(re1.getFCondition()+" ");
				}
				cond = cond.concat(")");
				if(!checkZ3Cond(cond)){
					System.out.println("SKIP 1 composition "+set);
					continue cm;
				}
			}
			for(CState reg1 : n.getSubstates()){
				ArrayList<CState> qr = new ArrayList<CState>();
				for(CState reg2 : n.getSubstates()){
					if(!reg1.equals(reg2)){
						if(checkZ3Equiv(reg1.getFCondition(),reg2.getFCondition())){
							qr.add(reg2);
						}
					}
				}
				if(set.contains(reg1) && !set.containsAll(qr)){
					System.out.println("SKIP 2 composition "+set);
					continue cm;
				}
			}
			
			String comp_cond = "(and ";
			for(CState region : n.getSubstates()){
				if(set.contains(region)){
					comp_cond = comp_cond.concat(region.getFCondition()+" ");
				}else{
					comp_cond = comp_cond.concat("(not "+region.getFCondition()+") ");
				}
			}
			comp_cond = comp_cond.substring(0,comp_cond.length()-1);
			comp_cond = comp_cond.concat(")");
			
			orderComp(set);
			System.out.println("SET "+set);
			if(set.size() <= 1){
				ArrayList<CState> rlist1 = getRSlist(set.get(0),rmap);		
				for(CState r1 : rlist1){
					System.out.println("ADD SINGLE "+r1);
					//composeSingleRegion(r1,cplist,ctlist,comp_cond,rlist1);
				}
			}else{
				ArrayList<CState> set2 = (ArrayList<CState>) set.clone();
				CState re1 = set2.remove(0);
				ArrayList<CState> rlist1 = getRSlist(re1,rmap);	
				ArrayList<CState> rlist11 = (ArrayList<CState>) rlist1.clone();
				//rlist11.add(getTransitionLeaf(rmap,rlist1));
				for(CState re2 : set2){
					ArrayList<CState> rlist2 = getRSlist(re2,rmap);
					//rlist2.add(getTransitionLeaf(rmap,rlist2));
					System.out.println("ADD INCREMENTAL "+rlist11);
					ArrayList<CState> rlist = composeRegionPair(rlist11,rlist2,cplist,ctlist,comp,comp_cond,rmap);
					if(!re2.equals(set2.get(0))) cplist.removeAll(rlist11);
					rlist11 = rlist;
					
					ArrayList<CState> desc = re1.getDescendants();				
					desc.addAll(rlist);
					re1.setDescendants(desc);
				}
			}			
		}
	}
	
	private void setNewComposition(CState n, CState comp, ArrayList<CState> cplist, 
			ArrayList<CTransition> ctlist, HashMap<CState,ArrayList<CState>> rmap,
			ArrayList<ArrayList<CState>> rcomposed){
		
		System.out.println("SET COMPOSITION "+rcomposed);
		cm:for(ArrayList<CState> set : rcomposed){			
			//check valid combination
			if(set.size() > 1){
				String cond = "(and ";
				for(CState re1 : set){					
					cond = cond.concat(re1.getFCondition()+" ");
				}
				cond = cond.concat(")");
				if(!checkZ3Cond(cond)){
					System.out.println("SKIP 1 composition "+set);
					continue cm;
				}
			}
			for(CState reg1 : n.getSubstates()){
				ArrayList<CState> qr = new ArrayList<CState>();
				for(CState reg2 : n.getSubstates()){
					if(!reg1.equals(reg2)){
						if(checkZ3Equiv(reg1.getFCondition(),reg2.getFCondition())){
							qr.add(reg2);
						}
					}
				}
				if(set.contains(reg1) && !set.containsAll(qr)){
					System.out.println("SKIP 2 composition "+set);
					continue cm;
				}
			}
			
			String comp_cond = "(and ";
			for(CState region : n.getSubstates()){
				if(set.contains(region)){
					comp_cond = comp_cond.concat(region.getFCondition()+" ");
				}else{
					comp_cond = comp_cond.concat("(not "+region.getFCondition()+") ");
				}
			}
			comp_cond = comp_cond.substring(0,comp_cond.length()-1);
			comp_cond = comp_cond.concat(")");			
			
			System.out.println("SET B4 "+set);
			orderComp(set);
			System.out.println("SET COND "+set+" "+comp_cond+" "+n.getSubstates());
			ArrayList<CState> composed1 = null;
			ArrayList<CState> invalid = new ArrayList<CState>();
			if(set.size() <= 1){
				ArrayList<CState> rlist1 = getRSlist(set.get(0),rmap);
				rlist1.add(0,getTransitionLeaf(rmap,rlist1,set.get(0)));
				//for(CState r1 : rlist1){
					//System.out.println("ADD SINGLE "+r1);
					composed1 = composeSingleRegion(cplist,ctlist,comp_cond,rlist1);
					cplist.removeAll(rlist1);
					//invalid.addAll(rlist1);
				//}
			}else{
				ArrayList<CState> set2 = (ArrayList<CState>) set.clone();
				CState re1 = set2.remove(0);
				ArrayList<CState> rlist1 = getRSlist(re1,rmap);	
				ArrayList<CState> rlist11 = (ArrayList<CState>) rlist1.clone();
				rlist11.add(0,getTransitionLeaf(rmap,rlist1,re1));
				for(CState re2 : set2){
					ArrayList<CState> rlist2 = getRSlist(re2,rmap);
					rlist2.add(0,getTransitionLeaf(rmap,rlist2,re2));
					System.out.println("ADD INCREMENTAL "+rlist11);
					
					composed1 = pairCompositionLeave(cplist,ctlist,comp_cond,rlist11,rlist2);
					ArrayList<CState> composed2 = pairCompositionReach(cplist,ctlist,comp_cond,rlist11,rlist2);
					for(CState s : composed2){
						if(!containState(composed1,s)){
							composed1.add(s);
						}
					}
					cplist.removeAll(rlist11);
					invalid.addAll(rlist11);
					for(CState s : rlist11){						
						s.setSuperState(re1);
						re1.getDescendants().add(s);
					}
					rlist11 = composed1;
				}				
			}
			
			ArrayList<CTransition> tcomposed = new ArrayList<CTransition>();
			for(CState s : composed1){
				for(CTransition t : s.getLeave()){
					if(!tcomposed.contains(t)) tcomposed.add(t);
				}
				for(CTransition t : s.getReach()){
					if(!tcomposed.contains(t)) tcomposed.add(t);
				}
			}	
			
			CState leaf = composed1.get(0);
			//System.out.println("LEAF OF +AND+ STATE B4 "+leaf+ " "+composed1);
			for(CState def : composed1){
				if(def.getDefault()){
					leaf = def;
					break;
				}
			}
			composed1.remove(leaf);
			System.out.println("LEAF OF +AND+ STATE "+leaf+" "+leaf.getFCondition()+ " "+composed1);
			ArrayList<CTransition> rem = new ArrayList<CTransition>();
			
			//ArrayList<CTransition> ctlist2 = (ArrayList<CTransition>) ctlist.clone();
			//for(CTransition t : ctlist2){
			System.out.println("INVALID "+invalid);
			for(CState i : invalid){
				System.out.println("ST "+i+" "+i.getFCondition());
			}
			
			//final link to initial state
			for(CTransition t : tcomposed){			
				//System.out.println("TO ADD 1 "+t);
				String cond = "(and "+t.getSource().getFCondition()+" "+t.getFCondition()
					+" "+t.getTarget().getFCondition()+")";				
				if(!checkZ3Cond(cond) || leafs_in.contains(t.getSource()) || leafs_in.contains(t.getTarget())){
					continue;
				}
				if(t.getTarget().equals(leaf) && !containState(invalid,t.getSource())){					
					ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
					mutlist.add(t);
					CState source = t.getSource();
					//if(t.getTarget().getLabel().equals(leaf.getLabel()) && 
					//		checkZ3Equiv(t.getTarget().getFCondition(), leaf.getFCondition())){	
					if(t.getSource().equals(leaf)){
						source = comp;
						composeMTransition(ctlist, mutlist, source, comp, comp_cond);
					}else composeMTransition(ctlist, mutlist, source, comp, null);
					rem.add(t);
				}
				if(t.getSource().equals(leaf) && !containState(invalid,t.getTarget())){					
					ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
					mutlist.add(t);
					CState target = t.getTarget();
					if(t.getTarget().equals(leaf)){
					//if(t.getTarget().getLabel().equals(leaf.getLabel()) && 
					//		checkZ3Equiv(t.getTarget().getFCondition(), leaf.getFCondition())){				
						target = comp;						
						composeMTransition(ctlist, mutlist, comp, target, comp_cond);
					}else composeMTransition(ctlist, mutlist, comp, target, null);
					rem.add(t);
				}
			}
			for(CTransition t : rem){
				ctlist.remove(t);
			}
			
			//remove unreachable composition			
			cplist.removeAll(composed1);
			cplist.remove(leaf);						
			AddReach(cplist,composed1,tcomposed,leaf,leaf.getFCondition());
			removed.addAll(leaf.getLeave());
			removed.addAll(leaf.getReach());
			System.out.println("REMOVED 00");
			for(CTransition h: removed){
				System.out.println(h);
			}
		}		
	}
	
	private void AddReach(ArrayList<CState> cplist, ArrayList<CState> composed,
			ArrayList<CTransition> tcomposed, CState state, String icond){
		for(CTransition t: state.getLeave()){
			String cond = "(and "+icond+" "+t.getSource().getFCondition()+" "+t.getFCondition()
				+" "+t.getTarget().getFCondition()+")";
			if(composed.contains(t.getTarget()) && tcomposed.contains(t)){
				if(checkZ3Cond(cond)){
					System.out.println("ADD LIST "+t);
					cplist.add(t.getTarget());
					tcomposed.remove(t);
					if(removed.contains(t)) removed.remove(t);
					AddReach(cplist,composed,tcomposed,t.getTarget(),cond);
				}else{
					//System.out.println("REMOVED "+t);
					//if(!removed.contains(t)) removed.add(t);
				}				
			}
		}
		System.out.println("REMOVED 111");
		for(CTransition h: removed){
			System.out.println(h);
		}
	}
	
	private ArrayList<CState> pairCompositionLeave(ArrayList<CState> cplist, ArrayList<CTransition> ctlist,
		 String comp_cond, ArrayList<CState> rlist11, ArrayList<CState> rlist2){
		
		ArrayList<CState> list = new ArrayList<CState>();
		ArrayList<CTransition> processed = new ArrayList<CTransition>();
		for(CState r1 : rlist11){
			for(CTransition l1 : r1.getLeave()){
				String type1 = getType(l1,rlist11,true);
				//try to find same input 				
				for(CState r2 : rlist2){
					boolean found = false;
					//get source state
					ArrayList<CState> slit = new ArrayList<CState>();
					slit.add(r1);
					slit.add(r2);
					System.out.println("SLIT B4 "+slit);
					orderComp(slit);
					System.out.println("SLIT AF "+slit);
					CState source = null;					
					if(r1.getDefault() && r2.getDefault()){						
						source = composeMStates(cplist,slit,comp_cond,true);
					}else source = composeMStates(cplist,slit,comp_cond);
					System.out.println("SOURCE "+ r1+" "+r2+" "+source+" "+slit+ " "+type1+" "+l1.getTarget());
					if(!list.contains(source)) list.add(source);
					
					//create new transition per combination 
					for(CTransition l2 : r2.getLeave()){
						String type2 = getType(l2,rlist2,true);
						if(l1.getInput().equals(l2.getInput())){
							found = true;
							processed.add(l2);
							//get target state
							ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
							slit = new ArrayList<CState>();
							if(type1.equals("normal") && type2.equals("normal")){
								slit.add(l1.getTarget());
								slit.add(l2.getTarget());
								orderComp(slit);
								mutlist.add(l1);
								mutlist.add(l2);
								CState target = composeMStates(cplist,slit,comp_cond);
								System.out.println("COMP NORMAL 1 "+l1+ " "+l2);
								if(r1.getDefault() && r2.getDefault() &&
										leafs_in.contains(l1.getTarget()) && leafs_in.contains(l2.getTarget())){
									//when it is a self loop from the leafs
									composeMTransition(ctlist, mutlist, source, target, comp_cond);
								}else composeMTransition(ctlist, mutlist, source, target, null);
								if(!list.contains(target)) list.add(target);
								
								//add the negation 
								//String neg = "(and "+comp_cond+" (not "+l2.getTarget().getFCondition()+"))";
								//String neg = "(not "+l2.getTarget().getFCondition()+")";
								String neg = "(not ";
								for(CTransition l22 : r2.getLeave()){
									neg = neg.concat("(and "+ l22.getFCondition()
										+l22.getTarget().getFCondition()+") ");
								}
								neg = neg.concat(")");
								//String neg = "(not (and "+ l2.getFCondition()+" "+
								//		l2.getTarget().getFCondition()+"))";
								mutlist = new ArrayList<CTransition>();
								mutlist.add(l1);
								slit = new ArrayList<CState>();
								slit.add(l1.getTarget());
								slit.add(r2);
								orderComp(slit);
								target = composeMStates(cplist,slit,comp_cond);
								if(!list.contains(target)) list.add(target);
								System.out.println("COMP NFOUND 1 "+l1);
								composeMTransition(ctlist, mutlist, source, target, neg);
								
								//neg = "(not (and "+ l1.getFCondition()+" "+
								//		l1.getTarget().getFCondition()+"))";
								neg = "(not ";
								for(CTransition l11 : r1.getLeave()){
									neg = neg.concat("(and "+ l11.getFCondition()
										+l11.getTarget().getFCondition()+") ");
								}
								neg = neg.concat(")");
								mutlist = new ArrayList<CTransition>();
								mutlist.add(l2);
								slit = new ArrayList<CState>();
								slit.add(l2.getTarget());
								slit.add(r1);
								orderComp(slit);
								target = composeMStates(cplist,slit,comp_cond);
								if(!list.contains(target)) list.add(target);
								System.out.println("COMP NFOUND -1 "+l2);
								composeMTransition(ctlist, mutlist, source, target, neg);
								
								//remove invalid transition
								removed.add(l1);
								removed.add(l2);
							}else if(type1.equals("out") && type2.equals("out")){								
								mutlist.add(l1);								
								System.out.println("COMP OUT 11 "+l1+ " "+l2);
								composeMTransition(ctlist, mutlist, source, l1.getTarget(), null);
								mutlist = new ArrayList<CTransition>();
								mutlist.add(l2);
								composeMTransition(ctlist, mutlist, source, l2.getTarget(), null);								
								//remove invalid transition
								removed.add(l1);
								removed.add(l2);
							}else if(type1.equals("normal") && type2.equals("out")){
								mutlist.add(l2);								
								System.out.println("COMP OUT 12 "+l1+ " "+l2);
								composeMTransition(ctlist, mutlist, source, l2.getTarget(), null);
								//remove invalid transition
								removed.add(l1);
								removed.add(l2);
							}else if(type1.equals("out") && type2.equals("normal")){
								mutlist.add(l1);								
								System.out.println("COMP OUT 13 "+l1+ " "+l2);
								composeMTransition(ctlist, mutlist, source, l1.getTarget(), null);
								//remove invalid transition
								removed.add(l1);
								removed.add(l2);
							}							
						}
					}
					//first combination
					if(!found){
						//get target
						slit = new ArrayList<CState>();
						ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
						mutlist.add(l1);
						if(type1.equals("normal")){
							slit.add(l1.getTarget());
							slit.add(r2);
							System.out.println("SLIT B4 "+slit);
							orderComp(slit);
							System.out.println("SLIT AF "+slit);
							CState target = composeMStates(cplist,slit,comp_cond);
							if(!list.contains(target)) list.add(target);
							System.out.println(".COMP NFOUND 1 "+l1);
							composeMTransition(ctlist, mutlist, source, target, null);
							removed.add(l1);
						}else if(type1.equals("out")){							
							System.out.println(".COMP NFOUND 2 "+l1);
							composeMTransition(ctlist, mutlist, source, l1.getTarget(), null);
							removed.add(l1);
						}						
					}
				}				
			}
			//add second combination
			for(CState r2 : rlist2){
				//get source state
				ArrayList<CState> slit = new ArrayList<CState>();
				slit.add(r1);
				slit.add(r2);				
				orderComp(slit);				
				CState source = null;					
				if(r1.getDefault() && r2.getDefault()){						
					source = composeMStates(cplist,slit,comp_cond,true);
				}else source = composeMStates(cplist,slit,comp_cond);
				System.out.println("SOURCE 111"+ r1+" "+r2+" "+source+" "+slit);
				if(!list.contains(source)) list.add(source);
				for(CTransition l2 : r2.getLeave()){
					if(!processed.contains(l2)){
						String type2 = getType(l2,rlist2,true);
						slit = new ArrayList<CState>();
						ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
						mutlist.add(l2);
						if(type2.equals("normal")){						
							slit.add(r1);
							slit.add(l2.getTarget());						
							orderComp(slit);
							CState target = composeMStates(cplist,slit,comp_cond);
							if(!list.contains(target)) list.add(target);
							System.out.println("COMP NFOUND 111 "+l2);
							composeMTransition(ctlist, mutlist, source, target, null);
							removed.add(l2);
						}else if(type2.equals("out")){	
							System.out.println("COMP NFOUND 222 "+l2);
							composeMTransition(ctlist, mutlist, source, l2.getTarget(), null);
							removed.add(l2);
						}
					}
				}
			}
		}
		for(CState s : list){
			CState region = rlist11.get(0).getSuperState();
			s.setSuperState(region);
			region.getDescendants().add(s);
		}		
		
		System.out.println("REMOVED 222");
		for(CTransition h: removed){
			System.out.println(h);
		}
		
		return list;
	}
	
	private ArrayList<CState> pairCompositionReach(ArrayList<CState> cplist, ArrayList<CTransition> ctlist,
		 String comp_cond, ArrayList<CState> rlist11, ArrayList<CState> rlist2){
		
		ArrayList<CTransition> processed = new ArrayList<CTransition>();
		ArrayList<CState> list = new ArrayList<CState>();
		for(CState r1 : rlist11){
			for(CTransition l1 : r1.getReach()){
				String type1 = getType(l1,rlist11,false);
				//try to find same input 				
				for(CState r2 : rlist2){
					boolean found = false;
					//get target state
					ArrayList<CState> slit = new ArrayList<CState>();
					slit.add(r1);
					slit.add(r2);
					orderComp(slit);
					CState target = null;
					if(r1.getDefault() && r2.getDefault()){					
						target = composeMStates(cplist,slit,comp_cond,true);						
					}else target = composeMStates(cplist,slit,comp_cond);	
					if(!list.contains(target)) list.add(target);
					
					//create new transition per combination 
					for(CTransition l2 : r2.getReach()){
						String type2 = getType(l2,rlist2,false);
						if(l1.getInput().equals(l2.getInput())){
							found = true;
							processed.add(l2);
							//get source state
							ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
							slit = new ArrayList<CState>();
							if(type1.equals("normal") && type2.equals("normal")){
								slit.add(l1.getSource());
								slit.add(l2.getSource());
								orderComp(slit);
								mutlist.add(l1);
								mutlist.add(l2);
								CState source = composeMStates(cplist,slit,comp_cond);
								if(!list.contains(source)) list.add(source);
								System.out.println("+COMP NORMAL 1 "+l1+ " "+l2);
								if(r1.getDefault() && r2.getDefault() &&
								//if(leafs_in.contains(r1) && leafs_in.contains(r2) &&
										leafs_in.contains(l1.getSource()) && leafs_in.contains(l2.getSource())){
									//when it is a self loop from the leafs
									composeMTransition(ctlist, mutlist, source, target, comp_cond);
								}else composeMTransition(ctlist, mutlist, source, target, null);								
								
								//add the negation 								
								//String neg = "(and "+comp_cond+" (not "+l2.getSource().getFCondition()+"))";
								//String neg = "(not "+l2.getSource().getFCondition()+")";
								/*String neg = "(not (and "+ l2.getFCondition()+" "+
										l2.getSource().getFCondition()+"))";
								mutlist = new ArrayList<CTransition>();
								mutlist.add(l1);
								slit = new ArrayList<CState>();
								slit.add(l1.getSource());
								slit.add(r2);
								orderComp(slit);
								source = composeMStates(cplist,slit,comp_cond);
								if(!list.contains(source)) list.add(source);
								System.out.println("COMP NFOUND 11 "+l1);
								composeMTransition(ctlist, mutlist, source, target, neg);
								
								neg = "(not (and "+ l1.getFCondition()+" "+
										l1.getSource().getFCondition()+"))";
								mutlist = new ArrayList<CTransition>();
								mutlist.add(l2);
								slit = new ArrayList<CState>();
								slit.add(l2.getSource());
								slit.add(r1);
								orderComp(slit);
								source = composeMStates(cplist,slit,comp_cond);
								if(!list.contains(source)) list.add(source);
								System.out.println("COMP NFOUND -11 "+l2);
								composeMTransition(ctlist, mutlist, source, target, neg);
								*/
								//remove invalid transition
								removed.add(l1);
								removed.add(l2);
							}else if(type1.equals("out") && type2.equals("out")){								
								//add first
								slit.add(r1);
								slit.add(rlist2.get(0));
								orderComp(slit);
								CState target2 = composeMStates(cplist,slit,comp_cond);
								if(!list.contains(target2)) list.add(target2);								
								mutlist.add(l1);								
								System.out.println("+COMP OUT 11 "+l1+ " "+l2);
								composeMTransition(ctlist, mutlist, l1.getSource(), target2, null);
								
								//add second
								slit = new ArrayList<CState>();
								slit.add(rlist11.get(0));
								slit.add(r2);
								orderComp(slit);
								target2 = composeMStates(cplist,slit,comp_cond);
								if(!list.contains(target2)) list.add(target2);
								mutlist = new ArrayList<CTransition>();
								mutlist.add(l2);
								composeMTransition(ctlist, mutlist, l2.getSource(), target2, null);								
								//remove invalid transition
								removed.add(l1);
								removed.add(l2);
							}else if(type1.equals("normal") && type2.equals("out")){
								slit.add(rlist11.get(0));
								slit.add(r2);
								orderComp(slit);
								CState target2 = composeMStates(cplist,slit,comp_cond);
								if(!list.contains(target2)) list.add(target2);
								mutlist.add(l2);								
								System.out.println("+COMP OUT 12 "+l1+ " "+l2);
								composeMTransition(ctlist, mutlist, l2.getSource(), target2, null);
								//remove invalid transition
								removed.add(l1);
								removed.add(l2);
							}else if(type1.equals("out") && type2.equals("normal")){
								slit.add(r1);
								slit.add(rlist2.get(0));
								orderComp(slit);
								CState target2 = composeMStates(cplist,slit,comp_cond);
								if(!list.contains(target2)) list.add(target2);
								mutlist.add(l1);								
								System.out.println("+COMP OUT 13 "+l1+ " "+l2);
								composeMTransition(ctlist, mutlist, l1.getSource(), target2, null);
								//remove invalid transition
								removed.add(l1);
								removed.add(l2);
							}							
						}
					}
					//add first combination
					if(!found){
						//get target
						slit = new ArrayList<CState>();
						ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
						mutlist.add(l1);
						if(type1.equals("normal")){
							slit.add(l1.getSource());
							slit.add(r2);
							orderComp(slit);
							CState source = composeMStates(cplist,slit,comp_cond);
							if(!list.contains(source)) list.add(source);
							System.out.println("+COMP NFOUND 1 "+l1);
							//composeMTransition(ctlist, mutlist, source, target, null);
							removed.add(l1);
						}else if(type1.equals("out")){
							slit = new ArrayList<CState>();
							slit.add(r1);
							slit.add(rlist2.get(0));
							orderComp(slit);
							CState target2 = composeMStates(cplist,slit,comp_cond);							
							if(!list.contains(target2)) list.add(target2);
							System.out.println("+COMP NFOUND 2 "+l1);
							//composeMTransition(ctlist, mutlist, l1.getSource(), target2, null);
							removed.add(l1);
						}						
					}
				}				
			}
			//add second combination
			for(CState r2 : rlist2){
				//get target state
				ArrayList<CState> slit = new ArrayList<CState>();
				slit.add(r1);
				slit.add(r2);
				orderComp(slit);
				CState target = null;
				if(r1.getDefault() && r2.getDefault()){					
					target = composeMStates(cplist,slit,comp_cond,true);						
				}else target = composeMStates(cplist,slit,comp_cond);	
				if(!list.contains(target)) list.add(target);			
				for(CTransition l2 : r2.getReach()){
					if(!processed.contains(l2)){
						String type2 = getType(l2,rlist2,false);
						slit = new ArrayList<CState>();
						ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
						mutlist.add(l2);
						if(type2.equals("normal")){
							//String neg = "(and "+comp_cond+" (not "+l2.getSource().getFCondition()+"))";
							//String neg = "(not "+l2.getSource().getFCondition()+")";
							slit.add(r1);
							slit.add(l2.getSource());							
							orderComp(slit);
							CState source = composeMStates(cplist,slit,comp_cond);
							if(!list.contains(source)) list.add(source);
							System.out.println("+COMP NFOUND 111 "+l2);
							//composeMTransition(ctlist, mutlist, source, target, null);
							removed.add(l2);
						}else if(type2.equals("out")){
							slit = new ArrayList<CState>();							
							slit.add(rlist11.get(0));
							slit.add(r2);
							orderComp(slit);
							CState target2 = composeMStates(cplist,slit,comp_cond);							
							if(!list.contains(target2)) list.add(target2);
							System.out.println("+COMP NFOUND 222 "+l2);
							//composeMTransition(ctlist, mutlist, l2.getSource(), target2, null);
							removed.add(l2);
						}
					}
				}
			}
		}
		for(CState s : list){
			CState region = rlist11.get(0).getSuperState();
			s.setSuperState(region);
			region.getDescendants().add(s);
		}
		
		System.out.println("REMOVED 333");
		for(CTransition h: removed){
			System.out.println(h);
		}
		return list;
	}
	
	/*		
			for(CState re1 : set){				
				for(CState s1 : leafs_in){
					if(re1.getDescendants().contains(s1)){
						ArrayList<CState> rlist1 = (ArrayList<CState>) rmap.get(s1).clone();
						ArrayList<CState> rlist11 = (ArrayList<CState>) rlist1.clone();
						System.out.println(" LIST 11 "+ rlist11);
						for(CState r1 : rlist11){
							ArrayList<CTransition> leave1 = (ArrayList<CTransition>) r1.getLeave().clone();
							ArrayList<CTransition> reach1 = (ArrayList<CTransition>) r1.getReach().clone();
							
							//find paired transition
							for(CState re2 : set){
								for(CState s2 : leafs_in){
									if(!s1.equals(s2) && re2.getDescendants().contains(s2)){
										f
										ArrayList<CState> rlist2 = (ArrayList<CState>) rmap.get(s2).clone();
										for(CState r2 : rlist2){
											ArrayList<CTransition> leave2 = (ArrayList<CTransition>) r2.getLeave().clone();
											ArrayList<CTransition> reach2 = (ArrayList<CTransition>) r2.getReach().clone();
											
											for(CTransition l1 : leave1){
												ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
												mutlist.add(l1);
												for(CTransition l2 : r2.getLeave()){
													if(l1.getInput().equals(l2.getInput())){	
														if(rlist11.contains(l1.getTarget()) && rlist2.contains(l2.getTarget())){
															//before - check condition 
															
															mutlist.add(l2);								
															//found = true;									
														}
													}
												}
												ArrayList<CState> slit = new ArrayList<CState>();
												for(CTransition tn : mutlist){
													slit.add(tn.getTarget());					
												}
												orderComp(slit);
												if(leafs_in.contains(t1.getTarget())){
													//System.out.println("LEAVE THEN ");
													composeMTransition(ctlist, mutlist, comp, comp, comp_cond);
												}else if(re1.getDescendants().contains(t1.getTarget())){
													//System.out.println("LEAVE ELSE 1 ");
													CState cpn = composeMStates(cplist,slit,comp_cond);
													composeMTransition(ctlist, mutlist, comp, cpn, null);
												}else{
													//System.out.println("LEAVE ELSE 2 ");
													composeMTransition(ctlist, mutlist, comp, t1.getTarget(), null);
												}
											}											
										}
									}
								}
							}
							
							//there is no paired region
							CState cp = composeMStates(cplist,slit,comp_cond);
						}
						
						
						
						for(CTransition t1 : s1.getLeave()){
							//boolean found = false;
							ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
							mutlist.add(t1);
							for(CState re2 : set){
								for(CState s2 : leafs_in){
									if(!s1.equals(s2) && re2.getDescendants().contains(s2)){
										//System.out.println("TEST "+t1+ " "+s2);
										ArrayList<CState> rlist2 = (ArrayList<CState>) rmap.get(s2).clone();				
										ArrayList<CState> rlist11 = (ArrayList<CState>) rlist1.clone();
										System.out.println(" LIST 11 "+ rlist11);
										System.out.println(" LIST 2 "+ rlist2);
										for(CState r1 : rlist11){
											for(CState r2 : rlist2){
												CState cp = composeStates(cplist,r1,r2);
												ArrayList<CTransition> leave1 = (ArrayList<CTransition>) r1.getLeave().clone();
												ArrayList<CTransition> reach1 = (ArrayList<CTransition>) r1.getReach().clone();
												ArrayList<CTransition> leave2 = (ArrayList<CTransition>) r2.getLeave().clone();
												ArrayList<CTransition> reach2 = (ArrayList<CTransition>) r2.getReach().clone();
											}
										}
										
										
										for(CTransition t2 : s2.getLeave()){
											if(t1.getInput().equals(t2.getInput())){
												//System.out.println("SAME INPUT "+t1+ " "+t2);
												if(n.getDescendants().contains(t1.getTarget()) &&
														n.getDescendants().contains(t2.getTarget()) &&
														!leafs_in.contains(t1.getTarget()) &&
														!leafs_in.contains(t2.getTarget())){
													//before - check condition 
													
													mutlist.add(t2);								
													//found = true;									
												}
											}
										}
									}
								}
							}							
							ArrayList<CState> slit = new ArrayList<CState>();
							for(CTransition tn : mutlist){
								slit.add(tn.getTarget());					
							}
							orderComp(slit);
							if(leafs_in.contains(t1.getTarget())){
								//System.out.println("LEAVE THEN ");
								composeMTransition(ctlist, mutlist, comp, comp, comp_cond);
							}else if(re1.getDescendants().contains(t1.getTarget())){
								//System.out.println("LEAVE ELSE 1 ");
								CState cpn = composeMStates(cplist,slit,comp_cond);
								composeMTransition(ctlist, mutlist, comp, cpn, null);
							}else{
								//System.out.println("LEAVE ELSE 2 ");
								composeMTransition(ctlist, mutlist, comp, t1.getTarget(), null);
							}							
						}
						for(CTransition t1 : s1.getReach()){
							//boolean found = false;
							ArrayList<CTransition> mutlist = new ArrayList<CTransition>();
							mutlist.add(t1);
							for(CState r2 : set){
								for(CState s2 : leafs_in){
									if(!s1.equals(s2) && r2.getDescendants().contains(s2)){
										//System.out.println("TEST REACHS "+t1+ " "+s2);
										for(CTransition t2 : s2.getReach()){
											if(t1.getInput().equals(t2.getInput())){
												//System.out.println("SAME INPUT "+t1+ " "+t2);
												if(n.getDescendants().contains(t1.getSource()) &&
														n.getDescendants().contains(t2.getSource())
														//!leafs_in.contains(t1.getSource()) &&
														//!leafs_in.contains(t2.getSource())
														){
													//before - check condition 
													
													mutlist.add(t2);								
													//found = true;									
												}
											}
										}
									}
								}
							}							
							ArrayList<CState> slit = new ArrayList<CState>();
							for(CTransition tn : mutlist){
								slit.add(tn.getSource());					
							}
							orderComp(slit);
							if(leafs_in.contains(t1.getSource())){
								//System.out.println("REACH THEN ");
								composeMTransition(ctlist, mutlist, comp, comp, comp_cond);
							}else if(re1.getDescendants().contains(t1.getSource())){
								//System.out.println("REACH ELSE 1 ");
								CState cp = composeMStates(cplist,slit,comp_cond);
								composeMTransition(ctlist, mutlist, cp, comp, null);
							}else{
								//System.out.println("REACH ELSE 2 ");
								composeMTransition(ctlist, mutlist, t1.getSource(), comp, null);
							}							
						}
					}					
				}
			}
			//
		}
	}*/
		/*
		
		CState s1 = leafs_in.get(0);
		ArrayList<CState> rlist1 = (ArrayList<CState>) rmap.get(s1).clone();
		ArrayList<CState> kset2 = new ArrayList<CState>();
		kset2.addAll(rmap.keySet());
		kset2.remove(s1);
		//create new states and transitions
		for(CState s2 : kset2){			
			ArrayList<CState> rlist2 = (ArrayList<CState>) rmap.get(s2).clone();				
			ArrayList<CState> rlist11 = (ArrayList<CState>) rlist1.clone();
			System.out.println(" LIST 11 "+ rlist11);
			System.out.println(" LIST 2 "+ rlist2);
			for(CState r1 : rlist11){
				for(CState r2 : rlist2){
					CState cp = composeStates(cplist,r1,r2);
					ArrayList<CTransition> leave1 = (ArrayList<CTransition>) r1.getLeave().clone();
					ArrayList<CTransition> reach1 = (ArrayList<CTransition>) r1.getReach().clone();
					ArrayList<CTransition> leave2 = (ArrayList<CTransition>) r2.getLeave().clone();
					ArrayList<CTransition> reach2 = (ArrayList<CTransition>) r2.getReach().clone();
										
					for(CTransition l1 : leave1){
						System.out.println("IN 11 TR "+l1);
						boolean found = false;
						for(CTransition l2 : leave2){
							if(l1.getInput().equals(l2.getInput())){	
								if(rlist11.contains(l1.getTarget()) && rlist2.contains(l2.getTarget())){
									found = true;
								}
							}														
						}
						if(!found){
							System.out.println("NOT FOUND "+l1);
							if(l1.getTarget().equals(comp)){
								CTransition t = composeTransition(ctlist, l1, cp, r2);
								//System.out.println("THEN 11 "+t);							
							}else if(l1.getTarget().equals(r1)){
								CTransition t = composeTransition(ctlist, l1, cp, cp);
								//System.out.println("ELSE 11 "+t);							
							}else if(!leafs_in.contains(l1.getTarget()) && rlist11.contains(l1.getTarget())){
								CState cpn = l1.getTarget();
								if(n.getDescendants().contains(l1.getTarget())){
									cpn = composeStates(cplist,l1.getTarget(),r2);								
								}							
								CTransition t = composeTransition(ctlist, l1, cp, cpn);
								//System.out.println("ELSE 22 "+t);							
							}
						}
					}
					for(CTransition l2 : leave2){
						System.out.println("IN 12 TR "+l2);
						boolean found = false;
						for(CTransition l1 : leave1){
							if(l1.getInput().equals(l2.getInput())){
								//compose transition2								
								if(rlist11.contains(l1.getTarget()) && rlist2.contains(l2.getTarget())){
									found = true;
									System.out.println("TO SOLVE "+l1+ " AND "+l2);
									if(l1.getTarget().equals(comp) && l2.getTarget().equals(comp)){
										CTransition t = composeTransition2(ctlist, l2, l1, cp, comp);
									}else if(l1.getTarget().equals(comp)){
										CState cpn = l2.getTarget();
										CTransition t = composeTransition2(ctlist, l2, l1, cp, cpn);
									}else if(l2.getTarget().equals(comp)){
										CState cpn = l1.getTarget();
										CTransition t = composeTransition2(ctlist, l2, l1, cp, cpn);
									}else{
										CState cpn = composeStates(cplist,l1.getTarget(),l2.getTarget());
										CTransition t = composeTransition2(ctlist, l2, l1, cp, cpn);
									}
								}
							}
						}
						if(!found){
							if(l2.getTarget().equals(comp)){
								CTransition t = composeTransition(ctlist, l2, cp, r1);
								//System.out.println("THEN 33 "+t);							
							}else if(l2.getTarget().equals(r2)){
								CTransition t = composeTransition(ctlist, l2, cp, cp);
								//System.out.println("ELSE 33 "+t);							
							}else if(!leafs_in.contains(l2.getTarget()) && rlist2.contains(l2.getTarget())){
								CState cpn = l2.getTarget();							
								if(n.getDescendants().contains(l2.getTarget())){
									cpn = composeStates(cplist,r1,l2.getTarget());								
								}							
								CTransition t = composeTransition(ctlist, l2, cp, cpn);
								//System.out.println("ELSE 44 "+t);							
							}
						}
					}
					for(CTransition e1 : reach1){
						System.out.println("IN 1 TR "+e1);
						boolean found = false;
						for(CTransition l2 : leave2){
							if(l2.getInput().equals(e1.getInput())){
								if(rlist11.contains(e1.getTarget()) && rlist2.contains(l2.getTarget())){
									found = true;
								}
							}							
						}
						if(!found){
							if(e1.getSource().equals(comp)){	
								CTransition t = composeTransition(ctlist, e1, r2, cp);
								//System.out.println("THEN "+t);							
							}else if(e1.getSource().equals(r1)){							
								CTransition t = composeTransition(ctlist, e1, cp, cp);
								//System.out.println("ELSE 1 "+t);							
							}else if(!leafs_in.contains(e1.getSource()) && rlist11.contains(e1.getSource())){							
								CState cpn = composeStates(cplist,e1.getSource(),r2);
								CTransition t = composeTransition(ctlist, e1, cpn, cp);
								//System.out.println("ELSE 2 "+t);							
							}
						}						
					}
					for(CTransition e2 : reach2){
						System.out.println("IN 2 TR "+e2);
						boolean found = false;
						for(CTransition l1 : leave1){
							if(l1.getInput().equals(e2.getInput())){
								if(rlist11.contains(l1.getTarget()) && rlist2.contains(e2.getTarget())){
									found = true;
								}
							}							
						}
						if(!found){
							if(e2.getSource().equals(comp)){							
								CTransition t = composeTransition(ctlist, e2, r1, cp);
								//System.out.println("THEN 0 "+t);							
							}else if(e2.getSource().equals(r2)){							
								CTransition t = composeTransition(ctlist, e2, cp, cp);
								//System.out.println("ELSE 01 "+t);							
							}else if(!leafs_in.contains(e2.getSource()) && rlist2.contains(e2.getSource())){							
								CState cpn = composeStates(cplist,r1,e2.getSource());
								CTransition t = composeTransition(ctlist, e2, cpn, cp);
								//System.out.println("ELSE 02 "+t);							
							}
						}					
					}				
				}
			}
			for(CState c : cplist){
				if(!rlist1.contains(c)){
					rlist1.add(c);
				}
			}
		}	
		//cplist.add(comp);
	}*/
	
	private ArrayList<CState> create_composedOR(CState n, ArrayList<CState> cplist){
		//ArrayList<CState> cplist = new ArrayList<CState>();
		for(CState sub : n.getSubstates().get(0).getSubstates()){			
			if(sub.getType().equals(StateType.simple)){
				cplist.add(sub);
			}
			if(sub.getType().equals(StateType.compositeOr)){
				ArrayList<CState> list = create_composedOR(sub,cplist);
				cplist.addAll(list);
			}
			if(sub.getType().equals(StateType.compositeAnd)){
				ArrayList<CState> list = create_composedAND(sub,cplist,null);
				cplist.addAll(list);
			}
		}		
		return cplist;
	}
	
	private ArrayList<CState> create_composedAND(CState n, ArrayList<CState> cplist, CState init){
		leafs_in = new ArrayList<CState>();								
		get_leafs_in(n);
		ArrayList<CState> leafs_in = (ArrayList<CState>) this.leafs_in.clone();
		String label = "";
		String hcond = "(or ";
		String id = "";
		for(CState s : leafs_in){
			label = label.concat(s.getLabel()+"*");
			hcond = hcond.concat(s.getFCondition()+" ");
			id = id.concat(s.getID()+" ");
		}
		label = label.substring(0,label.length()-1);
		hcond = hcond.substring(0,hcond.length()-1);
		id = id.substring(0,id.length()-1);
		hcond = hcond.concat(")");		
		CState comp = null;
		if(init != null){
			if(init.getLabel().equals(label) && 
					init.getFCondition().equals(hcond) &&
					init.getID().equals(id) ){
				comp = init;				
			}
		}	
		if(comp == null){
			comp = new CState(label, hcond, StateType.simple, true, id);
		}
		System.out.println("NEW STATE AND "+comp+" "+comp.getFCondition());
		
		//ArrayList<CState> cplist = new ArrayList<CState>();
		ArrayList<CTransition> ctlist = new ArrayList<CTransition>();
		
		//get region substates
		CState leaf = null;
		HashMap<CState,ArrayList<CState>> rmap = new HashMap<CState,ArrayList<CState>>();
		System.out.println("SUB "+n.getSubstates());
		System.out.println("LIST L "+leafs_in);
		for(CState region : n.getSubstates()){
			ArrayList<CState> rlist = new ArrayList<CState>();	
			System.out.println("LIST R "+region.getSubstates());
			for(CState s : region.getSubstates()){			
				if(leafs_in.contains(s)){
					leaf = s;
				}
			}
			if(leaf == null){
				System.out.println("LEAF NULL");
				CState def = null;
				for(CState s : region.getSubstates()){
					if(s.getDefault()){
						def = s;
						break;
					}
				}
				ArrayList<CState> list = null;
				if(def.getType().equals(StateType.compositeOr)){
					list = create_composedOR(def,cplist);
					rlist.addAll(list);
				}
				if(def.getType().equals(StateType.compositeAnd)){
					list = create_composedAND(def,cplist,null);
					rlist.addAll(list);
					cplist.removeAll(list);
					//leafs_in = new ArrayList<CState>();
					//get_leafs_in(n);
					System.out.println("COMP LIST 0 "+list+" "+rlist);
				}
				for(CState d : list){
					if(d.getDefault()){
						leaf = d;
						break;
					}
				}				
				for(CState s : region.getSubstates()){
					if(!s.toString().equals(region.toString()) && !leafs_in.contains(s) && !s.equals(def)){
						if(s.getType().equals(StateType.simple)){
							rlist.add(s);
						}
						if(s.getType().equals(StateType.compositeOr)){
							ArrayList<CState> list2 = create_composedOR(s,cplist);
							rlist.addAll(list2);
						}
						if(s.getType().equals(StateType.compositeAnd)){
							ArrayList<CState> list2 = create_composedAND(s,cplist,null);
							rlist.addAll(list2);
							cplist.removeAll(list2);
							//leafs_in = new ArrayList<CState>();
							//get_leafs_in(n);
							System.out.println("COMP LIST 1 "+list+" "+rlist);
						}
					}				
				}
				rlist.remove(leaf);				
				rmap.put(leaf, rlist);
				ArrayList<CState> desc = region.getDescendants();
				desc.add(leaf);
				desc.addAll(rlist);
				region.setDescendants(desc);
				ArrayList<CState> new_leafs = (ArrayList<CState>) leafs_in.clone();
				for(CState s : def.getDescendants()){			
					if(leafs_in.contains(s)){
						//System.out.println("RMAP 1 "+s+" "+rlist);
						//rmap.put(s, rlist);
						new_leafs.remove(s);
					}
				}
				leafs_in = new_leafs;
				leafs_in.add(leaf);							
				System.out.println("RMAP 1 "+leaf+" "+rlist);
			}else{
				System.out.println("LEAF NOT NULL "+leaf);
				for(CState s : region.getSubstates()){
					if(!s.toString().equals(region.toString()) && !leafs_in.contains(s)){
						if(s.getType().equals(StateType.simple)){
							rlist.add(s);
						}
						if(s.getType().equals(StateType.compositeOr)){
							ArrayList<CState> list = create_composedOR(s,cplist);
							rlist.addAll(list);
						}
						if(s.getType().equals(StateType.compositeAnd)){
							ArrayList<CState> list = create_composedAND(s,cplist,null);
							rlist.addAll(list);
							cplist.removeAll(list);
							//leafs_in = new ArrayList<CState>();
							//get_leafs_in(n);
							System.out.println("COMP LIST 2 "+list+" "+rlist);
						}
					}				
				}
				rmap.put(leaf, rlist);
				ArrayList<CState> desc = region.getDescendants();				
				desc.addAll(rlist);
				region.setDescendants(desc);
				System.out.println("RMAP 2 "+leaf+" "+rlist);
			}
		}
		//set region condition as leaf cond
		for(CState region : n.getSubstates()){
			for(CState s : leafs_in){
				if(region.getDescendants().contains(s)){
					region.setFCondition(s.getFCondition());
				}
			}
		}
		int combinations = ((int) Math.pow(2.0D, n.getSubstates().size()));
		ArrayList<ArrayList<CState>> rcomposed = find_combinations(1,combinations,n.getSubstates());
			
		this.leafs_in = leafs_in;
		
		//new composition ......
		setNewComposition(n,comp,cplist,ctlist,rmap,rcomposed);
		
		//leafs_in = new ArrayList<CState>();
		//get_leafs_in(n);
		//change transition target to the unique composed state	
		
		//setInitialComposition(n,comp,cplist,ctlist,rmap,rcomposed);
					
		//set composition on the rest for multiplication
		
		//setComposition(n,comp,cplist,ctlist,rmap,rcomposed);	
			
		cplist.add(comp);
		return cplist;
	}
	
	public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
	    Set<Set<T>> sets = new HashSet<Set<T>>();
	    if (originalSet.isEmpty()) {
	    	sets.add(new HashSet<T>());
	    	return sets;
	    }
	    List<T> list = new ArrayList<T>(originalSet);
	    T head = list.get(0);
	    Set<T> rest = new HashSet<T>(list.subList(1, list.size())); 
	    for (Set<T> set : powerSet(rest)) {
	    	Set<T> newSet = new HashSet<T>();
	    	newSet.add(head);
	    	newSet.addAll(set);
	    	sets.add(newSet);
	    	sets.add(set);
	    }	   
	    return sets;
	}
	
	public ArrayList<ArrayList<CState>> find_combinations(int size, int max, ArrayList<CState> regions){	
		
		ArrayList<ArrayList<CState>> inchecklist = new ArrayList<ArrayList<CState>>();		
		Set<CState> p_set = new HashSet<CState>();
		p_set.addAll(regions);	
		for(int i=size; i<=max; i++){
			for (Set<CState> set : powerSet(p_set)) {
				if(set.size() == i){
					ArrayList<CState> arr = new ArrayList<CState>();
					for(CState s : set){
						arr.add(s);
					}
					inchecklist.add(arr);
				}
			}
		}		
		return inchecklist;
	}
	
	public boolean checkZ3Equiv(String cond1, String cond2){
		try {			
			if(pp.check_equiv_cond(header, cond1, cond2, project_path, inner_path)){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}	
	
	public boolean checkZ3Cond(String cond){
		try {
			if(pp.check_condition(header, cond, project_path, inner_path)){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public String checkZ3(){
		String out = "";
		for(CTransition t : done){
			String cond = "(and "+t.getSource().getFCondition()+
					" "+t.getFCondition()+" "+
					t.getTarget().getFCondition()+")";
			try {
				if(pp.check_condition(header, cond, project_path, inner_path)){
					out = out.concat(t+"\n");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		return out;		
	}
	
	public ArrayList<CTransition> generate_FFSM_interm(FFSMProperties pp,
			String header, String project_path, String inner_path){
		
		this.pp = pp;
		this.header = header;
		this.project_path = project_path;
		this.inner_path = inner_path;
		
		try {			
			//CState init = null;	
			CState root = hsm.getStruct().getRoot();
			ArrayList<CState> states = hsm.getStruct().getStateSet();
			ArrayList<CState> newlist = new ArrayList<CState>();
			removed = new ArrayList<CTransition>();
			
			leafs_in = new ArrayList<CState>();								
			get_leafs_in(root);
			
			String label = "";
			String hhcond = "";
			String id = "";
			for(CState s : leafs_in){
				label = label.concat(s.getLabel()+"*");
				hhcond = hhcond.concat(s.getFCondition()+" ");
				id = id.concat(s.getID()+" ");
			}
			label = label.substring(0,label.length()-1);
			hhcond = hhcond.substring(0,hhcond.length()-1);
			id = id.substring(0,id.length()-1);
			String hcond = "";
			CState init = null;
			if(leafs_in.size() > 1){
				hcond = "(or "+hhcond+")";
				init = new CState(label, hcond, StateType.simple, true, id);
				System.out.println("NEW STATE INIT "+init+" "+init.getFCondition());
				newlist.add(init);
			}else{
				hcond = hhcond;
				newlist.add(leafs_in.get(0));
			}
			
			ArrayList<CState> cplist = new ArrayList<CState>();
			//cplist.add(init);
										
			//need to compose AND states		
			System.out.println("LEAF "+leafs_in);
			g:for(CState st : states){
				if(!leafs_in.contains(st) && st.getType().equals(StateType.simple)){
					if(!newlist.contains(st)){
						newlist.add(st);
					}
				}
				/*if(st.getType().equals(StateType.compositeOr)){
					System.out.println("COMP 11 "+st);
					for(CState a : st.getAncestors()){
						if(!st.equals(a) && a.getType().equals(StateType.compositeAnd)){
							continue g;
						}
					}
					System.out.println("COMP 22 "+st);					
					ArrayList<CState> clist = create_composedOR(st,cplist);					
					System.out.println("LIST 22\n"+clist);
					for(CState s : clist){
						if(!newlist.contains(s)){
							newlist.add(s);
						}
					}
				}*/
				if(st.getType().equals(StateType.compositeAnd)){
					System.out.println("COMP 1 "+st);
					for(CState a : st.getAncestors()){
						if(!st.equals(a) && a.getType().equals(StateType.compositeAnd)){
							continue g;
						}
					}
					System.out.println("COMP 2 "+st);					
					ArrayList<CState> clist = create_composedAND(st,cplist,init);					
					System.out.println("LIST\n"+clist);
					for(CState s : clist){
						if(!newlist.contains(s)){
							newlist.add(s);
						}
					}
				}
			}
			
			//print transitions out
			String file_log = "";
			System.out.println("NEW "+newlist);
			done = new ArrayList<CTransition>();			
			for(CState c : newlist){
				System.out.println("LEAVE "+c+" "+c.getLeave());
				ne:for(CTransition t : c.getLeave()){
					//if(leafs_in.contains(t.getSource()) || leafs_in.contains(t.getTarget())){
					//	System.out.println("AVOID 1 "+t);
					//	continue;
					//}
					for(CState st : states){
						if(st.getType().equals(StateType.compositeAnd)){
							if(st.getDescendants().contains(t.getSource()) || 
									st.getDescendants().contains(t.getTarget()) ){
								System.out.println("AVOID 2 "+t);
								continue ne;
							}
						}
					}
					//System.out.println("TR "+t);
					boolean found = false;
					for(CTransition f : done){
						if(f.toString().equals(t.toString())){
							found = true;
						}
					}
					if(!found){
						if(!removed.contains(t)){
							done.add(t);
							file_log = file_log.concat(t+"\n");
						}
					}					
				}	
			}		
			
			if(!file_log.equals(""))file_log = file_log.substring(0, file_log.length()-1);
			System.out.println("REMOVED");
			for(CTransition h: removed){
				System.out.println(h);
			}			
			System.out.println("RESULT");
			System.out.println(file_log);
			return done;
			
		} catch (Exception ex) {
			ex.printStackTrace();			
		}
		return null;
	}
	
	public String generate_FFSM_interm_old(){
		try {			
			CState init = null;			
			CState root = hsm.getStruct().getRoot();
			ArrayList<CState> states = hsm.getStruct().getStateSet();
			for(CState cs : states){
				if(cs.getDefault() && !cs.equals(root) && !cs.getType().equals(StateType.region)){
					init = cs;
					break;
				}
			}			
			states.remove(init);
			String file_log = "";
			for(CTransition t : hsm.getTransitions()){
				if(t.getSource().equals(init)){
					file_log = file_log.concat(t+"\n");
				}
			}
			for(CState c : states){
				for(CTransition t : hsm.getTransitions()){
					if(t.getSource().equals(c)){
						file_log = file_log.concat(t+"\n");
					}
				}
			}
			if(!file_log.equals(""))file_log = file_log.substring(0, file_log.length()-1);
			//System.out.println(file_log);
			return file_log;
			
		} catch (Exception ex) {
			ex.printStackTrace();			
		}
		return "";
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
	
	public static String getZ3(String ex){
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
						
			//find state tree
			root = null;
			states = new ArrayList<CState>();
			transitions = new ArrayList<CTransition>();
			
			p = new HFFSMProperties("hffsm");
			r = new BufferedReader(new StringReader(";"));
			//new FCONSTRAINT(r);
			
			findStates(nodeList, null);
			findTransitions(nodeList);
			
			hsm = new HFFSM(root, states, transitions);
			hsm.getStruct().print();
			
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
					//Element eElement = (Element) node;
					Element fElement = (Element) node.getParentNode();
					if (!fElement.getAttribute("xsi:type").equals("sgraph:Entry")) {
						if(node.getNodeName().equals("outgoingTransitions")){						
							createTransition(node, false); 
							//findTransitions(node.getChildNodes());
							continue;
						}
						if(node.getNodeName().equals("vertices")){
							//Element eElement = (Element) node;
							//System.out.println("VERTICE "+eElement.getAttribute("name"));
							createTransition(node, true); 
							findTransitions(node.getChildNodes());
							continue;
						}
					}
					findTransitions(node.getChildNodes());
				}
			}
		}
	}
	
	public static void createTransition(Node node, boolean isState) throws Exception{
		Element eElement = (Element) node;
		Element fElement = (Element) node.getParentNode();
		String cta = eElement.getAttribute("specification");
		
		String source = fElement.getAttribute("xmi:id");
		String target = eElement.getAttribute("target");
		if(cta.length() <= 0) return;
		
		//System.out.println("SPEC "+cta);
		String[] trs = cta.split("\n");
		for(int i=0; i<trs.length;i++){
			String id = eElement.getAttribute("xmi:id");
			//System.out.println("SPEC IT "+trs[i]+" "+trs.length);
			String ct = trs[i];
			//check if it connect two relevant states		
			CState a = null;
			CState b = null;
			if(isState){
				for(CState s : states){
					System.out.println("ST "+s.getID()+ " "+id);
					if(s.getID().equals(id)){
						a = s;
						b = s;
					}
				}				
			}else{
				boolean f1 = false;
				boolean f2 = false;
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
				if(!f1 || !f2 || ct.length() <= 0 || ct.indexOf("/") <= 0) return;
			}
			if(a == null || b == null){
				System.out.println("ERROR "+source+" "+ct+" "+target);
			}
			//if(ct.length() <= 0 || ct.indexOf("/") <= 0) return;
			
			String tr = ct;
			
			tr = tr.trim();
			String cond = "true";
			String input = tr.substring(0,tr.indexOf("/"));
			String output = tr.substring(tr.indexOf("/")+1,tr.length());
			output = output.trim();
			if(tr.indexOf("[") > 0){
				cond = tr.substring(tr.indexOf("[")+1, tr.lastIndexOf("]"));
				input = tr.substring(0,tr.indexOf("["));				
				
				cond = cond.trim();
				if(cond.length() <= 0){
					cond = "true";
				}else{
					FCONSTRAINT.ReInit(r);
					String ex = cond;
					ex = ex.concat(";");
					String z3 = "";
					z3 = getZ3(ex);			
					cond = z3;
				}
			}
			String[] inputs = input.split(",");
			
			//find set of leafs of composite states 
			leafs_in = new ArrayList<CState>();
			leafs_out = new ArrayList<CState>();
			if(b.getType().equals(StateType.compositeOr) || b.getType().equals(StateType.compositeAnd)){
				get_leafs_in(b);			
			}
			if(a.getType().equals(StateType.compositeOr) || a.getType().equals(StateType.compositeAnd)){
				get_leafs_out(a);			
			}
			
			if(isState){
				id = eElement.getAttribute("xmi:id")+":"+ct;
			}
			
			if(leafs_in.size() > 0 && leafs_out.size() > 0){
				for(CState outs : leafs_out){
					for(CState ins : leafs_in){
						for(String in : inputs){
							in = in.trim();
							CTransition t = new CTransition(in, new ArrayList<CState>(), cond, output, outs, ins, id);
							transitions.add(t);
						}
					}
				}
			}else if(leafs_in.size() > 0 && leafs_out.size() <= 0){
				for(CState ins : leafs_in){
					for(String in : inputs){
						in = in.trim();
						CTransition t = new CTransition(in, new ArrayList<CState>(), cond, output, a, ins, id);
						transitions.add(t);
					}
				}
			}else if(leafs_in.size() <= 0 && leafs_out.size() > 0){
				for(CState outs : leafs_out){				
					for(String in : inputs){
						in = in.trim();
						CTransition t = new CTransition(in, new ArrayList<CState>(), cond, output, outs, b, id);
						transitions.add(t);
					}				
				}
			}else{
				for(String in : inputs){
					in = in.trim();
					CTransition t = new CTransition(in, new ArrayList<CState>(), cond, output, a, b, id);
					transitions.add(t);
				}
			}	
		}
	}
	
	private static void get_leafs_out(CState n){
		if(n.getType().equals(StateType.simple)){
			leafs_out.add(n);
		}
		for(CState n1 : n.getSubstates()){			
			get_leafs_out(n1);
		}
	}
	
	private static void get_leafs_in(CState n){		
		if(n.getType().equals(StateType.simple) && n.getDefault()){
			leafs_in.add(n);
		}
		for(CState n1 : n.getSubstates()){
			if(n1.getDefault()){
				get_leafs_in(n1);
			}
		}
	}
	
	public static void createTransition_old(Node node) throws Exception{
		Element eElement = (Element) node;
		Element fElement = (Element) node.getParentNode();
		String ct = eElement.getAttribute("specification");
		String id = eElement.getAttribute("xmi:id");
		String source = fElement.getAttribute("xmi:id");
		String target = eElement.getAttribute("target");
		if(ct.length() <= 0) return;
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
		if(!f1 || !f2 || ct.length() <= 0 || ct.indexOf("/") <= 0) return;
		String tr = ct;
		
		tr = tr.trim();
		String cond = "true";
		String input = tr.substring(0,tr.indexOf("/"));
		String output = tr.substring(tr.indexOf("/")+1,tr.length());
		output = output.trim();
		if(tr.indexOf("[") > 0){
			cond = tr.substring(tr.indexOf("[")+1, tr.lastIndexOf("]"));
			input = tr.substring(0,tr.indexOf("["));				
			
			cond = cond.trim();
			if(cond.length() <= 0){
				cond = "true";
			}else{
				FCONSTRAINT.ReInit(r);
				String ex = cond;
				ex = ex.concat(";");
				String z3 = "";
				z3 = getZ3(ex);			
				cond = z3;
			}
		}
		String[] inputs = input.split(",");
		for(String in : inputs){
			in = in.trim();
			CTransition t = new CTransition(in, new ArrayList<CState>(), cond, output, a, b, id);
			transitions.add(t);
		}
	
	}
	
	private static void findStates(NodeList nodeList, CState superState) throws Exception {
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {	
					Element eElement = (Element) node;
					if(node.getNodeName().equals("regions")){
						//if (eElement.getAttribute("xmi:type").equals("uml:Region")) {
							CState st = createState(node,true,superState);
							//region states 
							if(st.getType().equals(StateType.region)){
								ArrayList<CState> sub = superState.getSubstates();
								sub.add(st);
								superState.setSubstates(sub);
								st.setSuperState(superState);
								findStates(node.getChildNodes(),st);
								continue;
							}
						//}
					}
					if(node.getNodeName().equals("vertices")){
						if (eElement.getAttribute("xsi:type").equals("sgraph:State")) {							
							CState st = createState(node,false,superState);							
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
									if (as.item(e).getNodeType() == Node.ELEMENT_NODE) {											
										if(as.item(e).getNodeName().equals("regions")){
											CState st2 = createState(as.item(e),true,superState);
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
					if(root==null && node.getNodeName().equals("sgraph:Statechart")){
						//if (eElement.getAttribute("type").equals("org.yakindu.sct.ui.editor.editor.StatechartDiagramEditor")) {
							CState rt = createRoot(node);
							findStates(node.getChildNodes(),rt);
							return;
						//}
					}
					findStates(node.getChildNodes(),superState);
				}
			}
		}
	}
	
	private static void findStates_old(NodeList nodeList, CState superState) throws Exception {
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {	
					Element eElement = (Element) node;
					if(node.getNodeName().equals("regions")){
						//if (eElement.getAttribute("xmi:type").equals("uml:Region")) {
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
						//}
					}
					if(node.getNodeName().equals("vertices")){
						if (eElement.getAttribute("xsi:type").equals("sgraph:State")) {							
							CState st = createState(node,false);							
							//simple states 
							if(st.getType().equals(StateType.simple)){
								ArrayList<CState> sub = superState.getSubstates();
								sub.add(st);
								superState.setSubstates(sub);
								st.setSuperState(superState);
							}
							//states with one region 
							/*if(st.getType().equals(StateType.compositeOr)){
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
							}*/
						}
					}	
					//root 	<packagedElement xmi:type="uml:StateMachine"					
					if(root==null && node.getNodeName().equals("sgraph:Statechart")){
						//if (eElement.getAttribute("type").equals("org.yakindu.sct.ui.editor.editor.StatechartDiagramEditor")) {
							CState rt = createRoot(node);
							findStates(node.getChildNodes(),rt);
							return;
						//}
					}
					findStates(node.getChildNodes(),superState);
				}
			}
		}
	}
	
	public static CState createState(Node node, boolean isRegion, CState superState) throws Exception{
		Element eElement = (Element) node;
		String state = eElement.getAttribute("name");
		String id = eElement.getAttribute("xmi:id");
		String cond = "true";
		String label = state;
		if(state.indexOf("[") > 0){
			cond = state.substring(state.indexOf("[")+1, state.lastIndexOf("]"));
			label = state.substring(0,state.indexOf("["));
			
			cond = cond.trim();
			if(cond.length() <= 0){
				cond = "true";
			}else{
				FCONSTRAINT.ReInit(r);
				String ex = cond;
				ex = ex.concat(";");
				String z3 = "";
				z3 = getZ3(ex);			
				cond = z3;
			}
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
		
		//get Z3 condition with hierarchy
		CState up = superState;
		String hcond = "";	
		if(!cond.equals("true")){
			if(up.getFCondition().equals("true")){
				hcond = cond;
			}else if(up.getFCondition().startsWith("(and ")){
				hcond = up.getFCondition();
				hcond = hcond.substring(0,hcond.length()-1);
				hcond = hcond.concat(" "+cond+")");
			}else{
				hcond = "(and "+up.getFCondition()+" "+cond+")";
			}
		}else{
			hcond = up.getFCondition();
		}		
		
		CState s = new CState(label, hcond, st, isdefault, id);		
		states.add(s);
		return s;
	}
	
	public static CState createState(Node node, boolean isRegion) throws Exception{
		Element eElement = (Element) node;
		String state = eElement.getAttribute("name");
		String id = eElement.getAttribute("xmi:id");
		String cond = "true";
		String label = state;
		if(state.indexOf("[") > 0){
			cond = state.substring(state.indexOf("[")+1, state.lastIndexOf("]"));
			label = state.substring(0,state.indexOf("["));
			
			cond = cond.trim();
			if(cond.length() <= 0){
				cond = "true";
			}else{
				FCONSTRAINT.ReInit(r);
				String ex = cond;
				ex = ex.concat(";");
				String z3 = "";
				z3 = getZ3(ex);			
				cond = z3;
			}
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
		//s.setSubstates(substates);
		states.add(s);
		return s;
	}
	
	public static boolean check_default(Node iNode, String id){						
		NodeList vList = doc.getElementsByTagName("outgoingTransitions");
		for (int i = 0; i < vList.getLength(); i++) {
			Node node = vList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {	
				Element fElement = (Element) node.getParentNode();
				Element eElement = (Element) node;	
				if (fElement.getAttribute("xsi:type").equals("sgraph:Entry")) {
					if(id.equals(eElement.getAttribute("target"))){
						return true;
					}					
				}
			}			
		}		
		return false;
	}
	
	public static CState createRoot(Node node) throws Exception{
		Element eElement = (Element) node;
		String state = eElement.getAttribute("name");
		String id = eElement.getAttribute("xmi:id");
		String cond = "true";
		String label = state;
		//if(state.indexOf("|") > 0){
		//	cond = state.substring(state.indexOf("|")+1, state.lastIndexOf("|"));
		//	label = state.substring(0,state.indexOf("|"));
		//}
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
			if(as.item(e).getNodeName().equals("regions")){			
				regs++;
			}
		}
		if(regs == 0) return StateType.simple;
		if(regs == 1) return StateType.compositeOr;
		if(regs > 1) return StateType.compositeAnd;
		return null;
	}
	
	public boolean isValidFile() {
		return validFile;
	}
	
	public String reduce_condition_old(String condition){		
		ArrayList<String> vars = new ArrayList<String>();
		if(condition == null || condition.length()<=4 || !condition.startsWith("(and")){
			return condition;
		}
		single_reduction(vars, condition);
		//System.out.println("VARS"+vars);
		if(vars.size() <= 0){
			return "true";
		}else if(vars.size() == 1){
			if(condition.startsWith("(not")){
				return "(not "+vars.get(0)+")";
			}
			return ""+vars.get(0); 
		}else {
			String out = "";
			if(condition.startsWith("(and")){
				out = "(and ";
			}	
			if(condition.startsWith("(or")){
				out = "(or ";
			}
			if(condition.startsWith("(not")){
				out = "(not ";
			}
			for(String v: vars){
				out = out.concat(v+" ");
			}
			out = out.substring(0, out.length()-1);
			out = out.concat(")");
			return out;
		}
	}
	public void single_reduction(ArrayList<String> vars, String condition){
		//String backup = condition;
		String red = condition.substring(4, condition.length()-1);// avoid operator
		red = red.trim();
		if(red.indexOf("(") < 0){
			ArrayList<String> vs = get_c_vars(red);
			add_set_item(vars, vs);		
			return;
		}else if(red.indexOf("(") == 0){
			//need grammar ....
		}
		while(condition.indexOf("(") != condition.lastIndexOf("(")){			
			String ext = red;
			String before = ext.substring(0, ext.indexOf("("));
			String in = ext.substring(ext.indexOf("("), ext.lastIndexOf(")")+1);
			String after = ext.substring(ext.lastIndexOf(")")+1, ext.length());
			before = before.trim();
			after = after.trim();			
			ArrayList<String> vs = get_c_vars(before);
			vs.add(in);
			add_set_item(vars, vs);			
			condition = after;
			//System.out.println("after:"+after);
		}
		if(condition.indexOf("(") < 0){
			ArrayList<String> vs = get_c_vars(condition);
			add_set_item(vars, vs);			
			return;
		}
	}
	public ArrayList<String> get_c_vars(String get){
		ArrayList<String> vars = new ArrayList<String>();
		ArrayList<String> ops = new ArrayList<String>();
		ops.add("and"); ops.add("or"); ops.add("not");
		
		String[] vs = get.split(" ");
		for(String s : vs){
			if(!ops.contains(s) && !vars.contains(s) && !s.equals("true") && !s.equals("")){
				//System.out.println("var:"+s);
				vars.add(s);
			}
		}		
		return vars;
	}
	public void add_set_item(ArrayList<String> vars, ArrayList<String> add){
		for(String s : add){
			if(!vars.contains(s)){
				vars.add(s);
			}
		}		
	}
	
}
