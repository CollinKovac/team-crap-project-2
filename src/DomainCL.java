import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

public class DomainCL implements Runnable{
    private static int M;
    private static int N;
    private int threadNum;
    static List<Object> list;
    static String[] object;
    static String[] writerObject = {"Chibaku Tensei", "Kotoamatsukami", "bijudama", "edo tensei", "kamui", "Reaper Death Seal"};
    static Lock[] lock;

    public DomainCL(int objects, int domains, int thread, ArrayList<Object> AL, String[] array, Lock[] lock) {
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
