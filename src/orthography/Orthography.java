package orthography;

import cache.ItemVectorCache;
import cache.ItemVectorPair;
import java.util.Collection;
import java.util.Map.Entry;
import vector.Vector;
import vector.real.DenseRealVector;

//IDEA: letters at the borders of words are more important than the others -> define (infinite) demarcator vectors such that they are further apart from each other at the beginning and end
public abstract class Orthography<V extends Vector> {
    
    protected final int dimensionality;
    protected final ItemVectorCache<String, V> cache;
    protected final Demarcators<V> dem;
    
    public Orthography(int dimensionality, ItemVectorCache<String, V> cache, Demarcators<V> dem){
        this.dimensionality = dimensionality;
        this.cache = cache;
        this.dem = dem;
    }
    
    public abstract V createStringVector(String string);
    public abstract void add(String string);
    public abstract Entry<Float, ItemVectorPair<String, V>> getClosest(V vector);
    public abstract Collection<Entry<Float, ItemVectorPair<String, V>>> getNClosest(V vector, int n);
    
    public V getVector(String string){
        ItemVectorPair<String, V> pair = cache.get(string);
        return (pair == null ? null : pair.vector());
    }
    
    
    
    public static void main(String[] args){
        String[] words1 = new String[]{
            "barbarous",
            "cagey",
            "grip",
            "shocking",
            "coal",
            "slip",
            "puncture",
            "pause",
            "knee",
            "cheese",
            "cynical",
            "line"
        };
        
        String[] words2 = new String[]{
            "barbara",
            "cage",
            "trip",
            "shucks",
            "cole",
            "ship",
            "picture",
            "pose",
            "knea",
            "chose",
            "dynamical",
            "finess"
        };
        
        //BinaryVectorOrthography o = new BinaryVectorOrthography(1024);
        RealVectorOrthography o = new RealVectorOrthography(128);
        
        for(String word : words1){
            o.add(word);
        }
        
        for(String word : words1){
            //Entry<Float, ItemVectorPair<String, DenseBinaryVector>> closest = o.getClosest(o.getVector(word));
            Entry<Float, ItemVectorPair<String, DenseRealVector>> closest = o.getClosest(o.getVector(word));
            System.out.println("closest to " + word + ": " + closest.getKey() + " " + closest.getValue().item());
        }
        
        int n = 3;
        for(String word : words2){
            //System.out.println("\nclosest to " + word + ": " + o.getClosest(o.createStringVector(word)));
            System.out.println("\nclosest to " + word + ":" + o.getNClosest(o.createStringVector(word), n).stream().map(e -> "\n" + e.getKey() + " " + e.getValue().item()).reduce(String::concat).get());
        }
    }

}
