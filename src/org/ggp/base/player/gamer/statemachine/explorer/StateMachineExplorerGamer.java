package org.ggp.base.player.gamer.statemachine;

import org.ggp.base.player.gamer.statemachine.StateMachineGamer;

import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
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
		return null;
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