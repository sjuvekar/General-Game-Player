package org.ggp.base.player.gamer.statemachine.explorer;

import org.ggp.base.player.gamer.statemachine.explorer.StateMachineExplorerGamer

import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;

import org.ggp.base.util.statemachine.Move;

import java.util.Array;

/**
 * This class implements compulsive delibrative action strategy: Iterate over all possible legal actions in a state 
 * and pick the one that gives the best payoff in current state. Locally "greedy" strategy
 */
public class DelibrativeActionGamer extends StateMachineExplorerGamer {
	
	public String getName()
	{
		return "RamSud Delibrative Action";
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

		List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());

		int bestScore = 0;
		Move selection = moves[0];
		
		for (Move m : moves) {
			int score = getStateMachine().getNextState(getCurrentState(), Arrays.asList(m)).
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