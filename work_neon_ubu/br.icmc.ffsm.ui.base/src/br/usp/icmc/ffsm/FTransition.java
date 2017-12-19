package br.usp.icmc.ffsm;


public class FTransition{

	private String output;
	private FState source, target;
	private CIn cinput;
	
	public FTransition(FState source, FState target, CIn c_input, String output) {		
		this.output = output;
		this.source = source;
		this.target = target;
		this.cinput = c_input;
	}
	
	@Override
	public String toString() 
	{
		String in = cinput.getIn().trim();
		String cond = cinput.getCond().trim();
		String sn = source.getStateName().trim();
		String sc = source.getCondition().trim();
		String tn = target.getStateName().trim();
		String tc = target.getCondition().trim();
		return sn +"@"+sc + "--" + in+"@"+cond + "/" + output + "->" + tn+"@"+tc;
	}
	
	public String getSimple() {
		String in = cinput.getIn();
		String sn = source.getStateName();
		String tn = target.getStateName();
		return sn + " -- " + in+ "/" + output + " -> " + tn;
	}
	
	public FState getSource() {
		return source;
	}
	
	public FState getTarget() {
		return target;
	}
	
	public CIn getCInput() {
		return cinput;
	}
	
	public String getOutput() {
		return output;
	}
	
	public void setSource(FState source) {
		this.source = source;
	}
	
	public void setTarget(FState target) {
		this.target = target;
	}
	
	public void setOutput(String output) {
		this.output = output;
	}
	
	public void setCInput(CIn cinput) {
		this.cinput = cinput;
	}
}

