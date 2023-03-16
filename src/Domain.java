import java.util.concurrent.Semaphore;

public class Domain implements Runnable {

    private static int M;
    private static int N;
    private int threadNum;
    public Domain(int objects, int domains, int thread ) {
        M = objects;
        N = domains;
        this.threadNum = thread;
    }

    //semaphore creation used for the readers and writers fucntions
    static Semaphore area = new Semaphore(1);
    static Semaphore mutex = new Semaphore(1);
    static int readcount = 0;

    //reader function to run when accessible. int resourceRequest is the index
    //of which resource is being requested
    private static void reader(int resourceRequest) throws InterruptedException {
        mutex.acquire();
        readcount++;
        if(readcount == 1){
            area.acquire();
        }
        mutex.release();

        System.out.println("Resource" +resourceRequest+ " contains: " +object[resourceRequest]);

        mutex.acquire();
        readcount--;
        if(readcount == 0){
            area.release();
        }
        mutex.release();
    }

    // this array will be used to choose what phrase to put in place of the
    // index that is being overwritten
    static String[] writerObject = {"Chibaku Tensei","Kotoamatsukami","bijudama", "edo tensei" , "kamui", "Reaper Death Seal"};

    // writer function to run when accessible. int resourceRequest is the resource
    // that is being requested.
    private static void write(int resourceRequest) throws InterruptedException {
        area.acquire();

        object[resourceRequest] = writerObject[(int) (Math.random() * (6))];
        System.out.println("writing " +object[resourceRequest]+ " to resource " +resourceRequest);

        area.release();
    }



    @Override
    public void run() {
        for(int i = 0; i < 5; i++){
            int request = (int) (Math.random() * (M+N));
        }

    }

}