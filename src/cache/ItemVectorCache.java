package cache;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import vector.Vector;

public abstract class ItemVectorCache<I extends Comparable, V extends Vector> implements Iterable<ItemVectorPair<I, V>>{
    
    public final TreeMap<I, ItemVectorPair<I, V>> cacheByItem;
    
    public ItemVectorCache(){
        cacheByItem = new TreeMap<>();
    }
    
    public ItemVectorPair<I, V> get(I item){
        return cacheByItem.get(item);
    }
    
    public abstract void add(ItemVectorPair<I, V>... pairs);
    public abstract void add(I item, V vector);
    public abstract Entry<Float, ItemVectorPair<I, V>> getClosest(V vector);
    public abstract Collection<Entry<Float, ItemVectorPair<I, V>>> getNClosest(V vector, int n);
    
    @Override
    public Iterator<ItemVectorPair<I, V>> iterator() {
        return cacheByItem.values().iterator();
    }
    
    @Override
    public String toString(){
        String s = "";
        
        Iterator<ItemVectorPair<I, V>> it = iterator();
        int i=0;
        while(it.hasNext()){
            s += it.next() + "\n";
            if(i >= 5){
                s += "...\n";
                break;
            }
        }
        
        return s;
    }

}
