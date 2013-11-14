package org.ggp.base.player.gamer.statemachine.explorer.heuristic;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.MachineState;

import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;

/**
 * This interface defines a function eval that takes the current state, role and current state machine and 
 * returns a numeric score.  
 */
public interface HeuristicEvalInterface {
	
	public int eval(MachineState state, Role role, StateMachine stateMachine) throws GoalDefinitionException, MoveDefinitionException;
}
