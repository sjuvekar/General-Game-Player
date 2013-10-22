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
import java.util.HashMap;
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
		
		OptimalPlanState optimalPlanState = this.nextMove(state, 
														  new HashMap<MachineState, OptimalPlanState>(),
														  new HashMap<MachineState, Boolean>());
		
		List<Move> moves = stateMachine.getLegalMoves(state, role);
		Move selection = moves.get(0);
		for (Move m: moves) {
			MachineState nextState = stateMachine.getNextState(state, Arrays.asList(m));
			if (nextState == optimalPlanState.getNextState()) {
				selection = m;
				break;
			}
		}
		
		System.out.println("################");
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
	
	
	private OptimalPlanState nextMove(MachineState currState, 
								  HashMap<MachineState, OptimalPlanState> rewards,
								  HashMap<MachineState, Boolean> visitedStates) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		
		// First add the current move to visited list
		if (!visitedStates.containsKey(currState))
			visitedStates.put(currState, true);		
				
		System.out.println(rewards.size() + " " + visitedStates.size());
		// Get the current state machine
		StateMachine stateMachine = getStateMachine();
		
		// Get current role
		Role role = getRole();
		
		// Base case: If the state is terminal, then return it
		if (stateMachine.isTerminal(currState)) {
			int score = stateMachine.getGoal(currState, role);
			OptimalPlanState optimalPlanState = new OptimalPlanState(currState, score); 
			rewards.put(currState, optimalPlanState);
			return optimalPlanState;
		}
		
		// Recursion terminates: If this state is already explored, return stored value
		if (rewards.containsKey(currState))
			return rewards.get(currState);
		
		// Brand new state. Iterate over all its neighbors and see which is the best.
		List<Move> moves = stateMachine.getLegalMoves(currState, role);
		OptimalPlanState optimalPlanState = null;
		int bestReward = -1;
		for (Move m: moves) {
			MachineState nextState = stateMachine.getNextState(currState, Arrays.asList(m));
			if (visitedStates.containsKey(nextState)) 
				continue;
			OptimalPlanState currOptimalState = null;
			if (rewards.containsKey(nextState))
				currOptimalState = rewards.get(nextState);
			else 
				currOptimalState = nextMove(nextState, rewards, visitedStates);
			
			System.out.println(currOptimalState);
			int reward = currOptimalState.getReward(); 
			if (reward > bestReward) {
				bestReward = reward;
				optimalPlanState = new OptimalPlanState(nextState, reward);
			}
		}
		rewards.put(currState, optimalPlanState);
		return optimalPlanState;
	}
	
}