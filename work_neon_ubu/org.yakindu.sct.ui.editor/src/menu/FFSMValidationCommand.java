/**
 * Copyright (c) 2013 committers of YAKINDU and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * 	committers of YAKINDU - initial API and implementation (http://www.statecharts.org)
 * 
 */
package menu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.EcoreUtil2;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.ui.editor.editor.StatechartDiagramEditor;

import br.usp.icmc.feature.logic.FFSMProperties;
import br.usp.icmc.ffsm.FState;
import br.usp.icmc.ffsm.FTransition;
import br.usp.icmc.fsm.common.FileHandler;
import br.usp.icmc.reader.FFSMModelReaderYAKINDU;
import parser.yakindu.FCONSTRAINT;


/**
 * 
 * @author vanderson hf
 * 
 */
public class FFSMValidationCommand extends AbstractHandler {

	public static final String ISSUE_INVALID_CONDITIONAL_STATE = "Invalid Feature Constraint:";
	
	ArrayList<FState> state_list;
	ArrayList<State> state_model_list;
	ArrayList<FTransition> transition_list;
	
	FFSMProperties p;
	boolean isvalid = false;
	boolean update = true;
	
	public FFSMProperties getFFSM_Properties(){
		return p;
	}
	
	public boolean isvalidFFSM(){
		return isvalid;
	}
	
