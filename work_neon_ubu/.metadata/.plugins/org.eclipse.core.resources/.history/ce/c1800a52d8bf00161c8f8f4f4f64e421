package br.usp.icmc.model_gen;

import java.io.IOException;

import br.usp.icmc.fsm.common.FileHandler;
import br.usp.icmc.fsm.common.Node;

public class FsmModel {
	
	String clause;

	public void gen_FSM(String folder, int amount) throws IOException{
		
		for(int k=1; k<=amount; k++){
			String header = "s0 -- a / 0 -> s0\n";
					
			String clause = "";
			for(int i=1; i<=k; i++){
				clause = clause.concat("s0 -- a"+i+" / 0 -> s"+i+"\n");			
			}
			String prop_aux = header.concat(clause);
			
			clause = "";
			for(int i=1; i<=k; i++){
				clause = clause.concat("s"+i+" -- a / 1 -> s0\n");			
			}			
			prop_aux = prop_aux.concat(clause);
			
			clause = "";
			for(int i=1; i<=k; i++){
				clause = clause.concat("s"+i+" -- b / o"+i+" -> s"+i+"\n");			
			}			
			prop_aux = prop_aux.concat(clause);
			
			
			String path = "./"+folder+"/fsm"+k+".txt";
			FileHandler fh = new FileHandler();
			fh.print_file(prop_aux, path);
		}
		
	}
	
	public void gen_FSM_best(String folder, int amount) throws IOException{
		
		for(int k=1; k<=amount; k++){
			String header = "s0 -- a / 0 -> s0\n";
					
			String clause = "";
			for(int i=1; i<=k; i++){
				clause = clause.concat("s"+(i-1)+" -- a"+i+" / 0 -> s"+i+"\n");			
			}
			String prop_aux = header.concat(clause);
			
			clause = "";
			for(int i=1; i<=k; i++){
				clause = clause.concat("s"+i+" -- a / 1 -> s"+(i-1)+"\n");			
			}			
			prop_aux = prop_aux.concat(clause);
			
			clause = "";
			for(int i=1; i<=k; i++){
				clause = clause.concat("s"+i+" -- b / o"+i+" -> s"+i+"\n");			
			}			
			prop_aux = prop_aux.concat(clause);
			
			
			String path = "./"+folder+"/fsm"+k+".txt";
			FileHandler fh = new FileHandler();
			fh.print_file(prop_aux, path);
		}
		
	}
	
	public void gen_FSM_mid(String folder, int amount) throws IOException{
		
		for(int k=1; k<=amount; k++){				
			String header = "s0 -- a / 0 -> s0\n";
					
			String clause = "";
			for(int i=1; i<=(k/2)+1; i++){
				clause = clause.concat("s0 -- a"+i+" / 0 -> s"+i+"\n");			
			}
			String prop_aux = header.concat(clause);
			
			clause = "";
			for(int i=1; i<=(k/2)+1; i++){
				clause = clause.concat("s"+i+" -- a / 1 -> s0\n");			
			}			
			prop_aux = prop_aux.concat(clause);
			
			clause = "";
			for(int i=1; i<=(k/2)+1; i++){
				clause = clause.concat("s"+i+" -- b / o"+i+" -> s"+i+"\n");			
			}			
			prop_aux = prop_aux.concat(clause);
			
			
						
			clause = "";
			for(int i=(k/2)+2; i<=k; i++){
				clause = clause.concat("s"+(i-1)+" -- a"+i+" / 0 -> s"+i+"\n");			
			}
			prop_aux = prop_aux.concat(clause);
			
			clause = "";
			for(int i=(k/2)+2; i<=k; i++){
				clause = clause.concat("s"+i+" -- a / 1 -> s"+(i-1)+"\n");			
			}			
			prop_aux = prop_aux.concat(clause);
			
			clause = "";
			for(int i=(k/2)+2; i<=k; i++){
				clause = clause.concat("s"+i+" -- b / o"+i+" -> s"+i+"\n");			
			}			
			prop_aux = prop_aux.concat(clause);
			
			
			String path = "./"+folder+"/fsm"+k+".txt";
			FileHandler fh = new FileHandler();
			fh.print_file(prop_aux, path);
		}
		
	}
	
	public void gen_FSM_mid_low(String folder, int amount) throws IOException{
		
		for(int k=1; k<=amount; k++){				
			String header = "s0 -- a / 0 -> s0\n";
					
			String clause = "";
			for(int i=1; i<=(k/3)+1; i++){
				clause = clause.concat("s0 -- a"+i+" / 0 -> s"+i+"\n");			
			}
			String prop_aux = header.concat(clause);
			
			clause = "";
			for(int i=1; i<=(k/3)+1; i++){
				clause = clause.concat("s"+i+" -- a / 1 -> s0\n");			
			}			
			prop_aux = prop_aux.concat(clause);
			
			clause = "";
			for(int i=1; i<=(k/3)+1; i++){
				clause = clause.concat("s"+i+" -- b / o"+i+" -> s"+i+"\n");			
			}			
			prop_aux = prop_aux.concat(clause);
			
			
						
			clause = "";
			for(int i=(k/3)+2; i<=k; i++){
				clause = clause.concat("s"+(i-1)+" -- a"+i+" / 0 -> s"+i+"\n");			
			}
			prop_aux = prop_aux.concat(clause);
			
			clause = "";
			for(int i=(k/3)+2; i<=k; i++){
				clause = clause.concat("s"+i+" -- a / 1 -> s"+(i-1)+"\n");			
			}			
			prop_aux = prop_aux.concat(clause);
			
			clause = "";
			for(int i=(k/3)+2; i<=k; i++){
				clause = clause.concat("s"+i+" -- b / o"+i+" -> s"+i+"\n");			
			}			
			prop_aux = prop_aux.concat(clause);
			
			
			String path = "./"+folder+"/fsm"+k+".txt";
			FileHandler fh = new FileHandler();
			fh.print_file(prop_aux, path);
		}
		
	}
	
	public void rec_genFSM(Node n){		
		for(Node n1 : n.getChildren()){	
			String state1 = n.getState().getLabel();
			String state2 = n1.getState().getLabel();
			String input = state2;
			String output = state2;
			
			clause = clause.concat("s"+state1+" -- a"+input+" / 0 -> s"+state2+"\n");
			clause = clause.concat("s"+state2+" -- a / 1 -> s"+state1+"\n");	
			clause = clause.concat("s"+state2+" -- b / o"+output+" -> s"+state2+"\n");
			rec_genFSM(n1);			
		}
	}
	
	public void gen_FSM_tree(Node root, String path_ffsm) throws IOException {
		
		String header = "s0 -- a / 0 -> s0\n";		
		clause = "";
		rec_genFSM(root);
		
		String prop_aux = header.concat(clause);			
		
		FileHandler fh = new FileHandler();
		fh.print_file(prop_aux, path_ffsm);
	}
	
}
