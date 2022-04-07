import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;
public class Pirate {
    
    private Dispatcher dispatcher;
    private List<Thread> threads;
    private final PrintWriter printer;
    
    public Pirate(){
        this.dispatcher = new Dispatcher();
        this.threads = new Vector<>();
        printer = new PrintWriter(System.out);
    }
    
    //turns out running backwards through the list is way faster!
    public void findTreasure(String path){
        //run first pass
        dispatcher.unhashFromFile(path);
        //sort cracked ints from first pass from low to high
        dispatcher.sortCrackedHashes();
        //write cracked hashes to our printer
        dispatcher.getCrackedHashes()
                    .parallelStream()
                    .forEach(hash -> printer.write(hash + "\n"));
        //spooky lambda operation which runs our unhash operation
        //on every subset of hashes where hash1 < hash2 in ascending order of hash1
        dispatcher.getCrackedHashes()
            .parallelStream()
            .forEach(hash1 -> 
                dispatcher.getCrackedHashes()
                            .parallelStream()
                            .filter(hash2 -> hash1 < hash2)
                            .forEach(hash2 -> {
                                Thread t = new Thread(new TreasureGnome(this, hash1, hash2, dispatcher.getUncrackedHashes()));
                                t.start();
                                threads.add(t);
                            }));
        
        
        //ensure all threads are done before finishing
        finishThreads();
        //write failed puzzles to output
        dispatcher.getUncrackedHashes()
                    .parallelStream()
                    .forEach(hash -> printer.write(hash + "\n"));
    }

    private void finishThreads(){
        threads.parallelStream().forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        });
    }

    public void setNumCPUS(int cpus){
        //dispatcher.setNumCPUS(cpus);
    }

    public void setTimeout(Long timeout){
        dispatcher.setTimeout(timeout);
    }

    public void printOuput(){
        printer.flush();
    }
    
    public void writeToOutput(String string) {
        printer.write(string);
    }
 
    public static void main(String[] args) {
        Pirate pirate = new Pirate();
        //pirate.setNumCPUS(Integer.valueOf(args[1])); who needs to cap cpus huh not me
        pirate.setTimeout(Long.valueOf(args[2]));
        pirate.findTreasure(args[0]);
        pirate.printOuput();
    }
}
