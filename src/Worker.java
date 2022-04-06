import java.util.List;

public class Worker implements Runnable{
    
    private String hash;
    private UnHash unhasher;
    private Integer result;
    private List<Integer> crackedHashes;
    private List<String> uncrackedHashes;

    Worker(String hash, Long timeout, List<String> uncracked, List<Integer> cracked){
        this.hash = hash;
        this.unhasher = new UnHash(timeout);
        this.uncrackedHashes = uncracked;
        this.crackedHashes = cracked;
    }

    public Integer getResult(){
        return result;
    }

    public String getHash(){
        return hash;
    }

    @Override
    public void run(){
        if((this.result = unhasher.unhash(hash)) == null){
            uncrackedHashes.add(hash);
        }else{
            crackedHashes.add(result);
        }
    }
}
