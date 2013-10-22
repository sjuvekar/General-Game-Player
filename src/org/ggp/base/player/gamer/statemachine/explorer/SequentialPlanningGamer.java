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

/**
 * This class implements sequential planning DFS action strategy: Precompute all best next states for every state.
 */
public class SequentialPlanningGamer extends StateMachineExplorerGamer {
	
	/**
	 * This members keeps track of the sequential plan for the game.
	 */
	private SequentialPlan sequentialPlan;
	
	/**
	 * Constructor should initialize the plan
	 */
	public SequentialPlanningGamer() {
		super();
		sequentialPlan = new SequentialPlan();
	}
	
	public String getName()
	{
		return "RamSud: Sequential Planning";
	}
	
	/**
	 * Metagaming explores the sequential plan from initial state.
	 * @param timeout time in milliseconds since the era when this function must return
	 * @throws TransitionDefinitionException
	 * @throws MoveDefinitionException
	 * @throws GoalDefinitionException
	 */    
	public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		StateMachine stateMachine = getStateMachine();
		Role role = getRole();
		MachineState state = stateMachine.getInitialState();
		sequentialPlan.explore(stateMachine, state, role);
	}
	
	/**
	 * This player simply returns the best next state stored in current state
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
		
		// Get the HashMap from sequential Plan.
		HashMap<MachineState, Move> plan = sequentialPlan.getPlan();
		
		Move selection = plan.get(state);
		
		// We get the end time
		// It is mandatory that stop<timeout
		long stop = System.currentTimeMillis();

		/**
		 * These are functions used by other parts of the GGP codebase
		 * You shouldn't worry about them, just make sure that you have
		 * moves, selection, stop and start defined in the same way as 
		 * this example, and copy-paste these two lines in your player
		 */
		notifyObservers(new GamerSelectedMoveEvent(Arrays.asList(selection), selection, stop - start));
		return selection;
	}
	
}