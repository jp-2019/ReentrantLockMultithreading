import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    static final int MAX_T = 2;

    public static void main(String[] args) {
        ReentrantLock reentrantLock = new ReentrantLock();
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_T);
        Runnable worker1 = new Worker(reentrantLock, "Job 1");
        Runnable worker2 = new Worker(reentrantLock, "Job 2");
        Runnable worker3 = new Worker(reentrantLock, "Job 3");
        Runnable worker4 = new Worker(reentrantLock, "Job 4");
        executorService.execute(worker1);
        executorService.execute(worker2);
        executorService.execute(worker3);
        executorService.execute(worker4);
        executorService.shutdown();
    }
}

class Worker implements Runnable {
    String name;
    ReentrantLock reentrantLock;

    public Worker(ReentrantLock reentrantLock, String name) {
        this.reentrantLock = reentrantLock;
        this.name = name;
    }

    @Override
    public void run() {
        boolean done = false;
        while(!done) {
            boolean answer = reentrantLock.tryLock();
            if(answer){
                try {
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                System.out.println( " task name " + name + " outer lock acquired at " + simpleDateFormat.format(date) + " doing outer work");
                System.out.println("-----------------------------------");
                Thread.sleep(1500);
                reentrantLock.lock();
                    try {
                        Date newDate = new Date();
                        SimpleDateFormat simpleNewDateFormat = new SimpleDateFormat("hh:mm:ss");
                        System.out.println( " task name " + name + " inner lock acquired at " + simpleNewDateFormat.format(newDate) + " doing inner work");
                        System.out.println("-----------------------------------");
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finally {
                        System.out.println(" task name " + name + " inner lock released ");
                        System.out.println("-----------------------------------");
                        reentrantLock.unlock();
                    }
                    System.out.println(" lock hold count " + reentrantLock.getHoldCount());
                    System.out.println("-----------------------------------");
                    System.out.println(" task name " + name + " is done ");
                    System.out.println("-----------------------------------");
                    done = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    System.out.println(" task name " + name + " outer lock released ");
                    System.out.println("-----------------------------------");
                    reentrantLock.unlock();
                    System.out.println(" lock hold count " + reentrantLock.getHoldCount());
                    System.out.println("-----------------------------------");
                }
            }else {
                System.out.println(" task name " + name + " waiting for lock ");
                System.out.println("-----------------------------------");
                try {
                    Thread.sleep(2000);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
