package org.ggp.base.player.gamer.statemachine.explorer.heuristic;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;

public class GoalProximityHeuristic implements HeuristicEvalInterface {

	@Override
	public int eval(MachineState state, Role role, StateMachine stateMachine) throws GoalDefinitionException, MoveDefinitionException {
		return stateMachine.getGoal(state, role);
	}

}
