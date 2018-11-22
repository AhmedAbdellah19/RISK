package agent;

import controller.Attack;
import graph.IContinent;
import graph.IGraph;
import graph.INode;

public class PassiveAgent implements IAgent {
	private AgentType agentType;
	private boolean player;
	
	public PassiveAgent(AgentType agentType, boolean player) {
		this.agentType = agentType;
		this.player = player;
	}
	
	@Override
	public AgentType getAgentType() {
		return this.agentType;
	}

	@Override
	public Attack attack(IGraph graph) {
		return null;
	}

	@Override
	public INode deploy(IGraph graph, int soldiers) {
		INode ret = graph.getContinents().get(0).getNodes().get(0);
		for(IContinent continent : graph.getContinents()) {
			for(INode node : continent.getNodes()) {
				if(node.getSoldiers() < ret.getSoldiers()) {
					ret = node;
				} else if( (node.getSoldiers() == ret.getSoldiers()) 
						&& (node.getId() < ret.getId())) {
					ret = node;
				}
			}
		}
		return ret;
	}

	@Override
	public boolean getPlayer() {
		return this.player;
	}

}
