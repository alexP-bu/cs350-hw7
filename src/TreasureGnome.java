import java.util.Set;

public class TreasureGnome implements Runnable {

    private Hash hasher;
    private Pirate pirate;
    private Set<String> uncrackedHashes;
    private int start;
    private int end;

    public TreasureGnome(Pirate pirate, int start, int end, Set<String> uncrackedHashes){
        this.hasher = new Hash();
        this.pirate = pirate;
        this.start = start;
        this.end = end;
        this.uncrackedHashes = uncrackedHashes;
    }

    //THE TREASURE GNOME DEPARTS ITS DEN TO FIND A HASH FOR THE PIRATE
    @Override
    public void run() {
        for (int j = start + 1; j < end; j++) {
            String currHash = hasher.hash(start + ";" + j + ";" + end);
            if (uncrackedHashes.contains(currHash)) {
                uncrackedHashes.remove(currHash);
                pirate.writeToOutput(start + ";" + j + ";" + end + "\n");
            }
        }
    }

}
