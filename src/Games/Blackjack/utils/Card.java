package Games.Blackjack.utils;

import java.io.Serializable;

public class Card implements Serializable {

  private static final String[] Suit = {"Hearts", "Clubs", "Spades", "Diamonds"};
  private static final String[] Rank = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};
  private static final int[] Value = {11, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10};

  private int cardSuit;
  private int cardRank;
  private int value;
  private boolean isHidden = false;

  public Card() {
    isHidden = true;
  }

  public Card(int suit, int rank) {
    cardRank = (int) rank;
    cardSuit = (int) suit;
    value = Value[cardRank];
  }

  public Card deepCopy() {
    if (isHidden) return new Card();
    return new Card(cardSuit, cardRank);
  }

  @Override
  public String toString() {
    if (isHidden) {return "Hidden";}
    return Rank[cardRank] + " of " + Suit[cardSuit];
  }

  public boolean isAce() {
    return this.cardRank == 0;
  }

  public int getValue() {
    if (isHidden) {return 0;}
    return value;
  }

  public String getFilename() {
    if (isHidden) {return "back.png";}
    return Suit[cardSuit].toLowerCase() + "_" + Rank[cardRank].toLowerCase() +".png";
  }

}
