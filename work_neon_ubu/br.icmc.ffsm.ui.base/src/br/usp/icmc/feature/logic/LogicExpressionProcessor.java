package br.usp.icmc.feature.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import logiccalculator.core.AtomsList;
import logiccalculator.core.Constants;
import logiccalculator.core.Processor;
import logiccalculator.core.TruthTable;
import logiccalculator.rpn.RPNConverter;

public class LogicExpressionProcessor {

	private static int numAtoms;
	private static int numInterpretations;
	private static String rpnExpr;
	private static String theExpr;
	private static List<Character> atomsList;
	RPNConverter rpnConverter;
	private static int numModels;
	private static boolean[][] modelsList;
	private static boolean[][] truthTable;
	

	public LogicExpressionProcessor(String Expression) {
		theExpr = Expression;
		rpnConverter = new RPNConverter(theExpr);
		rpnExpr = rpnConverter.process();
		atomsList = new AtomsList().parseAtoms(rpnExpr);
		numAtoms = atomsList.size();
		numInterpretations = ((int) Math.pow(2.0D, numAtoms));
	}

	public void process() {
		
		Processor p = new Processor(theExpr, null);
		System.out.println(p.getAtomsList());

		TruthTable t = new TruthTable(p.getNumAtoms());
		truthTable = t.getTruthTable();

		boolean[] result = process(true, 0, truthTable.length);		
	}

	public static boolean[] process(boolean printTruthTable, int initRank,
			int endRank) {
		numModels = 0;
		boolean[] result = new boolean[numInterpretations];
		boolean[][] tempModelsList = new boolean[numInterpretations][];
		//ArrayList<String> valid_conf = new ArrayList<String>();
		
		String out = "";

		if (printTruthTable) {
			String blanks = Constants.getBlanksLine(numInterpretations);
			int trimSpace = blanks.length() - 1;
			
			out = out.concat(blanks + "   ");
			for (int j = 0; j < numAtoms; j++) {				
				out = out.concat(atomsList.get(j) + " ");
			}

			//print head table
			out = out.concat("| ");
			out = out.concat(theExpr + "\n");			
			out = out.concat(blanks + "   ");			
			for (int i = 0; i < numAtoms; i++) {
				out = out.concat("--");
			}
			out = out.concat("|-");
			for (int i = 0; i < theExpr.length(); i++) {
				out = out.concat("-");
			}
			out = out.concat("\n");
			
			//print body
			for (int i = initRank; i < endRank; i++) {
				if (i == 9) {
					blanks = blanks.substring(0, trimSpace--);
				} else if (i == 99) {
					blanks = blanks.substring(0, trimSpace--);
				} else if (i == 999) {
					blanks = blanks.substring(0, trimSpace--);
				} else if (i == 9999) {
					blanks = blanks.substring(0, trimSpace--);
				} else if (i == 99999) {
					blanks = blanks.substring(0, trimSpace--);
				}
				
				out = out.concat(blanks + (i + 1) + "] ");				
				//Constants.printLineNumber(blanks, i + 1);
				for (int j = 0; j < numAtoms; j++) {
					//Constants.printBoolean(truthTable[i][j]);
					if (truthTable[i][j]) out = out.concat("1 ");
					else out = out.concat("0 ");						
				}

				out = out.concat("| ");				
				result[i] = processInterpretation(truthTable[i]);
				//Constants.printBoolean(result[i]);
				if(result[i]) out = out.concat("1 ");
				else out = out.concat("0 ");	
				
				if (result[i] != false) {
					out = out.concat(Constants.VISUAL_OK);					
					tempModelsList[numModels] = truthTable[i];
					numModels += 1;
				}
				//Constants.println();
				out = out.concat("\n");

			}
		} else {
			for (int i = 0; i < numInterpretations; i++) {
				result[i] = processInterpretation(truthTable[i]);
				if (result[i] != false) {
					tempModelsList[numModels] = truthTable[i];
					numModels += 1;
				}
			}
		}

		//print bottom
		out = out.concat("------------" + "\n");
		out = out.concat("Models: " + numModels + "\n");
		
		modelsList = new boolean[numModels][];
		System.arraycopy(tempModelsList, 0, modelsList, 0, numModels);

		System.out.println(out);
		return result;
	}

	public static boolean processInterpretation(boolean[] interpretation) {
		Stack<Boolean> stack = new Stack();
		for (int i = 0; i < rpnExpr.length(); i++) {
			char token = rpnExpr.charAt(i);
			if (!Constants.isVoid(token)) {
				if (Constants.isOperator(token)) {
					if (token == Constants.NOT) {
						boolean atom = ((Boolean) stack.pop()).booleanValue();
						boolean result = !atom;
						stack.push(Boolean.valueOf(result));
					} else {
						boolean atom2 = ((Boolean) stack.pop()).booleanValue();
						boolean atom1 = ((Boolean) stack.pop()).booleanValue();

						if (token == Constants.AND) {
							boolean result = (atom1) && (atom2);
							stack.push(Boolean.valueOf(result));
						} else if (token == Constants.OR) {
							boolean result = (atom1) || (atom2);
							stack.push(Boolean.valueOf(result));
						} else if (token == Constants.IMPLIES) {
							if ((atom1 == true) && (!atom2)) {
								stack.push(Boolean.valueOf(false));
							} else {
								stack.push(Boolean.valueOf(true));
							}
						} else if (token == Constants.BIMPLIES) {
							if (atom1 == atom2) {
								stack.push(Boolean.valueOf(true));
							} else
								stack.push(Boolean.valueOf(false));
						}
					}
				} else {
					int pos = findAtom(token);
					boolean atom = interpretation[pos];

					stack.push(Boolean.valueOf(atom));
				}
			}
		}
		boolean result = ((Boolean) stack.pop()).booleanValue();
		return result;
	}
	
	private static int findAtom(char atom) {
		int pos = 0;
		for (Iterator i$ = atomsList.iterator(); i$.hasNext();) {
			char c = ((Character) i$.next()).charValue();
			if (c == atom) {
				return pos;
			}
			pos++;
		}
		return -1;
	}
	
	public boolean[][] getModels(){
		return modelsList;
	}
	
	public List<Character> getAtomList(){
		return atomsList;
	}
	
	public int getNumModels(){
		return numModels;
	}
		
}
