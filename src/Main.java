import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // Get number of domains N
        Random random = new Random();
        int N = 3+random.nextInt(6);
        // Get number of objects M
        int M = 3+random.nextInt(6);
        // Create Access Matrix of size N*(M+N)
        String[][] AM = new String[N][M+N];
        // Populate Access Matrix

        // Create domain threads

        // Create object buffers
    }
}