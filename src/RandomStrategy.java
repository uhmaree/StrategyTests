import java.util.Random;

public class RandomStrategy implements Strategy {
    private static final String[] moves = {"R", "P", "S"};
    private Random rand = new Random();

    @Override
    public String getMove(String playerMove) {
        return moves[rand.nextInt(moves.length)];
    }
}