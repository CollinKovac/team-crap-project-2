import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class DomainAL implements Runnable {
    static int M;
    static int N;
    private final int threadNum;
    private int threadPerms;
    static ArrayList<LinkedList<String>> list;
    static String[] object;
    //semaphore creation used for the readers and writers fucntions
    static Semaphore[] area;
    static Semaphore[] mutex;
    static int[] readcount;
    static String[] writerObject = {"Chibaku Tensei", "Kotoamatsukami", "bijudama", "edo tensei", "kamui", "Reaper Death Seal"};

    public DomainAL(int objects, int domains, int thread, ArrayList<LinkedList<String>> AL, String[] array, Semaphore[] mutex, Semaphore[] area, int[] readcount) {
        this.threadNum = thread;
        this.threadPerms = thread;
        M = objects;
        N = domains;
    }

    private static Boolean arbitrator(int targetObject, String permission) {
        return list.get(targetObject).contains(permission);
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
        //System.out.println("D" + threadNum + ": Yielding " + randInt + " times");
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
        //System.out.println("D" + threadNum + ": Yielding " + randInt + " times");
        for (int j = 0; j < randInt; j++) Thread.yield();
    }

    @Override
    public void run() {
        Random random = new Random();

        // Generate 5 requests
        for(int i = 0; i < 5; i++){
            int request = random.nextInt(M+N);
            if (request < M){ // Read or Write
                int readNwrite = random.nextInt(2);
                if(readNwrite == 0) { // Read
                    System.out.println("D" + threadNum + ": Attempting to read F" + request);
                    if(arbitrator(request, ("D" + threadPerms + ": R")) || arbitrator(request, ("D" + threadPerms + ": R/W"))) { // Check permission to read
                        try {reader(this.threadNum ,request);} catch (InterruptedException e) {throw new RuntimeException(e);}
                    } else {
                        System.out.println("D" + threadNum + ": Permission NOT granted to read F" + request);
                        int randInt = 3 + (int)(Math.random() * ((7 - 3) + 1));
                        System.out.println("D" + threadNum + ": Yielding " + randInt + " times");
                        for (int j = 0; j < randInt; j++) Thread.yield();
                    }
                } else { // Write
                    System.out.println("D" + threadNum + ": Attempting to write to F" + request);
                    if(arbitrator(request, ("D" + threadPerms + ": W")) || arbitrator(request, ("D" + threadPerms + ": R/W"))) { // Check permission to write
                        try {writer(this.threadNum, request);} catch (InterruptedException e) {throw new RuntimeException(e);}
                    } else {
                        System.out.println("D" + threadNum + ": Permission NOT granted to write to F" + request);
                        int randInt = 3 + (int)(Math.random() * ((7 - 3) + 1));
                        System.out.println("D" + threadNum + ": Yielding " + randInt + " times");
                        for (int j = 0; j < randInt; j++) Thread.yield();
                    }
                }
            } else { // Domain Switch
                while (request-M == threadNum || request < M || request >= N+M) request = random.nextInt(N)+M; // Don't generate self
                System.out.println("D" + threadNum + ": Attempting to switch to D" + (request-M));
                if (arbitrator(request, ("D" + threadPerms + ": allow"))) { // Check permission to switch
                    threadPerms = request-M; // Acquire permission name of domain just switched to
                    System.out.println("D" + threadNum + ": Switched to D" + (request-M));
                }  else System.out.println("D" + threadNum + ": Permission NOT granted to switch to D" + (request-M));
            }
        }
    }
}