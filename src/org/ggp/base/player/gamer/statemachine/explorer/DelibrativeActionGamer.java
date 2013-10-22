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

		// Get current state
		MachineState state = getCurrentState();
		
		// Get state machine
		StateMachine stateMachine = getStateMachine();
		
		// Get my role
		Role role = getRole();
		
		// For each move, find the next state with best reward.
		List<Move> moves = stateMachine.getLegalMoves(state, role);
		Move selection = moves.get(0);
		int score = 0;
		for (Move m: moves) {
			MachineState nextState = stateMachine.getNextState(state, Arrays.asList(m));
			int currScore = this.stateValue(nextState, role);
			if (currScore > score) {
				score = currScore;
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
	
	
	/**
	 * The method recursively computes best possible reward that can be obtained from a state to a terminal state. 
	 * Recursive call without memoization: Could be costly!
	 * @param state
	 * @param role
	 * @return
	 * @throws TransitionDefinitionException
	 * @throws MoveDefinitionException
	 * @throws GoalDefinitionException
	 */
	private int stateValue(MachineState state, Role role) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		
		StateMachine stateMachine = getStateMachine();
		
		// Base case: If the state is terminal, then return it
		if (stateMachine.isTerminal(state)) {
			int score =  stateMachine.getGoal(state, role);
			return score;
		}
		
		List<Move> moves = stateMachine.getLegalMoves(state, role);
		int bestScore = 0;
		for (Move m : moves) {
			MachineState nextState = stateMachine.getNextState(state, Arrays.asList(m));
			int currScore = stateValue(nextState, role);
			if (currScore > bestScore) {
				bestScore = currScore;
			}
		}
		return bestScore;
		
	}
	
}