import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Calendar.AM;

public class Main {
    // Create strings for objects
    static String[] object = {"May chaos take the world!", "A man cannot kill a god...", "Bear witness!",
            "Thy strength befits a crown.", "I command thee kneel!", "Together, we will devour the very gods!",
            "Sir Gideon Ofnir, the All-knowing!"};
    public

    //creating the lock array for object accessing
    static Lock[] lock;

    public static void lockMaker(int M) {
        lock = new Lock[M];
        for (int i = 0; i < M; i++) lock[i] = new ReentrantLock();
    }

    public static void AccessMatrix() {
        // Get number of domains N
        Random random = new Random();
        int N = 3 + random.nextInt(5);
        // Get number of objects M
        int M = 3 + random.nextInt(5);

        // calling the lockmaker function to create the lock
        // array and initialize all permits to 1.
        lockMaker(M);

        // Create Access Matrix of size N*(M+N)
        String[][] AM = new String[N][M + N];
        // Populate Access Matrix
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
            if (i < M) System.out.print("F" + (i + 1) + "   ");
            else System.out.print(" D" + (i + 1 - M) + "    ");
        }
        for (int i = 0; i < N; i++) {
            System.out.print("\nD" + (i + 1) + "  ");
            for (int j = 0; j < M + N; j++)
                System.out.print(AM[i][j] + "  ");
        }

        // Create domain threads
        Domain domain;
        for(int i = 0; i < (int)(Math.random() * (7 - 3) + 3); i++){
            domain = new Domain(N,M,i,AM, object);
            domain.run();
        }
    }

    public static void AccessList() {
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
//        Domain domain;
//        for(int i = 0; i < (int)(Math.random() * (7-3) + 3); i++){
//            domain = new Domain(N,M,i,AM, object);
//            domain = new Domain(N,M,i,AM, object);
//        }
    }

    public static void main(String[] args) {
        AccessMatrix();
        //AccessList();
    }
}