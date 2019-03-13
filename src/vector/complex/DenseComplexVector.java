package vector.complex;

import edu.princeton.cs.algs4.Complex;
import edu.princeton.cs.algs4.FFT;
import orthography.Demarcators;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import orthography.Bobcat;
import misc.Meta;
import permutation.Permutation;
import vector.Vector;
import vector.real.DenseRealVector;
import vector.real.RealWeight;

public class DenseComplexVector extends ComplexVector {
    
    private final ComplexWeight[] weights;
    
    public DenseComplexVector(int dimensionality){
        weights = new ComplexWeight[dimensionality];
    }
    
    public DenseComplexVector(Complex[] weights){
        this.weights = Arrays.stream(weights).map(c -> new ComplexWeight(c)).toArray(ComplexWeight[]::new);
    }

    @Override
    public ComplexWeight getWeight(int dimension) {
        return weights[dimension];
    }

    @Override
    public void setWeight(int dimension, ComplexWeight w) {
        weights[dimension] = w;
    }

    @Override
    public int getDimensionality() {
        return weights.length;
    }
    
    public DenseComplexVector conjugate(){
        DenseComplexVector r = new DenseComplexVector(getDimensionality());
        
        for(int d=0; d<getDimensionality(); d++){
            r.setWeight(d, getWeight(d).conjugate());
        }
        
        return r;
    }

    @Override
    public DenseComplexVector plus(Vector v) {
        if(!(v instanceof DenseComplexVector)) return null;
        int dimensionality = getDimensionality();
        if(dimensionality != v.getDimensionality()) return null;

        DenseComplexVector r = new DenseComplexVector(dimensionality);
        for(int d=0; d<dimensionality; d++){
            r.setWeight(d, getWeight(d).plus(v.getWeight(d)));
        }
        
        return r;
    }
    
    @Override
    public DenseComplexVector minus(Vector v) {
        if(!(v instanceof DenseComplexVector)) return null;
        int dimensionality = getDimensionality();
        if(dimensionality != v.getDimensionality()) return null;

        DenseComplexVector r = new DenseComplexVector(dimensionality);
        for(int d=0; d<dimensionality; d++){
            r.setWeight(d, getWeight(d).minus(v.getWeight(d)));
        }
        
        return r;
    }
    
    @Override
    public DenseComplexVector times(float s){
        DenseComplexVector r = new DenseComplexVector(getDimensionality());
        
        for(int d=0; d<getDimensionality(); d++){
            r.setWeight(d, getWeight(d).times(s));
        }
        
        return r;
    }
    
    @Override
    public DenseComplexVector times(Complex c){
        DenseComplexVector r = new DenseComplexVector(getDimensionality());
        
        for(int d=0; d<getDimensionality(); d++){
            r.setWeight(d, getWeight(d).times(c));
        }
        
        return r;
    }

    @Override
    public DenseComplexVector times(ComplexWeight w){
        DenseComplexVector r = new DenseComplexVector(getDimensionality());
        
        for(int d=0; d<getDimensionality(); d++){
            r.setWeight(d, getWeight(d).times(w));
        }
        
        return r;
    }
    
    @Override
    public DenseComplexVector normalise(){
        float norm = euclideanNorm();
        if(Math.abs(1 - norm) < Meta.ALMOST_ZERO){
            return this;
        }else{
            return times(1 / norm);
        }
    }

    public DenseComplexVector pointwiseTimes(DenseComplexVector v) {
        int dimensionality = getDimensionality();

        DenseComplexVector r = new DenseComplexVector(dimensionality);
        for(int d=0; d<dimensionality; d++){
            r.setWeight(d, getWeight(d).times(v.getWeight(d)));
        }

        return r;
    }
    
    @Override
    public DenseComplexVector convolute(Vector v) {
        return pointwiseTimes((DenseComplexVector) v);
    }
    
    public DenseComplexVector involute(){
        return conjugate();
    }
    
    @Override
    public DenseComplexVector correlate(Vector v) {
        return convolute(((DenseComplexVector) v).involute());
    }
    
    public DenseRealVector ifft(){
        Complex[] cs = FFT.ifft(Arrays.stream(weights).map(w -> w.value()).toArray(Complex[]::new));
        DenseRealVector r = new DenseRealVector(cs.length);
        Complex c;
        for(int d=0; d<cs.length; d++){
            c = cs[d];
            if(Math.abs(c.im()) < Meta.ALMOST_ZERO){
                r.setWeight(d, new RealWeight((float) c.re()));
            }else{
                return null;
            }
        }
        
        return r;
    }

