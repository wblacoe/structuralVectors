package cache;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;
import vector.Vector;

public class FlatCache<I extends Comparable, V extends Vector> extends ItemVectorCache<I, V> {
    
    private final List<ItemVectorPair<I, V>> cacheByVector;
    
    public FlatCache(){
        cacheByVector = new LinkedList<>();
    }
    
    @Override
    public void add(ItemVectorPair<I, V>... pairs){
        for(ItemVectorPair<I, V> pair : pairs){
            cacheByItem.put(pair.item(), pair);
            cacheByVector.add(pair);
        }
    }
    
    @Override
    public void add(I item, V vector){
        ItemVectorPair<I, V> pair = new ItemVectorPair<>(item, vector);
        cacheByItem.put(item, pair);
        cacheByVector.add(pair);
    }
    
    @Override
    public Entry<Float, ItemVectorPair<I, V>> getClosest(V vector){
        float maxSim = Float.NEGATIVE_INFINITY, sim;
        ItemVectorPair<I, V> closest = null;
        
        for(ItemVectorPair<I, V> pair : cacheByVector){
            sim = vector.similarity(pair.vector(), true);
            if(sim > maxSim){
                maxSim = sim;
                closest = pair;
            }
        }
        
        return new SimpleEntry<>(maxSim, closest);
    }
    
    @Override
    public Collection<Entry<Float, ItemVectorPair<I, V>>> getNClosest(V vector, int n){
        //save the top n pairs in here closest to given vector
        TreeMap<Float, ItemVectorPair<I, V>> closestN = new TreeMap<>();
        
        float sim;
        for(ItemVectorPair<I, V> pair : cacheByVector){
            sim = vector.similarity(pair.vector(), true);
            closestN.put(sim, pair);
            while(closestN.size() > n) closestN.pollFirstEntry();
        }
        
        return closestN.descendingMap().entrySet().stream().collect(Collectors.toList());
    }
    
}