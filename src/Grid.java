
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * This program will take a text file containing a grid and find the shortest
 * path from the upper left corner to the lower right. In addition, it will
 * also find a path that allows us to transport the most weight and report the
 * weight that bottlenecks the whole grid
 * 
 * @author David Romero PID: 3624439
 */
public class Grid
{

    private static final int INFINITY = Integer.MAX_VALUE / 3;
    public final Square UPPER_LEFT;     //Upper left corner of grid
    public final Square LOWER_RIGHT;    //Lower right corner of grid
    private int numOfRows;              //Number of rows
    private int numOfCols;              //Number of columns
    private Square[][] squares;         //The grid system containing positions
    private int[][] board;              //The grid itself

    /**
     * Calls the private "buildGrid" method. Also populates
     * @param input The file containing the grid
     */
    public Grid(File input)
    {
        buildGrid(input);

        squares = new Square[numOfRows][numOfCols];
        
        //Populating the squares array
        for (int r = 0; r < numOfRows; ++r)
        {
            for (int c = 0; c < numOfCols; ++c)
            {
                squares[r][c] = new Square(r, c);
            }
        }

        UPPER_LEFT  = squares[0][0];
        LOWER_RIGHT = squares[numOfRows - 1][numOfCols - 1];
    }

    /**
     * Inner Square class
     */
    class Square
    {

        private int row = 0;        //The row
        private int col = 0;        //The column
        private int dist;           //The distance "traveled"
        private Square prev;        //The previous position

        /**
         * Creates a square object. Initializes the distance to infinity
         * @param r The position in row
         * @param c The position in column
         */
        public Square(int r, int c)
        {
            row = r;
            col = c;
            dist = INFINITY;
            prev = null;
        }

        /**
         * Creates an array list of adjacent squares. 
         * @return The array containing adjacent neighbors
         */
        public List<Square> getNeighbors()
        {
            //Checking if you're at the edge of the grid, else
            //there is a row before.
            int lowRow = (row == 0) ? 0 : (row - 1);
            //Checking if you're at the edge of the grid, else there is a 
            //column before
            int lowCol = (col == 0) ? 0 : (col - 1);
            //Checks if you're at the end of the row, if so then it's the last 
            //element in the array, else it gets the next element
            int highRow = (row == numOfRows - 1) ? row : (row + 1);
            //Checks if you're at the end of the column, if so then it's the 
            //last element in the array of arrays, else it gets the next element
            int highCol = (col == numOfCols - 1) ? col : (col + 1);

            List<Square> result = new ArrayList<>();

            for (int r = lowRow; r <= highRow; ++r)
            {
                for (int c = lowCol; c <= highCol; ++c)
                {
                    if (r != row || c != col)
                    {
                        result.add(squares[r][c]);
                    }
                }
            }

            return result;
        }

        /**
         * Gets the distance
         * @return The distance
         */
        public int getDistance()
        {
            return dist;
        }

        /**
         * Gets the previous square
         * @return The previous square
         */
        public Square getPrevious()
        {
            return prev;
        }

        /**
         * Updates the distance
         * @param newDist The updated distance
         * @param previous The last square
         */
        public void setDistance(int newDist, Square previous)
        {
            dist = newDist;
            prev = previous;
        }

        /**
         * Gets the value at the position of the board
         * @return The value at the position
         */
        public int getCost()
        {
            return board[row][col];
        }

        /**
         * Sets the cost of the square. "The toll".
         * @param newCost The cost of the square
         */
        public void setCost(int newCost)
        {
            board[row][col] = newCost;
        }

        /**
         * Prints out the square 
         * @return The square
         */
        @Override
        public String toString()
        {
            return "( " + row + ", " + col + " ) cell is " + board[row][col];
        }
    }

    /**
     * Constructs the grid itself based off the text file.
     * @param input The file that the board will be based off
     */
    private void buildGrid(File input)
    {
        //Array list containing the lines in the file
        ArrayList<String> lines = new ArrayList<>();
        String previous = "";           //Contains the previous line
        try
        {
            Scanner scan = new Scanner(input);

            while (scan.hasNext())
            {
                String next = scan.nextLine();
                //Tokenizing the current line
                StringTokenizer check = new StringTokenizer(next);
                //Tokenizing the previous line
                StringTokenizer checkPrev = new StringTokenizer(previous);

                //Checking if they have the same number of tokens. If they do
                //not then the file put will not make an acceptable grid
                if (!lines.isEmpty() && (check.countTokens() 
                                      != checkPrev.countTokens()))
                {
                    System.out.println("The input does not make a square. "
                            + "Please check your file and try again.");
                    System.exit(0);
                }
                
                lines.add(next);
                previous = next;
            }

            //Tokenizing the first line
            StringTokenizer st = new StringTokenizer(lines.get(0));

            //Setting the number of rows and columns
            numOfRows = lines.size();
            numOfCols = st.countTokens();

            //Creating the grid itself with recently determined rows and col.
            board = new int[numOfRows][numOfCols];

            //Traversing the 2d array inputing the values
            for (int r = 0; r < numOfRows; ++r)
            {
                //Gets the first line to be inserted to grid
                StringTokenizer st1 = new StringTokenizer(lines.get(r));
                
                while (st1.hasMoreTokens())
                {
                    for (int c = 0; c < numOfCols; ++c)
                    {
                        //Token to be parsed to an integer
                        String temp = st1.nextToken();
                        //Parsing the token to integer that will be placed
                        int insert = Integer.parseInt(temp);
                        board[r][c] = insert;
                    }
                }
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File could not be found, please check if"
                    + " file is in root and try again");
            System.exit(0);
        }
    }

