package org.ggp.base.player.gamer.statemachine.explorer;

import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.player.gamer.statemachine.explorer.StateMachineExplorerGamer;

import org.ggp.base.util.gdl.grammar.GdlPool;
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
 * This class implements sequential planning DFS action strategy: Precompute all best next states for every state.
 */
public class SequentialPlanningGamer extends StateMachineExplorerGamer {
	
	public String getName()
	{
		return "RamSud: Sequential Planning";
	}
	
	/**
	 * Defines the metagaming action taken by a player during the START_CLOCK
	 * @param timeout time in milliseconds since the era when this function must return
	 * @throws TransitionDefinitionException
	 * @throws MoveDefinitionException
	 * @throws GoalDefinitionException
	 */    
	public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		setBestPlan(getStateMachine().getInitialState());
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
		
		if (state.getBestMove() == null) {
			setBestPlan(state);
		}
		
		Move selection = state.getBestMove();
		
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
	
	/**
	 * This method performs the recursive DFS on state space to find the best next move and best score for every state.
	 * @param state
	 * @throws TransitionDefinitionException
	 * @throws MoveDefinitionException
	 * @throws GoalDefinitionException
	 */
	private void setBestPlan(MachineState state) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		StateMachine stateMachine = getStateMachine();
		Role myRole = getRole();
		if (state.getBestMove() != null) {
			return;
		}
		if (stateMachine.isTerminal(state)) {
			state.setBestScore(stateMachine.getGoal(state, myRole));
			Move noop = new Move(GdlPool.getConstant("noop"));
			state.setBestMove(noop);
			return;
		}
		List<Move> moves = stateMachine.getLegalMoves(state, myRole);
		state.setBestMove(moves.get(0));
		state.setBestScore(0);
		for (Move m: moves) {
			MachineState nextState = stateMachine.getNextState(state, Arrays.asList(m));
			setBestPlan(nextState);
			if (nextState.getBestScore() > state.getBestScore()) {
				state.setBestMove(m);
				state.setBestScore(nextState.getBestScore());
			}
		}
	}
}