package summativeassignment.m00910464;

import java.util.*;

public class DiceGame {

    public static int round = 1; // initial round
    public static int playerTurn; // holds and updates who's turn it is
    // holds all data to be filled in the scoring table when printed
    public static final String[][] SCORING_TABLE = new String[9][3];
    // initial 0, will be progressively filled with categories that have been played
    public static final int[][] SELECTED_CATEGORIES = new int[7][2];
    // array to hold the dice set aside, resets all elements to 0 after a play
    public static final int[] SEQUENCE = new int[5];

    public static void main(String[] args) {
        System.out.println("\t\t   WELCOME! A simple dice game.");
        initiateTableMatrix();
        final int ROUNDS_TO_PLAY = 8; // rounds to be played
        while (round < ROUNDS_TO_PLAY) { // loops 8 times(rounds)

            // print round number at the beginning of every round
            System.out.println("\n----------");
            System.out.println("  ROUND " + round + "  ");
            System.out.println("----------");
            
            initiateRound(); // get player to throw
            round++; // next round
        }
        // passing ROUND_TO_PLAY, reference of the TOTAL row in the table
        determineWinner(ROUNDS_TO_PLAY);
    }

    // initiate a round play, turns are switched as well in this method
    public static void initiateRound() {
        int turn = 1; // player1 goes first
        final int THROW_TIMES = 3; // throws allowed
        final int TOTAL_ROW = 8;
        // loop executed so that both players take a turn in the round
        while (turn < THROW_TIMES) {
            System.out.println("");
            playerTurn = turn; // set player's turn
            getPlayerToThrow(THROW_TIMES);
            
            // after each turn completion
            //reset the SEQUENCE array
            for (int i = 0; i < SEQUENCE.length; i++) {
                SEQUENCE[i] = 0;
            }
            updateTotal(); // updates the total of a player after turn is completed
            printScoringTable();
            System.out.println("\n" + getPlayerTurn() + " scored a total of " 
                                + SCORING_TABLE[getSelectedCategory()][playerTurn] + " this round "
                                + "which got the score to " + SCORING_TABLE[TOTAL_ROW][playerTurn] + ".");
            // turn separator line
            System.out.println("\n----------------------------------------------------------------------");
            turn++; // change turn
        }
    }

    // the method initiates the array SCORING_TABLE with the initial values
    public static void initiateTableMatrix() {
        String[] rowReferenceArray = {"", "Ones", "Twos", "Threes", "Fours", // first column elements
            "Fives", "Sixes", "Sequence 20", "TOTAL"};
        final int NUMBER_OF_ROWS = 9; // to generate 9 rows
        final int NUMBER_OF_COLUMNS = 3; // to generate 9 columns

        for (int row = 0; row < NUMBER_OF_ROWS; row++) {

            for (int column = 0; column < NUMBER_OF_COLUMNS; column++) {
                // loads the first column of the array with static data
                if (column == 0) {
                    SCORING_TABLE[row][column] = rowReferenceArray[row];
                } else {// loads the rest columns of the array with data that is dynamic
                    SCORING_TABLE[row][column] = getPositionValue(row, column);
                }
            }
        }

        printScoringTable(); // display at the beginning of the game
    }

    // returns default values to fill in the table
    public static String getPositionValue(int rowNumber, int colNumber) {
        String element = "";
        final int HEADER = 0; // index value for header is 0
        final int FOOTER = 8; // index value for "TOTAL" row is 8

        // when is a header or a footer
        switch (rowNumber) {
            case HEADER:
                element = (colNumber == 1) ? "Player 1" : "Player 2"; // non-dynamic data
                break;
            case FOOTER:
                element = "0"; // TOTAL initial 0
        }

        /* returns empty string when rowNumber is between 0 and 8
        as a placeholder for one category score until is played */
        return element;
    }