	public void setUpdate(boolean update){
		this.update = update;
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		System.out.println("SAVE VALIDATION:::");
						
		IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor();
		if (activeEditor instanceof StatechartDiagramEditor) {
			
			Diagram diagram = ((StatechartDiagramEditor) activeEditor).getDiagram();
			diagram.eResource().setModified(true);
			try {
				if(update)diagram.eResource().save(null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}		
		return null;
	}
		
	public Object execute_validation(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		System.out.println("VALIDATION:::");
						
		IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor();
		if (activeEditor instanceof StatechartDiagramEditor) {
			
			long startTime = System.currentTimeMillis();
			
			Display d0 = PlatformUI.getWorkbench().getDisplay();
			Display d = PlatformUI.createDisplay();
			//Shell s = d.getActiveShell();
			Shell s = new Shell(d);
			s.setBounds(d0.getBounds().width/2,d0.getBounds().height/2, 300, 300);
			ProgressBar pb = ProgressBarExample(s);
			
			Diagram diagram = ((StatechartDiagramEditor) activeEditor).getDiagram();
			List<View> result = EcoreUtil2.getAllContentsOfType(diagram, View.class);					
			
			pre_processing(diagram);			
			pb.setSelection(50);
			
			// identify all invalid states	
			ArrayList<State> invalid_states = check_states(diagram, result);			
			
			// identify all invalid transitions
			ArrayList<Transition> invalid_transitions = check_transitions(diagram, result);
			
			try {				
				if(invalid_states.size() == 0 && invalid_transitions.size() == 0 && p.is_valid_FFSM()){
					isvalid = true;
				}				
			} catch (IOException | InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
			
			pb.setSelection(100);			
			s.dispose();
			
			long stopTime = System.currentTimeMillis();
		    long elapsedTime = stopTime - startTime;
		    System.out.println("Validation took (secs) "+elapsedTime/1000);
		}		
		return null;
	}
	
	private ProgressBar ProgressBarExample(Shell s) {
	    
	    s.setText("Validating Model...");

	    final ProgressBar pb = new ProgressBar(s, SWT.HORIZONTAL);
	    pb.setMinimum(0);
	    pb.setMaximum(100);
	    pb.setSelection(0);
	    //pb.setBounds(50, 50, 200, 20);
	    
	    pb.setBounds(100, 10, 200, 20);
	    
	    Label label = new Label(s, SWT.NULL);
	    label.setText("Checking Properties");
	    
	    label.setAlignment(SWT.RIGHT);
	    label.setBounds(10, 10, 80, 20);
	   
	    s.pack();
	    s.open();
	    
	    return pb;
	  }
	
	public ArrayList<Transition> check_transitions(Diagram diagram, List<View> result){	
		ArrayList<Transition> invalid = new ArrayList<Transition>();
		for(FTransition f : transition_list){		
			String fname = get_FTransition_name(f);
			
			for(View view : result){				
				if(view.getType().equals("Transition")){					
					Transition t = (Transition) view.getElement();
					String sname = convert_Transition2FTransition(t);
					
					if(sname.equals(fname) || sname.equals("")){
						invalid.add(t);						
					}					
				}				
			}
		}
		return invalid;
	}
	
	public String get_FTransition_name(FTransition f){
		String source = f.getSource().getStateName().trim()+"["+f.getSource().getCondition()+"]";
		String target = f.getTarget().getStateName().trim()+"["+f.getTarget().getCondition()+"]";
		String tran = f.getCInput().getIn()+"["+f.getCInput().getCond().trim()+"]/"+f.getOutput();
		String fname = source+"--"+tran+"->"+target;				
		
		return fname;
	}
	
	public String convert_Transition2FTransition(Transition t){
		State _source = null;
		State _target = null;
		for(State s: state_model_list){
			if(t.getSource().getName().equals(s.getName())){
				_source = s;
			}
			if(t.getTarget().getName().equals(s.getName())){
				_target = s;
			}
		}
		if(_source == null || _target == null){
			return "";
		}
	
		String source = convert_State2FState(_source);
		String target = convert_State2FState(_target);
		
		String spec = t.getSpecification();
		String[] parts = spec.split("/");
		if(parts.length != 2){
			return "";
		}
		String sname = parts[0].trim();
		String output = parts[1].trim();
		
		String cond = "true";						
		String label = sname;		
		if(sname.indexOf("[") > 0){
			cond = sname.substring(sname.indexOf("[")+1, sname.lastIndexOf("]"));
			label = sname.substring(0,sname.indexOf("[")).trim();
							
			cond = get_Z3_condition(cond);
			
			sname = label+"["+cond+"]/"+output;
		}else{
			sname = label+"[true]/"+output;
		}
		
		String tran = sname;
		String fname = source+"--"+tran+"->"+target;
		return fname;
	}
	
	public String convert_State2FState(State state){		
		String sname = state.getName().trim();
		String cond = "true";						
		String label = sname;		
		if(sname.indexOf("[") > 0){
			cond = sname.substring(sname.indexOf("[")+1, sname.lastIndexOf("]"));
			label = sname.substring(0,sname.indexOf("[")).trim();
						
			cond = get_Z3_condition(cond);
			
			sname = label+"["+cond+"]";
		}else{
			sname = label+"[true]";
		}
		return sname;
	}
	
	public String get_Z3_condition(String cond){
		String ex = cond;
		ex = ex.concat(";");
		String z3 = "";
		z3 = FFSMModelReaderYAKINDU.getZ3(ex);
		return z3;
	}
	
	public ArrayList<State> check_states(Diagram diagram, List<View> result){
		ArrayList<State> invalid = new ArrayList<State>();
		state_model_list = new ArrayList<State>(); 
		for(FState f : state_list){		
			String fname = f.getStateName()+"["+f.getCondition()+"]";
			for(View view : result){				
				if(view.getType().equals("State")){					
					State state = (State) view.getElement();
					//if(update)state.eResource().setModified(true);					
					state_model_list.add(state);
					String sname = convert_State2FState(state);
					if(sname.equals(fname)){
						invalid.add(state);						
					}
				}				
			}
		}
		return invalid;
	}	
	
	public String getProjectPath(){
		String project_path = ""+ResourcesPlugin.getWorkspace().getRoot().getLocationURI();
		project_path = project_path.substring(project_path.indexOf(":")+1, project_path.length());	
		return project_path;
	}
	
	public String getResourcePath(Diagram diagram){
		String resource_path = ""+diagram.getElement().eResource().getURI();
		resource_path = resource_path.substring(resource_path.indexOf(":")+2, resource_path.length());
		resource_path = resource_path.substring(resource_path.indexOf("/"), resource_path.lastIndexOf("."));
		resource_path = resource_path.replaceAll("%20", " ");
		return resource_path;
	}
	
	public void post_processing(Diagram diagram, ArrayList<State> states, ArrayList<Transition> transitions){
			
		//get project paths
		String project_path = getProjectPath();			
		String resource_path = getResourcePath(diagram);	
		String diagram_name = resource_path.substring(resource_path.lastIndexOf("/")+1, resource_path.length());
		
		//get back to root
		while(resource_path.lastIndexOf("/") > resource_path.indexOf("/")){
			resource_path = resource_path.substring(0,resource_path.lastIndexOf("/"));
		}
		resource_path = resource_path + "/logs/"+diagram_name;
		
		String file_path_states = project_path.concat(resource_path+"_invalid_states.txt");
		String file_path_transitions = project_path.concat(resource_path+"_invalid_transitions.txt");	
		
		String state_model = "";
		for(State s : states){
			state_model = state_model.concat(s.getName()+"\n");
		}
		
		String transition_model = "";
		for(Transition t : transitions){
			String tr = t.getSource().getName()+"--"+t.getSpecification()+"->"+t.getTarget().getName();
			transition_model = transition_model.concat(tr+"\n");
		}
		
		FileHandler fh = new FileHandler();		
		try {
			fh.print_file(state_model, file_path_states);
			fh.print_file(transition_model, file_path_transitions);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public String getDerivationOP(Diagram diagram, boolean isFSM){		
		//get project paths
		String project_path = ""+ResourcesPlugin.getWorkspace().getRoot().getLocationURI();
		project_path = project_path.substring(project_path.indexOf(":")+1, project_path.length());		
		String resource_path = ""+diagram.getElement().eResource().getURI();
		resource_path = resource_path.substring(resource_path.indexOf(":")+2, resource_path.length());
		resource_path = resource_path.substring(resource_path.indexOf("/"), resource_path.lastIndexOf("."));
		resource_path = resource_path.replaceAll("%20", " ");
		while(resource_path.lastIndexOf("/") > resource_path.indexOf("/")){
			resource_path = resource_path.substring(0,resource_path.lastIndexOf("/"));
		}
		resource_path = resource_path + "/configs/default";
		String CONF_path = project_path.concat(resource_path+".config");
		
		ArrayList<String> ids = new ArrayList<String>();
		String cond = "(and ";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(CONF_path));
			String line = "";
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				String feature_id = "";
				String aux = line;
				aux.trim();
				aux = aux.replace(" ", "");
				aux.replaceAll("[^A-Za-z]+", "");
				if(aux.lastIndexOf("_") <= 0){
					feature_id = aux;
				}else{
					feature_id = aux.substring(aux.lastIndexOf("_")+1,aux.length());
					if(feature_id.length() <= 0){
						feature_id = aux;
					}
				}
				ids.add(feature_id);
				cond = cond.concat(feature_id+" ");
			}
			reader.close();
		} catch (Exception e) {				
			e.printStackTrace();
		}
		if(isFSM){
			for(String s : p.getFeatures()){
				if(!ids.contains(s)){
					cond = cond.concat("(not "+s+") ");
				}
			}
		}		
		cond = cond.concat(")");
		
		return cond;
	}
	
	public HashMap<String, ArrayList<String>> getDerivationMAP(Diagram diagram, boolean isFSM){		
		//get project paths
		String project_path = ""+ResourcesPlugin.getWorkspace().getRoot().getLocationURI();
		project_path = project_path.substring(project_path.indexOf(":")+1, project_path.length());		
		String resource_path = ""+diagram.getElement().eResource().getURI();
		resource_path = resource_path.substring(resource_path.indexOf(":")+2, resource_path.length());
		resource_path = resource_path.substring(resource_path.indexOf("/"), resource_path.lastIndexOf("."));
		resource_path = resource_path.replaceAll("%20", " ");
		while(resource_path.lastIndexOf("/") > resource_path.indexOf("/")){
			resource_path = resource_path.substring(0,resource_path.lastIndexOf("/"));
		}
		resource_path = resource_path + "/configs/default";
		String CONF_path = project_path.concat(resource_path+".config");
		
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		
		ArrayList<String> ids = new ArrayList<String>();
		ArrayList<String> nids = new ArrayList<String>();
		String cond = "(and ";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(CONF_path));
			String line = "";
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				String feature_id = "";
				String aux = line;
				aux.trim();
				aux = aux.replace(" ", "");
				aux.replaceAll("[^A-Za-z]+", "");
				if(aux.lastIndexOf("_") <= 0){
					feature_id = aux;
				}else{
					feature_id = aux.substring(aux.lastIndexOf("_")+1,aux.length());
					if(feature_id.length() <= 0){
						feature_id = aux;
					}
				}
				ids.add(feature_id);
				cond = cond.concat(feature_id+" ");
			}
			reader.close();
		} catch (Exception e) {				
			e.printStackTrace();
		}
		if(isFSM){
			for(String s : p.getFeatures()){
				if(!ids.contains(s)){
					cond = cond.concat("(not "+s+") ");
					nids.add(s);
				}
			}
		}		
		cond = cond.concat(")");
		
