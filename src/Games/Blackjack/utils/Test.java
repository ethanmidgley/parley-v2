package Games.Blackjack.utils;

import java.util.ArrayList;
import java.util.List;

public class Test {
  public static void main(String[] args) {

    Deck deck = new Deck(1);
    deck.shuffle();


    List<Card> hand = new ArrayList<>();
    for (int i = 0; i < 10; i++ ) {
      hand.add(deck.deal());
    }

    hand.forEach(System.out::println);
    System.out.println(
            Hand.calculateValue(hand)
    );

  }
}
