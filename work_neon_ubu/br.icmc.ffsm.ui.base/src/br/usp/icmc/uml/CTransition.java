package br.usp.icmc.uml;

import java.util.ArrayList;

public class CTransition {
	private String in, cond, out, id;
	private CState source, target, scope;
	private ArrayList<CState> guard;
	
	public CTransition(String input, ArrayList<CState> guard, String cond, String output, CState source, CState target, String id) {
		this.source = source;
		this.source.addLeaveTransition(this);		
		this.in = input;
		this.guard = guard;
		this.cond = cond;
		this.out = output;
		this.target = target;
		this.target.addReachTransition(this);
		this.id = id;
	}
	
	@Override
	public String toString(){
		/*String op_guard = "";
		String op_cond = "";
		if(!guard.equals("") && guard != null){
			op_guard = guard+"";
		}
		if(!cond.equals("") && cond != null){
			op_cond = "("+cond+")";
		}	*/	
		return source + "@" +source.getFCondition()+ " -- " 
			+ in + "@" + cond + "/" + out + " -> " + target + "@"+target.getFCondition();
		//return source + "--" + in + op_guard + op_cond + "/" + out + "->" + target;
	}
	
	public CState getSource() {
		return source;
	}
	
	public CState getScope() {
		return scope;
	}
	
	public void setScope(CState scope) {
		this.scope = scope;
	}
	
	public String getID() {
		return id;
	}
	
	public ArrayList<CState> getGuard() {
		return guard;
	}
	
	public String getFCondition() {
		return cond;
	}
	
	public CState getTarget() {
		return target;
	}
	
	public String getInput() {
		return in;
	}
	
	public String getOutput() {
		return out;
	}
	
	public void setIn(CState source) {
		this.source = source;		
	}
	
	public void setOut(CState target) {
		this.target = target;
	}
	
	public void setOutput(String out) {
		this.out = out;
	}
	
	public void setGuard(ArrayList<CState> guard) {
		this.guard = guard;
	}
	
	public void setFCondition(String fcondition) {
		this.cond = fcondition;
	}
	
	public void setInput(String in) {
		this.in = in;
	}
}
