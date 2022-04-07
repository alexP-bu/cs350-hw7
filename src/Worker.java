import java.util.List;
import java.util.Set;
public class Worker implements Runnable {

    private String hash;
    private Long timeout;
    private Integer result;
    private List<Generator> generators;
    private Set<String> uncrackedHashes;
    private List<Integer> crackedHashes;

    Worker(String hash, Long timeout, List<Generator> generators, List<Integer> crackedHashes, Set<String> uncrackedHashes) {
        this.hash = hash;
        this.timeout = timeout;
        result = null;
        this.generators = generators;
        this.crackedHashes = crackedHashes;
        this.uncrackedHashes = uncrackedHashes;
    }

    
    /** 
     * @return Integer
     */
    private Integer attemptUpdateResult() {
        //let's try some fancy lambda stuff for fun :)
            
        //private Integer attemptUpdateResult(){
        //  for (Generator g : dictionaries) {
        //      if(g.getDictionary.containsKey(hash)){
        //          result = g.getDictionary.get(hash);
        //          return result
        //      }
        //  }
        //  return null
        //}

        //this lambda takes the list of generators, filters the ones which contain the hash,
        //finds the first one, maps the hash to the Integer or maps null to it and returns
        return generators
                    .stream()
                    .filter(generator -> generator.getDictionary().containsKey(hash))
                    .findFirst()
                    .map(generator -> generator.getDictionary().get(hash))
                    .orElse(null);
    }

    //prints result at the end of looping
    private void getResult(){
        if(result == null)
            uncrackedHashes.add(hash);
        else
            crackedHashes.add(result);
    }

    //try to get an unhash from the dictionaries
    @Override
    public void run() {
        //if timeout is null, run until result is gotten
        if (timeout == null) {
            while (result == null) {
                result = attemptUpdateResult();
            }
        } else {
            long endTime = System.currentTimeMillis() + timeout;
            while ((System.currentTimeMillis() < endTime) && (result == null)) {
                result = attemptUpdateResult();
            }
        }
        getResult();
    }
}
