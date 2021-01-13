
import java.io.*;
import java.util.*;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import map.*;
import pathFinder.*;


/**
 * @author Jeffrey Chan, Youhan Xia, Phuc Chu
 * RMIT Algorithms & Analysis, 2019 semester 1
 * <p>
 * Main class for testing the maze generators and solvers.
 */
class PathFinderTester
{
    /**
     * Name of class, used in error messages.
     */
    protected static final String progName = "PathFindingTester";

    /**
     * Standard outstream.
     */
    protected static final PrintStream outStream = System.out;

    /**
     * Print help/usage message.
     */
    public static void usage(String progName) {
        System.err.println(progName + ": [-v -t: -w: -o:] <parameter fileName>");
        System.err.println("options are: ");
        System.err.println("-v ");
        System.err.println("-t <terrain parameter filename> ");
        System.err.println("-w <waypoint parameter filename> ");
        System.err.println("-o <path output filename> ");
        System.err.println("-v will activate map and path visualisation.");
        System.exit(1);
    } // end of usage

    /**
     * Main function of tester.
     *
     * @param args Two arguments which are input filename and "y/n" indicating whether to visualize the maze.
     */
    public static void main(String[] args) {

        //
        // parse command line options
        //

        OptionParser parser = new OptionParser("o:vt:w:");
        OptionSet options = parser.parse(args);

        String outputFilename = null;
        boolean isVisu = false;
        String terrainFilename = null;
        String waypointFilename = null;

        // -o <inputFilename> specifies the file that stores the shortest path results (optional)
        if (options.has("o")) {
            if (options.hasArgument("o")) {
                outputFilename = (String) options.valueOf("o");
            }
            else {
                System.err.println("Missing filename argument for -o option.");
                usage(progName);
            }
        }
        // -v to visualise graph
        if (options.has("v")) {
            isVisu = true;
        }
        // -t <terrain filename> specifies the (optional) terrain parameter filename
        if (options.has("t")) {
            if (options.hasArgument("t")) {
                terrainFilename = (String) options.valueOf("t");
            }
            else {
                System.err.println("Missing filename argument for -t option.");
                usage(progName);
            }
        }
        // -w <terrain filename> specifies the (optional) terrain parameter filename
        if (options.has("w")) {
            if (options.hasArgument("w")) {
                waypointFilename = (String) options.valueOf("w");
            }
            else {
                System.err.println("Missing filename argument for -w option.");
                usage(progName);
            }
        }


        // non option arguments
        List<?> tempArgs = options.nonOptionArguments();
        List<String> remainArgs = new ArrayList<String>();
        for (Object object : tempArgs) {
            remainArgs.add((String) object);
        }

        // check number of non-option command line arguments
        if (remainArgs.size() != 1) {
            System.err.println("Incorrect number of arguments.");
            usage(progName);
        }


        // parameter filename
        String paraFilename = remainArgs.get(0);

        // number of rows and columns in map
        int rowNum = 0;
        int colNum = 0;

        // origin and destination lists
        List<Coordinate> originCells = new ArrayList<Coordinate>();
        List<Coordinate> destCells = new ArrayList<Coordinate>();

        // impassable, terrain and waypoint containters
        Set<Coordinate> impassableCells = new HashSet<Coordinate>();
        Map<Coordinate, Integer> terrainCells = new HashMap<Coordinate, Integer>();
        List<Coordinate> waypointCells = new ArrayList<Coordinate>();

        //
        // Parse parameter files
        //

        // First parse main parameter file
        try {
            BufferedReader reader = new BufferedReader(new FileReader(paraFilename));
            String line;

            // read in row and column number
            if ((line = reader.readLine()) != null) {
                String[] tokens = line.trim().split("\\s+");

                if (tokens.length != 2) {
                    System.err.println(
                            "There should be two numbers representing the number of rows and columns.");
                } else {
                    rowNum = Integer.parseInt(tokens[0]);
                    colNum = Integer.parseInt(tokens[1]);

                    // check Input
                    if (rowNum <= 0 || colNum <= 0) {
                        throw new IllegalArgumentException("Map dimensions cannot be 0 or less.");
                    }
                }
            }

            // read in origin coordinates
            if ((line = reader.readLine()) != null) {
                String[] tokens = line.trim().split("\\s+");

                if (tokens.length < 2 || tokens.length % 2 != 0) {
                    System.err.println(
                            "Origin coordinates should be in pairs.");
                } else {
                    for (int i = 0; i < tokens.length; i += 2) {
                        int r = Integer.parseInt(tokens[i]);
                        int c = Integer.parseInt(tokens[i + 1]);
                        if (r < 0 || r >= rowNum || c < 0 || c >= colNum) {
                            throw new IllegalArgumentException(
                                    "Origin coordinates cannot be less than 0 or greater than the number of rows or columns in map.");
                        }
                        originCells.add(new Coordinate(r, c));
                    }
                }
            }

            // read in destination coordinates
            if ((line = reader.readLine()) != null) {
                String[] tokens = line.trim().split("\\s+");

                if (tokens.length < 2 || tokens.length % 2 != 0) {
                    System.err.println(
                            "Destination coordinates should be in pairs.");
                } else {
                    for (int i = 0; i < tokens.length; i += 2) {
                        int r = Integer.parseInt(tokens[i]);
                        int c = Integer.parseInt(tokens[i + 1]);
                        if (r < 0 || r >= rowNum || c < 0 || c >= colNum) {
                            throw new IllegalArgumentException(
                                    "Destination coordinates cannot be less than 0 or greater than the number of rows or columns in map.");
                        }
                        destCells.add(new Coordinate(r, c));
                    }
                }
            }

            // read in impassible coordinates
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.trim().split("\\s+");

                if (tokens.length != 2) {
                    System.err.println(
                            "Impassable coordinates should be in pairs");
                } else {
                    impassableCells.add(new Coordinate(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1])));
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Parameter file doesn't exist.");
            usage(progName);
        } catch (IOException e) {
            System.err.println("IO error.");
            usage(progName);
        } catch (IllegalArgumentException e) {
            System.err.println(e);
            usage(progName);
        }


