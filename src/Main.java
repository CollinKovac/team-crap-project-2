import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void AccessMatrix() {
        // Get number of domains N
        Random random = new Random();
        int N = 3 + random.nextInt(5);
        // Get number of objects M
        int M = 3 + random.nextInt(5);

        // Create Access Matrix of size N*(M+N)
        String[][] AM = new String[N][M + N];
        int RorW;
        int dSwitch;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M + N; j++) {
                if (j < M) { // Object R/W access
                    RorW = random.nextInt(4);
                    if (RorW == 0) AM[i][j] = " - ";
                    else if (RorW == 2) AM[i][j] = " R ";
                    else if (RorW == 3) AM[i][j] = " W ";
                    else AM[i][j] = "R/W";
                } else { // Domain switch access
                    dSwitch = random.nextInt(2);
                    if (dSwitch == 0 || j - M == i) AM[i][j] = "  -  "; // Don't allow switching to self
                    else AM[i][j] = "allow";
                }
            }
        }
        // Print Access Matrix
        System.out.print(N + " domains\n" + M + " objects\nAccess Matrix:\n     ");
        for (int i = 0; i < M + N; i++) {
            if (i < M) System.out.print("F" + i + "   ");
            else System.out.print(" D" + (i - M) + "    ");
        }
        for (int i = 0; i < N; i++) {
            System.out.print("\nD" + i + "  ");
            for (int j = 0; j < M + N; j++)
                System.out.print(AM[i][j] + "  ");
        }
        System.out.println();

        String[] object = {"May chaos take the world!", "A man cannot kill a god...", "Bear witness!",
                "Thy strength befits a crown.", "I command thee kneel!", "Together, we will devour the very gods!",
                "Sir Gideon Ofnir, the All-knowing!"};
        Semaphore[] area = new Semaphore[object.length];
        Semaphore[] mutex = new Semaphore[object.length];
        int[] readcount = new int[object.length];
        for (int i = 0; i < M; i++) {
            area[i] = new Semaphore(1);
            mutex[i] = new Semaphore(1);
            readcount[i] = 0;
        }

        Domain.M = M;
        Domain.N = N;
        Domain.matrix = AM;
        Domain.object = object;
        Domain.area = area;
        Domain.mutex = mutex;
        Domain.readcount = readcount;

        // Create domain threads
        for(int i = 0; i < N; i++){
            Domain domain = new Domain(M,N,i,AM,object,mutex,area,readcount);
            Thread myThread = new Thread(domain);
            myThread.start();
        }
    }

    public static void AccessList() {
        // Get number of domains N
        Random random = new Random();
        int N = 3 + random.nextInt(5);
        // Get number of objects M
        int M = 3 + random.nextInt(5);

        // Create Access List (arraylist of linked lists for each object/domain)
        ArrayList<LinkedList<String>> AL = new ArrayList<>(0);
        int RorW;
        int dSwitch;
        for (int i = 0; i < M + N; i++) { // For each object/domain...
            LinkedList<String> list = new LinkedList<String>(); // Create a linked list
            for (int j = 0; j < N; j++) { // For each domain...
                if (i < M) { // Object R/W access
                    RorW = random.nextInt(4);
                    if (RorW == 0) list.add("D" + j + ": R");
                    else if (RorW == 1) list.add("D" + j + ": W");
                    else if (RorW == 2) list.add("D" + j + ": R/W");
                } else { // Domain switch access
                    dSwitch = random.nextInt(2);
                    if (dSwitch == 0 && i - M != j) list.add("D" + j + ": allow"); // Don't allow switching to self
                }
            }
            AL.add(list); // Add linked list to Access List
        }
        // Print Access List
        System.out.print(N + " domains\n" + M + " objects\nAccess List:");
        for (int i = 0; i < M + N; i++) {
            if (i < M) System.out.print("\nF" + i + ": " + AL.get(i));
            else System.out.print("\nD" + (i - M) + ": " + AL.get(i));
        }
        System.out.println();

        String[] object = {"May chaos take the world!", "A man cannot kill a god...", "Bear witness!",
                "Thy strength befits a crown.", "I command thee kneel!", "Together, we will devour the very gods!",
                "Sir Gideon Ofnir, the All-knowing!"};
        Lock[] area = new Lock[object.length];
        Lock[] mutex = new Lock[object.length];
        int[] readcount = new int[object.length];
        for (int i = 0; i < M; i++) {
            area[i] = new ReentrantLock();
            mutex[i] = new ReentrantLock();
            readcount[i] = 0;
        }

        DomainAL.object = object;
        DomainAL.area = area;
        DomainAL.mutex = mutex;
        DomainAL.readcount = readcount;

        // Create domain threads
        for(int i = 0; i < 1; i++){
            DomainAL domain = new DomainAL(M,N,i,AL,object,mutex,area,readcount);
            Thread myThread = new Thread(domain);
            myThread.start();
        }
    }


    public static void CapabilityList(){
        // Get number of domains N
        Random random = new Random();
        int N = 3 + random.nextInt(5);
        // Get number of objects M
        int M = 3 + random.nextInt(5);

        // Create Capability List
        ArrayList<LinkedList<String>> capabilityLists = new ArrayList<>(0);
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

        String[] object = {"May chaos take the world!", "A man cannot kill a god...", "Bear witness!",
                "Thy strength befits a crown.", "I command thee kneel!", "Together, we will devour the very gods!",
                "Sir Gideon Ofnir, the All-knowing!"};
        Lock[] area = new Lock[object.length];
        Lock[] mutex = new Lock[object.length];
        int[] readcount = new int[object.length];
        for (int i = 0; i < M; i++) {
            area[i] = new ReentrantLock();
            mutex[i] = new ReentrantLock();
            readcount[i] = 0;
        }

        DomainAL.object = object;
        DomainAL.area = area;
        DomainAL.mutex = mutex;
        DomainAL.readcount = readcount;

        // Create domain threads
        for(int i = 0; i < 1; i++){
            DomainCL domain = new DomainCL(M,N,i,capabilityLists,object,mutex,area,readcount);
            Thread myThread = new Thread(domain);
            myThread.start();
        }
    }

    public static void main(String[] args) {
        AccessMatrix();
        //AccessList();
        //CapabilityList();
    }
}