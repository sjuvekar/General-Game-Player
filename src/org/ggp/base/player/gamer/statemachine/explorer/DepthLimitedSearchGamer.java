package org.ggp.base.player.gamer.statemachine.explorer;

import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;

import org.ggp.base.player.gamer.statemachine.explorer.heuristic.MobilityHeuristic;
import org.ggp.base.player.gamer.statemachine.explorer.heuristic.GoalProximityHeuristic;

import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.MachineState;

import java.util.List;

public class DepthLimitedSearchGamer extends MinMaxGamer {
	
	/**
	 * This is the maximum possible tree depth to be used for bounded search
	 */
	public static int S_MAX_TREE_DEPTH = 3;

	/**
	* Save progress to avoid recursion
	**/
	

	public String getName()
	{
		return "RamSud: Depth Limited Search";
	}

	/**
	 * This method is like the basic alpha-beta gamer except that it is cut off and minimum score is returned when the level increases max depth.
	 * And max_level is incremented before passing to the next recursion. 
	 * @param state
	 * @param role
	 * @param move
	 * @param alpha: Lower bound on the score obtained from every child state
	 * @param beta: Upper bound on score from every child state
	 * @param level: Current level of the tree being explored
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
			int score = maxScore(nextState, role, alpha, beta, level+1);
			if (score < beta) beta = score;
			if (alpha >= beta) return alpha; 
		}
		return beta;
	}


	/**
	 * This method is like the basic alpha-beta gamer except that it is cut off and minimum score is returned when the level increases max depth.  
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
		// Get the stateMachine
		StateMachine stateMachine = getStateMachine();
		// If the state is terminal, return the goal
		if (stateMachine.isTerminal(state)) {
			int score =  stateMachine.getGoal(state, role);
			return score;
		}

		if (level >= S_MAX_TREE_DEPTH) {
			return evalFunction(state, role, stateMachine);
		}
		
		// Otherwise, for every move that the player can potentially take, find the worst score. Find their best
		List<Move> moves = stateMachine.getLegalMoves(state, role);
		for (Move m : moves) {
			int score = minScore(state, role, m, alpha, beta, level);
			if (score > alpha) alpha = score;
			if (alpha >= beta) return beta;
		}
		return alpha;
	}
	
	/**
	 * Returns the eval function for limit-exceed case
	 */
	private int evalFunction(MachineState state, Role role, StateMachine stateMacine) throws GoalDefinitionException, MoveDefinitionException {
		return new MobilityHeuristic().eval(state, role, stateMacine);
	}
}
