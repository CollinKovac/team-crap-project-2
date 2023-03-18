/*import java.util.ArrayList;
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
                if(i < N + M){
                    readOrWriter = random.nextInt(4);
                    if(readOrWriter == 0)
                        list.add("D" + (j + ": R"));
                    else if(readOrWriter == 1)
                        list.add("D" + (j) + ": W");
                    else if(readOrWriter == 2)
                        list.add("D" + (j) + ": R/W");
                    else{ // Domain switch access
                        domainSwitch = random.nextInt(2);
                        if(domainSwitch == 0 && i - M != j);
                    }
                }
            }
        }

    }





    /*
    * public static void AccessList() {
        // Get number of domains N
        Random random = new Random();
        int N = 3 + random.nextInt(5);
        // Get number of objects M
        int M = 3 + random.nextInt(5);

        // calling the lockmaker function to create the lock
        // array and initialize all permits to 1.
        lockMaker(M);

        // Create Access List (arraylist of linked lists for each object/domain)
        List<Object> AL = new ArrayList<>(0);
        // Populate Access List
        int RorW;
        int dSwitch;
        for (int i = 0; i < M + N; i++) { // For each object/domain...
            LinkedList<String> list = new LinkedList<String>(); // Create a linked list
            for (int j = 0; j < N; j++) { // For each domain...
                if (i < M) { // Object R/W access
                    RorW = random.nextInt(4);
                    if (RorW == 0) list.add("D" + (j + 1) + ": R");
                    else if (RorW == 1) list.add("D" + (j + 1) + ": W");
                    else if (RorW == 2) list.add("D" + (j + 1) + ": R/W");
                } else { // Domain switch access
                    dSwitch = random.nextInt(2);
                    if (dSwitch == 0 && i - M != j) list.add("D" + (j + 1) + ": allow"); // Don't allow switching to self
                }
            }
            AL.add(list); // Add linked list to Access List
        }
        // Print Access List
        System.out.print(N + " domains\n" + M + " objects\nAccess List:");
        for (int i = 0; i < M + N; i++) {
            if (i < M) System.out.print("\nF" + (i + 1) + ": " + AL.get(i));
            else System.out.print("\nD" + (i + 1 - M) + ": " + AL.get(i));
        }

        // Create domain threads
        Domain domain;
        for(int i = 0; i < (int)Math.random() * (7-3) + 3; i++){
            domain = new Domain(N,M, i);
        }
    }
    *
    * */


//}