    // print array and its value in the scoring board
    public static void printScoringTable() {
        final int NUMBER_OF_LINES = 9;
        // constants holding relevant column index
        final int STATIC_DATA_INDEX = 0;
        final int PLAYER1_COLUMN = 1;
        final int PLAYER2_COLUMN = 2;

        System.out.println("");
        final String ROW_SEPARATOR = "\t---------------------------------------------------\n";
        for (int row = 0; row < NUMBER_OF_LINES; row++) {
            String firstElement = SCORING_TABLE[row][STATIC_DATA_INDEX]; // element relevant of category column
            String secondElement = SCORING_TABLE[row][PLAYER1_COLUMN]; // element relevant Player1 column
            String thirdElement = SCORING_TABLE[row][PLAYER2_COLUMN]; // element relevant Player2 column

            if (row == STATIC_DATA_INDEX) { // header
                System.out.print(ROW_SEPARATOR);
                System.out.printf("\t| %-14s|%12s%4s|%12s%4s|\n", firstElement, secondElement, "", thirdElement, "");
                System.out.print(ROW_SEPARATOR);
            } else { // rest of the table
                System.out.printf("\t| %-14s|%9s%7s|%9s%7s|\n", firstElement, secondElement, "", thirdElement, "");
                System.out.print(ROW_SEPARATOR);
            }
        }
    }

    // method to have the player throwing 3 times, stops at first or second throw if all dice matched
    public static void getPlayerToThrow(final int THROW_TIMES) {
        int diceToThrow = 5; // initial throw, will change whitin the loop if dice matches category
        final int SEQUENCE_REFERENCE = 7; // sequence category reference

        for (int throwCounter = 0; throwCounter < THROW_TIMES; throwCounter++) {
            char playerChoice = ' ';
            
            do {
                try {
                    printWhoThrows(throwCounter, diceToThrow);
                    playerChoice = promptToChoose();
                    // attemptThrow, throws an exception if wrong input given
                    // returns dice left to be thrown
                    diceToThrow = attemptThrow(playerChoice, diceToThrow, throwCounter);
                } catch (InputMismatchException e) {
                    System.out.println("\nPlease select a valid option:");
                }
                
                // when no more dice left to throw, after the first or second throw 
                if (diceToThrow == 0 && getSelectedCategory() < SEQUENCE_REFERENCE) {// and category is not sequence, to print below
                    System.out.println("\nAll dice are matched, well done!");
                    break; // to exit do-while loop
                }
                
            } while (!(isThrowing(playerChoice) || isForfeiting(playerChoice))); // until right input given

            /* when diceToThrow becomes 0, all dice are matched and the current
            throw is the first or second then break the for loop to avoid a third throw*/
            if (diceToThrow == 0 && throwCounter < THROW_TIMES - 1) {
                break; // exit for loop 
            }
        }
    }
    
    // void method to print who's throw and what turn it is
    public static void printWhoThrows(int throwNumber, int diceToThrow) {
        switch (throwNumber) {
            case 0:
                System.out.print("First throw of this turn, "
                        + getPlayerTurn() + " to throw " + diceToThrow + dieOrDice(diceToThrow) + "."
                        + "\nThrow " + diceToThrow + dieOrDice(diceToThrow) + " , enter 't' to throw"
                        + " or 'f' to forfeit > ");
                break;
            case 1:
                System.out.print("\nNext throw of this turn, "
                        + getPlayerTurn() + " to throw " + diceToThrow + dieOrDice(diceToThrow) + "."
                        + "\nThrow " + diceToThrow + dieOrDice(diceToThrow) + " , enter 't' to throw"
                        + " or 'f' to forfeit > ");
                break;
            case 2:
                System.out.print("\nLast throw of this turn, "
                        + getPlayerTurn() + " to throw " + diceToThrow + dieOrDice(diceToThrow) + "."
                        + "\nThrow " + diceToThrow + dieOrDice(diceToThrow) + " , enter 't' to throw"
                        + " or 'f' to forfeit > ");
        }
    }
    
    //  used to read inputs for everything apart of sequence labels selection, returns char
    public static char promptToChoose()
            throws InputMismatchException {
        String userChoice;
        Scanner getInput = new Scanner(System.in);
        userChoice = getInput.nextLine().trim();
        if (!(userChoice.length() == 1)) {
            throw new InputMismatchException("Not a char");
        }

        return userChoice.charAt(0);
    }

    // returns singular or plural form of the word
    public static String dieOrDice(int diceToThrow) {
        return (diceToThrow == 1) ? " die" : " dice";
    }

