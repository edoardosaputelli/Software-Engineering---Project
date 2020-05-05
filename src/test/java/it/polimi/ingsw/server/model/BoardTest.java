package it.polimi.ingsw.server.model;


import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Box;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Worker;
import org.junit.Test;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import java.lang.Math;
import java.util.zip.DataFormatException;


public class BoardTest {

    //declaring and initializing this variable just at the beginning of the class test
    //because it is an instance
    Board board = new Board();

    //I use this method to generate random numbers to test boxes inside the board
    public int generateInsideDimension(){
        Random random = new Random();
        return random.nextInt(4);
    }

    //I use this method to generate random numbers to test boxes outside the board
    public int generateOutsideDimension() {
        //60 could be a good range
        int max = 30;
        int min = -30;
        int range = max - min;

        int returningValue;
        //I strongly want the box outside the board
        do {
            returningValue = (int) (Math.random() * range) + min;
        } while (returningValue >= 0 && returningValue <= 4);
            return returningValue;
    }

    //I have to clear the board for future tests, because board is an instance
    public void clearBoardForFutureTests (Board board){
        for (int i=0; i<5; i++) {
            for (int j= 0; j < 5; j++) {

                //setting every box's level at 0
                while (board.getBox(i, j).getTower().size() != 0)
                    board.getBox(i, j).decreaseLevel();

                //removing every player
                if (board.getBox(i, j).getOccupier() != null)
                    board.getBox(i, j).setOccupier(null);

            }
        }
    }

    //here tests start
    @Test
    public void drawBoardTest() {
        board.drawBoard();
    }

