import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

public class DomainCL implements Runnable{
    private static int M;
    private static int N;
    private int threadNum;
    static ArrayList<LinkedList<String>> list;
    static String[] object;
    static String[] writerObject = {"Chibaku Tensei", "Kotoamatsukami", "bijudama", "edo tensei", "kamui", "Reaper Death Seal"};
    static Lock[] lock;

    public DomainCL(int objects, int domains, int thread, ArrayList<LinkedList<String>> AL, String[] array, Lock[] lock) {
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


    private static Boolean arbitrator(String[] domainPermissions, int targetDomain, String permission) {
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
        // Copying targeted domain permissions to current domain
        for (int i = 0; i < M+N; i++)
            //domainPermissions[i] =  list[targetDomain][i];
            System.out.println("D" + currentDomain + ": Switched to D" + targetDomain);
    }

    @Override
    public void run() {
        Random random = new Random();

        String[] domainPermissions = new String[M+N];
        for (int i = 0; i < M+N; i++) {
            //domainPermissions[i] = (String) list.get(threadNum);
            System.out.println(list.get(threadNum));
        }
    }
}
