import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Domain implements Runnable {

    private static int M;
    private static int N;
    private int threadNum;
    private static String[][] matrix;

    public Domain(int objects, int domains, int thread, String[][] AM) {
        M = objects;
        N = domains;
        this.threadNum = thread;
        matrix = AM;

    }
    //semaphore creation used for the readers and writers fucntions
    static Semaphore area = new Semaphore(1);
    static Semaphore mutex = new Semaphore(1);
    static int readcount = 0;

    //reader function to run when accessible
    private static void reader(int threadNum) throws InterruptedException {
        mutex.acquire();
        readcount++;
        if(readcount == 1){
            area.acquire();
        }
        mutex.release();

        //read here

        mutex.acquire();
        readcount--;
        if(readcount == 0){
            area.release();
        }
        mutex.release();
    }

    // writer function to run when accessible
    private static void write(int threadNum) throws InterruptedException {
        area.acquire();

        //write here

        area.release();
    }

    //Domain switching method
    public static void switchDomain(int currentDomain, int targetDomain, String[] domainPermission) {
        // Check if switching is allowed for the current domain and target domain
        if (matrix[currentDomain][targetDomain].equals("allow")) {
            //print out that a switch is a allowed
            System.out.println("Switch permission is granted from D" + (currentDomain + 1) + " to D" + (targetDomain - M + 1));
            System.out.println("D" + (currentDomain+1) + "has current permissions: " + Arrays.toString(domainPermission));
            //copying targeted domain permissions to current domain
            for (int i = 0; i < M+N; i++)
                domainPermission[i] =  matrix[targetDomain - M][i];
            System.out.println("D" + (currentDomain+1) + "now has permissions: " + Arrays.toString(domainPermission));
        } else {
            System.out.println("Switch permission is NOT granted");
            int randInt = 3 + (int)(Math.random() * ((7 - 3) + 1));
            for (int j = 0; j < randInt; j++) {
                Thread.yield();
            }
        }
    }

    @Override
    public void run() {
        // Create array of this domain's current permissions
        String[] domainPermissions = new String[M+N];
        for (int i = 0; i < M+N; i++) {
            domainPermissions[i] = matrix[threadNum][i];
        }

        // 5 requests
        Random random = new Random();
        for(int i = 0; i < 1; i++){
            int request = random.nextInt(M+N);
            while (request == threadNum + M) request = random.nextInt(M+N); // Don't generate self
            if (request <= M-1)// If read/write
                System.out.println("\nread/write");
            else if (request < M+N) // If domain switch
                switchDomain(threadNum, request, domainPermissions);
        }
    }
}