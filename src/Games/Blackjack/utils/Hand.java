package Games.Blackjack.utils;

import java.util.List;

public class Hand {

  public static int calculateValue(List<Card> cards) {

    int total = 0;
    int number_aces = 0;

    for (Card card : cards) {

      total += card.getValue();

      if (card.isAce()) {
        number_aces++;
      }

    }

    while (total > 21 && number_aces > 0) {

      total -= 10;
      number_aces--;

    }

    return total;


  };


}
