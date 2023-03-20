import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

public class DomainAL implements Runnable {

    private static int M;
    private static int N;
    private int threadNum;
    static ArrayList<LinkedList<String>> list;
    static String[] object;
    static String[] writerObject = {"Chibaku Tensei", "Kotoamatsukami", "bijudama", "edo tensei", "kamui", "Reaper Death Seal"};
    static Lock[] lock;

    public DomainAL(int objects, int domains, int thread, ArrayList<LinkedList<String>> AL, String[] array, Lock[] lock) {
        M = objects;
        N = domains;
        this.threadNum = thread;
        list = AL;
        object = array;
    }
    //semaphore creation used for the readers and writers fucntions
    static Semaphore area = new Semaphore(1);
    static Semaphore mutex = new Semaphore(1);
    static int readcount = 0;

    private static Boolean arbitrator(int targetObject, String permission) {
        return list.get(targetObject).contains(permission);
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
        System.out.println("D" +threadNum+ ": F" +resourceRequest+ " contains: ''" +object[resourceRequest] + "''");

        mutex.acquire();
        readcount--;
        if(readcount == 0){
            area.release();
        }
        mutex.release();
    }

    // writer function to run when accessible
    private static void writer(int threadNum, int resourceRequest) throws InterruptedException {
        area.acquire();

        //write here
        object[resourceRequest] = writerObject[(int) (Math.random() * (6))];
        System.out.println("D" +threadNum+ ": Writing ''" + object[resourceRequest]+ "'' to F" + resourceRequest);

        area.release();
    }

    //Domain switching method
    public static void switchDomain(int currentDomain, int targetDomain, LinkedList<String> domainPermissions) {
        // Copying targeted domain permissions to current domain
        //domainPermissions = (LinkedList<String>) list.get(targetDomain).clone();
        System.out.println("D" + currentDomain + ": Switched to D" + targetDomain);
    }

    @Override
    public void run() {
        Random random = new Random();

        // Make copy of current thread's permissions
        LinkedList<String> domainPermissions;
        domainPermissions = (LinkedList<String>) list.get(threadNum).clone();

        // Generate 5 requests
        for(int i = 0; i < 5; i++){
            int request = random.nextInt(M+N-1)+1;
            if (request <= M){ // Read or Write
                int readNwrite = random.nextInt(3);
                if(readNwrite == 0) { // Read
                    System.out.println("D" + threadNum + ": Attempting to read F" + (request-1));
                    if(arbitrator(request, ("D" + threadNum + ": R"))) { // Check permission to read
                        try {reader(this.threadNum ,request-1);} catch (InterruptedException e) {throw new RuntimeException(e);}
                    } else System.out.println("D" + threadNum + ": Permission NOT granted to read F" + (request-1));
                    int randInt = 3 + (int)(Math.random() * ((7 - 3) + 1));
                    //System.out.println("D" + threadNum + ": Yielding " + randInt + " times");
                    for (int j = 0; j < randInt; j++) Thread.yield();
                }
                else { // Write
                    System.out.println("D" + threadNum + ": Attempting to write to F" + (request-1));
                    if(arbitrator(request, ("D" + threadNum + ": W"))) { // Check permission to write
                        try {writer(this.threadNum, request-1);} catch (InterruptedException e) {throw new RuntimeException(e);}
                    } else System.out.println("D" + threadNum + ": Permission NOT granted to write to F" + (request-1));
                    int randInt = 3 + (int)(Math.random() * ((7 - 3) + 1));
                    //System.out.println("D" + threadNum + ": Yielding " + randInt + " times");
                    for (int j = 0; j < randInt; j++) Thread.yield();
                }
            } else { // Domain Switch
                while (M+N-request == threadNum) request = random.nextInt(N-1)+M+1; // Don't generate self
                System.out.println("D" + threadNum + ": Attempting to switch to D" + (M+N-request));
                if (arbitrator(request, ("D" + threadNum + ": allow"))) // Check permission to switch
                    switchDomain(threadNum, request, domainPermissions);
                else System.out.println("D" + threadNum + ": Permission NOT granted to switch to D" + (M+N-request));
                int randInt = 3 + (int)(Math.random() * ((7 - 3) + 1));
                //System.out.println("D" + threadNum + ": Yielding " + randInt + " times");
                for (int j = 0; j < randInt; j++) Thread.yield();
            }
        }
    }
}