package org.ggp.base.player.gamer.statemachine.explorer;

import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;

import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.MachineState;

import java.util.List;

public class AlphaBetaGamer extends MinMaxGamer {

	public String getName()
	{
		return "RamSud: AlphaBeta";
	}

	/**
	 * This method iterate over all possible actions of the opposition player and finds the "best" we could do if the opposition 
	 * had taken that action and we had taken move m. 
	 * @param state
	 * @param role
	 * @param move
	 * @param alpha: Lower bound on the score obtained from every child state
	 * @param beta: Upper bound on score from every child state
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
		for (List<Move> legalMove : allLegalMoves) {
			MachineState nextState = stateMachine.getNextState(state, legalMove);
			// Check if already visited
			if (maxExplored.contains(nextState)) continue;
			int score = maxScore(nextState, role, alpha, beta, level);
			if (score < beta) beta = score;
			if (alpha >= beta) return alpha; 
		}
		return beta;
	}


	/**
	 * This method iterates over all possible my actions in a given state and finds best possible score among them. 
	 * had taken that action and we had taken move m. 
	 * @param state
	 * @param role
	 * @param alpha: Lower bound on the score obtained from every child state
	 * @param beta: Upper bound on score from every child state
	 * @param level: Current level of the tree being explored
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
		for (Move m : moves) {
			int score = minScore(state, role, m, alpha, beta, level);
			if (score > alpha) alpha = score;
			if (alpha >= beta) return beta;
		}
		
		// Remove the state from maxExplored
		maxExplored.remove(state);

		// Cache update
		maxScoreMap.put(state, alpha);

		return alpha;
	}
}
