import java.util.List;
import java.util.Vector;

public class Pirate {

    private Dispatcher dispatcher;
    private List<Thread> threads;

    public Pirate(){
        this.dispatcher = new Dispatcher();
        this.threads = new Vector<>();
    }

    //lets think of way to solve it
    //we have a list of cracked integers
    //and a list of uncracked hashes
    
    //naive solution
    //we have a list of cracked hashes from the dispatcher.
    //sort the list from lowest to highest

    //say our list is 123, 234, 345, 456
    //we could have compound hints:
    //123;[124 - 233];234
    //123;[124-344];345
    //etc
    //so, for each hash, for each of the rest of the hashes greater than
    //check if any of the hashes in the range are in the uncracked hash set
    public void findTreasure(String path){
        //run first pass
        dispatcher.unhashFromFile(path);
        //sort cracked ints from first pass from low to high
        dispatcher.sortCrackedHashes();
        //run our unhash operation
        for(int i = 0; i < dispatcher.getCrackedHashes().size() - 1; i++){
            Integer curr = i;
            Thread t = new Thread(() -> {
                Hash hasher = new Hash();
                for(int k = curr + 1; k < dispatcher.getCrackedHashes().size(); k++){
                    for(int j = dispatcher.getCrackedHashes().get(curr) + 1;
                        j < dispatcher.getCrackedHashes().get(k);
                        j++){
                
                    String currHash = hasher.hash(dispatcher.getCrackedHashes().get(curr) + ";" + j + ";" + dispatcher.getCrackedHashes().get(k));
                    if(dispatcher.getUncrackedHashes().contains(currHash)){
                            System.out.println(dispatcher.getCrackedHashes().get(curr) + ";" + j + ";" + dispatcher.getCrackedHashes().get(k));
                            dispatcher.getUncrackedHashes().remove(currHash);
                        }
                
                    }
                }

            });
            threads.add(t);
            t.start();
        }

        threads.stream().forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        });

        dispatcher.listCracked();
        dispatcher.listUncracked();  
    }

    public void setNumCPUS(int cpus){
        dispatcher.setNumCPUS(cpus);
    }

    public void setTimeout(Long timeout){
        dispatcher.setTimeout(timeout);
    }
 
    public static void main(String[] args) {
        Pirate pirate = new Pirate();
        pirate.setNumCPUS(Integer.valueOf(args[1]));
        pirate.setTimeout(Long.valueOf(args[2]));
        pirate.findTreasure(args[0]);
        //pirate.dispatcher.printOuput();
    }
}
