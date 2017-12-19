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

import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.yakindu.sct.ui.editor.editor.StatechartDiagramEditor;

import br.usp.icmc.feature.logic.FFSMProperties;
import br.usp.icmc.ffsm.Cond_in_seq;
import br.usp.icmc.fsm.common.FileHandler;

/**
 * 
 * @author vanderson hf
 * 
 */
public class FFSMDeriveStateCommand extends AbstractHandler {
	
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
				p.genStateSuite();
				ArrayList<Cond_in_seq> suite = p.getStateSet();
				String cond = val.getDerivationOP(diagram,true);
				System.out.println("Derivation operator: "+cond);	
				String suite2 = p.derive_suite(suite, cond);
				
				//String suite =  p.getStateSuite();
				post_processing(diagram, suite2);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}		
		return null;
	}
	
	public void post_processing(Diagram diagram, String suite){
		
		//get project paths
		String project_path = val.getProjectPath();			
		String resource_path = val.getResourcePath(diagram);	
		String diagram_name = resource_path.substring(resource_path.lastIndexOf("/")+1, resource_path.length());
		
		//get back to root
		while(resource_path.lastIndexOf("/") > resource_path.indexOf("/")){
			resource_path = resource_path.substring(0,resource_path.lastIndexOf("/"));
		}
		resource_path = resource_path + "/derived/"+diagram_name;
		
		String file_path_states = project_path.concat(resource_path+"_states.txt");
			
		FileHandler fh = new FileHandler();		
		try {
			fh.print_file(suite, file_path_states);			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
}
