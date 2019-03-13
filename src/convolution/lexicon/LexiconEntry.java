package convolution.lexicon;

import vector.Vector;


public class LexiconEntry<K extends Comparable, V extends Vector> {

    private final K key;
    private final V vector;
    
    public LexiconEntry(K key, V vector){
        this.key = key;
        this.vector = vector;
    }
    
    public K key(){
        return key;
    }
    
    public V vector(){
        return vector;
    }
    
    @Override
    public String toString(){
        return "\"" + key + "\" -> " + vector.toString();
    }
    
}
