package sat;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.InstanceReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IOptimizationProblem;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;

public class Example_DIMACS_CNF {
	public static void main(String[] args) throws ParseFormatException, IOException, ContradictionException {
		ISolver solver = SolverFactory.newDefault();
		solver.setTimeout(3600); // 1 hour timeout
		Reader reader = new DimacsReader(solver);
		// Decide problem
		try {
			boolean unsat = true;
			IProblem problem = reader.parseInstance("./DIMACS/cnf.txt");
			if (problem.isSatisfiable()) {
				System.out.println("Satisfiable!");
				System.out.println(reader.decode(problem.model()));
			} else {
				System.out.println("Unsatisfiable!");
			}

			// iterate over models (you have 5 seconds)
			solver.setTimeout(5); 
			ModelIterator mi = new ModelIterator(solver);
			reader = new InstanceReader(mi);
			//problem = reader.parseInstance("./DIMACS/cnf2.txt");
			problem = reader.parseInstance("./DIMACS/cnf3.txt");
			int[] best_model = null;
			System.out.println("Iterate over models");
			while (problem.isSatisfiable()) {
				unsat = false;
				int[] model = problem.model();
				// do something with each model
				System.out.println(reader.decode(problem.model()));
				if(best_model == null){
					best_model = model;
				}else {
					// next model is better do something
					
				}
			}
			if (unsat) {
				// do something for unsat case
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		} catch (ParseFormatException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		} catch (ContradictionException e) {
			System.out.println("Unsatisfiable (trivial)!");
		} catch (TimeoutException e) {
			System.out.println("Timeout, sorry!");
		}
/*
		// optimize problem
		boolean isSatisfiable = false;
		ModelIterator mi = new ModelIterator(solver);
		reader = new InstanceReader(mi);
		IProblem problem = reader.parseInstance("./DIMACS/cnf.txt");
		IOptimizationProblem optproblem = (IOptimizationProblem) problem;		
		try {
			// while is it possible o find a better solution
			while (optproblem.admitABetterSolution()) {
				if (!isSatisfiable) {
					isSatisfiable = true;
					System.out.println(reader.decode(optproblem.model()));
					// can now start optimization
				}
				// make sure to prevent equivalent solutions to be found again
				optproblem.discardCurrentSolution();
			}
			if (isSatisfiable) {
				//return OPTIMUM_FOUND;
			} else {
				//return UNSATISFIABLE;
			}
		} catch (ContradictionException ex) {
			assert isSatisfiable;
			// discarting a solution may launch a contradiction.
			//return OPTIMUM_FOUND;
		} catch (TimeoutException ex) {
			if (isSatisfiable){}
			//	return UPPER_BOUND;
			//return null;
		}
*/
		
	}
	/*	
	ISolver solver = SolverFactory . newDefault ();
	// prepare the solver to accept MAXVAR variables . MANDATORY
	solver . newVar ( MAXVAR );
	// not mandatory for SAT solving . MANDATORY for MAXSAT solving
	solver . setExpectedNumberOfClauses ( NBCLAUSES );
	// Feed the solver using Dimacs format , using arrays of int
	// ( best option to avoid dependencies on SAT4J IVecInt )
	for (int i=0; < NBCLAUSES ;i++) {
	int [] clause = // get the clause from somewhere
	// the clause should not contain a 0 ,
	// only integer ( positive or negative )
	// with absolute values less or equal to MAXVAR
	// e.g. int [] clause = {1 , -3 , 7}; is fine
	// while int [] clause = {1 , -3 , 7 , 0}; is not fine
	solver . addClause ( new VecInt ( clause )); // adapt Array to IVecInt
	}
	// we are done . Working now on the IProblem interface
	IProblem problem = solver ;
	*/
	
}