package org.ggp.base.player.gamer.statemachine.explorer.heuristic;

import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;

import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

import java.util.List;

public class MobilityHeuristic implements HeuristicEvalInterface {

	@Override
	public int eval(MachineState state, Role role, StateMachine stateMachine) throws MoveDefinitionException {
		List<Move> moves = stateMachine.getLegalMoves(state, role);
		return 2;
	}

}
