package graph;

import java.util.List;

public interface INode {
	
	public boolean getOwnerType();
	
	public void setOwnerType(boolean ownerType);
	
	public int getSoldiers();
	
	public void setSoldiers(int value);
	
	public int getId();
	
	public int getLastOccupied();
	
	public void setLastOccupied(int lastOccupied);
	
	public List<INode> getNeighbours();
	
	public boolean isNeighbour(INode node);
	
	public void addNeighbour(INode node);
	
	public IContinent getContinent();
	
	public void setContinent(IContinent continent);
}
