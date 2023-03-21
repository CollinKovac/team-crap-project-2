import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CapabilityListsForDomains {
    public static Lock[] lock;
    public static void lockMaker(int M) {
        lock = new Lock[M];
        for (int i = 0; i < M; i++) lock[i] = new ReentrantLock();
    }

    static String[] object;
    static Lock area;
    static Lock mutex;
    static int readcount = 0;
    static String[] writerObject = {"Chibaku Tensei", "Kotoamatsukami", "bijudama", "edo tensei", "kamui", "Reaper Death Seal"};

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

    public static void capabilityListForDomains(){
        // Get number of domains N
        Random random = new Random();
        int N = 3 + random.nextInt(5);
        // Get number of objects M
        int M = 3 + random.nextInt(5);

        // calling the lockmaker function to create the lock
        // array and initialize all permits to 1.
        lockMaker(M);

        // Create Capability List
        ArrayList capabilityLists = new ArrayList<>(0);
        int readOrWriter;
        int domainSwitch;
        // N = for domains
        // M = for objects
        //looping through each domain
        for(int i = 0; i < N; i++){
            LinkedList<String> list = new LinkedList<String>();
            for(int j = 0; j < N + M; j++){
                if(j < M){ // each object does R | W | R/W - I'm unsure if this is correct
                    readOrWriter = random.nextInt(3);
                    if(readOrWriter == 0)
                        list.add("F" + (j + ": R"));
                    else if(readOrWriter == 1)
                        list.add("F" + (j) + ": W");
                    else if(readOrWriter == 2)
                        list.add("F" + (j) + ": R/W");
                    else{
                        System.out.println("Index out of bounds, line 44 in CL for domains");
                    }
                }
                else{ // Domain switch access
                    domainSwitch = random.nextInt(2);
                    if(domainSwitch == 0 && i - M != j)
                        list.add("D" + (j-M) + ": allow");
                }
            }
            //add to each object as it goes down each domain
            capabilityLists.add(list);
        }
        // Print Capability List
        System.out.print(N + " domains \n" + M + " objects\nCapability List:");
        for (int i = 0; i < N; i++){
            if(i < M)
                System.out.print("\nD" + i + ": " + capabilityLists.get(i));
            else
                System.out.print("\nF" + i + ": " + capabilityLists.get(i));
        }
        System.out.println();
    }
}