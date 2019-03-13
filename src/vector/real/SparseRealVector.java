package vector.real;

import java.util.Iterator;
import java.util.Map.Entry;
import vector.Vector;
import java.util.TreeMap;
import misc.Meta;
import permutation.Permutation;

public class SparseRealVector extends RealVector {

    private final int dimensionality;
    private final TreeMap<Integer, RealWeight> weights;
    
    public SparseRealVector(int dimensionality){
        this.dimensionality = dimensionality;
        weights = new TreeMap<>();
    }
    
    @Override
    public RealWeight getWeight(int dimension) {
        return weights.get(dimension);
    }
    
    @Override
    public void setWeight(int dimension, RealWeight w) {
        weights.put(dimension, w);
    }

    @Override
    public int getDimensionality() {
        return dimensionality;
    }
    
    @Override
    public SparseRealVector plus(Vector v) {
        if(!(v instanceof SparseRealVector)) return null;
        
        SparseRealVector r = new SparseRealVector(dimensionality);

        int dimension;
        for(Entry<Integer, RealWeight> entry : this){
            dimension = entry.getKey();
            r.setWeight(dimension, entry.getValue().plus(v.getWeight(dimension)));
        }

        return r;
    }
    
    @Override
    public SparseRealVector minus(Vector v) {
        if(!(v instanceof SparseRealVector)) return null;
        
        SparseRealVector r = new SparseRealVector(dimensionality);

        int dimension;
        for(Entry<Integer, RealWeight> entry : this){
            dimension = entry.getKey();
            r.setWeight(dimension, entry.getValue().minus(v.getWeight(dimension)));
        }

        return r;
    }
    
    @Override
    public SparseRealVector times(float s){
        SparseRealVector r = new SparseRealVector(getDimensionality());
        
        for(Entry<Integer, RealWeight> entry : this){
            r.setWeight(entry.getKey(), new RealWeight(entry.getValue().value() * s));
        }
        
        return r;
    }

    @Override
    public SparseRealVector times(RealWeight w){
        return times(w.value());
    }
    
    public SparseRealVector normalise(int pNorm){
        float norm;
        switch (pNorm) {
            
            case 1:
                norm = manhattanNorm();
                if(Math.abs(1 - norm) < Meta.ALMOST_ZERO){
                    return this;
                }else{
                    return times(1 / norm);
                }
                
            case 2:
                norm = euclideanNorm();
                if(Math.abs(1 - norm) < Meta.ALMOST_ZERO){
                    return this;
                }else{
                    return times(1 / norm);
                }
                
            default:
                return null;
        }
    }
    
    @Override
    public SparseRealVector normalise(){
        return normalise(2);
    }
    
    public SparseRealVector pointwiseTimes(Vector v) {
        SparseRealVector r = new SparseRealVector(dimensionality);

        int dimension;
        for(Entry<Integer, RealWeight> entry : this){
            dimension = entry.getKey();
            r.setWeight(dimension, entry.getValue().times(v.getWeight(dimension)));
        }

        return r;
    }

    @Override
    public SparseRealVector permute(Permutation p) {
        SparseRealVector r = new SparseRealVector(dimensionality);
        
        int fromDimension, toDimension;
        RealWeight w;
        for(Entry<Integer, RealWeight> entry : this){
            fromDimension = entry.getKey();
            toDimension = p.getMappingFrom(fromDimension);
            w = entry.getValue();
            r.setWeight(toDimension, w);
        }
        
        return r;
    }
    
    @Override
    public SparseRealVector reversePermute(Permutation p) {
        SparseRealVector r = new SparseRealVector(dimensionality);
        
        int fromDimension, toDimension;
        RealWeight w;
        for(Entry<Integer, RealWeight> entry : this){
            toDimension = entry.getKey();
            fromDimension = p.getMappingTo(toDimension);
            w = entry.getValue();
            r.setWeight(fromDimension, w);
        }
        
        return r;
    }
    
    public static SparseRealVector createZeroVector(int dimensionality){
        return new SparseRealVector(dimensionality);
    }
    
    public static SparseRealVector createRandomIndexingVector(int dimensionality, int cardinality){
        if(cardinality < 0 || cardinality % 2 != 0){
            System.err.println("cardinality must be positive and even!");
            return null;
        }
        
        SparseRealVector r = new SparseRealVector(dimensionality);
        int positiveAmount = cardinality / 2;
        int negativeAmount = cardinality / 2;
        
        int randomDimension;
        boolean positiveWeight;
        for(int i=0; i<cardinality; i++){
            randomDimension = (int) (Math.random() * dimensionality);
            
            if(positiveAmount == 0){
                positiveWeight = false;
            }else if(negativeAmount == 0){
                positiveWeight = true;
            }else{
                positiveWeight = Meta.RAND.nextBoolean();
            }
            
            if(positiveWeight){
                positiveAmount--;
            }else{
                negativeAmount--;
            }
            
            r.setWeight(randomDimension, positiveWeight ? RealWeight.ONE : RealWeight.MINUS_ONE);
        }
        
        return r;
    }
    
    @Override
    public Iterator<Entry<Integer, RealWeight>> iterator() {
        return weights.entrySet().iterator();
    }

    @Override
    public Vector convolute(Vector v) {
        return null;
    }

    @Override
    public Vector correlate(Vector v) {
        return null;
    }

}
