package com.adaptionsoft.games.uglytrivia;

import org.approvaltests.combinations.CombinationApprovals;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

class GameTest {

    @Test
    void testWithApprovalTests() {
        CombinationApprovals.verifyAllCombinations(
                this::rollWithResult,
                new Rolls[]{
                        new Rolls(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
                        new Rolls(2, 4, 8),
                },
                new Answers[]{
                        new Answers(true),
                        new Answers(false),
                        new Answers(true, false),
                        new Answers(false, false, false),
                        new Answers(false, false, true, true),
                });
    }

    private String rollWithResult(Rolls rolls, Answers answers){

        var byteStream = new ByteArrayOutputStream();
        var printStream = new PrintStream(byteStream);
        System.setOut(printStream);

        var testee = new Game();
        testee.add("Alex");
        testee.add("Taylor");
        for (int i = 0; i < rolls.rawValues().length; i++) {
            var roll = rolls.rawValues[i];
            var correctAnswer = answers.rawValues[i % answers.rawValues.length];
            testee.roll(roll);
            if(correctAnswer)
                testee.wasCorrectlyAnswered();
            else
                testee.wrongAnswer();
        }

        return byteStream.toString();
    }

    private record Rolls(int... rawValues){
        @Override
        public String toString() {
            return "Rolls" + Arrays.toString(rawValues);
        }
    }
    private record Answers(boolean... rawValues){
        @Override
        public String toString() {
            return "Wins" + Arrays.toString(rawValues);
        }
    }
}