    @Test
    public void BoardConstructionTest() {
        //firstly I test the board
        assertNotNull( board );
        //then I test every box
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                assertNotNull( board.getBox(i,j) );
                assertTrue( board.inBoundaries(i,j) );
            }
        }
    }

    @Test
    public void placeWorkerTest() throws DataFormatException {
        Player playerA = new Player("Giulio", 22, 12, 1990);
        Worker workerA = new Worker(playerA, Color.GREEN, "A");

        Player playerB = new Player("Marco", 22, 12, 1985);
        Worker workerB = new Worker(playerB, Color.RED, "A");

        Player playerC = new Player("Franco", 22, 12, 1980);
        Worker workerC = new Worker(playerC, Color.RED, "A");
        clearBoardForFutureTests(board);

        //getting random boxes to put the workers
        int rowA = generateInsideDimension();
        int columnA = generateInsideDimension();
        board.placeWorker(workerA, rowA, columnA);
        int rowB = generateInsideDimension();
        int columnB = generateInsideDimension();
        board.placeWorker(workerB, rowB, columnB);
        int rowC = generateInsideDimension();
        int columnC = generateInsideDimension();
        board.placeWorker(workerC, rowC, columnC);

        //where players put the workers
        Box placingBoxA = board.getBox(rowA, columnA);
        Box placingBoxB = board.getBox(rowB, columnB);
        Box placingBoxC = board.getBox(rowC, columnC);

        //are the occupiers of the placing boxes the same workers we defined?
        assertEquals(placingBoxA.getOccupier(), workerA);
        assertEquals(placingBoxB.getOccupier(), workerB);
        assertEquals(placingBoxC.getOccupier(), workerC);

        //the only boxes which are not null are the placingBoxes
        for (int i=0; i<5; i++) {
            for (int j=0; j<5; j++) {
                if (i==rowA && j==columnA || i==rowB && j==columnB || i==rowC && j==columnC)
                    assertNotNull( board.getBox(i, j).getOccupier() );
                else
                    assertNull( board.getBox(i, j).getOccupier() );
            }
        }

        //I have to clear the board for future tests, because board is an instance
        clearBoardForFutureTests(board);
    }

    @Test
    public void increaseLevelTest(){
        //getting the coordinates for the box to test
        int row = generateInsideDimension();
        int column = generateInsideDimension();

        //before increasing the height is 0
        int heightBeforeIncreasingLevel = board.getBox(row, column).getTower().size();
        assertTrue(heightBeforeIncreasingLevel == 0);

        board.getBox(row, column).increaseLevel();

        assertTrue(board.getBox(row, column).getTower().size() == heightBeforeIncreasingLevel + 1);

        clearBoardForFutureTests(board);
    }

    @Test
    public void decreaseLevelTest(){
        //getting the coordinates for the box to test
        int row = generateInsideDimension();
        int column = generateInsideDimension();

        //increasing level from 1 to 4 times
        int max = 4;
        int min = 1;
        int range = max - min;
        int increasingTimes = (int) (Math.random() * range) + min;

        for(int i=0; i<increasingTimes; i++)
            board.getBox(row, column).increaseLevel();

        int heightBeforeDecreasingLevel = board.getBox(row, column).getTower().size();


        board.getBox(row, column).decreaseLevel();

        //after decreasing, the height will be the previous - 1
        assertTrue(board.getBox(row, column).getTower().size() == heightBeforeDecreasingLevel - 1);

        clearBoardForFutureTests(board);
    }

    @Test
    public void inBoundariesTest (){
        //getting a random box in the board
        int insideRow = generateInsideDimension();
        int insideColumn = generateInsideDimension();

        //testing board's boxes
        for (int i=0; i<5; i++) {
            for (int j=0; j<5; j++) {
                assertTrue(board.inBoundaries(insideRow, insideColumn));
            }
        }

        //testing 10 boxes outside the board
        for (int k=0; k<10; k++) {
            int outsideRow = generateOutsideDimension();
            int outsideColumn = generateOutsideDimension();

            assertFalse(board.inBoundaries(outsideRow, outsideColumn));
        }
    }

    @Test
    public void BoxIsNearTest () {
        //getting a random box in the board
        int insideRow = generateInsideDimension();
        int insideColumn = generateInsideDimension();


        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {

                if (Math.abs(insideRow - i) == 1 || insideRow - i == 0) {

                    if (Math.abs(insideColumn - j) == 1 || insideColumn - j == 0) {

                        if (insideRow == i && insideColumn == j)
                            assertFalse(board.boxIsNear(insideRow, insideColumn, i, j));
                        else
                            assertTrue(board.boxIsNear(insideRow, insideColumn, i, j));

                    } else
                        assertFalse(board.boxIsNear(insideRow, insideColumn, i, j));

                } else
                    assertFalse(board.boxIsNear(insideRow, insideColumn, i, j));
            }
        }

        //testing 10 boxes outside the board
        for (int k=0; k<10; k++) {
            int outsideRow = generateOutsideDimension();
            int outsideColumn = generateOutsideDimension();

            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    assertFalse(board.boxIsNear(outsideRow, outsideColumn, i, j));
                }
            }
        }
    }

    @Test
    public void isScalableTest(){
        int rowA = 2;
        int columnA = 2;
        board.getBox(rowA, columnA).increaseLevel();
        //box A has 1 level

        //this box is adjacent to Box A and lower
        int rowB = rowA - 1;
        int columnB = columnA - 1;
        //box D has 0 levels

        //this box is adjacent to box A and has same height
        int rowC = rowA + 1;
        int columnC = columnA - 1;
        board.getBox(rowC, columnC).increaseLevel();
        //box C has 1 level

        //this box is adjacent to Box A, higher and scalable
        int rowD = rowA - 1;
        int columnD = columnA + 1;
        board.getBox(rowD, columnD).increaseLevel();
        board.getBox(rowD, columnD).increaseLevel();
        //box D has 2 levels

        //this box is adjacent to Box A, higher but not scalable
        int rowE = rowA + 1;
        int columnE = columnA + 1;
        board.getBox(rowE, columnE).increaseLevel();
        board.getBox(rowE, columnE).increaseLevel();
        board.getBox(rowE, columnE).increaseLevel();
        //box E has 3 levels

        //this box is not adjacent to Box A and has same height
        int rowF = rowA + 2;
        int columnF = columnA - 2;
        board.getBox(rowF, columnF).increaseLevel();
        //box F has 1 level

        //this box is not adjacent to Box A and has has Box A's height + 2
        int rowG = rowA - 2;
        int columnG = columnA + 2;
        board.getBox(rowG, columnG).increaseLevel();
        board.getBox(rowG, columnG).increaseLevel();
        board.getBox(rowG, columnG).increaseLevel();
        //box G has 3 levels

        //testing with attribute allowedToScale as true
        board.setAllowedToScale(true);
        //in addition to boxes F and G, only box E is not scalable
        assertTrue( board.isScalable(rowA, columnA, rowB, columnB) );
        assertTrue( board.isScalable(rowA, columnA, rowC, columnC) );
        assertTrue( board.isScalable(rowA, columnA, rowD, columnD) );
        assertFalse( board.isScalable(rowA, columnA, rowE, columnE) );
        assertFalse( board.isScalable(rowA, columnA, rowF, columnF) );
        assertFalse( board.isScalable(rowA, columnA, rowG, columnG) );


        //testing with attribute allowedToScale as false
        board.setAllowedToScale(false);
        //in addition to boxes F and G, both box D and E are not scalable
        assertTrue( board.isScalable(rowA, columnA, rowB, columnB) );
        assertTrue( board.isScalable(rowA, columnA, rowC, columnC) );
        assertFalse( board.isScalable(rowA, columnA, rowD, columnD) );
        assertFalse( board.isScalable(rowA, columnA, rowE, columnE) );
        assertFalse( board.isScalable(rowA, columnA, rowF, columnF) );
        assertFalse( board.isScalable(rowA, columnA, rowG, columnG) );

        //I have to clear the board for future tests, because board is an instance
        clearBoardForFutureTests(board);
        board.setAllowedToScale(true);

    }

    @Test
    public void isNearbySpaceFreeTest () throws DataFormatException {
        Player player1 = new Player("Marco", 2, 2, 2000);
        Worker workerA1 = new Worker( player1, Color.GREEN, "A" );
        Worker workerB1 = new Worker( player1, Color.GREEN, "B" );

        Player player2 = new Player("Luca", 3, 4, 2001);
        Worker workerA2 = new Worker( player2, Color.RED, "A" );
        Worker workerB2 = new Worker( player2, Color.RED, "B" );

        board.placeWorker(workerA1, 0, 2);
        board.placeWorker(workerB1, 0, 3);

        board.getBox(0, 1).increaseLevel();
        board.getBox(0, 1).increaseLevel();
        // (0,1) has level 2
        board.getBox(1, 2).increaseLevel();
        board.getBox(1, 2).increaseLevel();
        // (1,2) has level 2
        board.getBox(1, 1).increaseLevel();
        board.getBox(1, 1).increaseLevel();
        // (1,1) has level 2
        board.getBox(1, 3).increaseLevel();
        // (1,3) has level 1

        board.drawBoard();

        assertTrue(board.isNearbySpaceFree(0,2)); //rn (0,2) is free to move
        assertTrue(board.isNearbySpaceFree(0, 3)); //rn (0,3) is free to move
        board.getBox(1, 3).increaseLevel();
        //(1,3) has level 2

        board.drawBoard();

        //but now (0,2) is blocked!
        assertFalse(board.isNearbySpaceFree(0,2));

        board.placeWorker(workerB2,4,4);

        board.getBox(3, 3).increaseLevel();
        board.getBox(3, 3).increaseLevel();
        // (3,3) has level 2
        board.getBox(3, 4).increaseLevel();
        board.getBox(3, 4).increaseLevel();
        // (3,4) has level 2
        board.getBox(4, 3).increaseLevel();
        board.getBox(4,3).increaseLevel();
        // (4,3) has level 2

        board.drawBoard();

        //check the corner worker
        assertFalse(board.isNearbySpaceFree(4,4));

        //I have to clear the board for future tests, because board is an instance
        clearBoardForFutureTests(board);

    }

    @Test
    public void artemisCanBeUsedTest() throws DataFormatException {
        Board board = new Board();
        Player playerA = new Player("Giulio", 22, 12, 1990);
        Worker workerA = new Worker(playerA, Color.GREEN, "A");

        Player playerB = new Player("Marco", 22, 12, 1985);
        Worker workerB = new Worker(playerB, Color.RED, "A");

        Player playerC = new Player("Franco", 22, 12, 1980);
        Worker workerC = new Worker(playerC, Color.RED, "A");
        clearBoardForFutureTests(board);

        board.placeWorker(workerA, 1,2);
        board.placeWorker(workerB, 1,1);
        board.placeWorker(workerC, 2 ,2);
        board.getBox(0,0).placeDome();
        board.getBox(0,1).placeDome();
        board.getBox(0,2).placeDome();
        board.getBox(0,3).placeDome();
        board.getBox(2,1).placeDome();
        board.getBox(0,4).placeDome();
        board.getBox(2,3).placeDome();
        board.getBox(1,4).placeDome();
        board.getBox(2,4).placeDome();
        //in 1,2 it is already blocked
        assertFalse(board.artemisCanBeUsed(1,2));
        board.drawBoard();
        board.getBox(3,1).placeDome();
        board.getBox(3,3).placeDome();
        board.getBox(4,1).placeDome();
        board.getBox(4,3).placeDome();
        board.drawBoard();
        //while in 2,2 it is not
        assertTrue(board.artemisCanBeUsed(2,2));
        //now i block also 2,2
        board.getBox(4,2).placeDome();
        assertFalse(board.artemisCanBeUsed(2,2));
        board.drawBoard();
        //1,1 was never blocked
        assertTrue(board.artemisCanBeUsed(1,1));
        board.getBox(2,3).decreaseLevel();
        //now i remove a dome and 1,2 and 2,2 could use the effect
        assertTrue(board.artemisCanBeUsed(1,2));
        assertTrue(board.artemisCanBeUsed(2,2));
        board.drawBoard();
        //1,1 will now be blocked
        board.getBox(1,0).placeDome();
        board.getBox(2,0).placeDome();
        assertFalse(board.artemisCanBeUsed(1,1));
        board.drawBoard();
        //1,2 and 2,2 will be blocked again
        board.getBox(1,3).placeDome();
        board.getBox(3,4).placeDome();
        board.getBox(3,2).placeDome();
        assertFalse(board.artemisCanBeUsed(1,2));
        assertFalse(board.artemisCanBeUsed(2,2));
        board.drawBoard();

        clearBoardForFutureTests(board);
    }

    @Test
    public void demeterCanBeUsedTest() throws DataFormatException {
        Board board = new Board();
        Player playerA = new Player("Giulio", 22, 12, 1990);
        Worker workerA = new Worker(playerA, Color.GREEN, "A");

        Player playerB = new Player("Marco", 22, 12, 1985);
        Worker workerB = new Worker(playerB, Color.RED, "A");

        Player playerC = new Player("Franco", 22, 12, 1980);
        Worker workerC = new Worker(playerC, Color.RED, "A");
        clearBoardForFutureTests(board);

        board.placeWorker(workerA, 1,2);
        board.placeWorker(workerB, 1,1);
        board.placeWorker(workerC, 2 ,2);

        board.getBox(0,0).placeDome();
        board.getBox(0,1).placeDome();
        board.getBox(0,2).placeDome();
        board.getBox(0,3).placeDome();
        board.getBox(2,1).placeDome();
        board.getBox(0,4).placeDome();
        board.getBox(2,3).placeDome();
        board.getBox(1,4).placeDome();
        board.getBox(2,4).placeDome();
        board.drawBoard();
        //the worker in 1,2 cannot use the effect, the others can
        assertFalse(board.demeterCanBeUSed(1,2));
        assertTrue(board.demeterCanBeUSed(2,2));
        assertTrue(board.demeterCanBeUSed(1,1));
        board.getBox(3,3).placeDome();
        board.getBox(3,1).placeDome();
        board.getBox(3,2).placeDome();
        //i'll block the one in 2,2
        assertFalse(board.demeterCanBeUSed(2,2));
        board.getBox(3,0).placeDome();
        board.drawBoard();
        //leave just two free spaces to the one in 1,1
        assertTrue(board.demeterCanBeUSed(1,1));
        board.getBox(2,0).placeDome();
        //and then even him won't be able to use the effect
        assertFalse(board.demeterCanBeUSed(1,1));
        //then i free a position far from the worker in 1,2, still cannot use the effect
        board.getBox(0,4).decreaseLevel();
        assertFalse(board.demeterCanBeUSed(1,2));
        //then i free one near him, now he could use the effect
        board.getBox(0,3).decreaseLevel();
        assertTrue(board.demeterCanBeUSed(1,2));
        board.drawBoard();
    }

    @Test
    public void cloneBoardTest () throws DataFormatException {
        Player playerA = new Player("Giulio", 22, 12, 1990);
        Worker workerA = new Worker(playerA, Color.GREEN, "A");
        Board board = new Board();
        board.getBox(1,2).increaseLevel();
        board.getBox(0,2).increaseLevel();
        board.getBox(0,2).increaseLevel();
        board.placeWorker(workerA, 3,0);


        Board clonedBoard = board.cloneBoard();
        for (int r = 0 ; r < 5; r++) {
            for (int c = 0 ; c < 5; c++){
                assertEquals(clonedBoard.getBox(r, c).toString(), board.getBox(r,c).toString());
            }
        }
        assertEquals(board.getBox(1,2).getTower().size(), clonedBoard.getBox(1,2).getTower().size());
        assertEquals(board.getBox(1,2).getTower().get(0).getLevel(), clonedBoard.getBox(1,2).getTower().get(0).getLevel());
        assertEquals(board.getBox(0,2).getTower().size(), clonedBoard.getBox(0,2).getTower().size());
        assertEquals(board.getBox(0,2).getTower().get(1).getLevel(), clonedBoard.getBox(0,2).getTower().get(1).getLevel());
        assertEquals(board.getBox(3,0).getOccupier(), clonedBoard.getBox(3,0).getOccupier());
        assertNull(clonedBoard.getBox(4, 0).getOccupier());


    }

    @Test
    public void PhotoGraphyTest() throws DataFormatException{
        Board board = new Board();
        clearBoardForFutureTests(board);
        Player playerA = new Player("Giulio", 22, 12, 1990);
        Worker workerA = new Worker(playerA, Color.GREEN, "A");
        board.getBox(2,3).increaseLevel();
        board.getBox(2,3).increaseLevel();
        board.getBox(2,3).increaseLevel();
        board.getBox(4,1).placeDome();
        board.placeWorker(workerA, 2,2);
        board.getBox(2,2).increaseLevel();


        BoardPhotography photo = board.takePhotograph();
        //level three without dome or worker
        assertEquals(photo.getBoxPhoto(2,3).getLevel(), 3);
        assertFalse(photo.getBoxPhoto(2,3).isDomed());
        assertFalse(photo.getBoxPhoto(2,3).isOccupied());
        //with worker
        assertEquals(photo.getBoxPhoto(4,1).getLevel(), 1);
        assertTrue(photo.getBoxPhoto(4,1).isDomed());
        assertFalse(photo.getBoxPhoto(4,1).isOccupied());
        //with dome on level 1
        assertTrue(photo.getBoxPhoto(2,2).isOccupied());
        assertEquals(photo.getBoxPhoto(2,2).getLevel(), 1);
        assertFalse(photo.getBoxPhoto(2,2).isDomed());
        board.drawBoard();
        photo.show();
    }

    /*@Test
    public void increaseLevelTest() {
        Board board = new Board();

        board.increaseLevel(0, 2);
        board.increaseLevel(0, 2);
        board.increaseLevel(0, 2);
        board.increaseLevel(0, 2);
        // (0,2) has level 4
        board.increaseLevel(0, 2);
        //the tower is complete

        board.drawBoard();

        board.increaseLevel(1, 3); //ok
        board.increaseLevel(2, 4); //ok
        board.increaseLevel(0, 5); //outside the board
        board.increaseLevel(0, 6); //outside the board
        board.drawBoard();
    }*/

    /*@Test
    void placeWorker() {
        Board b = new Board();
        Player p1 = null;
        try { p1 = new Player("Marco", 2, 2,2000);}
        catch (DataFormatException e) {e.printStackTrace();}
        Worker w1 = new Worker( p1, "Y", "A" );
        Worker w2 = new Worker( p1, "Y", "B" );
        b.placeWorker(w1, 1, 3);
        b.placeWorker(w2, 2, 0);

        b.drawBoard();
    }

    @Test
    void increaseLevel() {
        Board b = new Board();
        b.increaseLevel(0, 0);
        b.increaseLevel(0, 0);
        b.increaseLevel(2, 3);
        b.drawBoard();

    }*/

}