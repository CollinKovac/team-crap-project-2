import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

public class Domain implements Runnable {
    static int M;
    static int N;
    private int threadNum;
    static String[][] matrix;
    static String[] object;
    static String[] writerObject = {"Chibaku Tensei", "Kotoamatsukami", "bijudama", "edo tensei", "kamui", "Reaper Death Seal"};
    static Semaphore[] mutex;
    static Semaphore[] area;
    static int[] readcount;

    public Domain(int objects, int domains, int thread, String[][] AM, String[] array, Semaphore[] mutex, Semaphore[] area, int[] readcount) {
        this.threadNum = thread;
    }

    private static Boolean arbitrator(int currentDomain, int targetDomain, String permission) {
        return matrix[currentDomain][targetDomain].contains(permission);
    }

    //reader function to run when accessible
    private static void reader(int threadNum, int resourceRequest) throws InterruptedException {
        mutex[resourceRequest].acquire();
        readcount[resourceRequest]++;
        if(readcount[resourceRequest] == 1){
            area[resourceRequest].acquire();
        }
        mutex[resourceRequest].release();

        //read here
        System.out.println("D" +threadNum+ ": F" +resourceRequest+ " contains: ''" +object[resourceRequest] + "''");

        mutex[resourceRequest].acquire();
        readcount[resourceRequest]--;
        if(readcount[resourceRequest] == 0){
            area[resourceRequest].release();
        int randInt = 3 + (int)(Math.random() * ((7 - 3) + 1));
        System.out.println("D" + threadNum + ": Yielding " + randInt + " times");
        for (int j = 0; j < randInt; j++) Thread.yield();
        }
        mutex[resourceRequest].release();
    }

    // writer function to run when accessible
    private static void writer(int threadNum, int resourceRequest) throws InterruptedException {
        area[resourceRequest].acquire();

        //write here
        object[resourceRequest] = writerObject[(int) (Math.random() * (6))];
        System.out.println("D" +threadNum+ ": Writing ''" + object[resourceRequest]+ "'' to F" + resourceRequest);

        area[resourceRequest].release();

        int randInt = 3 + (int)(Math.random() * ((7 - 3) + 1));
        System.out.println("D" + threadNum + ": Yielding " + randInt + " times");
        for (int j = 0; j < randInt; j++) Thread.yield();

    }

    //Domain switching method
    public static void switchDomain(int currentDomain, int targetDomain, String[] domainPermissions) {
        // Copying targeted domain permissions to current domain
        for (int i = 0; i < M+N; i++)
            domainPermissions[i] =  matrix[targetDomain][i];
        System.out.println("D" + currentDomain + ": Switched to D" + targetDomain);
    }

    @Override
    public void run() {
        Random random = new Random();

        // Make copy of current thread's permissions
        String[] domainPermissions = new String[M+N];
        for (int i = 0; i < M+N; i++) {
            domainPermissions[i] = matrix[threadNum][i];
        }
        // Generate 5 requests
        for(int i = 0; i < 5; i++){
            int request = random.nextInt(M+N-1)+1;
            if (request <= M){ // Read or Write
                int readNwrite = random.nextInt(3);
                if(readNwrite == 0) { // Read
                    System.out.println("D" + threadNum + ": Attempting to read F" + request);
                    if(arbitrator(threadNum, request, "R")) { // Check permission to read
                        try {reader(this.threadNum ,request);} catch (InterruptedException e) {throw new RuntimeException(e);}
                    } else {
                        System.out.println("D" +this.threadNum+ ": Permission NOT granted to read F" + request);
                        int randInt = 3 + (int)(Math.random() * ((7 - 3) + 1));
                        //System.out.println("D" + threadNum + ": Yielding " + randInt + " times");
                        for (int j = 0; j < randInt; j++) Thread.yield();
                    }
                } else { // Write
                    System.out.println("D" +this.threadNum+ ": Attempting to write to F" + request);
                    if(arbitrator(threadNum, request, "W")) { // Check permission to write
                        try {writer(this.threadNum, request);} catch (InterruptedException e) {throw new RuntimeException(e);}
                    } else {
                        System.out.println("D" +this.threadNum+ ": Permission NOT granted to write to F" + request);
                        int randInt = 3 + (int)(Math.random() * ((7 - 3) + 1));
                        //System.out.println("D" + threadNum + ": Yielding " + randInt + " times");
                        for (int j = 0; j < randInt; j++) Thread.yield();
                    }
                }
            } else { // Domain Switch
                while (request-M == threadNum || request < M || request >= N+M) request = random.nextInt(N)+M; // Don't generate self
                System.out.println("D" + threadNum + ": Attempting to switch to D" + (request-M));
                if (arbitrator(threadNum, request, "allow")) // Check permission to switch
                    switchDomain(threadNum, request-M, domainPermissions);
                else System.out.println("D" + threadNum + ": Permission NOT granted to switch to D" + (request-M));
            }
        }
    }
}