/**
* Copyright (c) 2016 committers of YAKINDU and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     committers of YAKINDU - initial API and implementation
*/
package org.yakindu.sct.simulation.core.sexec.test;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yakindu.sct.model.sexec.ExecutionFlow;
import org.yakindu.sct.model.sexec.interpreter.test.util.AbstractExecutionFlowTest;
import org.yakindu.sct.model.sexec.interpreter.test.util.SExecInjectionProvider;
import org.yakindu.sct.test.models.SCTUnitTestModels;
import com.google.inject.Inject;
import static org.junit.Assert.assertTrue;
/**
 *  Unit TestCase for ExitOnSelfTransition
 */
@SuppressWarnings("all")
@RunWith(XtextRunner.class)
@InjectWith(SExecInjectionProvider.class)
public class ExitOnSelfTransitionTest extends AbstractExecutionFlowTest {
	@Before
	public void setup() throws Exception {
		ExecutionFlow flow = models.loadExecutionFlowFromResource("ExitOnSelfTransition.sct");
		initInterpreter(flow);
	}
	@Test
	public void ExitOnSelfTransitionTest() throws Exception {
		interpreter.enter();
		assertTrue(isStateActive("A"));
		assertTrue(getInteger("entryCount") == 1l);
		assertTrue(getInteger("exitCount") == 0l);
		raiseEvent("e");
		interpreter.runCycle();
		assertTrue(getInteger("entryCount") == 2l);
		assertTrue(getInteger("exitCount") == 1l);
		raiseEvent("f");
		interpreter.runCycle();
		assertTrue(getInteger("entryCount") == 2l);
		assertTrue(getInteger("exitCount") == 2l);
	}
}