		map.put("in", ids);
		map.put("out", nids);
		
		return map;
	}
	
	public void pre_processing(Diagram diagram){
	
		boolean islog = false;
		boolean debug = true;
		//get project paths
		String project_path = ""+ResourcesPlugin.getWorkspace().getRoot().getLocationURI();
		project_path = project_path.substring(project_path.indexOf(":")+1, project_path.length());		
		String resource_path = ""+diagram.getElement().eResource().getURI();
		resource_path = resource_path.substring(resource_path.indexOf(":")+2, resource_path.length());
		resource_path = resource_path.substring(resource_path.indexOf("/"), resource_path.lastIndexOf("."));
		resource_path = resource_path.replaceAll("%20", " ");		
		String FM_path = project_path.concat(resource_path+".xml");
		String folder = FM_path.substring(0,FM_path.lastIndexOf("/"));
		
		//read feature model file
		p = new FFSMProperties(folder, islog, debug);
		String header = p.read_XML_FeatureModel(FM_path);
		//System.out.println(header);
		
		String file_path = project_path.concat(resource_path+".txt");				
		String fpath = project_path.concat(resource_path+".ffsm");
		File yakindu_file = new File(fpath);
		
		//start condition expression parser (specially for states and so on)
		Reader r = new BufferedReader(new StringReader(";"));
		try{
			FCONSTRAINT.ReInit(r);
		} catch (Exception e) {
			new FCONSTRAINT(r);
		}
	
		// generate intermediate FFSM
		FFSMModelReaderYAKINDU reader = new FFSMModelReaderYAKINDU(yakindu_file);
		//String model = reader.generate_FFSM_interm();
		//reader.generate_FFSM_interm();
		
		String diagram_name = resource_path.substring(resource_path.lastIndexOf("/")+1, resource_path.length());
		//get back to root
		while(resource_path.lastIndexOf("/") > resource_path.indexOf("/")){
			resource_path = resource_path.substring(0,resource_path.lastIndexOf("/"));
		}
		resource_path = resource_path + "/z3/"+diagram_name;
		
		reader.generate_FFSM_interm(p, header, project_path, resource_path);
		FileHandler fh = new FileHandler();		
		try {
			//fh.print_file(model, file_path);
			//System.out.println(model);
			//check states and transitions
			//System.out.println(""+resource_path);
			
			//System.out.println(file_path+"");
			//System.out.println(""+project_path);		
			//System.out.println(""+resource_path);
			
			//String model = reader.setZ3(p, header, project_path, resource_path);
			String model = reader.checkZ3();
			fh.print_file(model, file_path);
			
			p.setFFSM(file_path, header, project_path, resource_path);			
			state_list = p.checkFFSM_states();
			transition_list = p.checkFFSM_transitions();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
}
