package cache;

import vector.Vector;

public class ItemVectorPair<I extends Comparable, V extends Vector> {
    
    private final I item;
    private final V vector;
    
    public ItemVectorPair(I item, V vector){
        this.item = item;
        this.vector = vector;
    }
    
    public I item(){
        return item;
    }
    
    public V vector(){
        return vector;
    }
    
    @Override
    public String toString(){
        return item + " => " + vector;
    }

}