    /**
     * Computes the shortest path from upper left to lower right. Accomplishes 
     * this by finding sum of surrounding values and moving on. Implements 
     * Dijkstra's algorithm
     * @param s The starting position
     */
    public void shortestPath(Square s)
    {
        //Setting all squares distance to infinity and previous squares to null
        for (int r = 0; r < numOfRows; ++r)
        {
            for (int c = 0; c < numOfCols; ++c)
            {
                squares[r][c].setDistance(INFINITY, null);
            }
        }

        //Using lambda notion, creates a priority queue that will store the
        //squares from smallest to largest
        PriorityQueue<Square> pq = new PriorityQueue<>(
                (lhs, rhs) -> lhs.getDistance() - rhs.getDistance() );

        //All distances should be initilized to zero except the start
        s.setDistance(0, null);
        //Add start to the priority queue
        pq.add(s);

        while (!pq.isEmpty())
        {
            //Getting start from queue
            Square v = pq.remove();

            for (Square w : v.getNeighbors())
            {
                if (w.getDistance() == INFINITY)
                {
                    w.setDistance( v.getDistance() + w.getCost(), v );
                    pq.add(w);
                }
            }
        }
    }

    /**
     * Finds the shortest path that allows you to transport the most weight
     * @param s 
     */
    public void weightLimit(Square s)
    {
        //Setting all squares distance to 0 and previous squares to null
        for (int r = 0; r < numOfRows; ++r)
        {
            for (int c = 0; c < numOfCols; ++c)
            {
                squares[r][c].setDistance(0, null);
            }
        }
        
        //Using lambda notion, creates a priority queue that will store the
        //squares from largest to smallest
        PriorityQueue<Square> pq = new PriorityQueue<>(
                (lhs, rhs) -> rhs.getDistance() - lhs.getDistance() );

        //Saving cost of old lower right
        int oldLowerCost = LOWER_RIGHT.getCost();
        //Initilizing both 
        LOWER_RIGHT.setCost(INFINITY);
        s.setDistance(INFINITY, null);
        //Adding start point to queue
        pq.add(s);

        while (!pq.isEmpty())
        {
            //Getting start from queue
            Square v = pq.remove();

            for (Square w : v.getNeighbors())
            {
                if (w.getDistance() == 0)
                {
                    w.setDistance(Math.min(v.getDistance(), w.getCost()), v);
                    pq.add(w);
                }
            }
        }

        LOWER_RIGHT.setCost(oldLowerCost);
    }

    /**
     * Prints the path computed by the shortestPath method and 
     * weighLimit method
     * @param t The current square
     * @param weightOrShortest Specify whether the shortest path or weight limit
     * problem is being printed
     */
    public void printPath(Square t, String weightOrShortest)
    {
        //Array list containing all of the squares to be printed
        ArrayList<Square> printList = new ArrayList<>();
        //Add the square being passed to the list
        printList.add(t);
        //Saving the current square's previous
        Square prev = t.getPrevious();

        //Adding the values to the list to be printed
        while (prev != null)
        {
            printList.add(prev);
            prev = prev.getPrevious();
        }
        
        //Due to the fact that we are traversing the list from the lower right 
        //to upper left, the entries were added in reverse and so the list
        //must be traversed in reverse.
        if (printList.size() > 20)
        {
            for (int i = printList.size() - 1; i >= printList.size() - 10; --i)
            {
                System.out.println(printList.get(i));
            }

            System.out.println("...");

            for (int i = 10; i >= 0; --i)
            {
                System.out.println(printList.get(i));
            }
        }
        else
        {
            for (int i = printList.size() - 1; i >= 0; --i)
            {
                System.out.println(printList.get(i));
            }
        }
        switch (weightOrShortest)
        {
            case "shortest":
                System.out.println("Total cost is " + 
                                    LOWER_RIGHT.getDistance());
                break;
            case "weightLimit":
                System.out.println("All cells support " + 
                                    LOWER_RIGHT.getDistance());
                break;
        }

    }

    public static void main(String[] args)
    {
        if (args.length != 1)
        {
            System.out.println("Grid file not inserted, please make sure"
                    + " the file is at root and try again.");
            System.exit(0);
        }
        
        Grid path = new Grid(new File(args[0]));
        System.out.println("Finding shortest path\n");
        path.shortestPath(path.UPPER_LEFT);
        path.printPath(path.LOWER_RIGHT, "shortest");
        System.out.println("\nFinding the weight limit\n");
        path.weightLimit(path.UPPER_LEFT);
        path.printPath(path.LOWER_RIGHT, "weightLimit");
    }
}
