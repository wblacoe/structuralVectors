package orthography;

import cache.FlatCache;
import cache.ItemVectorPair;
import java.util.Collection;
import java.util.Map.Entry;
import vector.binary.DenseBinaryVector;

public class BinaryVectorOrthography extends Orthography<DenseBinaryVector>{
    
    public BinaryVectorOrthography(int dimensionality){
        super(
            dimensionality,
            new FlatCache<>(),
            DenseBinaryVector.createInfiniteDemarcators(dimensionality, 0.7f)
            //DenseBinaryVector.createFiniteDemarcators(dimensionality, 20)
        );
    }

    @Override
    public DenseBinaryVector createStringVector(String string){
        DenseBinaryVector stringVector = null;
        
        DenseBinaryVector letterVector;
        for(int i=0; i<string.length(); i++){
            String letter = string.substring(i, i + 1);
            
            letterVector = DenseBinaryVector.createDeterministicVector(dimensionality, letter);
            letterVector = letterVector.convolute(dem.get(i));
            
            if(stringVector == null){
                stringVector = letterVector;
            }else{
                stringVector = stringVector.plus(letterVector);
            }
        }
        
        return stringVector;
    }
    
    @Override
    public void add(String string){
        if(string != null && !string.isEmpty()){
            cache.add(string, createStringVector(string));
        }
    }
    
    @Override
    public Entry<Float, ItemVectorPair<String, DenseBinaryVector>> getClosest(DenseBinaryVector vector){
        return cache.getClosest(vector);
    }
    
    @Override
    public Collection<Entry<Float, ItemVectorPair<String, DenseBinaryVector>>> getNClosest(DenseBinaryVector vector, int n){
        return cache.getNClosest(vector, n);
    }
    
}