    // get who's turn it is
    public static String getPlayerTurn() {
        return (playerTurn == 1) ? "Player 1" : "Player 2";
    }

    /* if a valid choice is made, is either throwin or forfeiting, throws exception if input is invalid
    returns the number of dice left to be thrown if dice are thrown and category played*/
    public static int attemptThrow(char choice, int diceToThrow, int throwCounter)
            throws InputMismatchException {
        final int FIRST_THROW = 0;
        final int SECOND_THROW = FIRST_THROW + 1;
        
        if (isForfeiting(choice)) {
            System.out.println("\n" + getPlayerTurn() + " lost.");
            // end the game
            System.exit(0);
        } else if (isThrowing(choice)) {
            // printing how many throws remained after current throw
            if (throwCounter == FIRST_THROW) { 
                System.out.println("\n2 throws remaining...");
            } else if (throwCounter == SECOND_THROW) {
                System.out.println("\n1 throw remaining...");
            }
            
            // throwDice return the dice left to be thrown executes next step
            diceToThrow = throwDice(diceToThrow, throwCounter);
        } else { // wrong input
            throw new InputMismatchException("Enter 't' or 'f'");
        }
        
        return diceToThrow; // dice left to be thrown returned
    }

    public static boolean isForfeiting(char choice) { // method checking if forfeiting
        return choice == 'f';
    }

    public static boolean isThrowing(char choice) { // method checking if throwing
        return choice == 't';
    }

    // method that "throws" the dice, returns dice left to be thrown
    public static int throwDice(int diceToThrow, int throwCounter) {
        // save generated thrown dice in an array
        int[] generatedThrow = generateDiceThrow(diceToThrow);

        final int ARRAY_DEFAULT_VALUE = 0; // to represent default value of an unmodified array
        final int SEQUENCE_PLAY = 7;
        
        // print the throw
        printThrownDice(generatedThrow);

        // when the player hasn't selected category
        if (getSelectedCategory() == ARRAY_DEFAULT_VALUE) {
            deferringOrSelecting(throwCounter);
        }

        if (!(getSelectedCategory() == ARRAY_DEFAULT_VALUE)) { // case when already selected
            // redirects to the relevant category play
            if (getSelectedCategory() == SEQUENCE_PLAY) { // if playing the sequence
                // checkSequence also returns dice left to be thrown after selection
                diceToThrow = checkSequence(generatedThrow, diceToThrow, throwCounter);
            } else { // any other category play
                // adds score to the table if dice matched the category, otherwise adds 0
                diceToThrow = checkAndAddScore(generatedThrow); 
            }
        }

        // dice left to be thrown, 5 if no match or any kept aside
        return diceToThrow;
    }

    // method to generate a throw and save into an array which is returned
    public static int[] generateDiceThrow(final int TO_THROW) {
        // TO_THROW number of dice to be thrown
        int[] generatedThrow = new int[TO_THROW];

        // generate the number on the die
        for (int count = 0; count < TO_THROW; count++) {
            generatedThrow[count] = 1 + (int) (Math.random() * 6); // in the range 1 to 6 inclusive
        }
        // array of thrown dice
        return generatedThrow;
    }

    // prints a throw of random generated dice
    public static void printThrownDice(int[] thrownDice) {
        System.out.print("\nThrow: ");
        for (int i : thrownDice) {// prints dice throw
            System.out.print(formatDiceDisplay(i) + " ");
        }
        System.out.println("");
    }

    // method that prints a die in a certain format
    public static String formatDiceDisplay(int die) {
        return " [ " + die + " ] ";
    }
    
    // getting user to select or defer
    public static void deferringOrSelecting(int throwCounter) {
        char choice = ' ';
        final int LAST_THROW = 2; // last throw of the turn
        
        // prompt the user to select or defer
        while (!(isSelectingCategory(choice) || isDeferring(choice)) && throwCounter < LAST_THROW) {
            System.out.print("Enter 's' to select category (number on die/dice) "
                    + " or 'd' to defer > ");
            try {
                choice = promptToChoose();
                deferringOrSelecting(choice);
            } catch (InputMismatchException e) {
                System.out.println("\nPlease select a valid option:");
            }
        }

        if (throwCounter == LAST_THROW) {// last throw of the turn and category not selected
            System.out.println("You must select a category now!");
            selectCategory();
        }
    }

