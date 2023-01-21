package test;


import java.util.ArrayList;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;
import test.BlackJackTest.Card;
import static test.BlackJackTest.Card.*;

public class BlackJackTest {
    @Test
    public void test_value_one_card(){
        assertEquals(3,createHand(_3).value());
        assertEquals(11,createHand(Ace).value());
        assertEquals(10,createHand(Jack).value());
        assertEquals(10,createHand(Queen).value());
        assertEquals(0,createHand().value());
    }

    @Test
    public void test_value_two_card(){
        assertEquals(5,createHand(_3,_2).value());
        assertEquals(12,createHand(Ace, Ace).value());
        assertEquals(20,createHand(Jack,Jack).value());
        assertEquals(21,createHand(Queen,Ace).value());
    }

    @Test
    public void test_blackjack(){
        assertEquals(false,createHand(_3,Jack).isBlackJack());
        assertEquals(true,createHand(Ace,Jack).isBlackJack());
        assertEquals(true,createHand(Queen,Ace).isBlackJack());
        assertEquals(false,createHand(_5,_5,Ace).isBlackJack());
        assertEquals(false,createHand(_10,_10,Ace).isBlackJack());
    }

    @Test
    public void test_three_or_more_cards(){
        assertEquals(14,createHand(Ace,Ace,Ace,Ace).value());
        assertEquals(21,createHand(Ace,Jack,Jack).value());
        assertEquals(22,createHand(_3,_4,_5,_9,Ace).value());
    }

    @Test
    public void test_create_deck(){
        Deck deck = createDeck(_3,_4,_5);
        assertEquals(3,deck.lengthDeck());
        assertEquals(3,deck.takeCard().value());
        assertEquals(4,deck.takeCard().value());
    }
    
    @Test
    public void test_players(){
        assertEquals(13,createPlayer("Player1",createHand(_3,_5,_5)).getHand().value());
        assertEquals("Player1",createPlayer("Player1",createHand(_3,_5,_5)).getName());
        assertEquals(false,createPlayer("Player2",createHand(_3,_10,_5)).getHand().isBlackJack());
    }
    
    @Test
    public void test_addCard(){
        Hand mano = createHand();
        assertEquals(0,mano.value());
        mano.addCard(Ace);
        assertEquals(11,mano.value());
    }
    
    @Test
    public void test_crupier(){
        Deck deck = createDeck(_2,Ace,_5,Queen,_9,Jack,_3,_7);
        assertEquals(18,createCrupier(createHand(),deck).getHand().value());
        assertEquals(19,createCrupier(createHand(),deck).getHand().value());
    }
    
    @Test
    public void test_game(){
        Deck deck = createDeck(_2,_6,_3,Ace,_2,_7,Queen);
        //assertEquals(["Player2","Player3"],;
        String[] ganadores = createWinners(createPlayer("Player1", createHand(_2,Queen)),
                                          createPlayer("Player2", createHand(Ace,Queen)),
                                          createPlayer("Player3", createHand(Ace,King)),
                                          createCrupier(createHand(), deck)).getWinners();
        for (String ganadore : ganadores) {
            System.out.println(ganadore);
        }
    }

    private Hand createHand(Card... cards) {
        return new Hand() {
            int valuehand;
            Card[] cartas = cards;

            @Override
            public int value() {
                valuehand = 0;
                for (Card card : cartas){ 
                    valuehand += card.value();
                }

                valuehand = CanSumEspecialAce() ? valuehand + 10 : valuehand;

                return valuehand;
            }

            @Override
            public boolean isBlackJack() {
                if (cartas.length == 2){
                    return conteinAce() && conteincardWithValue(10);
                }
                return false;
            }

            @Override
            public void addCard(Card card) {
                Card[] mano_intermedia = new Card[cartas.length+1];
                for (int i = 0; i < cartas.length; i++) {
                    mano_intermedia[i] = cartas[i];
                }
                mano_intermedia[cartas.length] = card;
                cartas = mano_intermedia;
            }

            private boolean conteincardWithValue(int value) {
                for (Card card : cartas) {
                    if(card.value() == value){
                        return true;
                    }
                }
                return false;
            }

            private boolean CanSumEspecialAce(){
                return (conteinAce() && valuehand + 10 <= 21);
            }

            private boolean conteinAce() {
                for (Card card : cartas) {
                    if (card.isAce()){
                        return true;
                    }
                }
                return false;
            }
        };
    }

    private Deck createDeck(Card... cards) {
        return new Deck_BlackJack(cards);
    }

    private Player createPlayer(String player1, Hand createHand) {
        return new Player() {
            @Override
            public String getName() {
                return player1;
            }

            @Override
            public Hand getHand() {
                return createHand;
            }
        };
    }

    private Crupier createCrupier(Hand hand, Deck deck) {
        return new Crupier() {
            @Override
            public Hand getHand() {
                while (hand.value() < 17){
                    hand.addCard(deck.takeCard());
                }
                return hand;
            }
        };
    }

    private Winners createWinners(Player player1, Player player2, Player player3, Crupier crupier) {
        return new Winners() {
            Player[] players = {player1, player2, player3};
            String[] winners; 
            
            @Override
            public String[] getWinners() {
                Player[] array = new Player[3]; 
                int tamaño = 0; 
                int valor_crupier = crupier.getHand().value();
                if (valor_crupier > 21){
                    for (Player player : players) {
                        if (player.getHand().value() <= 21){
                            array[tamaño] = player;
                            tamaño++;
                        }
                    }
                    final_array(array);
                    return winners;
                }
                
                for (Player player : players) {
                    if (player.getHand().isBlackJack() == true && crupier.getHand().isBlackJack() == false){
                        array[tamaño] = player;
                        tamaño++;
                        continue;
                    }
                    else if (player.getHand().value() > valor_crupier && player.getHand().value() <= 21){
                        array[tamaño] = player;
                        tamaño++;
                    }
                    
                }
                final_array(array);
                
                return winners;
            }

            private void final_array(Player[] array) {
                int num_win=0;
                for (Player player : array) {
                    if (player instanceof Player){
                        num_win++;
                    }
                }
                
                winners = new String[num_win];
                num_win=0;
                for (Player player : array) {
                    if (player instanceof Player){
                        winners[num_win] = player.getName();
                        num_win++;
                    }
                }
            }
        };
        
    }

    private static class Deck_BlackJack implements Deck {
        private List<Card> array_cards;
        private int position = 0;
        Deck_BlackJack(Card[] cards) {
            array_cards = new ArrayList<>();
            for (Card card : cards) {
               array_cards.add(card);
            }
        }

        @Override
        public Card takeCard() {
            if (position <= lengthDeck()){
                position += 1;
                return array_cards.get(position-1);
            }
            return null;
        }

        @Override
        public int lengthDeck() {
            return array_cards.size();
        }
    }

    public interface Deck{
        public Card takeCard();
        public int lengthDeck();
    }

    public interface Hand{
        public int value();
        public boolean isBlackJack();
        public void addCard(Card card);
    }
    
    public interface Player{
        public String getName();
        public Hand getHand();
    }
    
    public interface Crupier{
        public Hand getHand();
    }
    
    public interface Winners{
        public String[] getWinners();
    }

    public static enum Card{
        Ace, _2, _3, _4, _5, _6, _7, _8, _9, _10, Jack, King, Queen;

        private int value(){
            return isFace() ? 10 : ordinal()+1;
        }

        private boolean isAce() {
            return this == Ace;
        }

        private boolean isFace() {
            return this == Jack || this == King || this == Queen;
        }
    }
}

