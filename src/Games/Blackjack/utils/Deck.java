package Games.Blackjack.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

  List<Card> cards = new ArrayList<>();

  public Deck() {
    for (int suite = 0; suite < 4; suite++) {
      for (int rank = 0; rank< 13; rank++ ) {
        cards.add(new Card(suite, rank));
      }
    }
  }

  public Deck(int num_decks) {

    for (int i = 0; i < num_decks; i++) {
      for (int suite = 0; suite < 4; suite++) {
        for (int rank = 0; rank< 13; rank++ ) {
          cards.add(new Card(suite, rank));
        }
      }
    }

  }

  public void shuffle() {
    Collections.shuffle(cards);
  }

  public Card deal() {
    return cards.remove(0);
  }



}
