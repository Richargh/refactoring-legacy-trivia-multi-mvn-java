package com.adaptionsoft.games.uglytrivia;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Game {

    private final List<Player> players = new ArrayList<>();

    private final Catalogue catalogue = new Catalogue();

    private final PenaltyBox penaltyBox = new PenaltyBox();

    private final Board board = new Board();

    private final Purses purses = new Purses();

    private Player currentPlayer = null;

    public  Game(){
    }

    public void add(String playerName) {
        players.add(new Player(players.size(), new PlayerName(playerName)));
        board.addPlayerToBoard(players.size());
        purses.addPlayerPurse(players.size());
        if(currentPlayer == null)
            currentPlayer = players.get(0);

        System.out.println(playerName + " was added");
        System.out.println("They are player number " + players.size());
    }

    public void roll(int roll) {
        System.out.println(currentPlayer.name() + " is the current player");
        System.out.println("They have rolled a " + roll);

        if (penaltyBox.isInPenaltyBox(this.currentPlayer.index())) {
            if (isEven(roll)) {
                penaltyBox.keepInPenaltyBox(currentPlayer);
            } else {
                penaltyBox.fetchFromPenaltyBox(currentPlayer);

                play(roll);
            }
        } else {
            play(roll);
        }
    }

    public boolean playerAnsweredCorrectly() {
        int playerIndex = currentPlayer.index();

        if (penaltyBox.isNotAllowedToPlay(playerIndex)){
            selectNextPlayer();
            return true;
        } else {
            purses.winCoin(currentPlayer, this);
            selectNextPlayer();

            return purses.shouldGameContinue(playerIndex, this);
        }
    }

    public boolean playerAnsweredWrong(){
        System.out.println("Question was incorrectly answered");
        penaltyBox.sendToPenaltyBox(currentPlayer);

        selectNextPlayer();
        return true;
    }

    private static boolean isEven(int roll) {
        return roll % 2 == 0;
    }

    private void play(int roll) {
        var category = board.movePlayer(roll, currentPlayer);
        catalogue.askQuestion(category);
    }

    private void selectNextPlayer() {
        var nextPlayerIndex = players.indexOf(currentPlayer) + 1;
        if (nextPlayerIndex == players.size()) nextPlayerIndex = 0;
        currentPlayer = players.get(nextPlayerIndex);
    }

    /**
     * Extracted Types
     */
    private enum Category {
        Pop,
        Science,
        Sports,
        Rock
    }

    public class Purses {

        /**
         * Purses
         */
        final int[] purses = new int[6];

        public int[] getPurses() {
            return purses;
        }

        public Purses() {
        }

        public void winCoin(Player player, Game game) {
            System.out.println("Answer was correct!!!!");
            getPurses()[player.index()]++;
            System.out.println(player.name()
                               + " now has "
                               + getPurses()[player.index()]
                               + " Gold Coins.");
        }

        public boolean shouldGameContinue(int playerIndex, Game game) {
            return getPurses()[playerIndex] != 6;
        }

        private void addPlayerPurse(int playerIndex) {
            getPurses()[playerIndex] = 0;
        }
    }

    private record Player(int index, PlayerName name) {
    }

    private record PlayerName(String rawValue){
        @Override
        public String toString() {
            return rawValue;
        }
    }

    private record Question(String rawValue){
        @Override
        public String toString() {
            return rawValue;
        }
    }

    public static class Board {

        /**
         * Places
         */
        private final int[] places = new int[6];

        public Board() {
        }

        private void addPlayerToBoard(int playerIndex) {
            places[playerIndex] = 0;
        }

        public Category movePlayer(int roll, Player player) {
            int index = player.index();
            places[index] = places[index] + roll;
            if (places[index] > 11) places[index] = places[index] - 12;

            System.out.println(player.name()
                               + "'s new location is "
                               + places[index]);

            return currentCategory(places[index]);
        }

        /**
         * Category
         *
         * @param place
         */
        private Category currentCategory(int place) {
            if (place == 0) return Category.Pop;
            if (place == 4) return Category.Pop;
            if (place == 8) return Category.Pop;
            if (place == 1) return Category.Science;
            if (place == 5) return Category.Science;
            if (place == 9) return Category.Science;
            if (place == 2) return Category.Sports;
            if (place == 6) return Category.Sports;
            if (place == 10)return Category.Sports;
            return Category.Rock;
        }
    }

    public class PenaltyBox {

        /**
         * Penalty Box
         */
        private final boolean[] inPenaltyBox = new boolean[6];

        private final boolean[] isGettingOutOfPenaltyBox = new boolean[6];

        public PenaltyBox() {
        }

        public boolean isNotAllowedToPlay(int playerIndex) {
            return inPenaltyBox[playerIndex] && !isGettingOutOfPenaltyBox[playerIndex];
        }

        public boolean isInPenaltyBox(int playerIndex) {
            return inPenaltyBox[playerIndex];
        }

        public void sendToPenaltyBox(Player player) {
            System.out.println(player.name() + " was sent to the penalty box");
            inPenaltyBox[player.index()] = true;
        }

        public void fetchFromPenaltyBox(Player player) {
            isGettingOutOfPenaltyBox[player.index()] = true;
            System.out.println(player.name() + " is getting out of the penalty box");
        }

        public void keepInPenaltyBox(Player player) {
            isGettingOutOfPenaltyBox[player.index()] = false;
            System.out.println(player.name() + " is not getting out of the penalty box");
        }
    }

    public static class Catalogue {

        private final List<Question> popQuestions = new LinkedList<Question>();

        private final List<Question> scienceQuestions = new LinkedList<Question>();

        private final List<Question> sportsQuestions = new LinkedList<Question>();

        private final List<Question> rockQuestions = new LinkedList<Question>();

        public Catalogue() {
            initQuestionCatalogue();
        }

        private void initQuestionCatalogue() {
            for (int i = 0; i < 50; i++) {
                popQuestions
                        .add(new Question("Pop Question " + i));
                scienceQuestions
                        .add(new Question(("Science Question " + i)));
                sportsQuestions
                        .add(new Question(("Sports Question " + i)));
                rockQuestions
                        .add(new Question("Rock Question " + i));
            }
        }

        public void askQuestion(Category category) {
            System.out.println("The category is " + category);

            if (category == Category.Pop) System.out.println(popQuestions
                                       .remove(0));
            if (category == Category.Science) System.out.println(scienceQuestions
                                       .remove(0));
            if (category == Category.Sports) System.out.println(sportsQuestions
                                       .remove(0));
            if (category == Category.Rock) System.out.println(rockQuestions
                                       .remove(0));
        }
    }
}