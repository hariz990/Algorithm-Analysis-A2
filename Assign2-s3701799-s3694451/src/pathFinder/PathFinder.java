package pathFinder;

import map.PathMap;
import map.Coordinate;

import java.util.List;

/**
 * @author Jeffrey Chan, Youhan Xia, Phuc Chu
 * RMIT Algorithms & Analysis, 2019 semester 1
 * <p>
 * Interface of a path finder algorithm.
 */
public interface PathFinder {
    /**
     * Find a shortest path for the map.
     */
    public abstract List<Coordinate> findPath();


    /**
     * Use after findPath(), counting the number of cells explored when finding the path.
     *
     * @return The number of coordinates explored.
     * It is not required to be accurate and no marks are given (or lost) for it.
     * For your own curiousity
     */
    public abstract int coordinatesExplored();
} // end of interface PathFinder
