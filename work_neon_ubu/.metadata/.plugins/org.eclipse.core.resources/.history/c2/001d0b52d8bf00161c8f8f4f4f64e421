package br.usp.icmc.fsm.common;

import br.usp.icmc.fsm.common.Ruler.AddedTo;

public class Trigger 
{
	private Pair pair;
	private AddedTo addedto;
	
	public Trigger(Pair p, AddedTo a) 
	{
		pair = p;
		addedto = a;
	}
	
	public void setAddedto(AddedTo addedto) {
		this.addedto = addedto;
	}
	
	public AddedTo getAddedto() {
		return addedto;
	}

	public Pair getPair() {
		return pair;
	}
	
	public void setPair(Pair pair) {
		this.pair = pair;
	}
	
	@Override
	public String toString() {
		return "( " + pair + " ; " + addedto + " )";
	}
}
