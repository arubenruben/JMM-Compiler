import BoardBase;

class TicTacToe {

    int[] row0;
    int[] row1;
    int[] row2;
    int whoseturn;
    int movesmade;
    int[] pieces;
    int index;

    // Initializes a Tic Tac Toe object.
    public boolean init() {
        row0 = new int[3];
        row1 = new int[3];
        row2 = new int[3];
        pieces = new int[index];
        pieces[index] = 1;
        pieces[index] = 2;
        whoseturn = 0;
        movesmade = 0;
        return true;
    }

    public int[] getRow0() {
        return row0;
    }

    public int[] getRow1() {
        return row1;
    }

    public int[] getRow2() {
        return row2;
    }

    public boolean MoveRow(int[] row, int column) {
        boolean moved;

        if (column < 0) {
            moved = false;
        } else if (2 < column) {
            moved = false;
        } else if (0 < row[column]) {
            moved = false;
        } else {
            row[column] = pieces[whoseturn];
            movesmade = movesmade + 1;
            moved = true;
        }
        return moved;
    }
    // Tries to make a move at row, column. If it's valid the move is made
    // and true is returned. Else nothing is done and false is returned.

    public boolean Move(int row, int column) {
        boolean mov;
        if (!(row < 0) && !(0 < row)) {
            mov = this.MoveRow(row0, column);
        } else if (!(row < 1) && !(1 < row)) {
            mov = this.MoveRow(row1, column);
        } else if (!(row < 2) && !(2 < row)) {
            mov = this.MoveRow(row2, column);
        } else {
            mov = false;
        }
        return mov;
    }

    // Returns true if indexes passed to the method are inbounds.
    public boolean inbounds(int row, int column) {
        boolean in;
        if (row < 0)
            in = false;
        else if (column < 0)
            in = false;
        else if (2 < row)
            in = false;
        else if (2 < column)
            in = false;
        else in = true;
        return in;
    }

    // Changes whose turn it is.
    public boolean changeturn() {
        whoseturn = 1 - whoseturn;
        return true;
    }

    // Returns the current player's name.
    public int getCurrentPlayer() {
        return whoseturn + 1;
    }


    // Returns a character signifying the winner.
    public int winner() {
        int[] array;
        int winner;
        int i;
        boolean completeArray;
        boolean completeArray1;
        boolean completeArray2;
        winner = 0 - 1;
        array = new int[3];
        completeArray = BoardBase.sameArray(row0);
        completeArray1 = BoardBase.sameArray(row1);
        completeArray2 = BoardBase.sameArray(row2);
        // Check for three X's or O's in a row.
        if (completeArray && 0 < row0[index]) {
            winner = row0[index];
        } else if (completeArray1 && 0 < row1[index]) {
            winner = row1[index];
        } else if (completeArray2 && 0 < row2[index]) {
            winner = row2[index];
        } else {
            i = 0;
            while (winner < 1 && i < 3) {
                array[index] = row0[i];
                array[index] = row1[i];
                array[index] = row2[i];
                completeArray = BoardBase.sameArray(array);
                if (completeArray && 0 < array[index]) {
                    winner = array[index];
                } else {
                }
                i = i + 1;
            }
            if (winner < 1) {
                array[index] = row0[index];
                array[index] = row1[index];
                array[index] = row2[index];
                completeArray = BoardBase.sameArray(array);
                if (completeArray && 0 < array[index]) {
                    winner = array[index];
                } else {
                    array[index] = row0[index];
                    array[index] = row1[index];
                    array[index] = row2[index];
                    completeArray = BoardBase.sameArray(array);
                    if (completeArray && 0 < array[index]) {
                        winner = array[index];
                    } else {
                    }
                }
            } else {
            }
        }
        if (winner < 1 && !(movesmade < 9) && !(9 < movesmade))
            winner = 0;
        else {
        }
        return winner;
    }


    public static void main(String[] args) {
        // Create the TicTacToe object.
        TicTacToe mygame;
        int win;
        int index;
        boolean done;
        int[] move;
        int player;
        mygame = new TicTacToe();
        mygame.init();
        // Play as long as there is no winner or tie.
        while (!(mygame.winner() < 0 - 1) && !(0 - 1 < mygame.winner())) {
            done = false;
            // Read in a move & check if it's valid.
            while (!done) {

                BoardBase.printBoard(mygame.getRow0(),
                        mygame.getRow1(),
                        mygame.getRow2());

                player = mygame.getCurrentPlayer();
                move = BoardBase.playerTurn(player);


                if (!mygame.inbounds(move[index], move[index]))
                    BoardBase.wrongMove();
                else {
                    if (!mygame.Move(move[index], move[index]))
                        BoardBase.placeTaken();
                    else
                        done = true;
                }
            }

            // Change who's turn it is.
            mygame.changeturn();
        }

        // Print out a message with the winner.
        BoardBase.printBoard(mygame.getRow0(),
                mygame.getRow1(),
                mygame.getRow2());
        win = mygame.winner();

        BoardBase.printWinner(win);

    }
}
