import java.util.concurrent.Semaphore;

public class Domain implements Runnable {

    private int threadNum;
    private static int N;
    private static int M;
    public Domain(int objects, int domains, int thread) {
        M = objects;
        N = domains;
        this.threadNum = thread;
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



    @Override
    public void run() {
        for(int i = 0; i < 5; i++){
            int request = (int) (Math.random() * (M+N));
        }

    }

}