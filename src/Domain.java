import java.util.ArrayList;
import java.util.Arrays;
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

    //reader function to run when accessible
    private static void reader(int threadNum) throws InterruptedException {
        mutex.acquire();
        readcount++;
        if(readcount == 1){
            area.acquire();
        }
        mutex.release();

        //read here

        mutex.acquire();
        readcount--;
        if(readcount == 0){
            area.release();
        }
        mutex.release();
    }

    // writer function to run when accessible
    private static void write(int threadNum) throws InterruptedException {
        area.acquire();

        //write here

        area.release();
    }





    //Domain switching method
    public static void switchDomain(int currentDomain, int targetDomain, String[] domainPermission) {

        int totalColumns = M + N;
        boolean switchAllowed = false;
        /*
        String [] curDomainArr = new String[totalColumns];
        for (int i = 0; i < totalColumns; i++) {
            curDomainArr[i] = AM[currentDomain][i];
        }


        String [] tarDomainArr = new String[totalColumns];
        for (int i = 0; i < totalColumns; i++) {
            tarDomainArr[i] = AM[targetDomain][i];
        }

         */




        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //System.out.println(Arrays.toString(curDomainArr));
        //System.out.println(Arrays.toString(tarDomainArr));

        // Check if switching is allowed for the current domain and target domain
        if (currentDomain != targetDomain && matrix[targetDomain][currentDomain].equals("allow")) {
            switchAllowed = true;
        }

        // If switching is allowed, update the Access Matrix to switch the domains
        if (switchAllowed) {
            //print out that a switch is a allowed
            int tempPrinter = currentDomain + 1;
            int tempPrinter2 = targetDomain + 1;
            System.out.println("Switch permission is granted from Domain " + tempPrinter + " to Domain " + tempPrinter2);
            //copying domain permissions from targeted domain to current domain
            for (int i = 0; i < M+N; i++) {
                domainPermission[i] =  matrix[targetDomain][i];
            }


            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            System.out.println("final change _________  " + Arrays.toString(matrix[currentDomain]));
        }




        else{
            System.out.println("Switch permission is NOT granted");
            int randInt = 3 + (int)(Math.random() * ((7 - 3) + 1));
            for (int j = 0; j < randInt; j++) {
                Thread.yield();
            }
        }
    }



    @Override
    public void run() {

        for (int i = 0; i < M+N; i++) {
            domainPermissions[i] = matrix[threadNum][i];
        }


        for(int i = 0; i < 5; i++){
            int request = (int) (Math.random() * (M+N));
            if (request <= M){
                //do the read and write
            }
            else {
                switchDomain(threadNum , request , domainPermissions);
            }
        }




    }

}