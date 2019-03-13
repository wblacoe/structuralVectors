package orthography;

import cache.FlatCache;
import cache.ItemVectorPair;
import java.util.Collection;
import java.util.Map.Entry;
import vector.real.DenseRealVector;

public class RealVectorOrthography extends Orthography<DenseRealVector>{
    
    public RealVectorOrthography(int dimensionality){
        super(
            dimensionality,
            new FlatCache<>(),
            DenseRealVector.createInfiniteDemarcators(dimensionality, 0.7f)
            //DenseRealVector.createFiniteDemarcators(dimensionality, 20)
        );
    }

    @Override
    public DenseRealVector createStringVector(String string){
        DenseRealVector stringVector = null;
        
        DenseRealVector letterVector;
        for(int i=0; i<string.length(); i++){
            String letter = string.substring(i, i + 1);
            
            letterVector = DenseRealVector.createDeterministicGaussianVector(dimensionality, letter);
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
    public Entry<Float, ItemVectorPair<String, DenseRealVector>> getClosest(DenseRealVector vector){
        return cache.getClosest(vector);
    }
    
    @Override
    public Collection<Entry<Float, ItemVectorPair<String, DenseRealVector>>> getNClosest(DenseRealVector vector, int n){
        return cache.getNClosest(vector, n);
    }
    
}
