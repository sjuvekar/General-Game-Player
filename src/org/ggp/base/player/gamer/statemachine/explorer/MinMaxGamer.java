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
import java.util.HashMap;
import java.util.HashSet;

public class MinMaxGamer extends StateMachineExplorerGamer {
	
	/**
	 * This is the max possible score on any branch of minmax game
	 */
	public static int S_MAX_SCORE = 100;
	
	/**
	 * This is the min possible score on any branch of minmax game
	 */
	public static int S_MIN_SCORE = 0;
	
	/**
	 * These hashtables maintain the cache for new states
	 */
	protected HashMap<MachineState, Integer> minScoreMap, maxScoreMap;
	
	/**
	 * These sets maintain currently explored states 
	 */
	protected HashSet<MachineState> minExplored, maxExplored;
	
	
	public MinMaxGamer() {
		minScoreMap = new HashMap<MachineState, Integer>();
		maxScoreMap = new HashMap<MachineState, Integer>();
		minExplored = new HashSet<MachineState>();
		maxExplored = new HashSet<MachineState>();
	}
	
	
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
		int minPossibleScore = S_MIN_SCORE;

		// Initialize the HashSets
		minExplored = new HashSet<MachineState>();
		maxExplored = new HashSet<MachineState>();
		
		// For each move by current player, find worst possible reward.
		List<Move> moves = stateMachine.getLegalMoves(state, role);
		Move selection = moves.get(0);
		for (Move m: moves) {
			int score = minScore(state, role, m, S_MIN_SCORE, S_MAX_SCORE, 0);
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
	 * @param move
	 * @param alpha: ignored
	 * @param beta: ignored
	 * @param level: ignored
	 * @return minimum possible score obtained for role in given state after taking move by role and any other move by any other role.
	 * @throws TransitionDefinitionException
	 * @throws MoveDefinitionException
	 * @throws GoalDefinitionException
	 */
	protected int minScore(MachineState state, Role role, Move move, int alpha, int beta, int level) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		StateMachine stateMachine = getStateMachine();
		
		// Get all possible legal moves from state with move for role fixed
		List<List<Move>> allLegalMoves = stateMachine.getLegalJointMoves(state, role, move);
		
		// Next, for each legal move combination, find the worst possible score.
		int bestScore = S_MAX_SCORE;
		for (List<Move> legalMove : allLegalMoves) {
			MachineState nextState = stateMachine.getNextState(state, legalMove);
			// Check if already visited
			if (maxExplored.contains(nextState)) continue;
			int score = maxScore(nextState, role, alpha, beta, level);
			if (score == S_MIN_SCORE) return score;
			if (score < bestScore) bestScore = score;
		}
		return bestScore;
	}
	
	
	/**
	 * This method iterates over all possible my actions in a given state and finds best possible score among them. 
	 * had taken that action and we had taken move m. 
	 * @param state
	 * @param role
	 * @param alpha: ignored
	 * @param beta: ignored
	 * @param level: ignored
	 * @return maximum possible score obtained for role in given state after taking all possible moves.
	 * @throws TransitionDefinitionException
	 * @throws MoveDefinitionException
	 * @throws GoalDefinitionException
	 */

	protected int maxScore(MachineState state, Role role, int alpha, int beta, int level) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		// Cache check
		if (maxScoreMap.containsKey(state)) 
			return maxScoreMap.get(state);
	
		// Add the state to currently explored
		maxExplored.add(state);
		
		// Get the stateMachine
		StateMachine stateMachine = getStateMachine();
		// If the state is terminal, return the goal
		if (stateMachine.isTerminal(state)) {
			int score =  stateMachine.getGoal(state, role);
			return score;
		}
		
		// Otherwise, for every move that the player can potentially take, find the worst score. Find their best
		List<Move> moves = stateMachine.getLegalMoves(state, role);
		int bestScore = S_MIN_SCORE;
		for (Move m : moves) {
			int score = minScore(state, role, m, alpha, beta, level);
			if (score == S_MAX_SCORE) return score;
			if (score > bestScore) bestScore = score;
		}
		
		// Remove the state from maxExplored
		maxExplored.remove(state);
		
		// Cache update
		maxScoreMap.put(state, bestScore);
		return bestScore;
	}
	
}