    // method overloaded, to check user input, throws exception if the input is not acceptable
    public static void deferringOrSelecting(char selection)
            throws InputMismatchException {

        if (isDeferring(selection)) { // print message, continue continue to next throw
            System.out.println("\nSelection deferred.");
        } else if (isSelectingCategory(selection)) {
            selectCategory();
        } else {
            throw new InputMismatchException("Only char 's' or 'd' accepted.");
        }
    }

    public static boolean isDeferring(char selection) { // to check if deferring
        return selection == 'd';
    }

    public static boolean isSelectingCategory(char selection) { // to check if throwing
        return selection == 's';
    }

    // prompts user to select a category
    public static void selectCategory() {
        char choice = ' ';
        int integerChoice = -1; // out of range value
        
        do {
            try {
                System.out.print("Ones (1) Twos (2) Threes (3) Fours (4) Fives (5) "
                        + "Sixes (6) or Sequence (7) > ");
                choice = promptToChoose();

                integerChoice = choice - '0'; // compute the char to its integer value
                
                if (integerChoice < 1 || integerChoice > 7)
                    System.out.println("\nSelect a number from the list...");
            } catch (InputMismatchException e) {
                System.out.println("\nSelect a number from the list...");
            }
        } while (!(choice > '0' && choice < '8')); // until the right selectedLabelsString is given
        
        if (wasNotAlreadySelected(integerChoice)) { // if wasn't selected, set in the SELECTED_CATEGORIES array
            System.out.println(SCORING_TABLE[integerChoice][0] + " selected.\n");
            // set category selection
            SELECTED_CATEGORIES[round - 1][playerTurn - 1] = integerChoice;
        } else {
            // if playerChoice already made, loop to choose another
            System.out.println(SCORING_TABLE[integerChoice][0] + " already played, try again.");
            selectCategory();
        }
    }

    // loops array to check category selections, returns true if not selected
    public static boolean wasNotAlreadySelected(int categorySelection) {
        boolean result = true;
        for (int i = 0; i < round; i++) { // check all choices in the previous rounds
            // change result to false when two elements are equal
            if (SELECTED_CATEGORIES[i][playerTurn - 1] == categorySelection) {
                result = false;
                break;
            }
        }
        return result;
    }

    // returns the category that has been selected for the current turn
    public static int getSelectedCategory() {
        return SELECTED_CATEGORIES[round - 1][playerTurn - 1];
    }

    // holds cases to check for a sequence 
    public static int checkSequence(int[] diceThrown, int diceToThrow, int throwCounter) {
        
        // copy contents of the diceThrown into a new one
        int[] arrayOfThrownDice = new int[diceThrown.length];
        System.arraycopy(diceThrown, 0, arrayOfThrownDice, 0, diceThrown.length);

        // if last throw, automatically check if the dice thrown make a SEQUENCE or not
        if (throwCounter == 2) {
            setDiceInSequence(diceThrown); 
            if (isSequence(SEQUENCE)) { // if a sequence is validated
                sequenceConfirmedAction();
                diceToThrow = 0; // set as 0 to skip selection of dice
            } else { 
                sequenceInvalidAction();
                diceToThrow = 0;
            }
        } else if (isSequence(diceThrown)) { // diceThrown is getting sorted in isSequence
            sequenceConfirmedAction();
            diceToThrow = 0;
        } else if (diceToThrow != 0) { // if full throw wasn't a SEQUENCE, advance to select dice to set aside
            diceToThrow = printOptionsAndSelect(arrayOfThrownDice); // to select options from unsorted array
        }
        
        return diceToThrow;
    }

    // to check if an array is one of the accepted sequences
    public static boolean isSequence(int[] arrayOfDice) {
        // constants of accepted sequences
        final int[] FIRST_ACCEPTED_SEQ = {1, 2, 3, 4, 5};
        final int[] SECOND_ACCEPTED_SEQ = {2, 3, 4, 5, 6};
        Arrays.sort(arrayOfDice);
        return Arrays.equals(arrayOfDice, FIRST_ACCEPTED_SEQ)
                || Arrays.equals(arrayOfDice, SECOND_ACCEPTED_SEQ);
    }

