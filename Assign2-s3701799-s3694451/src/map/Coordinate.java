package map;

import java.util.*;

public class Coordinate 
{
    protected int r;

    protected int c;

    protected boolean isImpassable;

    protected int terrainCost;


    public Coordinate(int r, int c) 
    {
        this(r, c, false);
    }
    public Coordinate(int r, int c, boolean b) 
    {
        this.r = r;
        this.c = c;
        this.isImpassable = b;
        this.terrainCost = 1;
    }

    public Coordinate() 
    {
        this(0, 0);
    }

    
    public int getRow() { return r; }

    public int getColumn() { return c; }


    public void setImpassable(boolean impassable) 
    {
        isImpassable = impassable;
    }

    public boolean getImpassable() { return isImpassable; }

    public void setTerrainCost(int cost) 
    {
        terrainCost = cost;
    }

    public int getTerrainCost() { return terrainCost; }


    @Override
    public boolean equals(Object o) 
    {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Coordinate coord = (Coordinate) o;
        return r == coord.getRow() && c == coord.getColumn();
    }

    @Override
    public int hashCode() 
    {
        return Objects.hash(r, c);
    }

    @Override
    public String toString() 
    {
        return "(" + r + "," + c + "), " + isImpassable + ", " + terrainCost;
    }
}