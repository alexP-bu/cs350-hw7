public class Pirate {

    private Dispatcher dispatcher;
    private String result;
    private static final Hash hasher = new Hash();

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
    //use a pointer.
    //our first compound result could be 
        //a) 123;[124-233];234
        //b) 234;[235-344];345
        //c) this pattern onwards
    //if the result is the first result, we get the compound hint of a. That means our next
    //hint cannot contain 123 or 234 - we can essentially remove them from the list and run the above
    //again
    

    //work in progress
    public void findTreasure(String path){
        dispatcher.unhashFromFile(path);
        //sort cracked hashes from first pass
        dispatcher.listCracked();
        dispatcher.listUncracked();
    }
 
    public static void main(String[] args) {
        Pirate pirate = new Pirate();
        pirate.findTreasure(args[0]);
        //pirate.dispatcher.printOuput();
    }
}