    @Override
    public DenseComplexVector permute(Permutation p) {
        DenseComplexVector r = new DenseComplexVector(getDimensionality());
        
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
    public DenseComplexVector reversePermute(Permutation p) {
        DenseComplexVector r = new DenseComplexVector(getDimensionality());
        
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
        return new Iterator<Entry<Integer, ComplexWeight>>() {
            
            int d = 0;
            
            @Override
            public boolean hasNext() {
                return d < getDimensionality();
            }

            @Override
            public Entry<Integer, ComplexWeight> next() {
                if(hasNext()){
                    Entry<Integer, ComplexWeight> r = new SimpleEntry<>(d, getWeight(d));
                    d++;
                    return r;
                }else{
                    return null;
                }
            }
        };
    }
    
    public static DenseComplexVector createRandomGaussianVector(int dimensionality){
        DenseComplexVector r = new DenseComplexVector(dimensionality);
        
        Complex c;
        for(int d=0; d<dimensionality; d++){
            c = new Complex(Meta.RAND.nextGaussian(), Meta.RAND.nextGaussian());
            r.setWeight(d, new ComplexWeight(c));
        }
        
        return r;
    }
    
    public static DenseComplexVector createDeterministicVector(int dimensionality, String s){
        DenseComplexVector r = new DenseComplexVector(dimensionality);
        
        Random rand = new Random(Bobcat.asLong(s));
        
        Complex c;
        for(int d=0; d<dimensionality; d++){
            c = new Complex(rand.nextGaussian(), rand.nextGaussian());
            r.setWeight(d, new ComplexWeight(c));
        }
        
        return r;
    }
    
    public static Demarcators<DenseComplexVector> createFiniteDemarcators(int dimensionality, int amount){
        return new Demarcators<DenseComplexVector>() {
            
            private final DenseComplexVector[] vectors = new DenseComplexVector[amount];
            private boolean areVectorsPrepared = false;
            
            private void prepare(){
                DenseComplexVector cvAlpha = createRandomGaussianVector(dimensionality).normalise();
                DenseComplexVector cvOmega = createRandomGaussianVector(dimensionality).normalise();
                cvOmega = (DenseComplexVector) cvOmega.orthogonalise(cvAlpha, false);
                
                vectors[0] = cvAlpha;
                vectors[amount - 1] = cvOmega;
                double phi;
                for(int i=1; i<amount-1; i++){
                    phi = Math.PI * i / (2 * (amount-1));
                    vectors[i] = cvAlpha.times((float) Math.cos(phi)).plus(cvOmega.times((float) Math.sin(phi)));
                }
            }
            
            @Override
            public DenseComplexVector get(int i) {
                if(!areVectorsPrepared){
                    prepare();
                    areVectorsPrepared = true;
                }
                
                if(0 <= i && i < amount){
                    return vectors[i];
                }else{
                    return null;
                }
            }
            
        };
    }
    
    public static DenseComplexVector createZeroVector(int dimensionality){
        DenseComplexVector v = new DenseComplexVector(dimensionality);
        for(int d=0; d<dimensionality; d++){
            v.setWeight(d, ComplexWeight.ZERO);
        }
        
        return v;
    }
    
    public static Demarcators<DenseComplexVector> createInfiniteDemarcators(int dimensionality, float fractionBase){
        return new Demarcators<DenseComplexVector>() {
            
            private DenseComplexVector cvAlpha = null, cvOmega = null;
            private final TreeMap<Integer, DenseComplexVector> vectors = new TreeMap<>();
            
            private void prepare(){
                cvAlpha = createRandomGaussianVector(dimensionality).normalise();
                cvOmega = createRandomGaussianVector(dimensionality).normalise();
                cvOmega = (DenseComplexVector) cvOmega.orthogonalise(cvAlpha, false);
            }
            
            private DenseComplexVector createDemarcator(int i){
                if(cvAlpha == null || cvOmega == null) prepare();

                float fraction = (float) Math.pow(fractionBase, i);
                double phi = Math.PI * fraction / 2;
                
                return cvAlpha.times((float) Math.sin(phi)).plus(cvOmega.times((float) Math.cos(phi)));
            }

            @Override
            public DenseComplexVector get(int i) {
                DenseComplexVector vector = vectors.get(i);
                if(vector == null){
                    vector = createDemarcator(i);
                    vectors.put(i, vector);
                }
                
                return vector;
            }
            
        };
    }

}