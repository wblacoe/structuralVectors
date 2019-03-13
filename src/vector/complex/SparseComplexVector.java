package vector.complex;

import edu.princeton.cs.algs4.Complex;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import misc.Meta;
import permutation.Permutation;
import vector.Vector;

public class SparseComplexVector extends ComplexVector {
    
    private final int dimensionality;
    private final TreeMap<Integer, ComplexWeight> weights; //null = 0
    
    public SparseComplexVector(int dimensionality){
        this.dimensionality = dimensionality;
        weights = new TreeMap<>();
    }
    
    @Override
    public ComplexWeight getWeight(int dimension) {
        return weights.get(dimension);
    }
    
    @Override
    public void setWeight(int dimension, ComplexWeight w) {
        weights.put(dimension, w);
    }

    @Override
    public int getDimensionality() {
        return dimensionality;
    }
    
    public SparseComplexVector conjugate(){
        SparseComplexVector r = new SparseComplexVector(getDimensionality());
        
        for(Entry<Integer, ComplexWeight> entry : this){
            r.setWeight(entry.getKey(), entry.getValue().conjugate());
        }
        
        return r;
    }
    
    @Override
    public SparseComplexVector plus(Vector v) {
        if(!(v instanceof SparseComplexVector)) return null;
        
        SparseComplexVector r = new SparseComplexVector(dimensionality);

        int dimension;
        for(Entry<Integer, ComplexWeight> entry : this){
            dimension = entry.getKey();
            r.setWeight(dimension, entry.getValue().plus(v.getWeight(dimension)));
        }

        return r;
    }
    
    @Override
    public SparseComplexVector minus(Vector v) {
        if(!(v instanceof SparseComplexVector)) return null;
        
        SparseComplexVector r = new SparseComplexVector(dimensionality);

        int dimension;
        for(Entry<Integer, ComplexWeight> entry : this){
            dimension = entry.getKey();
            r.setWeight(dimension, entry.getValue().minus(v.getWeight(dimension)));
        }

        return r;
    }

    public SparseComplexVector pointwiseTimes(SparseComplexVector v) {
        SparseComplexVector r = new SparseComplexVector(dimensionality);

        int dimension;
        for(Entry<Integer, ComplexWeight> entry : this){
            dimension = entry.getKey();
            r.setWeight(dimension, entry.getValue().times(v.getWeight(dimension)));
        }

        return r;
    }
    
    @Override
    public SparseComplexVector normalise(){
        float norm = euclideanNorm();
        if(Math.abs(1 - norm) < Meta.ALMOST_ZERO){
            return this;
        }else{
            return times(1 / norm);
        }
    }
    
    @Override
    public SparseComplexVector times(float s){
        SparseComplexVector r = new SparseComplexVector(getDimensionality());
        
        for(Entry<Integer, ComplexWeight> entry : this){
            r.setWeight(entry.getKey(), entry.getValue().times(s));
        }
        
        return r;
    }
    
    @Override
    public SparseComplexVector times(Complex s){
        SparseComplexVector r = new SparseComplexVector(getDimensionality());
        
        for(Entry<Integer, ComplexWeight> entry : this){
            r.setWeight(entry.getKey(), entry.getValue().times(s));
        }
        
        return r;
    }

    @Override
    public SparseComplexVector times(ComplexWeight w){
        SparseComplexVector r = new SparseComplexVector(getDimensionality());
        
        for(Entry<Integer, ComplexWeight> entry : this){
            r.setWeight(entry.getKey(), entry.getValue().times(w));
        }
        
        return r;
    }
    
    @Override
    public SparseComplexVector convolute(Vector v) {
        return pointwiseTimes((SparseComplexVector) v);
    }
    
    public SparseComplexVector involute(){
        return conjugate();
    }
    
    @Override
    public SparseComplexVector correlate(Vector v) {
        return convolute(((SparseComplexVector) v).involute());
    }

    @Override
    public SparseComplexVector permute(Permutation p) {
        SparseComplexVector r = new SparseComplexVector(dimensionality);
        
        int fromDimension, toDimension;
        ComplexWeight w;
        for(Entry<Integer, ComplexWeight> entry : this){
            fromDimension = entry.getKey();
            toDimension = p.getMappingFrom(fromDimension);
            w = entry.getValue();
            r.setWeight(toDimension, w);
        }
        
        return r;
    }
    
    @Override
    public SparseComplexVector reversePermute(Permutation p) {
        SparseComplexVector r = new SparseComplexVector(dimensionality);
        
        int fromDimension, toDimension;
        ComplexWeight w;
        for(Entry<Integer, ComplexWeight> entry : this){
            toDimension = entry.getKey();
            fromDimension = p.getMappingTo(toDimension);
            w = entry.getValue();
            r.setWeight(fromDimension, w);
        }
        
        return r;
    }
    
    @Override
    public Iterator<Entry<Integer, ComplexWeight>> iterator() {
        return weights.entrySet().iterator();
    }

}
