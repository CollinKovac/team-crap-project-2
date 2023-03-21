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

    public DomainAL(int objects, int domains, int thread, ArrayList<LinkedList<String>> AL, String[] array, Lock[] mutex, Lock[] area, int[] readcount) {
        this.threadNum = thread;
    }
    //semaphore creation used for the readers and writers fucntions
    static Lock[] area;
    static Lock[] mutex;
    static int readcount[];

    private static Boolean arbitrator(int targetObject, String permission) {
        return list.get(targetObject).contains(permission);
    }

    //reader function to run when accessible
    private static void reader(int threadNum, int resourceRequest) throws InterruptedException {
        mutex[resourceRequest].lock();
        readcount[resourceRequest]++;
        if(readcount[resourceRequest] == 1){
            area[resourceRequest].lock();
        }
        mutex[resourceRequest].unlock();

        //read here
        System.out.println("D" +threadNum+ ": F" +resourceRequest+ " contains: ''" +object[resourceRequest] + "''");

        mutex[resourceRequest].lock();
        readcount[resourceRequest]--;
        if(readcount[resourceRequest] == 0){
            area[resourceRequest].unlock();
        int randInt = 3 + (int)(Math.random() * ((7 - 3) + 1));
        //System.out.println("D" + threadNum + ": Yielding " + randInt + " times");
        for (int j = 0; j < randInt; j++) Thread.yield();
        }
        mutex[resourceRequest].unlock();
    }

    // writer function to run when accessible
    private static void writer(int threadNum, int resourceRequest) throws InterruptedException {
        area[resourceRequest].lock();

        //write here
        object[resourceRequest] = writerObject[(int) (Math.random() * (6))];
        System.out.println("D" +threadNum+ ": Writing ''" + object[resourceRequest]+ "'' to F" + resourceRequest);

        area[resourceRequest].unlock();

        int randInt = 3 + (int)(Math.random() * ((7 - 3) + 1));
        //System.out.println("D" + threadNum + ": Yielding " + randInt + " times");
        for (int j = 0; j < randInt; j++) Thread.yield();
    }

    //Domain switching method
    public static void switchDomain(int currentDomain, int targetDomain, String permission, ArrayList<LinkedList<String>> list) {
        for (int i = 0; i < M+N; i++) { // For each object + domain, check...
            if (list.get(i).contains("D" + currentDomain)) { // Current domain in list
                // Remove current domain
                list.get(i).remove();
                if (list.get(i).contains("D" + targetDomain)) { // Target domain in list too
                    // Add copy of target domain w current domain's name
                    //list.get(i).add();
                }
            } else if (list.get(i).contains("D" + targetDomain)) { // Target domain in list only
                // Add copy of target domain w current domain's name
            }
            // Case that neither current nor target domains are in linked list
            // Do nothing!!! :)
        }
        System.out.println("D" + currentDomain + ": Switched to D" + targetDomain);
    }

    @Override
    public void run() {
        Random random = new Random();

        // Make dynamic copy of original permission arraylist
        ArrayList<LinkedList<String>> listCopy = (ArrayList<LinkedList<String>>) list.clone();

        // Make copy of current thread's permissions
        LinkedList<String> domainPermissions = (LinkedList<String>) list.get(threadNum).clone();

        // Generate 5 requests
        for(int i = 0; i < 5; i++){
            int request = random.nextInt(M+N);
            if (request < M){ // Read or Write
                int readNwrite = random.nextInt(3);
                if(readNwrite == 0) { // Read
                    System.out.println("D" + threadNum + ": Attempting to read F" + request);
                    if(arbitrator(request, (threadNum + ": R")) || arbitrator(request, (threadNum + ": R/W"))) { // Check permission to read
                        try {reader(this.threadNum ,request);} catch (InterruptedException e) {throw new RuntimeException(e);}
                    } else {
                        System.out.println("D" + threadNum + ": Permission NOT granted to read F" + request);
                        int randInt = 3 + (int)(Math.random() * ((7 - 3) + 1));
                        //System.out.println("D" + threadNum + ": Yielding " + randInt + " times");
                        for (int j = 0; j < randInt; j++) Thread.yield();
                    }
                } else { // Write
                    System.out.println("D" + threadNum + ": Attempting to write to F" + request);
                    if(arbitrator(request, (threadNum + ": W")) || arbitrator(request, (threadNum + ": R/W"))) { // Check permission to write
                        try {writer(this.threadNum, request);} catch (InterruptedException e) {throw new RuntimeException(e);}
                    } else {
                        System.out.println("D" + threadNum + ": Permission NOT granted to write to F" + request);
                        int randInt = 3 + (int)(Math.random() * ((7 - 3) + 1));
                        //System.out.println("D" + threadNum + ": Yielding " + randInt + " times");
                        for (int j = 0; j < randInt; j++) Thread.yield();
                    }
                }
            } else { // Domain Switch
                while (request-M == threadNum || request < M || request >= N+M) request = random.nextInt(N)+M; // Don't generate self
                System.out.println("D" + threadNum + ": Attempting to switch to D" + (request-M));
                //if (arbitrator(request, (threadNum + ": allow"))) // Check permission to switch
                //switchDomain(threadNum, request-M, , listCopy);
                //else System.out.println("D" + threadNum + ": Permission NOT granted to switch to D" + (request-M));
            }
        }
    }
}