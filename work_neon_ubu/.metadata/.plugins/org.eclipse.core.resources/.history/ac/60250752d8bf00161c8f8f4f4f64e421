package br.usp.icmc.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import br.usp.icmc.fsm.common.FiniteStateMachine;
import br.usp.icmc.fsm.common.State;
import br.usp.icmc.fsm.common.Transition;

public class FsmModelReader {
	protected File file;
	protected boolean validFile;
	protected FiniteStateMachine fsm;
	protected HashMap<String, State> states;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public FiniteStateMachine getFsm() throws Exception {
		if (fsm.getStates().size() == 0 || fsm.getTransitions().size() == 0)
			throw new Exception("Empty FSM");

		return fsm;
	}
	public FsmModelReader() {
		//fsm = new FiniteStateMachine();
		//states = new HashMap<String, State>();
	}

	public FsmModelReader(File file) {
		this.file = file;

		fsm = new FiniteStateMachine();
		states = new HashMap<String, State>();

		read();
	}

	public FsmModelReader(File file, boolean adesFormat) {
		this.file = file;

		fsm = new FiniteStateMachine();
		states = new HashMap<String, State>();

		readAdes();
	}

	protected void addTransition(String line) throws Exception {
		// s1 * ignore s1
		// st1 input output st2		
		String token[] = line.split("#");
		if (token.length != 4)
			throw new Exception("Non well formed transition");

		token[0] = token[0].trim();
		token[1] = token[1].trim();
		token[2] = token[2].trim();
		token[3] = token[3].trim();
		
		State s1 = states.get(token[0]);
		if (s1 == null) {
			s1 = new State(token[0]);
			if (fsm.getStates().size() == 0)
				fsm.setInitialState(s1);
			fsm.addState(s1);

			states.put(token[0], s1);
		}

		State s2 = states.get(token[3]);
		if (s2 == null) {
			s2 = new State(token[3]);
			fsm.addState(s2);
			states.put(token[3], s2);
		}

		Transition t = new Transition(s1, s2, token[1], token[2]);
		fsm.addTransition(t);
	}

	protected void read() {
		validFile = true;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (!(line.equals("") || line.startsWith("#")))
					addTransition(line);
			}
			reader.close();
		} catch (Exception e) {
			validFile = false;
			e.printStackTrace();
		}
	}

	protected void readAdes() {
		validFile = true;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (!(line.equals("") || line.startsWith("#"))) {
					line = line.replaceAll("--", "#");
					line = line.replaceAll("/", "#");
					line = line.replaceAll("->", "#");
					// System.out.println(line);
					addTransition(line);
				}
			}
			reader.close();
		} catch (Exception e) {
			validFile = false;
			e.printStackTrace();
		}
	}

	public boolean isValidFile() {
		return validFile;
	}
}
