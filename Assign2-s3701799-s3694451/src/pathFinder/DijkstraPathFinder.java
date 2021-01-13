package pathFinder;

import java.util.*;
import map.Coordinate;
import map.PathMap;

public class DijkstraPathFinder implements PathFinder
{
	private PathMap map;
	private int coordinatesExplored=0;
	private Coordinate[][] coordinates =null;
	private int[][] cost =null;

	//constructor initilaized with the map
	public DijkstraPathFinder(PathMap map)
    {
    	this.map=map;
    	setInitialValues(true);
    }

    @Override
    //finds the shortest path by using of waypoints, origins and destinations
    public List<Coordinate> findPath() 
    {
    	List<Coordinate> path=new ArrayList<Coordinate>();

    	//if we have waypoints(TaskD)
    	if(map.waypointCells.size()!=0)
    	{
    		findPathWithWaypoints(path);
    	}
    	//Task A, B, C
    	else
    	{
    		if(map.destCells.size()==1)//single destination Task A
    			calculateShortestPath(map.destCells.get(0));
    		else
    			calculateShortestPath(null);// multiple destination for Task C
    		//find shortest route
    		int value = Integer.MAX_VALUE;
    		for(Coordinate c : map.destCells)
    		{
    			List<Coordinate> temp=getShortestPath(c);
    			if(fetchTerrainCosts(temp)<value)
    			{
    				value= fetchTerrainCosts(temp);
    				path=temp;
    			}
    		}
    	}
        return path;
    }

    // calculate distance from source to waypoint , then waypoint to next waypoint and then finally to destination, each using shortest path
    private List<Coordinate> findPathWithWaypoints( List<Coordinate>  paths){
		//get intermediate paths to waypoints and add to path
		for(Coordinate c : map.waypointCells)
		{
			calculateShortestPath(c);
			if(paths.size()>0)
				paths.remove(paths.size()-1);
			paths.addAll(getShortestPath(c));
			setInitialValues(false);
			cost[c.getRow()][c.getColumn()]=0;
		}
		if(paths.size()>0)
			paths.remove(paths.size()-1);
		paths.addAll(getShortestPath(map.waypointCells.get(map.waypointCells.size()-1)));

		setInitialValues(false);
		Coordinate c=map.waypointCells.get(map.waypointCells.size()-1);
		cost[c.getRow()][c.getColumn()]=0;//set last waypoint as origin
		//set null to get routes for all destinations
		calculateShortestPath(null);

		//find closest destination from last waypoint and add to path
		List<Coordinate> destination=null;
		int destinationCost=Integer.MAX_VALUE;

		for(int i=0;i<map.destCells.size();++i)
		{
			List<Coordinate> tpath=getShortestPath(map.destCells.get(i));
			if(destination==null || fetchTerrainCosts(tpath)<destinationCost)
			{
				destination=tpath;
				destinationCost= fetchTerrainCosts(tpath);
			}
		}
		if(paths.size()>0)
			paths.remove(paths.size()-1);
		paths.addAll(destination);

		return paths;
	}

	//initialize weights and cell value
	private void setInitialValues(boolean setOriginsZero)
	{
		//initialize the cost array based oon the inital conditions
		if(cost ==null)
			cost =new int[map.sizeR][map.sizeC];
		for(int i=0;i<map.sizeR;++i)
			for(int j=0;j<map.sizeC;++j)
				cost[i][j]=Integer.MAX_VALUE;

		if(setOriginsZero)
			for(Coordinate c : map.originCells)
				cost[c.getRow()][c.getColumn()]=0;

		if(coordinates ==null)
			coordinates =new Coordinate[map.sizeR][map.sizeC];
		for(int i=0;i<map.sizeR;++i)
			for(int j=0;j<map.sizeC;++j)
				coordinates[i][j]=null;
	}

