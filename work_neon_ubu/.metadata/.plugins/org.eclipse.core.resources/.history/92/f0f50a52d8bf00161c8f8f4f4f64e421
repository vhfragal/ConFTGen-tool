package br.usp.icmc.fsm.constructor;

import java.util.ArrayList;
import java.util.HashMap;

import br.usp.icmc.fsm.common.FiniteStateMachine;
import br.usp.icmc.fsm.common.Node;
import br.usp.icmc.fsm.common.Ntree;
import br.usp.icmc.fsm.common.State;
import br.usp.icmc.fsm.common.TestSequence;
import br.usp.icmc.fsm.common.Transition;

public class TestingTreeSetsConstructor extends AbstractSetsConstructor 
{
	protected Ntree tree;
	protected HashMap<State, String> visited;
	
	public TestingTreeSetsConstructor(FiniteStateMachine fsm) {
		super(fsm);
	}

	@Override
	public void construct() 
	{
		createTestingTreeBreadth(fsm);
		generateCoverSets(tree);
	}
	
	private void createTestingTreeBreadth(FiniteStateMachine fsm) 
	{
		visited = new HashMap<State, String>();
		tree = new Ntree();
		ArrayList<Node> queue = new ArrayList<Node>();
		
		Node root = new Node(fsm.getInitialState());
		//Node root = new Node(fsm.getInitialState(),"");
		tree.setRoot(root);
		queue.add(root);
		
		while(! queue.isEmpty())
		{
			Node current = queue.remove(0);//first
			if(visited.get(current.getState()) == null)	//not visited
			{
				visited.put(current.getState(), "ok");	//to visit
				for(Transition t : current.getState().getOut())
				{
					Node newnode = new Node(t.getOut());
					//Node newnode = new Node(t.getOut(), "");
					tree.addNode(current, t.getInput(), newnode);
					//tree.addNode(current, newnode);
					queue.add(newnode);
				}
			}
		}
	}

	protected void generateCoverSets(Ntree tree)
	{
		stateCover = new ArrayList<String>();
		transitionCover = new ArrayList<String>();		
		visited = new HashMap<State, String>();
		ArrayList<Node> queue = new ArrayList<Node>();
		ArrayList<String> queue_seqs = new ArrayList<String>();
		queue.add(tree.getRoot());
		queue_seqs.add("EPSILON");
		
		while(! queue.isEmpty())
		{
			Node current = queue.remove(0);				//first
			String curr_seq = queue_seqs.remove(0);
			if(visited.get(current.getState()) == null)	//not visited
			{
				visited.put(current.getState(), "ok");
				stateCover.add(curr_seq);
			}
			//transition cover
			transitionCover.add(curr_seq);
			
			for (int i = 0; i < current.getChildren().size(); i++) 
			{
				queue.add(current.getChildren().get(i));
				queue_seqs.add( TestSequence.concat(curr_seq, current.getLabels().get(i)) );
			}
		}
	}
}
