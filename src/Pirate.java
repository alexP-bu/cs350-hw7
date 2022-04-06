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
    
    public void findTreasure(String path){
        //run first pass
        dispatcher.unhashFromFile(path);
        //sort cracked ints from first pass from low to high
        dispatcher.sortCrackedHashes();
        dispatcher.getCrackedHashes().parallelStream().forEach(hash -> printer.write(hash + "\n"));
        //run our unhash operation
        for(int i = 0; i < dispatcher.getCrackedHashes().size() - 1; i++){
            Integer curr = i;
            Integer endPoint = dispatcher.getCrackedHashes().get(curr);
            for(int k = curr + 1; k < dispatcher.getCrackedHashes().size(); k++){
                Integer startPoint = dispatcher.getCrackedHashes().get(k);
                Thread t = new Thread(() -> {
                    Hash hasher = new Hash();
                    for(int j = startPoint + 1; j < endPoint; j++){
                        String currHash = hasher.hash(startPoint + ";" + j + ";" + endPoint);
                        if(dispatcher.getUncrackedHashes().contains(currHash)){
                            printer.write(startPoint + ";" + j + ";" + endPoint + "\n");
                            dispatcher.getUncrackedHashes().remove(currHash);
                        }
                    }
                });
                t.start();
                threads.add(t);
            }
        }

        threads.parallelStream().forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        });

        dispatcher.getUncrackedHashes().parallelStream().forEach(hash -> printer.write(hash + "\n"));
    }

    public void setNumCPUS(int cpus){
        dispatcher.setNumCPUS(cpus);
    }

    public void setTimeout(Long timeout){
        dispatcher.setTimeout(timeout);
    }

    public void printOuput(){
        printer.flush();
    }
 
    public static void main(String[] args) {
        Pirate pirate = new Pirate();
        pirate.setNumCPUS(Integer.valueOf(args[1]));
        pirate.setTimeout(Long.valueOf(args[2]));
        pirate.findTreasure(args[0]);
        pirate.printOuput();
    }
}
