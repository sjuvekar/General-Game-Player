package org.ggp.base.player.gamer.statemachine.explorer;

import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.StateMachine;

import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;

import org.ggp.base.util.gdl.grammar.GdlPool;

import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class represents the sequence of moves that can be taken starting the current state
 * in the game tree. 
 */
public class SequentialPlan {
	
	// This map contains the actual plan - a unique move for every state
	private HashMap<MachineState, Move> plan;
	
	// The map contains the reward for each state obtained when executing the current plan
	private HashMap<MachineState, Integer> planReward;
	
	// This Set keeps track of currently visited states during state-space exploration. Helps avoid visiting same state again
	private HashSet<MachineState> visitedStates;

	
	public SequentialPlan() {
		plan = new HashMap<MachineState, Move>();
		planReward = new HashMap<MachineState, Integer>();
		visitedStates = new HashSet<MachineState>();
	}
	
	// Getters
	public HashMap<MachineState, Move> getPlan() { 
		return new HashMap<MachineState, Move>(plan); 
	}
	public HashMap<MachineState, Integer> getPlanReward() { 
		return new HashMap<MachineState, Integer>(planReward); 
	}
	public HashSet<MachineState> getVisitedStates() { 
		return new HashSet<MachineState>(visitedStates); 
	}
	
	
	/**
	 * This method performs a DFS on state space and modifies the plan and planReward maps to give the best possible 
	 * action and its reward for every reachable state.
	 * @param stateMachine
	 * @param state
	 * @param role
	 * @return
	 * @throws TransitionDefinitionException
	 * @throws MoveDefinitionException
	 * @throws GoalDefinitionException
	 */
	public void explore(StateMachine stateMachine, MachineState state, Role role) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		// Check if the state is terminal. In that case, add the appropriate state and its reward to the HashMaps.
		if (stateMachine.isTerminal(state)) {
			plan.put(state, new Move(GdlPool.getConstant("noop")));
			planReward.put(state, stateMachine.getGoal(state, role));
			return;
		}
		
		// Iterate over possible moves and check for the best successor.
		// First put the current state in the visitedStates.
		visitedStates.add(state);
		List<Move> moves = stateMachine.getLegalMoves(state, role);
		Move selection = moves.get(0);
		int bestReward = 0;
		for (Move m : moves) {
			// Find next state. 
			MachineState nextState = stateMachine.getNextState(state, Arrays.asList(m));
			// There are multiple cases. 
			// If the state is being visited but not found in hashmap, that means there is a cycle. Don't recurse in that case.
			if (visitedStates.contains(nextState) && !plan.containsKey(nextState) && !planReward.containsKey(nextState)) {
				continue;
			}
			// If the state is in neither the recurse.
			if (!visitedStates.contains(nextState) && !plan.containsKey(nextState) && !planReward.containsKey(nextState)) {
				explore(stateMachine, nextState, role);
			}
			// At this point, the hashmap is guaranteed to contain the state.
			assert(plan.containsKey(nextState) && planReward.containsKey(nextState));
			int reward = planReward.get(nextState);
			if (reward > bestReward) {
				bestReward = reward;
				selection = m;
			}
		}
		// Done. Remove the state from the visitedStates and add it to plan hashmaps.
		visitedStates.remove(state);
		plan.put(state, selection);
		planReward.put(state, bestReward);
	}
	
}
