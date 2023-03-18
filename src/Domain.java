import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Domain implements Runnable {

    private static int M;
    private static int N;
    private int threadNum;
    private static String[][] matrix;
    private static String[] object;
    private String[]domainPermissions = new String[M+N];

    public Domain(int objects, int domains, int thread, String[][] AM, String[] array) {
        M = objects;
        N = domains;
        this.threadNum = thread;
        matrix = AM;
        object = array;

    }
    //semaphore creation used for the readers and writers fucntions
    static Semaphore area = new Semaphore(1);
    static Semaphore mutex = new Semaphore(1);
    static int readcount = 0;

    private static Boolean arbitrator(int currentDomain, int targetDomain) {
        return !matrix[currentDomain][targetDomain].contains("-");
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
        System.out.println("thread " +threadNum+ ": Resource" +resourceRequest+ " contains: " +object[resourceRequest]);

        mutex.acquire();
        readcount--;
        if(readcount == 0){
            area.release();
        }
        mutex.release();
    }

    static String[] writerObject = {"Chibaku Tensei","Kotoamatsukami","bijudama", "edo tensei" , "kamui", "Reaper Death Seal"};

    // writer function to run when accessible
    private static void writer(int threadNum, int resourceRequest) throws InterruptedException {
        area.acquire();

        //write here
        object[resourceRequest] = writerObject[(int) (Math.random() * (6))];
        System.out.println("thread" +threadNum+ ": writing " +object[resourceRequest]+ " to resource " +resourceRequest);

        area.release();
    }

    //Domain switching method
    public static void switchDomain(int currentDomain, int targetDomain, String[] domainPermissions) {
        // Check if switching is allowed for the current domain and target domain
        System.out.println("D" + currentDomain + ": Attempting to switch to D" + targetDomain);
        if (arbitrator(currentDomain, M+targetDomain)) {
            //copying targeted domain permissions to current domain
            for (int i = 0; i < M+N; i++)
                domainPermissions[i] =  matrix[targetDomain][i];
            System.out.println("D" + currentDomain + ": Switched to D" + targetDomain);
        } else System.out.println("D" + currentDomain + ": Permission NOT granted to switch to D" + targetDomain);
        int randInt = 3 + (int)(Math.random() * ((7 - 3) + 1));
        System.out.println("D" + currentDomain + ": Yielding " + randInt + " times");
        for (int j = 0; j < randInt; j++) {
            Thread.yield();
        }
    }

    @Override
    public void run() {
        Random random = new Random();

        for (int i = 0; i < M+N; i++) {
            domainPermissions[i] = matrix[threadNum][i];
        }


        for(int i = 0; i < 5; i++){
            int request = (int) (Math.random() * (M+N));
            if (request <= M){
                // procudes a number if 0 then it attempts to read and
                // 1 attempts to write
                int readNwrite = random.nextInt(1);
                if(readNwrite == 0){
                    System.out.println("thread " +this.threadNum+ ": attempting to read resource: " +matrix[0][request]);
                    if(domainPermissions[request].contains("R")){
                        // call the reader function
                        try {
                            reader(this.threadNum ,request);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else{
                        System.out.println("thread: " +this.threadNum+ ": operation failed, access denied");
                    }
                }
                else if(readNwrite == 1){
                    System.out.println("thread " +this.threadNum+ ": attempting to write to resource " +request);
                    if(domainPermissions[request].contains("W")){
                        try {
                            writer(this.threadNum, request);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else{
                        System.out.println("thread: " +this.threadNum+ ": operation failed, access denied");
                    }
                }
                else{
                    //testing purposes
                    System.out.println("OH NO SOMETHING WENT WRONGD!!!!!!!");
                }
            } else { // If domain switch
                while (M+N-request == threadNum) request = M + random.nextInt(N); // Don't generate self
                System.out.println(request);
                System.out.println("D" + threadNum + ": Request " + (i+1) + ": Switch to D" + (M+N-request));
                switchDomain(threadNum, M+N-request, domainPermissions);
            }
        }
    }
}