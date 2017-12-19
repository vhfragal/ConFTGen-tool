package br.usp.icmc.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import br.usp.icmc.ffsm.CIn;
import br.usp.icmc.ffsm.FFSM;
import br.usp.icmc.ffsm.FState;
import br.usp.icmc.ffsm.FTransition;

public class FFSMModelReader {
	protected File file;
	protected boolean validFile;
	protected FFSM ffsm;
	protected HashMap<String, FState> fstates;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public FFSM getFsm() throws Exception {
		if (ffsm.getFStates().size() == 0 || ffsm.getFTransitions().size() == 0)
			throw new Exception("Empty FFSM");

		return ffsm;
	}
	public FFSMModelReader() {
		//fsm = new FiniteStateMachine();
		//states = new HashMap<String, State>();
	}

	public FFSMModelReader(File file) {
		this.file = file;

		ffsm = new FFSM();
		fstates = new HashMap<String, FState>();

		read();
	}
	
	public FFSM getFFSM(){
		return ffsm;
	}

	protected void addTransition(String line) throws Exception {
		// s1 * ignore s1
		// st1 input output st2		
		String token[] = line.split("#");
		//System.out.println(line);
		if (token.length != 4)
			throw new Exception("Non well formed transition");
					
		token[0] = token[0].trim();
		token[1] = token[1].trim();
		token[2] = token[2].trim();
		token[3] = token[3].trim();
		
		String[] c_st = token[0].split("@");
		FState s1 = fstates.get(token[0]);
		if (s1 == null) {
			//System.out.println(line);
			s1 = new FState(c_st[0],c_st[1]);
			if (ffsm.getFStates().size() == 0)
				ffsm.setFInitialState(s1);
			ffsm.addFState(s1);

			fstates.put(token[0], s1);
		}

		String[] c_st2 = token[3].split("@");
		FState s2 = fstates.get(token[3]);
		if (s2 == null) {
			s2 = new FState(c_st2[0],c_st2[1]);
			ffsm.addFState(s2);
			fstates.put(token[3], s2);
		}

		String[] c_st1 = token[1].split("@");
		CIn cin = new CIn(c_st1[0],c_st1[1]);
		FTransition ft = new FTransition(s1, s2, cin, token[2]);
		ffsm.addFTransition(ft);
		//System.out.println(line);
		//System.out.println(c_st[0]+" "+c_st[1]);
		//System.out.println(token[1]);
		//System.out.println(token[2]);
		//System.out.println(token[3]);
	}
	

	protected void read() {
		validFile = true;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (!(line.equals("") || line.startsWith("%"))) {
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
