package org.ggp.base.player.gamer.statemachine.explorer;

import java.util.List;
import java.util.Random;

import org.ggp.base.player.gamer.statemachine.StateMachineGamer;

import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.player.gamer.exception.GamePreviewException;

import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;
import org.ggp.base.apps.player.detail.SimpleDetailPanel;

/**
* My attempt at making a GGP based game using state machines
*/
	
public class StateMachineExplorerGamer extends StateMachineGamer {
	/**
	 * Defines the metagaming action taken by a player during the START_CLOCK
	 * @param timeout time in milliseconds since the era when this function must return
	 * @throws TransitionDefinitionException
	 * @throws MoveDefinitionException
	 * @throws GoalDefinitionException
	 */    
	public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{

	}

	/**
	 * Defines the algorithm that the player uses to select their move.
	 * @param timeout time in milliseconds since the era when this function must return
	 * @return Move - the move selected by the player
	 * @throws TransitionDefinitionException
	 * @throws MoveDefinitionException
	 * @throws GoalDefinitionException
	 */
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		// TODO
		// We get the current start time
		long start = System.currentTimeMillis();

		/**
		 * We put in memory the list of legal moves from the 
		 * current state. The goal of every stateMachineSelectMove()
		 * is to return one of these moves. The choice of which
		 * Move to play is the goal of GGP.
		 */
		List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());

		// Move selection = moves.get(0);
		Move selection = (moves.get(new Random().nextInt(moves.size())));
		
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
	 * Name of my game
	 */
	public String getName()
	{
		return "RamSud v1.0";
	}
	
	/**
	 * This is returned using tutorial at GGP website
	 */
	public StateMachine getInitialStateMachine() 
	{
		return new ProverStateMachine();
	}
	
	/**
	 * This is returned using tutorial at GGP website
	 */
	public DetailPanel getDetailPanel() {
		return new SimpleDetailPanel();
	}
	
	/**
	 * This is returned using tutorial at GGP website
	 */
	public void stateMachineAbort() {
		
	}

	/**
	 * This is returned using tutorial at GGP website
	 */
	public void stateMachineStop() {

	}
	
	/**
	 * This is returned using tutorial at GGP website
	 */
	public void preview(Game g, long timeout) throws GamePreviewException 
	{
	
	}
}