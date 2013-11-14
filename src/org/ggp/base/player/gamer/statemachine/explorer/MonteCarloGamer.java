package org.ggp.base.player.gamer.statemachine.explorer;

import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;

import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.MachineState;

import java.util.List;
import java.util.HashSet;
import java.util.Random;
import java.util.Collections;

public class MonteCarloGamer extends DepthLimitedSearchGamer {

	/**
	 * This static variable limits the number of random tries from a maxScore level
	 */
	public static int S_MAX_RANDOM_TRIES = 2;
	
	/**
	 * This variabel limits the number of neighbors to search during maxScore (breadth limit)
	 */
	public static int S_MAX_NEIGHBORS = 2;
	/**
	 * this HashSet stores all visited states in random exploration. Don't visit same state again.
	 */
	private HashSet<MachineState> visitedStates;
	
	public String getName()
	{
		return "RamSud: Monte Carlo";
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
		System.out.println(level);
		// Get the stateMachine
		StateMachine stateMachine = getStateMachine();
		// If the state is terminal, return the goal
		if (stateMachine.isTerminal(state)) {
			int score =  stateMachine.getGoal(state, role);
			return score;
		}

		if (level >= DepthLimitedSearchGamer.S_MAX_TREE_DEPTH) {
			return monteCarlo(state, role, stateMachine);
		}
		
		// Otherwise, for every move that the player can potentially take, find the worst score. Find their best
		List<Move> moves = stateMachine.getLegalMoves(state, role);
		
		int count = 0;
		for (Move m : moves) {
			int score = minScore(state, role, m, alpha, beta, level);
			if (score > alpha) alpha = score;
			if (alpha >= beta) return beta;
			count++;
			if (count >= S_MAX_NEIGHBORS) break;
		}
		return alpha;
	}
	
	
	/** 
	 * This method performs average of random moves from current state
	 * @param state
	 * @param role
	 * @paramm stateMachine
	 * @throws TransitionDefinitionException
	 * @throws MoveDefinitionException
	 * @throws GoalDefinitionException
	 */
	private int monteCarlo(MachineState state, Role role, StateMachine stateMachine) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		double total = 0.0;
		for (int i = 0; i < S_MAX_RANDOM_TRIES; i++) {
			visitedStates = new HashSet<MachineState>();
			total += depthCharge(state, role, stateMachine);
		}
		return (int)(total/S_MAX_RANDOM_TRIES);
	}
	
	
	/** 
	 * This method keeps on making random moves till we hit the end state. In that case, it returns the score of the state.
	 * @param state
	 * @param role
	 * @paramm stateMachine
	 * @throws TransitionDefinitionException
	 * @throws MoveDefinitionException
	 * @throws GoalDefinitionException
	 */
	private int depthCharge(MachineState state, Role role, StateMachine stateMachine) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		MachineState currState = state;
		while (!stateMachine.isTerminal(currState)) {
			List<MachineState> allNextStates = stateMachine.getNextStates(currState);
			allNextStates.removeAll(visitedStates);
			if (allNextStates.isEmpty())
				return 0;
			visitedStates.add(currState);
			Collections.shuffle(allNextStates); 
			currState = allNextStates.get(0);
		}
		System.out.println(currState);
		return stateMachine.getGoal(currState, role);
	}
}