    public static int printOptionsAndSelect(int[] diceThrown){
        int[] selection;
        do{
            StringTokenizer newInput;
            do{
                int count = 0;
                // print options
                System.out.println(count + ". None");
                for (int i : diceThrown) { // print each dice with a label before it
                    count++;
                    System.out.println(count + ". " + formatDiceDisplay(i));
                }

                // save input
                System.out.print("Enter which dice you wish to set aside using the number labels "
                    + "separated by a space (e.g., 1 3 5), or enter 0 for none > ");
                String selectedLabelsString = promptToSelectSequence(); // get input
                newInput = new StringTokenizer(selectedLabelsString);
                
                if(!(newInput.hasMoreTokens())) // case where nothing is entered
                        System.out.println("\nYou have to select at least one label...");

            } while (!(newInput.hasMoreTokens())); // loops if enter is pressed without entering anything  
            
            selection = readTokensInArray(newInput, diceThrown);
            
        } while (isSelectionInvalid(selection, diceThrown.length)); 
        
        if (selection.length == 1 && selection[0] == 0) { // 0 selected
            System.out.println("\nYou haven't selected anything, better luck next time!");
            return diceThrown.length; // same number of thrown dice to be thrown again
        } else {
            return sequenceValidation(selection, diceThrown); // validates sequence, returns dice left to be thrown
        }
    }
    
    // used to select labels of dice to put in the sequence, returns String of selected options
    public static String promptToSelectSequence() {
        String userChoice;
        Scanner getInput = new Scanner(System.in);
        userChoice = getInput.nextLine().trim();
        return userChoice;
    }
    
