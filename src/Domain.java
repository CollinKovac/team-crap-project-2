import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

public class Domain implements Runnable {

    private static int M;
    private static int N;
    private int threadNum;
    private static String[][] matrix;
    static String[] object;
    static Lock[] lock;
    static String[] writerObject = {"Chibaku Tensei", "Kotoamatsukami", "bijudama", "edo tensei", "kamui", "Reaper Death Seal"};
    static Lock mutex;
    static Lock area;
    static int readcount;

    public Domain(int objects, int domains, int thread, String[][] AM, String[] array, Semaphore mutex, Semaphore area, int readcount, Lock[] lock) {
        M = objects;
        N = domains;
        this.threadNum = thread;
        matrix = AM;
        object = array;
    }

    private static Boolean arbitrator(String[] domainPermissions, int targetDomain, String permission) {
        return domainPermissions[targetDomain].contains(permission);
    }

    //reader function to run when accessible
    private static void reader(int threadNum, int resourceRequest) throws InterruptedException {
        mutex.lock();
        readcount++;
        if(readcount == 1){
            area.lock();
        }
        mutex.unlock();

        //read here
        System.out.println("D" +threadNum+ ": F" +resourceRequest+ " contains: " +object[resourceRequest]);

        mutex.lock();
        readcount--;
        if(readcount == 0){
            area.unlock();
        }
        mutex.unlock();
    }


    // writer function to run when accessible
    private static void writer(int threadNum, int resourceRequest) throws InterruptedException {
        area.lock();

        //write here
        object[resourceRequest] = writerObject[(int) (Math.random() * (6))];
        System.out.println("D" +threadNum+ ": Writing " + object[resourceRequest]+ " to F" + resourceRequest);

        area.unlock();
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
                    System.out.println("D" + threadNum + ": Attempting to read F" + (request-1));
                    if(arbitrator(domainPermissions, request-1, "R")) { // Check permission to read
                        try {
                            reader(this.threadNum ,request-1);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else System.out.println("D" +this.threadNum+ ": Permission NOT granted to read F" + (request-1));
                    int randInt = 3 + (int)(Math.random() * ((7 - 3) + 1));
                    //System.out.println("D" + threadNum + ": Yielding " + randInt + " times");
                    for (int j = 0; j < randInt; j++) Thread.yield();
                }
                else { // Write
                    System.out.println("D" +this.threadNum+ ": Attempting to write to F" + (request-1));
                    if(arbitrator(domainPermissions, request-1, "W")) { // Check permission to write
                        try {
                            writer(this.threadNum, request-1);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else System.out.println("D" +this.threadNum+ ": Permission NOT granted to write to F" + (request-1));
                    int randInt = 3 + (int)(Math.random() * ((7 - 3) + 1));
                    //System.out.println("D" + threadNum + ": Yielding " + randInt + " times");
                    for (int j = 0; j < randInt; j++) Thread.yield();
                }
            } else { // Domain Switch
                while (M+N-request == threadNum) request = random.nextInt(N-1)+M+1; // Don't generate self
                System.out.println("D" + threadNum + ": Attempting to switch to D" + (M+N-request));
                if (arbitrator(domainPermissions, M+M+N-request, "allow")) // Check permission to switch
                    switchDomain(threadNum, M+N-request, domainPermissions);
                else System.out.println("D" + threadNum + ": Permission NOT granted to switch to D" + (M+N-request));
                int randInt = 3 + (int)(Math.random() * ((7 - 3) + 1));
                //System.out.println("D" + threadNum + ": Yielding " + randInt + " times");
                for (int j = 0; j < randInt; j++) Thread.yield();
            }
        }
    }
}