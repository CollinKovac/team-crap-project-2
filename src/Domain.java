import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Domain implements Runnable {

    private static int M;
    private static int N;
    private int threadNum;
    private static String[][] matrix;
    private static String[] object;

    public Domain(int objects, int domains, int thread, String[][] AM, String[] array) {
        M = objects;
        N = domains;
        this.threadNum = thread;
        matrix = AM;
        object = array;
    }
    //semaphore creation used for the readers and writers fucntions
    static Semaphore area = new Semaphore(1);
    static Semaphore mutex = new Semaphore(1);
    static int readcount = 0;

    private static Boolean arbitrator(String[] domainPermissions, int currentDomain, int targetDomain, String permission) {
        return domainPermissions[targetDomain].contains(permission);
    }

    //reader function to run when accessible
    private static void reader(int threadNum, int resourceRequest) throws InterruptedException {
        mutex.acquire();
        readcount++;
        if(readcount == 1){
            area.acquire();
        }
        mutex.release();

        //read here
        System.out.println("D" +threadNum+ ": F" +resourceRequest+ " contains: " +object[resourceRequest]);

        mutex.acquire();
        readcount--;
        if(readcount == 0){
            area.release();
        }
        mutex.release();
    }

    static String[] writerObject = {"Chibaku Tensei", "Kotoamatsukami", "bijudama", "edo tensei", "kamui", "Reaper Death Seal"};

    // writer function to run when accessible
    private static void writer(int threadNum, int resourceRequest) throws InterruptedException {
        area.acquire();

        //write here
        object[resourceRequest] = writerObject[(int) (Math.random() * (6))];
        System.out.println("D" +threadNum+ ": Writing " + object[resourceRequest]+ " to F" + resourceRequest);

        area.release();
    }

    //Domain switching method
    public static void switchDomain(int currentDomain, int targetDomain, String[] domainPermissions) {
        // Check if switching is allowed for the current domain and target domain
        if (arbitrator(domainPermissions, currentDomain, M+targetDomain, "allowed")) {
            //copying targeted domain permissions to current domain
            for (int i = 0; i < M+N; i++)
                domainPermissions[i] =  matrix[targetDomain][i];
            System.out.println("D" + currentDomain + ": Switched to D" + targetDomain);
        } else System.out.println("D" + currentDomain + ": Permission NOT granted to switch to D" + targetDomain);
        int randInt = 3 + (int)(Math.random() * ((7 - 3) + 1));
        System.out.println("D" + currentDomain + ": Yielding " + randInt + " times");
        for (int j = 0; j < randInt; j++) {
            Thread.yield();
        }
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
            int request = random.nextInt(M+N);
            if (request <= M){ // Read or Write
                int readNwrite = random.nextInt(3);
                if(readNwrite == 0) { // Read
                    System.out.println("D" + threadNum + ": Attempting to read F" + request);
                    if(arbitrator(domainPermissions, threadNum, request, "R")) { // Check permission to read
                        try {
                            reader(this.threadNum ,request);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else System.out.println("D" +this.threadNum+ ": Permission NOT granted to read F" + request);
                }
                else if(readNwrite == 1) { // Write
                    System.out.println("D" +this.threadNum+ ": Attempting to write to F" + request);
                    if(arbitrator(domainPermissions, threadNum, request, "W")) { // Check permission to write
                        try {
                            writer(this.threadNum, request);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else System.out.println("D" +this.threadNum+ ": Permission NOT granted to write to F" + request);
                }
            } else { // Domain Switch
                while (M+N-request == threadNum) request = M + random.nextInt(N); // Don't generate self
                System.out.println("D" + threadNum + ": Attempting to switch to D" + (M+N-request)); // Check permission to switch
                switchDomain(threadNum, M+N-request, domainPermissions);
            }
        }
    }
}