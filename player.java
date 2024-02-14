import java.util.*;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class player {

    static Random random=new Random();
    static int bestestindex;
    static long timeLimitInMillis = 3000; //Specify time in milliseconds
    static int repeatedMoveCount = 0;
    //static List<Integer> playerMoves = new ArrayList<>();
    //static int maxConsecutiveRepeats = 2; // Maximum consecutive repeats allowed
    
    static void setupBoardState(state state, int player, char[][] board)
    {
        /* Set up the current state */
        state.player = player;
        playerhelper.memcpy(state.board,board);

        /* Find the legal moves for the current state */
        playerhelper.FindLegalMoves(state);
    }

    
    static void PerformMove(state state, int moveIndex)
    {
        playerhelper.PerformMove(state.board, state.movelist[moveIndex], playerhelper.MoveLength(state.movelist[moveIndex]));
        state.player = state.player%2+1;
        playerhelper.FindLegalMoves(state);
    }
   
    static double minmax(state state, int depth, double alpha, double beta, boolean maxplayer,long startTime, boolean timeLimitExceeded) {
        if (depth <= 0) {
            if (maxplayer) {
                return evalBoard(state);
            } else {
                return -evalBoard(state);
            }
        }
        if (System.currentTimeMillis() - startTime >= timeLimitInMillis) {
            if (maxplayer) {
                timeLimitExceeded = true;
                return evalBoard(state);
            } else {
                timeLimitExceeded = true;
                return -evalBoard(state);
            }
        }
        if (maxplayer) {
            for (int i = 0; i < state.numLegalMoves; i++) {
                state nextState = new state(state);
                PerformMove(nextState, i);
                double value = minmax(nextState, depth - 1, alpha, beta, false, startTime, timeLimitExceeded);
                if (value > alpha){
                    alpha=value;
                    if (beta <= alpha) {
                    return beta;
                    }
                }
            }
            return alpha;
        } else {
            for (int i = 0; i < state.numLegalMoves; i++) {
                state nextState = new state(state);
                PerformMove(nextState, i);
                double value = minmax(nextState, depth - 1, alpha, beta, true,startTime, timeLimitExceeded);
                if (value < beta){
                    beta = value;
                    if (beta <= alpha){
                        return alpha;
                    }
                }
            }
            return beta;
        }
    }

    public static void FindBestMove(int player, char[][] board, char[] bestmove) {
        int myBestMoveIndex=0;
        //int mySecondBestMoveIndex = -1; // Initialize to -1
        double bestMoveValue;
        //double secondBestMoveValue;
        double alpha;
        double beta;
        boolean timeLimitExceeded = false;
        //double randomFactor = Math.random(); // Generate a random number between 0 and 1
        //double evalscore = 0;
        state state = new state();
        setupBoardState(state, player, board);
        
        for (int i = 9; i <= 9; i++) {
            myBestMoveIndex = 0;
            //mySecondBestMoveIndex = -1; // Reset to -1 at the beginning of each iteration
            bestMoveValue = -Double.MAX_VALUE;
            //secondBestMoveValue = -Double.MAX_VALUE;
            alpha = -Double.MAX_VALUE;
            beta = Double.MAX_VALUE;
            long startTime = System.currentTimeMillis();
    
            for (int x = 0; x < state.numLegalMoves; x++) {
                state nextState = new state(state);
                PerformMove(nextState, x);
    
                double temp = minmax(nextState, i, alpha, beta, false, startTime, timeLimitExceeded);
    
                if (temp > bestMoveValue) {
                    //secondBestMoveValue = bestMoveValue;
                    //mySecondBestMoveIndex = myBestMoveIndex;
                    bestMoveValue = temp;
                    myBestMoveIndex = x;
                }/*  else if (temp > secondBestMoveValue) {
                    secondBestMoveValue = temp;
                    mySecondBestMoveIndex = x;
                }*/

                if (timeLimitExceeded) {
                // If time limit exceeded, break out of the loop
                    break;
                }
    
                if (System.currentTimeMillis() - startTime >= timeLimitInMillis) {
                    timeLimitExceeded = true;
                    break; // Time limit exceeded, exit the loop
                }
    
            }
            //evalscore = bestMoveValue;
            if (timeLimitExceeded) {
                // If time limit exceeded, break out of the loop
                break;
            }
        }
        //System.err.println("Eval of board: " + evalscore);

        playerhelper.memcpy(bestmove, state.movelist[myBestMoveIndex], playerhelper.MoveLength(state.movelist[myBestMoveIndex])); 
        
    }
    /*public static void FindBestMove(int player, char[][] board, char[] bestmove) {
        //double evalscore;
        AtomicInteger myBestMoveIndex = new AtomicInteger(0);

        ASstate state = new ASstate();
        setupBoardState(state, player, board);
        final long timeLimitInMillis = 3000; // Specify the time limit in milliseconds
        final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        final Thread searchThread = new Thread(() -> {
            
            //int mySecondBestMoveIndex = -1; // Initialize to -1
            double bestMoveValue;
            double secondBestMoveValue;
            double alpha;
            double beta;
            boolean timeLimitExceeded = false;
            //double randomFactor = Math.random(); // Generate a random number between 0 and 1
            
            for (int i = 5; i <= 7; i++) { //max 7 depth complete search
                myBestMoveIndex.set(0);
                //mySecondBestMoveIndex = -1; // Reset to -1 at the beginning of each iteration
                bestMoveValue = -Double.MAX_VALUE;
                secondBestMoveValue = -Double.MAX_VALUE;
                alpha = -Double.MAX_VALUE;
                beta = Double.MAX_VALUE;
                long startTime = System.currentTimeMillis();
        
                for (int x = 0; x < state.numLegalMoves; x++) {
                    ASstate nextState = new ASstate(state);
                    PerformMove(nextState, x);
        
                    double temp = minmax(nextState, i, alpha, beta, false, startTime, timeLimitExceeded);
        
                    if (temp > bestMoveValue) {
                        secondBestMoveValue = bestMoveValue;
                        //mySecondBestMoveIndex = myBestMoveIndex.get();;
                        bestMoveValue = temp;
                        myBestMoveIndex.set(x);
                    } else if (temp > secondBestMoveValue) {
                        secondBestMoveValue = temp;
                        //mySecondBestMoveIndex = x;
                    }
        
                }
                System.err.println("depth: " + i);
            }
        });
        searchThread.start();

        executorService.schedule(() -> {
            if (searchThread.isAlive()) {
                searchThread.interrupt();
            }
        }, timeLimitInMillis, TimeUnit.MILLISECONDS);

        try {
            // Wait for the search thread to complete
            searchThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdownNow();
        }

        ASplayerhelper.memcpy(bestmove, state.movelist[myBestMoveIndex.get()], ASplayerhelper.MoveLength(state.movelist[myBestMoveIndex.get()]));
        
    }*/

    static void printBoard(state state)
    {
        int y,x;

        for(y=0; y<8; y++) 
        {
            for(x=0; x<8; x++)
            {
                if(x%2 != y%2)
                {
                     if(playerhelper.empty(state.board[y][x]))
                     {
                         System.err.print(" ");
                     }
                     else if(playerhelper.king(state.board[y][x]))
                     {
                         if(playerhelper.color(state.board[y][x])==2) System.err.print("B");
                         else System.err.print("A");
                     }
                     else if(playerhelper.piece(state.board[y][x]))
                     {
                         if(playerhelper.color(state.board[y][x])==2) System.err.print("b");
                         else System.err.print("a");
                     }
                }
                else
                {
                    System.err.print("@");
                }
            }
            System.err.print("\n");
        }
    }

    
    static int calculateBaitValue(state state, int x, int y) {
        int baitValue = 0;
        if (y > 1 && x >= 2) {
            if (state.board[y - 1][x - 1] != ' ' && playerhelper.color(state.board[y - 1][x - 1]) != state.player) {
                if (state.board[y - 2][x - 2] == ' ') {
                    baitValue++;
                }
            }
        }
        if (y > 1 && x <= 5) {
            if (state.board[y - 1][x + 1] != ' ' && playerhelper.color(state.board[y - 1][x + 1]) != state.player) {
                if (state.board[y - 2][x + 2] == ' ') {
                    baitValue++;
                }
            }
        }
        return baitValue;
    }
    static int countTriangleFormation(state state, int x, int y, int player) {
        int count = 0;
        
        // Check the surrounding squares for the presence of the player's pieces
        if (x > 0 && y > 0 && state.board[y - 1][x - 1] == player) {
            count++;
        }
        if (x < 7 && y > 0 && state.board[y - 1][x + 1] == player) {
            count++;
        }
        if (x > 1 && y > 1 && state.board[y - 2][x - 2] == player) {
            count++;
        }
        if (x < 6 && y > 1 && state.board[y - 2][x + 2] == player) {
            count++;
        }
    
        return count;
    }
    
     /* An example of how to walk through a board and determine what pieces are on it*/
    static double evalBoard(state state) {
        // Initialize metrics
        int p1Nums[] = new int[11];
        int p2Nums[] = new int[11];
    
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (x % 2 != y % 2) {
                    char piece = state.board[y][x];
                    if (!playerhelper.empty(piece)) {
                        if (playerhelper.color(piece) == state.player) {
                            // Check for pawns
                            if (!playerhelper.king(piece)) {
                                p1Nums[0]++;
                                // Encourage pawns to be bait
                                p1Nums[0] += calculateBaitValue(state, x, y);
                                p1Nums[7] += countTriangleFormation(state, x, y, state.player);
                            }
                            // Check for kings
                            else {
                                p1Nums[1]++;
                            }
                            
                            int r = y;
                            int c = x;
                            // Check for back row
                            if (r == 7) {
                                p1Nums[2]++;
                                p1Nums[6]++;
                            } else {
                                // Check for middle rows
                                if (r == 3 || r == 4) {
                                    // Mid box
                                    if (c >= 2 && c <= 5) {
                                        p1Nums[3]++;
                                    }
                                    // Non-box
                                    else {
                                        p1Nums[4]++;
                                    }
                                }
                                // Check if can be taken this turn
                                if (r > 0) {
                                    if (c > 0 && c < 7) {
                                        if (state.board[r - 1][c - 1] != ' ' && playerhelper.color(state.board[r - 1][c - 1]) != state.player
                                                && state.board[r + 1][c + 1] == ' ') {
                                            p1Nums[5]++;
                                        }
                                        if (state.board[r - 1][c + 1] != ' ' && playerhelper.color(state.board[r - 1][c + 1]) != state.player
                                                && state.board[r + 1][c - 1] == ' ') {
                                            p1Nums[5]++;
                                        }
                                    }
                                }
                                // Check for protected checkers
                                if (r < 7) {
                                    if (c == 0 || c == 7) {
                                        p1Nums[6]++;
                                    } else {
                                        if ((state.board[r + 1][c - 1] != ' ' && (playerhelper.color(state.board[r + 1][c - 1]) == state.player
                                                || !playerhelper.king(state.board[r + 1][c - 1])))
                                                && (state.board[r + 1][c + 1] != ' ' && (playerhelper.color(state.board[r + 1][c + 1]) == state.player
                                                        || !playerhelper.king(state.board[r + 1][c + 1])))) {
                                            p1Nums[6]++;
                                        }
                                    }
                                }
                            }
                        } else {
                            // Check for pawns (Player 2)
                            if (!playerhelper.king(piece)) {
                                p2Nums[0]++;
                                p2Nums[0] += calculateBaitValue(state, x, y);
                                p2Nums[7] += countTriangleFormation(state, x, y, state.player);
                            }
                            // Check for kings (Player 2)
                            else {
                                p2Nums[1]++;
                            }
    
                            // Calculate other metrics (similar to the previous function)
                            int r = y;
                            int c = x;
                            // Check for back row (Player 2)
                            if (r == 0) {
                                p2Nums[2]++;
                                p2Nums[6]++;
                            } else {
                                // Check for middle rows (Player 2)
                                if (r == 3 || r == 4) {
                                    // Mid box
                                    if (c >= 2 && c <= 5) {
                                        p2Nums[3]++;
                                    }
                                    // Non-box
                                    else {
                                        p2Nums[4]++;
                                    }
                                }
                                // Check if can be taken this turn (Player 2)
                                if (r < 7) {
                                    if (c > 0 && c < 7) {
                                        if (state.board[r + 1][c - 1] != ' ' && playerhelper.color(state.board[r + 1][c - 1]) != state.player
                                                && state.board[r - 1][c + 1] == ' ') {
                                            p2Nums[5]++;
                                        }
                                        if (state.board[r + 1][c + 1] != ' ' && playerhelper.color(state.board[r + 1][c + 1]) != state.player
                                                && state.board[r - 1][c - 1] == ' ') {
                                            p2Nums[5]++;
                                        }
                                    }
                                }
                                // Check for protected checkers (Player 2)
                                if (r > 0) {
                                    if (c == 0 || c == 7) {
                                        p2Nums[6]++;
                                    } else {
                                        if ((state.board[r - 1][c - 1] != ' ' && (playerhelper.color(state.board[r - 1][c - 1]) == state.player
                                                || !playerhelper.king(state.board[r - 1][c - 1])))
                                                && (state.board[r - 1][c + 1] != ' ' && (playerhelper.color(state.board[r - 1][c + 1]) == state.player
                                                        || !playerhelper.king(state.board[r - 1][c + 1])))) {
                                            p2Nums[6]++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Weight factors for different features
        double pawnWeight = 1.4;
        double kingWeight = 2;
        double backRowWeight = 0.5;
        double midBoxWeight = 0.75;
        double midRowNonBoxWeight = 0.6;
        double canBeTakenWeight = 0.25;
        double protectedCheckersWeight = 0.6;
        double triangleFormationWeight = 0.5;


        // Calculate the score based on weighted features
        double score = (pawnWeight * (p1Nums[0] - p2Nums[0]))
                    + (kingWeight * (p1Nums[1] - p2Nums[1]))
                    + (backRowWeight * (p1Nums[2] - p2Nums[2]))
                    + (midBoxWeight * (p1Nums[3] - p2Nums[3]))
                    + (midRowNonBoxWeight * (p1Nums[4] - p2Nums[4]))
                    + (canBeTakenWeight * (p1Nums[5] - p2Nums[5]))
                    + (protectedCheckersWeight * (p1Nums[6] - p2Nums[6]))
                    + (triangleFormationWeight * (p1Nums[7] - p2Nums[7]));
    
        return score;
    }
}
    
