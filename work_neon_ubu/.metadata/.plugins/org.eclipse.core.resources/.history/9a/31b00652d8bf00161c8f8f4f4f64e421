package br.usp.icmc.uml;

import java.util.ArrayList;

public class CState {
	protected String label;
	protected String fcondition;
	public enum StateType {simple,compositeOr,compositeAnd,region}
	protected StateType type;
	protected Boolean isdefault;

	protected ArrayList<CTransition> reach, leave; //is reached by / leave from
	protected CState superstate; //father
	protected ArrayList<CState> substates; //child
	protected ArrayList<CState> descendants;
	protected ArrayList<CState> ancestors;
	protected String id;
				
	public CState(String label, String fcondition, StateType type, Boolean isdefault, String id) {
		this.label = label;		
		this.fcondition = fcondition;
		this.type = type;
		this.isdefault = isdefault;
		this.id = id;
		reach = new ArrayList<CTransition>();
		leave = new ArrayList<CTransition>();
		substates = new ArrayList<CState>();
	}
			
	@Override
	public String toString() {
		//return label+"|"+fcondition+"| ("+type+ ","+isdefault+")->"+substates;
		//return label+ "("+type+ ","+isdefault+"," +fcondition+")";
		return label;
	}
	
	public String getID() {
		return id;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public void setSuperState(CState superstate) {
		this.superstate = superstate;
	}
	
	public void setType(StateType type) {
		this.type = type;
	}
	
	public void setDefault(Boolean isdefault) {
		this.isdefault = isdefault;
	}
	
	public void setDescendants(ArrayList<CState> descendants) {
		this.descendants = descendants;
	}
	
	public void setAncestors(ArrayList<CState> ancestors) {
		this.ancestors = ancestors;
	}
	
	public void setSubstates(ArrayList<CState> substates) {
		this.substates = substates;
	}
	
	public String getLabel() {
		return label;
	}
	
	public CState getSuperState() {
		return superstate;
	}
	
	public StateType getType() {
		return type;
	}
	
	public Boolean getDefault() {
		return isdefault;
	}
	
	public ArrayList<CState> getSubstates() {
		return substates;
	}
	
	public ArrayList<CState> getDescendants() {
		return descendants;
	}
	
	public ArrayList<CState> getAncestors() {
		return ancestors;
	}
	
	public String getFCondition() {
		return fcondition;
	}
	
	public void setFCondition(String fcondition) {
		this.fcondition = fcondition;
	}
	
	public ArrayList<CTransition> getReach() {
		return reach;
	}

	public void setReach(ArrayList<CTransition> reach) {
		this.reach = reach;
	}

	public ArrayList<CTransition> getLeave() {
		return leave;
	}

	public void setLeave(ArrayList<CTransition> leave) {
		this.leave = leave;
	}
	
	public void addReachTransition(CTransition t) {
		reach.add(t);
	}

	public void addLeaveTransition(CTransition t) {
		leave.add(t);
	}
	
	public boolean isDefinedForInput(String input) 
	{
		for(CTransition t : leave)
		{
			if(t.getInput().equals(input))
				return true;
		}
		return false;
	}

}