    //the core logic for the shortest path algorithm
    private void calculateShortestPath(Coordinate dest)
    {
    	List<Coordinate> unvisited= getAllUnvisitedCoorodinates();
        
    	while(unvisited.size()>0)
    	{
    		Coordinate curr= getMinimumDistanceOFAllCorordinates(unvisited);
    		unvisited.remove(curr);
    		coordinatesExplored++;
    		if(dest!=null && curr.equals(dest))
    		{
    			System.out.println("No path found between source and destination.");
    			break;
    		}
    		List<Coordinate> nbrs=notVisited(unvisited, getNeighbourOfCell(curr));
    		for(Coordinate n : nbrs)
    		{
    			int cost= this.cost[curr.getRow()][curr.getColumn()]+map.cells[n.getRow()][n.getColumn()].getTerrainCost();
    			if(cost< this.cost[n.getRow()][n.getColumn()])
    			{
    				this.cost[n.getRow()][n.getColumn()]=cost;
    				coordinates[n.getRow()][n.getColumn()]=curr;
    			}
    		}
    	}
    }

    // returns shortest path to specified destination by looking up coordinates array for each coord
    private List<Coordinate> getShortestPath(Coordinate destination)
    {
    	List<Coordinate> reverse = new ArrayList<Coordinate>();
    	
    	Coordinate curr=destination;
    	while(curr!=null)
    	{
    		reverse.add(curr);
    		curr= coordinates[curr.getRow()][curr.getColumn()];
    	}
    	List<Coordinate> path=new ArrayList<Coordinate>();
    	for(int i=reverse.size()-1;i>=0;i--)
    		path.add(reverse.get(i));
        return path;
    }
	
    //returns the coord with lowest distance/cost out of the given unvisited list
    private Coordinate getMinimumDistanceOFAllCorordinates(List<Coordinate> coordinates)
    {
    	Coordinate coordinate=coordinates.get(0);
    	for(Coordinate c : coordinates)
    		if(cost[c.getRow()][c.getColumn()]< cost[coordinate.getRow()][coordinate.getColumn()])
    			coordinate=c;
    	return coordinate;
    }

	//calculate path cost by adding the terrain cost if it has terrains
	private int fetchTerrainCosts(List<Coordinate> path)
	{
		int cost=0;
		for(Coordinate c: path)
			cost+=map.cells[c.getRow()][c.getColumn()].getTerrainCost();

		return cost;
	}

    //returns a list containing all passable coordinates in the map
    private List<Coordinate> getAllUnvisitedCoorodinates()
	{
    	List<Coordinate> unvisited=new ArrayList<Coordinate>();
    	for(int i=0;i<map.sizeR;++i)
    		for(int j=0;j<map.sizeC;++j)
    		{
    			Coordinate c=map.cells[i][j];
    			if(map.isPassable(i, j))
    				unvisited.add(c);
    		}
   		return unvisited;
	}
    //returns a list containing the nodes not been visited yet
    private List<Coordinate> notVisited(List<Coordinate> unvisited, List<Coordinate> coords)
    {
    	List<Coordinate> list=new ArrayList<Coordinate>();
    	for(int i=0;i<coords.size();++i)
    	{
    		Coordinate coordinate = coords.get(i);
    		if(unvisited.contains(coordinate))
    			list.add(coordinate);
    	}
    	return list;
    }
	
    //returns a list with the 4 neighbours of the specified coordinate
    private List<Coordinate> getNeighbourOfCell(Coordinate coordinate)
    {
    	List<Coordinate> neighbours = new ArrayList<Coordinate>();
    	
    	//add the upper neigbour of the cell
    	if(coordinate.getRow()>0 && map.isPassable(coordinate.getRow()-1,coordinate.getColumn()))
    		neighbours.add(map.cells[coordinate.getRow()-1][coordinate.getColumn()]);
    	//add the lower neighbour of the cell
    	if(coordinate.getRow()<map.sizeR-1 && map.isPassable(coordinate.getRow()+1,coordinate.getColumn()))
    		neighbours.add(map.cells[coordinate.getRow()+1][coordinate.getColumn()]);
    	//add the left neighbour of the cell
    	if(coordinate.getColumn()>0 && map.isPassable(coordinate.getRow(),coordinate.getColumn()-1))
    		neighbours.add(map.cells[coordinate.getRow()][coordinate.getColumn()-1]);
    	//add right
    	if(coordinate.getColumn()<map.sizeC-1 && map.isPassable(coordinate.getRow(),coordinate.getColumn()+1))
    		neighbours.add(map.cells[coordinate.getRow()][coordinate.getColumn()+1]);
    	
    	return neighbours;
    }

    // return final result with all the explored coordinates
    @Override
    public int coordinatesExplored() 
    {
        return coordinatesExplored;
    }
}