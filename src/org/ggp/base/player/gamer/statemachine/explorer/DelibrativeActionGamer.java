package org.ggp.base.player.gamer.statemachine.explorer;

import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.player.gamer.statemachine.explorer.StateMachineExplorerGamer;

import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;

import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.MachineState;

import java.util.Arrays;
import java.util.List;

/**
 * This class implements compulsive delibrative action strategy: Iterate over all possible legal actions in a state 
 * and pick the one that gives the best payoff in current state. Locally "greedy" strategy
 */
public class DelibrativeActionGamer extends StateMachineExplorerGamer {
	
	public String getName()
	{
		return "RamSud: Delibrative Action";
	}
	
	/**
	 * This player iterates over all possible actions and pick the one that gives the best payoff
	 * @param timeout time in milliseconds since the era when this function must return
	 * @return Move - the move selected by the player
	 * @throws TransitionDefinitionException
	 * @throws MoveDefinitionException
	 * @throws GoalDefinitionException
	 */
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		// We get the current start time
		long start = System.currentTimeMillis();

		// Get the current stateMachine
		StateMachine stateMachine = getStateMachine();
		
		// Get current state
		MachineState state = getCurrentState();
		
		// Get my role
		Role role = getRole();
		
		// Get all legal moves
		List<Move> moves = stateMachine.getLegalMoves(state, role);
		
		int bestScore = 0;
		Move selection = moves.get(0);
		
		// Iterate over the moves
		for (Move m : moves) {
			MachineState nextState = stateMachine.getNextState(state, Arrays.asList(m));
			int score = stateMachine.getGoal(nextState, role);
			if (score > bestScore) {
				bestScore = score;
				selection = m;
			}
		}
		// We get the end time
		// It is mandatory that stop<timeout
		long stop = System.currentTimeMillis();

		/**
		 * These are functions used by other parts of the GGP codebase
		 * You shouldn't worry about them, just make sure that you have
		 * moves, selection, stop and start defined in the same way as 
		 * this example, and copy-paste these two lines in your player
		 */
		notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
		return selection;
	}
}