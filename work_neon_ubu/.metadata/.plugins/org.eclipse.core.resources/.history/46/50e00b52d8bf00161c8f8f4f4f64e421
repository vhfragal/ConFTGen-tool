package br.usp.icmc.fsm.common;

public class Transition 
{
	private String input, output;
	private State in, out;
	
	public Transition(){
		
	}
	
	public Transition(State in, State out, String input, String output) {
		this.in = in;
		this.in.addOutTransition(this);
		this.out = out;
		this.out.addInTransition(this);
		this.input = input;
		this.output = output;
	}
	
	@Override
	public String toString() 
	{
		return in + "--" + input + "/" + output + "->" + out;
	}
	
	public State getIn() {
		return in;
	}
	
	public State getOut() {
		return out;
	}
	
	public String getInput() {
		return input;
	}
	
	public String getOutput() {
		return output;
	}
	
	public void setIn(State in) {
		this.in = in;
	}
	
	public void setOut(State out) {
		this.out = out;
	}
	
	public void setOutput(String output) {
		this.output = output;
	}
	
	public void setInput(String input) {
		this.input = input;
	}
}
