package map;

import java.util.*;

import map.StdDraw;
import map.Coordinate;

/**
 * @author Jeffrey Chan, Youhan Xia, Phuc Chu
 * RMIT Algorithms & Analysis, 2019 semester 1
 * <p>
 * Class of a map (for path finding).
 * For the assignment it is used to read in information but also to visualise it.
 * It is a grid representation.
 */
public class PathMap
{
    /**
     * map properties
     */
    // number of rows
    public int sizeR;
    // number of columns
    public int sizeC;
    // 2D grid of cells
    public Coordinate cells[][] = null;
    // List of origin cells/coordinates
    public List<Coordinate> originCells;
    // list of destination cells/coordinates
    public List<Coordinate> destCells;
    // list of waypoint cells/coordinates
    public List<Coordinate> waypointCells;
    // whether to visualise or not
    public boolean isVisu = true;


    /**
     * Initialise the map.
     *
     * @param rowNum Number of rows.
     * @param colNum Number of columns.
     * @param oriCells List of origin coordinates.
     * @param desCells List of destination coordinates.
     * @param impassableCells List of impassable coordinates.
     * @param terrainCells Map of terrain coordinates and their costs.
     * @param waypointCells List of waypoint coordinates.
     */
    public void initMap(int rowNum, int colNum, List<Coordinate> oriCells, List<Coordinate> desCells, Set<Coordinate> impassableCells,
        Map<Coordinate, Integer> terrainCells, List<Coordinate> waypointCells)
    {
        // initialise parameters
        sizeR = rowNum;
        sizeC = colNum;
        originCells = oriCells;
        destCells = desCells;
        this.waypointCells = waypointCells;

        cells = new Coordinate[sizeR][sizeC];

        // construct the coordinates in the grid and also update inforamtion about impassable
        // and terrain costs.
        for (int i = 0; i < sizeR; i++) {
            for (int j = 0; j < sizeC; j++) {
                Coordinate coord = new Coordinate(i, j);
                // add impassable cells
                if (impassableCells.contains(coord)) {
                    coord.setImpassable(true);
                }
                // add terrain information
                // should not be both
                if (terrainCells.containsKey(coord)) {
                    int cost = terrainCells.get(coord).intValue();
                    coord.setTerrainCost(cost);
                }

                cells[i][j] = coord;
            }
        }
    } // end of initMap()


    //
    // Auxiliary functions
    //


    /**
     * Check whether coordinate (r, c) is in the map.
     *
     * @param r Row coordinate
     * @param c Column coordinate
     * @return True if in the maze. Otherwise false.
     */
    public boolean isIn(int r, int c) {
        return r >= 0 && r < sizeR && c >= 0 && c < sizeC;
    } // end of isIn()


    /**
     * Check whether the coordinate is in the map.
     *
     * @param coord The coordinate being checked.
     * @return True if in the map. Otherwise false.
     */
    public boolean isIn(Coordinate coord) {
        if (coord == null)
            return false;
        return isIn(coord.getRow(), coord.getColumn());
    } // end of isIn()


    /**
     * Check if a coordinate (r,c) is passable/can be traversed.
     */
    public boolean isPassable(int r, int c) {
        return isIn(r, c) && !cells[r][c].getImpassable();
    } // end of isPassable()


    /**
     * Draw the map in a window.
     */
    public void draw() {
        // draw nothing if visualization is switched off
        if (!isVisu)
            return;

        StdDraw.setCanvasSize(900, 900);
        StdDraw.setXscale(-1, sizeR + 1);
        StdDraw.setYscale(-1, sizeC + 1);
        StdDraw.setFont();

        // draw origins
        StdDraw.setPenColor(StdDraw.BLUE);
        Iterator<Coordinate> it = originCells.iterator();
        while (it.hasNext()) {
            Coordinate coord = it.next();
            StdDraw.filledCircle(coord.getColumn() + 0.5, coord.getRow() + 0.5, 0.375);
        }


        // draw destinations
        StdDraw.setPenColor(StdDraw.RED);
        it = destCells.iterator();
        while (it.hasNext()) {
            Coordinate coord = it.next();
            StdDraw.filledCircle(coord.getColumn() + 0.5, coord.getRow() + 0.5, 0.375);
        }


        // draw waypoitns
        StdDraw.setPenColor(StdDraw.ORANGE);
        it = waypointCells.iterator();
        while (it.hasNext()) {
            Coordinate coord = it.next();
            StdDraw.filledCircle(coord.getColumn() + 0.5, coord.getRow() + 0.5, 0.375);
        }


        // Draw coordinate boundaries
        StdDraw.setPenColor(StdDraw.BLACK);

        for (int r = 0; r < sizeR; r++) {
            for (int c = 0; c < sizeC; c++) {
                // System.out.println(cells[r][c]);
                StdDraw.line(c + 1, r, c + 1, r + 1);
                StdDraw.line(c, r + 1, c + 1, r + 1);
                StdDraw.line(c, r, c, r + 1);
                StdDraw.line(c, r, c + 1, r);
                // draw impassable cells
                if (cells[r][c].getImpassable()) {
                    StdDraw.filledSquare(c + 0.5, r + 0.5, 0.5);
                }
                // draw terrain costs
                if (cells[r][c].getTerrainCost() > 1) {
                    StdDraw.text(c + 0.5, r + 0.5, String.valueOf(cells[r][c].getTerrainCost()));
                }
            }
        }
    } // end of draw()


    /**
     * Draw the found shortest path from the origin to the destination
     *
     * @param path Path to be drawn.
     */
    public void drawPath(List<Coordinate> path) {
        // draw nothing if visualization is switched off
        if (!isVisu)
            return;

        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.setPenRadius(0.01);

        Iterator<Coordinate> it = path.iterator();
        Coordinate currCell = null;
        if (it.hasNext()) {
            currCell = it.next();
            StdDraw.filledEllipse(currCell.getColumn() + 0.5, currCell.getRow() + 0.5, 0.3, 0.5);
            while (it.hasNext()) {
                currCell = it.next();
                StdDraw.filledEllipse(currCell.getColumn() + 0.5, currCell.getRow() + 0.5, 0.3, 0.5);
            }
        }
    } // end of drawPath()

} // end of class PathMap
