package agent;

import java.util.ArrayList;
import java.util.Collections;

import controller.Attack;
import graph.IGraph;
import graph.INode;
import state.State;
import state.StateContainer;

public class GreedyAgent implements IAgent {
	private AgentType agentType;
	private boolean player;
	private boolean lastTurnAttack;

	// The best sequence of moves found by the search algorithm
	private int deployIndex, attackIndex;
	private ArrayList<INode> deploySequence;
	private ArrayList<Attack> attackSequence;

	public GreedyAgent(AgentType agentType, boolean player, IGraph graph) {
		this.agentType = agentType;
		this.player = player;
		this.lastTurnAttack = false;

		deployIndex = attackIndex = 0;
		deploySequence = new ArrayList<INode>();
		attackSequence = new ArrayList<Attack>();

		preprocess(graph);
	}

	@Override
	public AgentType getAgentType() {
		return this.agentType;
	}

	@Override
	public Attack attack(IGraph graph) {
		return attackSequence.get(attackIndex++);
	}

	@Override
	public INode deploy(IGraph graph, int soldiers) {
		return deploySequence.get(deployIndex++);
	}

	@Override
	public boolean getPlayer() {
		return player;
	}

	@Override
	public boolean lastTurnAttack() {
		return lastTurnAttack;
	}

	private void preprocess(IGraph graph) {
		StateContainer frontier = new StateContainer();
		StateContainer visited = new StateContainer();
		frontier.add(getInitialState(graph));
		while (!frontier.isEmpty()) {
			State cur = frontier.getMin();
			if (cur.gameOver()) {
				buildPlayStrategy(cur, graph);
				return;
			}
			visited.add(cur);

			ArrayList<State> neighbours = getNeighbours(cur, graph);
			for (State s : neighbours)
				if (!frontier.exists(s) && !visited.exists(s)) {
					s.setParent(cur);
					frontier.add(s);
				}
			// This is useless in greedy with this heuristic (number of enemy nodes)
			// else if(frontier.exists(s))
			// frontier.decreaseKey(s, s.getCost());
		}
	}

	// Makes 2 moves, one for greedy and one for passive
	private ArrayList<State> getNeighbours(State cur, IGraph graph) {
		ArrayList<Integer> p1Nodes = new ArrayList<Integer>();
		for (int i = 0; i < cur.getNodeOwner().size(); ++i)
			if (!cur.getNodeOwner().get(i))
				p1Nodes.add(i);

		int bonus = Math.max(3, p1Nodes.size() / 3) + getContinentBonus(p1Nodes, graph);
		if (cur.getLastAttackSoldiers() > 0)
			bonus += 2;

		int cost = graph.getNodes().size() - p1Nodes.size();

		ArrayList<State> ret = new ArrayList<State>();
		for (int deployNode = 0; deployNode < graph.getNodes().size(); ++deployNode) {
			// Skip Attack
			ArrayList<Integer> newSoldiers = (ArrayList<Integer>) cur.getSoldiers().clone();
			ArrayList<Boolean> newOwners = (ArrayList<Boolean>) cur.getNodeOwner().clone();
			newSoldiers.set(deployNode, newSoldiers.get(deployNode) + bonus);
			State newState = new State(cost, newSoldiers, newOwners, deployNode, 0, 0, 0);
			ret.add(newState);

			// Attack
			for (int i : p1Nodes) {
				INode from = graph.getNodeById(i);
				for (INode to : from.getNeighbours()) {
					if (from.getOwnerType() == to.getOwnerType())
						continue;
					if (from.getSoldiers() - to.getSoldiers() <= 1)
						continue;
					for (int soldiers = 1; soldiers < from.getSoldiers() - to.getSoldiers(); ++soldiers) {
						// TODO
					}
				}
			}
		}
		return ret;
	}

	private int getContinentBonus(ArrayList<Integer> p1Nodes, IGraph graph) {
		// TODO Auto-generated method stub
		return 0;
	}

	private void buildPlayStrategy(State lastState, IGraph graph) {
		while (lastState != null) {
			deploySequence.add(graph.getNodeById(lastState.getLastDeploy()));
			attackSequence.add(new Attack(graph.getNodeById(lastState.getLastAttackFrom()),
					graph.getNodeById(lastState.getLastAtatckTo()), lastState.getLastAttackSoldiers()));
			lastState = lastState.getParent();
		}
		Collections.reverse(deploySequence);
		Collections.reverse(attackSequence);
	}

	private State getInitialState(IGraph graph) {
		int cost = 0;
		ArrayList<Integer> soldiers = new ArrayList<Integer>();
		ArrayList<Boolean> ownerTypes = new ArrayList<Boolean>();
		for (INode node : graph.getNodes()) {
			if (node.getOwnerType())
				++cost;
			soldiers.add(node.getSoldiers());
			ownerTypes.add(node.getOwnerType());
		}
		State ret = new State(cost, soldiers, ownerTypes, 0, 0, 0, 0);
		return ret;
	}

}
