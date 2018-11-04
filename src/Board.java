import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class Board
{
    // Array to hold board cards
    private Card cards[];

    // Resource loader
    private ClassLoader loader = getClass().getClassLoader();

    public Board(int size, ActionListener AL)
    {
        // Allocate and configure the game board: an array of cards
        cards = new Card[size];

        // Fill the Cards array
        int imageIdx = 1;
        for (int i = 0; i < size; i++) {

            // Load the front image from the resources folder
            String imgPath = "res/hub" + imageIdx + ".jpg";
            ImageIcon img = new ImageIcon(loader.getResource(imgPath));

            // Setup one card at a time
            Card c = new Card(img);
            c.addActionListener(AL);
            c.setID(imageIdx);
            if(imageIdx == 13){
                c.setCustomName("wildcard");
            }
            else{
                c.setCustomName("card " + i);
            }
            c.hideFront();

            // Add them to the array
            cards[i] = c;

            if(i % 2 != 0){ //We only want two cards to have the same image, so change the index on every odd i
                imageIdx++;  // get ready for the next pair of cards
            }
        }

        randomizeCards();
    }

    public void randomizeCards(){
        //randomizes card positions
        Random numGen = new Random();
        for(int i=0; i<cards.length; i++){
            int random = numGen.nextInt(cards.length);
            Card temp = cards[i];
            cards[i] = cards[random];
            cards[random] = temp;
        }
    }

    public void fillBoardView(JPanel view){
        for (Card c : cards) {
            view.add(c);
        }
    }

    public void resetBoard()
    {
        for(int i=0; i<cards.length; i++){
            cards[i].hideFront();
            cards[i].setEnabled(true);
        }

        randomizeCards();
    }
}