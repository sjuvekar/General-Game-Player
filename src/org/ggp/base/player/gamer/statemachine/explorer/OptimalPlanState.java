package org.ggp.base.player.gamer.statemachine.explorer;

import org.ggp.base.util.statemachine.MachineState;

/**
 * @author sjuvekar
 * This class represents the small state data structure that keeps a pair of next state and reward
 * for the optimal exploration plan. 
 */
class OptimalPlanState {
	 
	private final MachineState nextState;
	private final int reward;
	 
	public OptimalPlanState(MachineState n, int r) {
		this.nextState = n;
		this.reward = r;
	}
	
	public MachineState getNextState() {
		return nextState;
	}
	
	public int getReward() {
		return reward;
	}
}