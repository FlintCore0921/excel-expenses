package org.flintcore.excel_expenses.models.laters;
/**Task made from interview.*/
@Deprecated
public class Class {

        private enum Card {
            two(2),
            three(3),
            four(4),
            five(5),
            six(6),
            seven(7),
            eight(8),
            nine(9),
            ten(10),
            jack(10),
            queen(10),
            king(10),
            ace(11);

            public final int value;

            Card(int value) { this.value = value; }
        }

        private static final int SUM_INDEX = 2;
        private static final int MAX_CARD_POINTS = 21;

        private static final String UP = "above", DOWN = "below",
                SAME = "blackjack";

        public static String ArrayChallenge(String[] strArr) {
            // code goes here
            int points = 0;
            Card highestCard = Card.two,
                    secondHighestCard = Card.two;
            boolean containsAce = false;

            for(String card : strArr) {
                Card actualCard = Card.valueOf(card.toLowerCase());

                containsAce = containsAce || actualCard.equals(Card.ace);
                points += actualCard.value;

                if(highestCard.value < actualCard.value){
                    highestCard = actualCard;
                }

                if(secondHighestCard.value < actualCard.value
                        && highestCard.value > actualCard.value) {
                    secondHighestCard = actualCard;
                }
            }

            if(containsAce && points > MAX_CARD_POINTS) {
                highestCard = secondHighestCard;
                points -= (Card.ace.value -1);
            }

            String prefix = "";

            if(points > MAX_CARD_POINTS) {
                prefix = UP;
            } else if (points < MAX_CARD_POINTS) {
                prefix = DOWN;
            } else  {
                prefix = SAME;
            }

            return "%s %s".formatted(prefix, highestCard.name().toLowerCase());
        }
}
