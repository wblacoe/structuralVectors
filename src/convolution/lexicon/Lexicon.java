package convolution.lexicon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import vector.Vector;
import vector.real.DenseRealVector;
import vector.real.RealWeight;

public class Lexicon<K extends Comparable, V extends Vector> {
    
    public static final float THRESHOLD = 0.2f;

    public ArrayList<LexiconEntry<K, V>> entries;
    public int dimensionality;

    public Lexicon(){
        entries = new ArrayList<>();
        dimensionality = -1;
    }
    
    public Lexicon(int dimensionality){
        this();
        this.dimensionality = dimensionality;
    }
    
    
    public int getDimensionality(){
        return dimensionality;
    }
    
    public void addEntry(LexiconEntry entry){
        entries.add(entry);
        dimensionality = entry.vector().getDimensionality();
    }
    
    public void addEntry(String name, Vector vector){
        addEntry(new LexiconEntry(name, vector));
    }
    
    public LexiconEntry getEntry(K key){
        for(LexiconEntry<K, V> entry : entries){
            if(entry.key().equals(key)){
                return entry;
            }
        }
        
        return null;
    }
    
    public LexiconEntry getNearestEntry(V vector){
        String debug = "";
        LexiconEntry r;
        
        float maxSim = Float.MIN_VALUE, sim;
        LexiconEntry nearestEntry = null;
        for(LexiconEntry entry : entries){
            sim = entry.vector().similarity(vector);
            //debug += "similarity for \"" + entry.key() + "\" = " + sim + "\n"; //DEBUG
            if(sim > maxSim){
                maxSim = sim;
                nearestEntry = entry;
            }
        }
        
        if(nearestEntry == null){
            System.out.println();
            return null;
        }
        
        debug += "nearest entry is \"" + nearestEntry.key() + "\" (" + maxSim + ")";
        
        if(maxSim >= THRESHOLD){
            debug += " => ACCEPT";
            r = nearestEntry;
        }else{
            debug += " => REJECT";
            r = null;
        }
        
        System.out.println(debug);
        return r;
    }
    
    @Override
    public String toString(){
        String s = "";
        
        for(LexiconEntry entry : entries){
            s += entry.toString() + "\n";
        }
        
        return s;
    }
    
    public void exportTo(File f){
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(f));
            
            String s;
            for(LexiconEntry entry : entries){
                s = entry.key() + "\t";
                for(int d=0; d<entry.vector().getDimensionality(); d++){
                    s += entry.vector().getWeight(d) + " ";
                }
                out.write(s + "\n");
            }
            
            out.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public static Lexicon importWithDenseRealVectors(File f){
        Lexicon lexicon = new Lexicon();
        
        try{
            BufferedReader in = new BufferedReader(new FileReader(f));
            
            String line;
            while((line = in.readLine()) != null){
                
                String[] e1 = line.split("\t");
                String name = e1[0];
                
                String[] e2 = e1[1].split(" ");
                DenseRealVector vector = new DenseRealVector(e2.length);
                for(int d=0; d<e2.length; d++){
                    String valueString = e2[d];
                    float value = Float.parseFloat(valueString);
                    vector.setWeight(d, new RealWeight(value));
                }
                
                lexicon.addEntry(name, vector);
            }
            
            in.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        
        return lexicon;
    }

}
