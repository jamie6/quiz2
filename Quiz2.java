/*
Write a multi-class multi-threaded Java program that simulates the game 
of life. Each cell will have its own thread dedicated to it to compute 
the cell's value in the next generation. This thread will be embedded in a 
class that is instantiated M*N times by the driver, once for each cell 
in the grid. 
 */
/**
 *
 * @author Jamie
 */
public class Quiz2
{
    static int n = 20;
    static int m = 20;
    static Cell[][] grid;
    static Thread[][] threads;
    public static void main(String[] args)
    {
        initializeGrid();
        int maxGenerations = 50;
        int currentGeneration = 1;
        while ( currentGeneration <= maxGenerations )
        {
            System.out.println("Generation: " + currentGeneration);
            printGrid();
            threads = new Thread[n][m];
            for ( int i = 0; i < n; i++ )
            {
                for ( int j = 0; j < m; j++ )
                {
                    final int row = i;
                    final int column = j;
                    threads[i][j] = new Thread(() ->
                    {
                        grid[row][column].run();
                    });
                    threads[i][j].start();
                }
            }
            waitForAllThreadsToFinish();
            for ( int i = 0; i < n; i++ )
            {
                for ( int j = 0; j < m; j++ )
                {
                    final int row = i;
                    final int column = j;
                    threads[i][j] = new Thread(() ->
                    {
                        grid[row][column].processNextGeneration();
                    });
                    threads[i][j].start();
                }
            }
            waitForAllThreadsToFinish();
            currentGeneration++;
        }
    }
    
    public static void printGrid()
    {
        for ( int i = 0; i < n; i++ )
        {
            for ( int j = 0; j < m; j++ )
            {
                System.out.print(" " + grid[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void initializeGrid()
    {   
        grid = new Cell[n][m];
        java.util.Random random = new java.util.Random();
        for ( int i = 0; i < n; i++ )
        {
            for ( int j = 0; j < m; j++ )
            {
                if ( random.nextDouble() < 0.5 )
                {
                    grid[i][j] = new Cell(i,j,2); // alive
                }
                else grid[i][j] = new Cell(i,j,0); // dead
            }
        }
    }
    
    public static void waitForAllThreadsToFinish()
    {
        // wait for all threads to finish
        try 
        {
            for ( int i = 0; i < n; i++ )
                for ( int j = 0; j < m; j++ )
                    threads[i][j].join();
        }
        catch (InterruptedException e )
        {
            System.out.println(e.getMessage());
        }
    }
    
    public static class Cell
    {
        int row, column;
        int status;
        public Cell(int row, int column, int status)
        {
            this.row = row;
            this.column = column;
            this.status = status;
        }
        
        public void run()
        {
            int numberOfNeighbors = getNumberOfNeighbors(grid);
            if ( numberOfNeighbors < 2 || numberOfNeighbors > 3)
            {  // kill current cell
                if ( status == 2 ) status = 1; // set to dying
            }
            else if ( numberOfNeighbors == 3 && status == 0)
            {
                grid[row][column].status = -1;
            }
        }
        
        public void processNextGeneration()
        {
            if ( grid[row][column].status == -1 )
                grid[row][column].status = 2;
            else if ( grid[row][column].status == 1 )
                grid[row][column].status = 0;
        }
        
        public int getNumberOfNeighbors(Cell[][] grid)
        { // -1 = ressurect, 0 = dead, 1 = dying, 2 = alive
            int count = 0;
            //  north
            if ( row-1 > -1 && grid[row-1][column].status > 0  )
                count++;
            // north east
            if ( row-1 > -1 && column+1 < m && grid[row-1][column+1].status > 0 )
                count++;
            // east
            if ( column+1 < m && grid[row][column+1].status > 0 )
                count++;
            // south east
            if ( row+1 < n && column+1 < m && grid[row+1][column+1].status > 0 )
                count++;
            // south
            if ( row+1 < n && grid[row+1][column].status > 0 )
                count++;
            // south west
            if ( row+1 < n && column-1 > -1 && grid[row+1][column-1].status > 0 )
                count++;
            // west
            if ( column-1 > -1 && grid[row][column-1].status > 0 )
                count++;
            // north west
            if ( row-1 > -1 && column-1 > -1 && grid[row-1][column-1].status > 0 )
                count++;
            return count;
        }
        
        @Override
        public String toString()
        {
            return ( status == 2 ) ? "O" : "-" ;
        }
    }
}
