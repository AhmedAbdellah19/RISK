package agent;

import controller.Attack;
import graph.IContinent;
import graph.IGraph;
import graph.INode;

public class PacifistAgent implements IAgent {
	private AgentType agentType;
	private boolean player;
	private boolean lastTurnAttack;
	
	public PacifistAgent(AgentType agentType, boolean player) {
		this.agentType = agentType;
		this.player = player;
		this.lastTurnAttack = false;
		
	}
	
	@Override
	public AgentType getAgentType() {
		return this.agentType;
	}

	@Override
	public Attack attack(IGraph graph) {
		INode from = null, to = null;
		int soldiers = -1;
		for(IContinent continent : graph.getContinents()) {
			for(INode node: continent.getNodes()) {
				if(node.getOwnerType() == player) {
					for(INode neighbor: node.getNeighbors()) {
						if(neighbor.getOwnerType() == !player) {
							if(node.getSoldiers() - neighbor.getSoldiers() > 1) {
								if(from == null) {
									from = node;
									to = neighbor;
									soldiers = neighbor.getSoldiers()+1;
								} else {
									if(neighbor.getSoldiers() + 1 < soldiers) {
										from = node;
										to = neighbor;
										soldiers = neighbor.getSoldiers()+1;
									} else if ((neighbor.getSoldiers() + 1 == soldiers)
											&& (neighbor.getNodeId() < to.getNodeId())) {
										from = node;
										to = neighbor;
									}
								}
							}
						}
					}
				}
			}
		}
		if(from == null) {
			this.lastTurnAttack = false;
			return null;
		}
		this.lastTurnAttack = true;
		return new Attack(from, to, soldiers);
	}

	@Override
	public INode deploy(IGraph graph, int soldiers) {
		INode ret = graph.getContinents().get(0).getNodes().get(0);
		for(IContinent continent : graph.getContinents()) {
			for(INode node : continent.getNodes()) {
				if(node.getSoldiers() < ret.getSoldiers()) {
					ret = node;
				} else if( (node.getSoldiers() == ret.getSoldiers()) 
						&& (node.getNodeId() < ret.getNodeId())) {
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

	@Override
	public boolean lastTurnAttack() {
		return this.lastTurnAttack;
	}

}
