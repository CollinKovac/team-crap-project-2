import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.Random;

public class Main {

    //creating the lock array for object accessing
    static Semaphore[] lock;
    public static void lockMaker(int objects){
        lock = new Semaphore[objects];
        for(int i = 0; i < lock.length; i++){
            lock[i] = new Semaphore(1);
        }
    }


    public static void main(String[] args) {
        // Get number of domains N
        Random random = new Random();
        int N = 3+random.nextInt(6);
        // Get number of objects M
        int M = 3+random.nextInt(6);
        // Create Access Matrix of size N*(M+N)
        String[][] AM = new String[N][M+N];

        // calling the lockmaker function to create the lock
        // array and initialize all permits to 1.
        lockMaker(M);

        // Populate Access Matrix

        // Create domain threads

        // Create object buffers
    }
}