        // check if need to parse terrain parameter file
        if (terrainFilename != null) {
            // parse terrain file
            try {
                BufferedReader reader = new BufferedReader(new FileReader(terrainFilename));
                String line;

                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.trim().split("\\s+");

                    if (tokens.length != 3) {
                        System.err.println(
                                "Terrain should be two coordinates and cost");
                    } else {
                        int cost = Integer.parseInt(tokens[2]);
                        if (cost < 1) {
                            System.err.println(
                                    "Terrain cost must be 1 or more");
                        } else {
                            Coordinate coord = new Coordinate(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
                            terrainCells.put(coord, new Integer(cost));
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                System.err.println("Parameter file doesn't exist.");
                usage(progName);
            } catch (IOException e) {
                System.err.println("IO error.");
                usage(progName);
            }
        }

        // check if need to parse waypoint parameter file
        if (waypointFilename != null) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(waypointFilename));
                String line;

                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.trim().split("\\s+");

                    if (tokens.length != 2) {
                        System.err.println(
                                "Waypoints should be two coordinates");
                    } else {
                        waypointCells.add(new Coordinate(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1])));
                    }
                }
                reader.close();
            } catch (FileNotFoundException e) {
                System.err.println("Parameter file doesn't exist.");
                usage(progName);
            } catch (IOException e) {
                System.err.println("IO error: " + e);
                usage(progName);
            }
        }


        //
        // Construct map
        //

        PathMap map = new PathMap();
        // load map
        map.initMap(rowNum, colNum, originCells, destCells, impassableCells, terrainCells, waypointCells);
        map.isVisu = isVisu;

        // display it
        map.draw();

        //
        // Find path
        //

        // setup path finding algorithm
        PathFinder pathFinder = new DijkstraPathFinder(map);

        outStream.println(pathFinder.getClass().getSimpleName() + " is finding a path.");

        // find path
        List<Coordinate> path = pathFinder.findPath();

        // check if a path has been found
        if (path.size() == 0) {
            outStream.println("No path found.");
        } else {
            outStream.println("A path has been found.");
            // print out path
            Iterator<Coordinate> it = path.iterator();
			if (it.hasNext()) {
				Coordinate coord = it.next();
				outStream.print("(" + coord.getRow() + "," + coord.getColumn() +")" );
			}
			while(it.hasNext()){
				Coordinate coord = it.next();
				outStream.print(" -> (" + coord.getRow() + "," + coord.getColumn() +")" );
			}
			outStream.println("");
            // This is optional, more for your own curiousity (not tested)
            outStream.println("Number of coordinates visited = " + pathFinder.coordinatesExplored());

            // display the path on screen
            map.drawPath(path);

            // see if we need to output to file also
            if (outputFilename != null) {
                try {
                    PrintWriter writer = new PrintWriter(new FileWriter(outputFilename));
                    it = path.iterator();
                    if (it.hasNext()) {
        				Coordinate coord = it.next();
        				writer.print("(" + coord.getRow() + "," + coord.getColumn() +")" );
        			}
        			while(it.hasNext()){
        				Coordinate coord = it.next();
        				writer.print(" (" + coord.getRow() + "," + coord.getColumn() +")" );
        			}
                    writer.println("");
                    writer.close();
                }
                catch (FileNotFoundException e) {
                    System.err.println("Parameter file doesn't exist.");
                    usage(progName);
                }
                catch (IOException e) {
                    System.err.println("IO Error: " + e);
                    usage(progName);
                }
            }
        }
    } //end of main.
}
