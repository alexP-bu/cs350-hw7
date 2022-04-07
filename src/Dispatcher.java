import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * this dispatcher is pretty cool
 * it generates several subets at the same time, all in the form of hashmaps, and cracks hashes based on those subsets
 * so its pretty quick for large numbers o.O
 */

 /*
 * Oh, and lots of lambda expressions
 */
public class Dispatcher {

    public static final int NUM_GENS = 20; //number of generators generating hashmap subsets 
    private BlockingQueue<String> workQueue;
    private List<Generator> generators;
    private List<Thread> workerThreads;
    private List<Integer> crackedHashes;
    private Set<String> uncrackedHashes;
    private Long timeout;

    // optimization: let's write everything to a buffered writer and spit it
    // out at the end because sysout is too slow
    public static final BufferedWriter printer = new BufferedWriter(new OutputStreamWriter(System.out));

    public Dispatcher() {
        this.workQueue = new LinkedBlockingQueue<>();
        this.generators = new Vector<>();
        this.workerThreads = new Vector<>();
        this.uncrackedHashes = new HashSet<>();
        this.crackedHashes = new Vector<>(); 
    }

    /**
     * @param path
     */
    // read file line by line and dispatch them to the queue
    public void unhashFromFile(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            // send generators off to begin generating values in the hashmap
            initGenerators(NUM_GENS);
            //read lines with fancy lambda :o
            br.lines().parallel().forEach(line -> workQueue.add(line));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //dispatch
        this.dispatch();
        // ensure all threads finish
        this.completeThreads();
    }

    /**
     * @param hash
     */
    public void dispatch() {
        workerThreads = Stream
                            .generate(() -> {
                                Thread t = new Thread(new Worker(
                                    workQueue.poll(), timeout, generators, crackedHashes, uncrackedHashes));
                                t.start();
                                return t;
                            })
                            .limit(workQueue.size())
                            .collect(Collectors.toList());
    }

    /**
     * @param timeout
     */
    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    /**
     * @param numGensInit
     */
    private void initGenerators(int numGensInit) {
        AtomicInteger a = new AtomicInteger(0);
        this.generators = Stream
                            .generate(() -> new Generator(a.getAndIncrement()))
                            .limit(numGensInit++)
                            .collect(Collectors.toList());

        generators
            .parallelStream()
            .forEach(generator -> {
                        Thread t = new Thread(generator);
                        t.start();
                    }
            );
    }

    private void completeThreads() {
        workerThreads
            .parallelStream()
            .forEach(thread -> {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        });

        generators
            .parallelStream()
            .forEach(Generator::stop);
    }

    /**
     * @param s
     */
    public static void writeToOutput(String s) {
        try {
            printer.write(s + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getCrackedHashes(){
        return crackedHashes;
    }
    
    public Set<String> getUncrackedHashes(){
        return uncrackedHashes;
        
    } 

    public void sortCrackedHashes(){
        crackedHashes = crackedHashes.parallelStream().sorted(Comparator.reverseOrder()).toList();
    }

    /**
     * @param args[0] file path
     * @param args[1] num cpus
     * @param args[2] OPTIONAL timeout
     * @throws IOException
     */
    public static void main(String[] args) {
        // initialize dispatcher
        Dispatcher dispatcher = new Dispatcher();
        // the submission portal is kinda buggy with the second argument
        // so set the timeout manually
        if (args.length > 2) {
            dispatcher.setTimeout(Long.valueOf(args[2]));
        }
        // import hashes into dispatcher
        dispatcher.unhashFromFile(args[0]);
        //print output
        try {
            printer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