    public static int[] readTokensInArray(StringTokenizer input, int[] diceThrown) {
        int[] selection = new int[input.countTokens()];
        final int INVALID_FORMAT = -1; // a way to tell the program the wrong input was given
        
        // save tokens in an array
        for (int i = 0; input.hasMoreTokens(); i++) {
            try {
                // convert tokens to int
                int tokenStringToNum = Integer.parseInt(input.nextToken());

                if (tokenStringToNum >= 0 && tokenStringToNum <= diceThrown.length) {
                    selection[i] = tokenStringToNum;
                } else { // when the selectedLabelsString is not in range of the options
                    selection[i] = INVALID_FORMAT; // set value to invalid format
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                // if conversion failed, string not number
                selection[i] = INVALID_FORMAT;
            }
        }
        
        return selection;
    }
    
    // method to check the selection of labels to set aside dice in the sequence
    public static boolean isSelectionInvalid(int[] selection, int numberThrownDice){
        boolean result = false;
        final int INVALID_FORMAT = -1;
        
        for (int i : selection) {
            if (selection.length > 1 && i == 0) {
                System.out.println("\nYou must select the right labels...");
                System.out.println("If you want to select none, enter 0 by itself!");
                result = true;
            } else if (i == INVALID_FORMAT) {
                System.out.println("\nEnter one or more whole numbers within the" +
                                    " range of below metioned list of labels...");
                result = true;
            } else if (hasDuplicates(selection)) {
                System.out.println("\nYou cannot pick two identical labels to establish a sequence...");
                result = true;
            } else if (selection.length > numberThrownDice) {
                System.out.println("\nYou cannot select more elements than dice you thrown...");
                result = true;
            }
            
            if(result) // to avoid printing all messages if multiple mistakes entered
                break;
        }
        
        return result;
    }
    
    // check if an array has duplicates
    public static boolean hasDuplicates(int[] anArray){
        boolean result = false;
        // to avoid checking last element, i will stop at (array.length - 1)
        for(int i = 0; i < anArray.length - 1; i++){
            // to compare with next elements of the array
            for(int j = i + 1; j < anArray.length; j++){
                if(anArray[i] == anArray[j])
                    result = true;
            }
        }
        
        return result;
    }
    
    /* overload check if an array has duplicates, apart from toIgnore integer
    used to ignore 0s in SEQUENCE when not all dice were selected*/
    public static boolean hasDuplicates(int[] anArray, int toIgnore){
        boolean result = false;
        // to avoid checking last element, i will stop at (array.length - 1)
        for(int i = 0; i < anArray.length - 1; i++){
            // skip toIgnore elemnt
            if(anArray[i] == toIgnore)
                continue;
            
            // to compare with next elements of the array
            for(int j = i + 1; j < anArray.length; j++){
                if(anArray[i] == anArray[j])
                    result = true;
            }
        }
        
        return result;
    }

    // validates a SEQUENCE, where diceToKeep is an array of player's selected dice to put aside
    public static int sequenceValidation(int[] diceToKeep, int[] diceThrown) {
        for (int i = 0; i < diceToKeep.length; i++) {
            final int POSITION_OF_DIE = diceToKeep[i] - 1; // index for die value in thrown dice array
            // rewriting the indexes of selected dice with values of the selected dice
            diceToKeep[i] = diceThrown[POSITION_OF_DIE];
        }

        // set in global variable the dice to keep
        setDiceInSequence(diceToKeep);

        Arrays.sort(SEQUENCE);
        printAsideDice();

        if (isSequence(SEQUENCE)) {
            sequenceConfirmedAction();
        } else if (isSequenceImpossible()){
            sequenceInvalidAction();
            return 0; // as dice left to be thrown, ends turn
        } else {
            for(int i = 0; i < SEQUENCE.length; i++){
                if (SEQUENCE[i] == 0){ // if an element is 0, it is an empty slot
                    System.out.println("\nIncomplete sequence... ");                    
                    break;
                } else if (i == SEQUENCE.length - 1) { // last element 
                    sequenceInvalidAction();
                }
            }
        }
        
        return diceThrown.length - diceToKeep.length; // dice left to be thrown
    }
    
    public static boolean isSequenceImpossible(){
        boolean isImpossible = false;
        final int IGNORE_NUMBER = 0; 
        
        if(hasDuplicates(SEQUENCE, IGNORE_NUMBER)){ // two identical values, ignore 0s when user haven't selected all 5 dice
            System.out.println("\nThere are two or more dice of the same value!");
            isImpossible = true;
        } else {
            for (int element : SEQUENCE)
                if(element == 1) // when a die of value 1 is in the array
                    for(int i = 0; i < SEQUENCE.length; i++)
                        if (SEQUENCE[i] == 6){ // check if there is also a die of value 6
                            System.out.println("\nIt is impossible to make a sequence having both dice values of 1 and 6!");
                            isImpossible = true;
                        }
        }
        
        return isImpossible;
    }

    // method to set aside dice selected by player
    public static void setDiceInSequence(int[] diceToSet) {
        final int EMPTY_SLOT = 0; // any value 0 in the array serves an empty slot

        // a die on index i of diceToSet array to be added to SEQUENCE array
        for (int i = 0; i < diceToSet.length; i++) {
            for (int j = 0; j < SEQUENCE.length; j++) {
                if (SEQUENCE[j] == EMPTY_SLOT) { // if slot empty in SEQUENCE array
                    SEQUENCE[j] = diceToSet[i]; // add dice value to the slot
                    break; // stop at first occurance of 0 in SEQUENCE array
                }
            }
        }
    }
    
    // prints confirmation message and adds score to the table
    public static void sequenceConfirmedAction(){
        System.out.println("\nCongratulations, a sequence has been established!");
        SCORING_TABLE[getSelectedCategory()][playerTurn] = 20 + "";
    }
    
    // prints confirmation message and adds score 0 to the table
    public static void sequenceInvalidAction(){
        System.out.println("\nA correct sequence has not been established...");
        SCORING_TABLE[getSelectedCategory()][playerTurn] = 0 + "";
    }

    // ----------------------------------------------------------SCORE COMPUTING
    public static int checkAndAddScore(int[] diceThrows) {
        int matches = getDiceMatches(diceThrows);
        try {
            int previousScore = Integer.parseInt(SCORING_TABLE[getSelectedCategory()][playerTurn]);
            SCORING_TABLE[getSelectedCategory()][playerTurn] = (matches * getSelectedCategory() + previousScore) + "";
        } catch (NumberFormatException e) { // if there is no previous score, and the element was an empty string
            SCORING_TABLE[getSelectedCategory()][playerTurn] = (matches * getSelectedCategory()) + "";
        }
        
        if (getSelectedCategory() < 7) { // display the dice set aside when is not SEQUENCE
            printAsideDice(diceThrows, matches);
        }
        
        return diceThrows.length - matches; // return number of dice that didn't match
    }

    // returns the number of matched dice in a throw
    public static int getDiceMatches(int[] diceThrows) {
        int count = 0;
        for (int i : diceThrows) {
            if (i == getSelectedCategory()) { // when one die is equal to category number, it is a match
                count++;
            }
        }
        return count;
    }

    /* void method to print the dice set aside in any category except sequence */
    public static void printAsideDice(int[] diceThrown, int currentTurnMatches) {
        final int MAX_THROWS = 5; // maximum possible thrown dice

        /* the difference between MAX_THROWS and number of dice thrown is the number of dice previously matched*/
        final int PREVIOUSLY_MATCHED_DICE = MAX_THROWS - diceThrown.length;

        if (currentTurnMatches == 0) { // no match this throw
            System.out.print("The throw had no die with value " + getSelectedCategory()
                    + ". Not setting any aside.");
        } else { // print all matches, current + previous
            System.out.print("The throw had " + currentTurnMatches + dieOrDice(currentTurnMatches)
                    + " with value " + getSelectedCategory() + ". ");
            System.out.print("Putting aside " + currentTurnMatches + dieOrDice(currentTurnMatches) + ": ");

            int matchedDice = PREVIOUSLY_MATCHED_DICE + currentTurnMatches;

            // prints DICE_MATCHED number of the category selection 
            for (int i = 0; i < matchedDice; i++) {
                System.out.print(formatDiceDisplay(getSelectedCategory()));
            }
        }
        System.out.println("");
    }
    
    /* method overloaded used for printing dice set aside to complete a sequence */
    public static void printAsideDice(){        
        System.out.print("You set aside the following dice: ");
        for (int i = 0; i < SEQUENCE.length; i++) {
            if (SEQUENCE[i] != 0) {
                System.out.print(formatDiceDisplay(SEQUENCE[i]));
            }
        }
        System.out.println("");
    }

    // update TOTAL score in the array
    public static void updateTotal() {
        final int TOTAL_ROW = 8;
        SCORING_TABLE[TOTAL_ROW][playerTurn] = getTotal();
    }

    // calculates the sum of all score in the 7 categories, and return it as a string
    public static String getTotal() {
        int result = 0;
        final int FIRST_CATEGORY = 1; // row position of first category in the array
        final int LAST_CATEGORY = 8; // row position of last category in the array

        // calculates the score of all categories
        for (int i = FIRST_CATEGORY; i < LAST_CATEGORY; i++) {
            try {
                result += Integer.parseInt(SCORING_TABLE[i][playerTurn]);
            } catch (NumberFormatException e) {
                // do nothing when one of the categories doesn't have a score
            }
        }
        return result + "";
    }
    
    public static void determineWinner(final int TOTAL_ROW){
        // determine the winner
        final int FINAL_SCORE_PLAYER1 = Integer.parseInt(SCORING_TABLE[TOTAL_ROW][1]);
        final int FINAL_SCORE_PLAYER2 = Integer.parseInt(SCORING_TABLE[TOTAL_ROW][2]);
        
        if (FINAL_SCORE_PLAYER1 > FINAL_SCORE_PLAYER2) {
            System.out.print("Player1 won the game with a score of " + FINAL_SCORE_PLAYER1 + ". Congratulations!");
        } else if (FINAL_SCORE_PLAYER1 == FINAL_SCORE_PLAYER2) {
            System.out.print("It is a tie, both scores are " + FINAL_SCORE_PLAYER1 + ".");
        } else {
            System.out.print("Player 2 won the game with a score of " + FINAL_SCORE_PLAYER2 + ". Congratulations!");
        }
        // bottom line... the top line is printed in initiateRound after every end of a turn
        System.out.println("\n----------------------------------------------------------------------");
        System.out.println("\n");
    }
}
