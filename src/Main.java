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
        Lock[] lock = new Lock[M];
        for (int i = 0; i < M; i++) lock[i] = new ReentrantLock();
        Semaphore area = new Semaphore(1);
        Semaphore mutex = new Semaphore(1);
        int readcount = 0;

        Domain.object = object;
        Domain.lock = lock;
        Domain.area = area;
        Domain.mutex = mutex;
        Domain.readcount = readcount;

        // Create domain threads
        for(int i = 0; i < N; i++){
            Domain domain = new Domain(M,N,i,AM,object,area,mutex,readcount,lock);
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
        Lock[] lock = new Lock[M];
        for (int i = 0; i < M; i++) lock[i] = new ReentrantLock();
        Semaphore area = new Semaphore(1);
        Semaphore mutex = new Semaphore(1);
        int readcount = 0;

        DomainAL.object = object;
        DomainAL.lock = lock;
        DomainAL.area = area;
        DomainAL.mutex = mutex;
        DomainAL.readcount = readcount;

        // Create domain threads
        for(int i = 0; i < 1; i++){
            DomainAL domain = new DomainAL(M,N,i,AL,object,lock);
            Thread myThread = new Thread(domain);
            myThread.start();
        }
    }

    public static void main(String[] args) {
        //AccessMatrix();
        AccessList();
    }
}