package org.ggp.base.player.gamer.statemachine.explorer;

import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;

import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;

import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.MachineState;

import java.util.List;
import java.util.ArrayList;

public class MinMaxGamer extends StateMachineExplorerGamer {
	
	/**
	 * This is the max possible score on any branch of minmax game
	 */
	public static int S_MAX_SCORE = 100;
	
	public String getName()
	{
		return "RamSud: MinMax";
	}
	
	/**
	 * This player iterates over all moves by the player. For every move, it finds the worst possible score ever obtained (min part)
	 * It compares the worst possible scores of all moves and finds the best of them (max part).
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

		// Minimum possible score on any branch
		int minPossibleScore = 0;

		// For each move by current player, find worst possible reward.
		List<Move> moves = stateMachine.getLegalMoves(state, role);
		Move selection = moves.get(0);
		for (Move m: moves) {
			int score = minScore(state, role, m);
			// If worst score is max score, we have found optimal move. Do not recurse further.
			if (score >= S_MAX_SCORE) {
				selection = m;
				break;
			}
			if (score > minPossibleScore) {
				minPossibleScore = score;
				selection = m;
			}
		}
		
		// We get the end time
		// It is mandatory that stop<timeout
		long stop = System.currentTimeMillis();

		notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
		return selection;
		
	}
	
	
	/**
	 * This method iterate over all possible actions of the opposition player and finds the "best" we could do if the opposition 
	 * had taken that action and we had taken move m. 
	 * @param state
	 * @param role
	 * @param m
	 * @return minimum possible score obtained for role in given state after taking move m.
	 * @throws TransitionDefinitionException
	 * @throws MoveDefinitionException
	 * @throws GoalDefinitionException
	 */
	private int minScore(MachineState state, Role role, Move m) throws MoveDefinitionException
	{
		StateMachine stateMachine = getStateMachine();
		
		// Get all roles in the game
		List<Role> allRoles = stateMachine.getRoles();
		
		List<Move> allPlayerMoves = new ArrayList<Move>();
		for (Role r : allRoles)
			if (r.equals(role))
				allPlayerMoves.add(m);
			else 
				allPlayerMoves.add(stateMachine.getLegalMoves(state, r).get(0));
	}
	
}
