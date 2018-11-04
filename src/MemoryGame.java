import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MemoryGame extends JFrame implements ActionListener
{
    // Core game play objects
    private Board gameBoard;

    // Labels to display game info
    private JLabel matchesLabel, timerLabel, guessesLabel;

    // layout objects: Views of the board and the label area
    private JPanel boardView, labelView;

    // Record keeping counts and times
    private int seconds = 0, matchesMade = 0, guessesMade = 0;

    // Game timer: will be configured to trigger an event every second
    private Timer gameTimer;
    private long startTime;
    private long currentTime;

    //record keeping counts
    private boolean firstClicked = false;
    private int cardsFaceUp = 0;

    //temporary card objects used for comparison
    private Card currCard;
    private Card lastCard;

    public MemoryGame()
    {
        // Call the base class constructor
        super("Hubble Memory Game");

        // Allocate the interface elements
        JButton restart = new JButton("Restart");
        JButton quit = new JButton("Quit");
        timerLabel = new JLabel("Timer: 0");
        matchesLabel = new JLabel("Matches: 0");
        guessesLabel = new JLabel("Guesses: 0");

        //restarts the game on click
        restart.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                restartGame();
            }
        });

        //quits game on click
        quit.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        });

        //shows time for length of play session
        gameTimer = new Timer(1000, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - startTime;
                seconds++;
                timerLabel.setText("Timer: " + elapsedTime/1000 + " seconds");
            }
        });

        // Allocate two major panels to hold interface
        labelView = new JPanel();  // used to hold labels
        boardView = new JPanel();  // used to hold game board

        // get the content pane, onto which everything is eventually added
        Container c = getContentPane();

        // Setup the game board with cards
        gameBoard = new Board(25, this);

        // Add the game board to the board layout area
        boardView.setLayout(new GridLayout(5, 5, 2, 0));
        gameBoard.fillBoardView(boardView);

        // Add required interface elements to the "label" JPanel
        labelView.setLayout(new GridLayout(1, 5, 2, 2));
        labelView.add(quit);
        labelView.add(restart);
        labelView.add(timerLabel);
        labelView.add(matchesLabel);
        labelView.add(guessesLabel);

        // Both panels should now be individually laid out
        // Add both panels to the container
        c.add(labelView, BorderLayout.NORTH);
        c.add(boardView, BorderLayout.SOUTH);

        setSize(745, 500);
        setVisible(true);
    }

    /* Handle anything that gets clicked and that uses MemoryGame as an
     * ActionListener */
    public void actionPerformed(ActionEvent e)
    {
        //if this is the first card clicked in play session, begins timer from current time
        if(firstClicked == false){
            startTime = System.currentTimeMillis();
            firstClicked = true;
        }
        else{
            //if one card is face up and it is the wildcard, removes it from play
            if(cardsFaceUp == 1 && currCard.customName() == "wildcard"){
                currCard.setEnabled(false);
                cardsFaceUp = 0;
            }
            if(cardsFaceUp == 2){
                //if cards picked do not match
                if (lastCard.id() != currCard.id()) {
                    //if neither card is wildcard, flips them back down
                    if(lastCard.customName() != "wildcard" && currCard.customName() != "wildcard"){
                        lastCard.hideFront();
                        currCard.hideFront();
                    }
                    //if second picked card is wildcard, flips normal card back down, removes wildcard from play
                    else{
                        lastCard.hideFront();
                        currCard.setEnabled(false);
                    }
                }
                //if cards picked do match and user did not click same card twice
                else if(lastCard.customName() != currCard.customName()){
                    lastCard.setEnabled(false);
                    currCard.setEnabled(false);
                }
                //if user clicked same card twice, flips it back over
                else {
                    lastCard.hideFront();
                    currCard.hideFront();
                }
                cardsFaceUp = 0;
            }
        }
        gameTimer.start();

        // Get the currently clicked card from a click event
        currCard = (Card)e.getSource();
        currCard.showFront();
        cardsFaceUp++;

        //handles actions taken immediately after user clicks card
        switch(cardsFaceUp){
            case 0: break;
            //if only 1 card currently chosen, saves it to lastCard for future comparison
            case 1: if(currCard.customName() != "wildcard"){
                        lastCard = currCard;
                    }
                    break;
            //if two cards chosen, increments guesses and matches accordingly
            case 2: if(lastCard.id() != currCard.id()) {
                        if (lastCard.customName() != "wildcard" && currCard.customName() != "wildcard"){
                            guessesMade++;
                        }
                        else if (currCard.customName() == "wildcard"){
                            guessesMade++;
                        }
                    }
                    else if(lastCard.customName() != currCard.customName()){
                        guessesMade++;
                        matchesMade++;
                    }

                    guessesLabel.setText("Guesses: " + guessesMade);
                    matchesLabel.setText("Matches: " + matchesMade);
                    break;
        }
    }

    private void restartGame()
    {
        matchesMade = 0;
        guessesMade = 0;

        cardsFaceUp = 0;
        firstClicked = false;
        gameTimer.stop();

        timerLabel.setText("Timer: 0");
        matchesLabel.setText("Matches: 0");
        guessesLabel.setText("Guesses: 0");

        // Clear the boardView and have the gameBoard generate a new layout
        boardView.removeAll();
        gameBoard.resetBoard();
        gameBoard.fillBoardView(boardView);
    }

    public static void main(String args[])
    {
        MemoryGame M = new MemoryGame();
        M.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });
    }
}