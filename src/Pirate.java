public class Pirate {

    private Dispatcher dispatcher;

    public Pirate(){
        this.dispatcher = new Dispatcher();
    }

    //lets think of way to solve it
    //we have a list of cracked integers
    //and a list of uncracked hashes
    
    //naive solution
    //we have a list of cracked hashes from the dispatcher.
    //sort the list from lowest to highest

    //say our list is 123, 234, 345, 456
    //we could have compound hints like this:
    // 123;[124 - 233];234
    // 234;[235 - 344];345
    // 345;[346 - 455];456
    // So, if we sort our cracked hashes, we could brute force for every subset    

    //work in progress
    public void findTreasure(String path){
        dispatcher.unhashFromFile(path);
        //sort cracked hashes from first pass
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
        if(args.length > 2){
            pirate.setTimeout(Long.valueOf(args[2]));
        }
        pirate.findTreasure(args[0]);
        //pirate.dispatcher.printOuput();
    }
}
