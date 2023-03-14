import java.util.concurrent.Semaphore;

public class Domain implements Runnable {

    private int threadNum;
    public Domain(int objects) {
        this.threadNum = objects;
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

    }

}
