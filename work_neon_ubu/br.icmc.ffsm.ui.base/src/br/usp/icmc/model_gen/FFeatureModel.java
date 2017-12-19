package br.usp.icmc.model_gen;

import java.io.IOException;

import br.usp.icmc.fsm.common.FileHandler;

public class FFeatureModel {

	public void gen_FM(String folder, int amount) throws IOException{
		
		for(int k=1; k<=amount; k++){
			String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n";
			
			header = header.concat("	<featureModel chosenLayoutAlgorithm=\"4\">\n");
			header = header.concat("		<struct>\n");
			header = header.concat("			<and mandatory=\"true\" name=\"Root [f0]\">\n");
			
			String clause = "";
			for(int i=1; i<=k; i++){
				clause = clause.concat("				<feature name=\"Optional [f"+i+"]\"/>\n");			
			}
			
			String bottom = "			</and>\n";
			bottom = bottom.concat("		</struct>\n");
			bottom = bottom.concat("		<constraints/>\n");
			bottom = bottom.concat("		<calculations Auto=\"true\" Constraints=\"true\" Features=\"true\" Redundant=\"true\" Tautology=\"true\"/>\n");
			bottom = bottom.concat("		<comments/>\n");
			bottom = bottom.concat("		<featureOrder userDefined=\"false\"/>\n");
			bottom = bottom.concat("	</featureModel>\n");
			
			String prop_aux = header.concat(clause);
			prop_aux = prop_aux.concat(bottom);
			
			String path = "./"+folder+"/example"+k+".xml";
			FileHandler fh = new FileHandler();
			fh.print_file(prop_aux, path);
		}
		
	}
	
	public void gen_FM_best(String folder, int amount) throws IOException{
		
		for(int k=1; k<=amount; k++){
			String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n";
			
			header = header.concat("	<featureModel chosenLayoutAlgorithm=\"4\">\n");
			header = header.concat("		<struct>\n");
			header = header.concat("			<and mandatory=\"true\" name=\"Root [f0]\">\n");
			
			String clause = "";
			String pre = "				";
			for(int i=1; i<=k; i++){
				if(i == k){
					clause = clause.concat(pre+"<feature name=\"Optional [f"+i+"]\"/>\n");
				}else{
					clause = clause.concat(pre+"<feature name=\"Optional [f"+i+"]\">\n");
				}				
				pre = pre.concat("	");
			}
			for(int i=k; i>1; i--){
				pre = pre.substring(0,pre.length()-1);
				clause = clause.concat(pre+"</feature>\n");
			}
			
			String bottom = "			</and>\n";
			bottom = bottom.concat("		</struct>\n");
			bottom = bottom.concat("		<constraints/>\n");
			bottom = bottom.concat("		<calculations Auto=\"true\" Constraints=\"true\" Features=\"true\" Redundant=\"true\" Tautology=\"true\"/>\n");
			bottom = bottom.concat("		<comments/>\n");
			bottom = bottom.concat("		<featureOrder userDefined=\"false\"/>\n");
			bottom = bottom.concat("	</featureModel>\n");
			
			String prop_aux = header.concat(clause);
			prop_aux = prop_aux.concat(bottom);
			
			String path = "./"+folder+"/example"+k+".xml";
			FileHandler fh = new FileHandler();
			fh.print_file(prop_aux, path);
		}
		
	}
	

	public void gen_FM_mid(String folder, int amount) throws IOException{
		
		for(int k=1; k<=amount; k++){
			String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n";
			
			header = header.concat("	<featureModel chosenLayoutAlgorithm=\"4\">\n");
			header = header.concat("		<struct>\n");
			header = header.concat("			<and mandatory=\"true\" name=\"Root [f0]\">\n");
			
			String clause = "";
			String pre = "				";
			
			for(int i=1; i<=(k/2); i++){
				clause = clause.concat("				<feature name=\"Optional [f"+i+"]\"/>\n");			
			}
			
			for(int i=(k/2)+1; i<=k; i++){
				if(i == k){
					clause = clause.concat(pre+"<feature name=\"Optional [f"+i+"]\"/>\n");
				}else{
					clause = clause.concat(pre+"<feature name=\"Optional [f"+i+"]\">\n");
				}				
				pre = pre.concat("	");
			}
			for(int i=k; i>(k/2)+1; i--){
				pre = pre.substring(0,pre.length()-1);
				clause = clause.concat(pre+"</feature>\n");
			}			
			
			String bottom = "			</and>\n";
			bottom = bottom.concat("		</struct>\n");
			bottom = bottom.concat("		<constraints/>\n");
			bottom = bottom.concat("		<calculations Auto=\"true\" Constraints=\"true\" Features=\"true\" Redundant=\"true\" Tautology=\"true\"/>\n");
			bottom = bottom.concat("		<comments/>\n");
			bottom = bottom.concat("		<featureOrder userDefined=\"false\"/>\n");
			bottom = bottom.concat("	</featureModel>\n");
			
			String prop_aux = header.concat(clause);
			prop_aux = prop_aux.concat(bottom);
			
			String path = "./"+folder+"/example"+k+".xml";
			FileHandler fh = new FileHandler();
			fh.print_file(prop_aux, path);
		}
		
	}
	
	public void gen_FM_mid_low(String folder, int amount) throws IOException{
		
		for(int k=1; k<=amount; k++){
			String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n";
			
			header = header.concat("	<featureModel chosenLayoutAlgorithm=\"4\">\n");
			header = header.concat("		<struct>\n");
			header = header.concat("			<and mandatory=\"true\" name=\"Root [f0]\">\n");
			
			String clause = "";
			String pre = "				";
			
			for(int i=1; i<=(k/3); i++){
				clause = clause.concat("				<feature name=\"Optional [f"+i+"]\"/>\n");			
			}
			
			for(int i=(k/3)+1; i<=k; i++){
				if(i == k){
					clause = clause.concat(pre+"<feature name=\"Optional [f"+i+"]\"/>\n");
				}else{
					clause = clause.concat(pre+"<feature name=\"Optional [f"+i+"]\">\n");
				}				
				pre = pre.concat("	");
			}
			for(int i=k; i>(k/3)+1; i--){
				pre = pre.substring(0,pre.length()-1);
				clause = clause.concat(pre+"</feature>\n");
			}			
			
			String bottom = "			</and>\n";
			bottom = bottom.concat("		</struct>\n");
			bottom = bottom.concat("		<constraints/>\n");
			bottom = bottom.concat("		<calculations Auto=\"true\" Constraints=\"true\" Features=\"true\" Redundant=\"true\" Tautology=\"true\"/>\n");
			bottom = bottom.concat("		<comments/>\n");
			bottom = bottom.concat("		<featureOrder userDefined=\"false\"/>\n");
			bottom = bottom.concat("	</featureModel>\n");
			
			String prop_aux = header.concat(clause);
			prop_aux = prop_aux.concat(bottom);
			
			String path = "./"+folder+"/example"+k+".xml";
			FileHandler fh = new FileHandler();
			fh.print_file(prop_aux, path);
		}
		
	}
	
}
