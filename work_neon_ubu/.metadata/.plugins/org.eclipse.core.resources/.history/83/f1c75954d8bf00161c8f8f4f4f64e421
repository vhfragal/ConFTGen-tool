package reader;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;

import org.junit.Test;

import br.usp.icmc.parser.FCONSTRAINT;
import br.usp.icmc.reader.UMLModelReader;
import br.usp.icmc.uml.CState;
import br.usp.icmc.uml.CTransition;
import br.usp.icmc.uml.HFFSM;
import br.usp.icmc.uml.HFFSMProperties;
import br.usp.icmc.uml.StateStructure;

public class UMLReaderTest {
	@Test
	public void test01() {
		System.out.println("-----------------------------------------");
		System.out.println("TEST 01");
		try {
			// File file = new File("./uml/bcs.uml");
			File file = new File("./agm.uml");
			UMLModelReader reader = new UMLModelReader(file);
			HFFSM hsm = reader.getHsm();
			System.out.println("Root ->" + hsm.getStruct().getRoot());

			System.out.println("-----------------------------------------");
			hsm.getStruct().print();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@Test
	public void test02() {
		System.out.println("-----------------------------------------");
		System.out.println("TEST 02");
		try {
			File file = new File("./agm.uml");
			UMLModelReader reader = new UMLModelReader(file);
			HFFSM hsm = reader.getHsm();
			// hsm.getStruct().getStateSet();

			System.out.println("State Descendants");
			for (CState s : hsm.getStruct().getStateSet()) {
				System.out.println(s + "->" + s.getDescendants());
			}
			System.out.println("-----------------------------------------");
			System.out.println("State Ancestors");
			for (CState s : hsm.getStruct().getStateSet()) {
				System.out.println(s + "->" + s.getAncestors());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@Test
	public void test03() {
		System.out.println("-----------------------------------------");
		System.out.println("TEST 03");
		try {
			File file = new File("./agm.uml");
			UMLModelReader reader = new UMLModelReader(file);
			HFFSM hsm = reader.getHsm();
			// hsm.getStruct().getStateSet();

			System.out.println("State Substates");
			for (CState s : hsm.getStruct().getStateSet()) {
				System.out.println(s + " //->" + s.getSubstates() + " //<- "
						+ s.getSuperState());
			}
			System.out.println("-----------------------------------------");
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@Test
	public void test04() {
		System.out.println("-----------------------------------------");
		System.out.println("TEST 04");
		try {
			File file = new File("./agm.uml");
			UMLModelReader reader = new UMLModelReader(file);
			HFFSM hsm = reader.getHsm();

			StateStructure st = hsm.getStruct();
			ArrayList<CState> arr = st.getStateSet();
			CState s1 = arr.get(10);
			CState s2 = arr.get(7);
			ArrayList<CState> list = new ArrayList<CState>();
			list.add(s1);
			list.add(s2);

			System.out.println("Set of State Descendants for " + s1 + " and "
					+ s2);
			System.out.println(st.getDescendants(list));

			System.out.println("-----------------------------------------");
			System.out.println("Set of State Ancestors for " + s1 + " and "
					+ s2);
			System.out.println(st.getAncestors(list));
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@Test
	public void test05() {
		System.out.println("-----------------------------------------");
		System.out.println("TEST 05");
		try {
			// File file = new File("./uml/bcs.uml");
			File file = new File("./agm.uml");
			UMLModelReader reader = new UMLModelReader(file);
			HFFSM hsm = reader.getHsm();
			System.out.println("Transitions");
			for (CTransition t : hsm.getTransitions()) {
				System.out.println(t);
			}
			System.out.println("-----------------------------------------");
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@Test
	public void test06() {
		System.out.println("-----------------------------------------");
		System.out.println("TEST 06");
		try {
			// File file = new File("./uml/bcs.uml");
			File file = new File("./agm.uml");
			UMLModelReader reader = new UMLModelReader(file);
			HFFSM hsm = reader.getHsm();

			StateStructure st = hsm.getStruct();
			ArrayList<CState> arr = st.getStateSet();
			CState s1 = arr.get(7);
			CState s2 = arr.get(11);
			ArrayList<CState> list = new ArrayList<CState>();
			list.add(s1);
			list.add(s2);

			System.out
					.println("Least Common Ancestor for " + s1 + " and " + s2);
			System.out.println(st.getLeastCommonAncestor(list));

			System.out.println("-----------------------------------------");
			System.out.println("Default descendants of " + s1);
			System.out.println(st.getDefaultDesc(s1));

			System.out.println("-----------------------------------------");
			System.out.println("Is state " + s1 + " a default descendant of "
					+ s2 + "?");
			System.out.println(st.isDefaultDesc(s1, s2));
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@Test
	public void test07() {
		System.out.println("-----------------------------------------");
		System.out.println("TEST 07");
		try {
			// File file = new File("./uml/bcs.uml");
			File file = new File("./agm.uml");
			UMLModelReader reader = new UMLModelReader(file);
			HFFSM hsm = reader.getHsm();
			StateStructure st = hsm.getStruct();

			System.out.println("Feature composition of states");
			for (CState s : st.getStateSet()) {
				System.out
						.println("State " + s + " ->" + st.getFComposition(s));
			}
			System.out.println("-----------------------------------------");
			System.out.println("Feature composition of transitions");
			for (CTransition t : hsm.getTransitions()) {
				System.out.println("Transition " + t + " ->"
						+ st.getTComposition(t));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@Test
	public void test08() {
		System.out.println("-----------------------------------------");
		System.out.println("TEST 08");
		try {
			// File file = new File("./uml/bcs.uml");
			File file = new File("./agm.uml");
			UMLModelReader reader = new UMLModelReader(file);
			HFFSM hsm = reader.getHsm();
			StateStructure st = hsm.getStruct();

			System.out.println("Transition check R");
			CTransition t1 = hsm.checkR();
			if (t1 != null) {
				System.out.println(t1);
			}
			System.out.println("-----------------------------------------");
			System.out.println("Transition check orthogonal");
			CTransition t2 = hsm.checkR();
			if (t2 != null) {
				System.out.println(t2);
			}
			System.out.println("-----------------------------------------");
			System.out.println("Transition check orthogonal guard");
			CTransition t3 = hsm.checkR();
			if (t3 != null) {
				System.out.println(t3);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@Test
	public void test09() {
		System.out.println("-----------------------------------------");
		System.out.println("TEST 09");
		try {
			// File file = new File("./uml/bcs.uml");
			File file = new File("./agm.uml");
			UMLModelReader reader = new UMLModelReader(file);
			HFFSM hsm = reader.getHsm();

			System.out.println("Path Map");
			HFFSMProperties p = new HFFSMProperties(hsm);
			p.find_all_paths();
			p.print_paths(p.getPathMap());
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@Test
	public void test_token() throws IOException {
		String filePath = "./hffsm/f.txt";
		Reader r = new BufferedReader(new FileReader(new File(filePath)));
		StreamTokenizer st = new StreamTokenizer(r);
		// print the stream tokens
		boolean eof = false;
		do {
			int token = st.nextToken();
			switch (token) {
			case StreamTokenizer.TT_EOF:
				System.out.println("End of File encountered.");
				eof = true;
				break;
			case StreamTokenizer.TT_EOL:
				System.out.println("End of Line encountered.");
				break;
			case StreamTokenizer.TT_WORD:
				System.out.println("Word: " + st.sval);
				break;
			case StreamTokenizer.TT_NUMBER:
				System.out.println("Number: " + st.nval);
				break;
			default:
				System.out.println((char) token + " encountered.");
				if (token == '!') {
					eof = true;
				}
			}
		} while (!eof);

	}

	@Test
	public void test_parser() throws Exception {
		//String filePath = "./hffsm/f.txt";
		//Reader r = new BufferedReader(new FileReader(new File(filePath)));
		
		File file = new File("./agm.uml");
		UMLModelReader reader = new UMLModelReader(file);
		HFFSM hsm = reader.getHsm();
		StateStructure st = hsm.getStruct();
		HFFSMProperties p = new HFFSMProperties("hffsm");
		Reader r = new BufferedReader(new StringReader(";"));
		new FCONSTRAINT(r);
		
		System.out.println("Feature composition of transitions");
		for (CTransition t : hsm.getTransitions()) {
			//String ex = "W&!S&W||(A&W)";
			String ex = st.getTComposition(t);
			ex = ex.concat(";");
			String z3 = "";
			if(!ex.equals("")){
				//System.out.println(ex);
				z3 = p.getZ3(ex);
			}
			System.out.println(t + " ->"+ ex + ":"+z3);
			//break;
		}		
	}
	
	@Test
	public void test007_deterministic() {
		HFFSMProperties p = new HFFSMProperties("hffsm");
		String header = p.read_XML_FeatureModel("./hffsm/agm_fm.xml");
		String file = "./agm.uml";
		Reader r = new BufferedReader(new StringReader(";"));
		new FCONSTRAINT(r);
		
		try {
			if (!p.set_checkHFFSM(file, header)) {
				System.out.println("Invalid HFFSM!!!");
				return;
			}
			System.out.println("Valid HFFSM...");
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean is = p.is_deterministic();
		System.out.println("\nIs the HFFSM deterministic?");
		System.out.println(is);
	}
	
	@Test
	public void test007_init_connect() {
		HFFSMProperties p = new HFFSMProperties("hffsm");
		String header = p.read_XML_FeatureModel("./hffsm/agm_fm.xml");
		String file = "./agm.uml";
		Reader r = new BufferedReader(new StringReader(";"));
		new FCONSTRAINT(r);
		
		try {
			if (!p.set_checkHFFSM(file, header)) {
				System.out.println("Invalid HFFSM!!!");
				return;
			}
			System.out.println("Valid HFFSM...");
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean is = p.is_initially_connected();
		System.out.println("\nIs the HFFSM initially connected?");
		System.out.println(is);
	}
	
	@Test
	public void test008_minimal() {
		HFFSMProperties p = new HFFSMProperties("hffsm");
		String header = p.read_XML_FeatureModel("./hffsm/agm_fm.xml");
		String file = "./agm.uml";
		Reader r = new BufferedReader(new StringReader(";"));
		new FCONSTRAINT(r);
		
		try {
			if(!p.set_checkHFFSM(file, header)){
				System.out.println("Invalid HFFSM!!!");
				return;
			}
			System.out.println("Valid HFFSM...");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		boolean is = p.is_minimal();
		System.out.println("\nIs the HFFSM minimal?");
		System.out.println(is);
	}

}
