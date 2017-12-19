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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.yakindu.sct.ui.editor.editor.StatechartDiagramEditor;

import br.usp.icmc.feature.logic.FFSMProperties;
import br.usp.icmc.fsm.common.FileHandler;
import br.usp.icmc.reader.FFSMModelReaderYAKINDU;

/**
 * 
 * @author vanderson hf
 * 
 */
public class FFSMDeriveModelCommand extends AbstractHandler {
	
	FFSMValidationCommand val;
	FFSMProperties p;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		System.out.println("GENERATION STATE:::");
		
		IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor();
		if (activeEditor instanceof StatechartDiagramEditor) {
			Diagram diagram = ((StatechartDiagramEditor) activeEditor).getDiagram();
						
			val = new FFSMValidationCommand();
			val.setUpdate(false);
			val.execute_validation(event);
			if(!val.isvalidFFSM()) return null;
			p = val.getFFSM_Properties();		
			try {
				
				String cond = val.getDerivationOP(diagram, false);
				HashMap<String, ArrayList<String>> map = val.getDerivationMAP(diagram, false);
				System.out.println("Derivation operator: "+cond);	
				
				String project_path = ""+ResourcesPlugin.getWorkspace().getRoot().getLocationURI();
				project_path = project_path.substring(project_path.indexOf(":")+1, project_path.length());		
				String resource_path = ""+diagram.getElement().eResource().getURI();
				resource_path = resource_path.substring(resource_path.indexOf(":")+2, resource_path.length());
				resource_path = resource_path.substring(resource_path.indexOf("/"), resource_path.lastIndexOf("."));
				resource_path = resource_path.replaceAll("%20", " ");
				String fpath = project_path.concat(resource_path+".ffsm");
				File yakindu_file = new File(fpath);
				FFSMModelReaderYAKINDU reader = new FFSMModelReaderYAKINDU(yakindu_file);
				
				ArrayList<String> invalid = p.removeNodesDerive(reader.getHsm(), cond);
				reader.removeIDs(invalid, cond);				
				String suite2 = reader.printXML();
				
				//update feature model
				String FM_path = project_path.concat(resource_path+".xml");
				String fmodel = p.update_feature_model(FM_path, map, cond);
							
				post_processing(diagram, suite2, fmodel);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}		
		return null;
	}
	
	public void post_processing(Diagram diagram, String suite, String fmodel){
		
		//get project paths
		String project_path = val.getProjectPath();			
		String resource_path = val.getResourcePath(diagram);	
		String diagram_name = resource_path.substring(resource_path.lastIndexOf("/")+1, resource_path.length());
		
		//get back to root
		while(resource_path.lastIndexOf("/") > resource_path.indexOf("/")){
			resource_path = resource_path.substring(0,resource_path.lastIndexOf("/"));
		}
		resource_path = resource_path + "/derived/"+diagram_name;
		
		String file_path_states = project_path.concat(resource_path+"_derived.ffsm");
		String file_path_fm = project_path.concat(resource_path+"_derived.xml");
			
		FileHandler fh = new FileHandler();		
		try {
			fh.print_file(suite, file_path_states);
			fh.print_file(fmodel, file_path_fm);		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
}
