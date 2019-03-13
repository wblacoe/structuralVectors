package vector.real;

import edu.princeton.cs.algs4.Complex;
import edu.princeton.cs.algs4.FFT;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import orthography.Demarcators;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import orthography.Bobcat;
import misc.Meta;
import permutation.Permutation;
import vector.Vector;
import vector.complex.DenseComplexVector;

public class DenseRealVector extends RealVector {

    private final RealWeight[] weights;
    
    public DenseRealVector(int dimensionality){
        weights = new RealWeight[dimensionality];
    }
    
    @Override
    public RealWeight getWeight(int dimension) {
        return weights[dimension];
    }
    
    @Override
    public void setWeight(int dimension, RealWeight w) {
        weights[dimension] = w;
    }

    @Override
    public int getDimensionality() {
        return weights.length;
    }
    
    @Override
    public DenseRealVector plus(Vector v) {
        if(!(v instanceof DenseRealVector)) return null;
        int dimensionality = getDimensionality();
        if(dimensionality != v.getDimensionality()) return null;
        
        DenseRealVector r = new DenseRealVector(dimensionality);
        for(int d=0; d<dimensionality; d++){
            r.setWeight(d, getWeight(d).plus(v.getWeight(d)));
        }
        
        return r;
    }
    
    @Override
    public DenseRealVector minus(Vector v) {
        if(!(v instanceof DenseRealVector)) return null;
        int dimensionality = getDimensionality();
        if(dimensionality != v.getDimensionality()) return null;
        
        DenseRealVector r = new DenseRealVector(dimensionality);
        for(int d=0; d<dimensionality; d++){
            r.setWeight(d, getWeight(d).minus(v.getWeight(d)));
        }
        
        return r;
    }
    
    @Override
    public DenseRealVector times(RealWeight w){
        return times(w.value());
    }
    
    @Override
    public DenseRealVector times(float s){
        DenseRealVector r = new DenseRealVector(getDimensionality());
        
        for(int d=0; d<getDimensionality(); d++){
            r.setWeight(d, new RealWeight(getWeight(d).value() * s));
        }
        
        return r;
    }
    
    public DenseRealVector normalise(int pNorm){
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
    public DenseRealVector normalise(){
        return normalise(2);
    }

    public DenseRealVector pointwiseTimes(DenseRealVector v) {
        int dimensionality = getDimensionality();
        if(dimensionality == v.getDimensionality()){
            DenseRealVector r = new DenseRealVector(dimensionality);
            for(int d=0; d<dimensionality; d++){
                r.setWeight(d, getWeight(d).times(v.getWeight(d)));
            }
            return r;
            
        }else{
            System.err.println("Mismatching dimensionalities!");
            return null;
        }
    }
    
    public DenseComplexVector fft(){
        Complex[] vArray = new Complex[getDimensionality()];
        for(int d=0; d<getDimensionality(); d++){
            vArray[d] = new Complex(getWeight(d).value(), 0);
        }
        
        return new DenseComplexVector(FFT.fft(vArray));
    }

    @Override
    public DenseRealVector convolute(Vector v) {
        return this.fft().pointwiseTimes(((DenseRealVector) v).fft()).ifft();
    }
    
    public DenseRealVector involute(){
        int dd = getDimensionality();
        
        DenseRealVector r = new DenseRealVector(dd);
        for(int d=0; d<dd; d++){
            r.setWeight((dd - d) % dd, getWeight(d));
        }
        
        return r;
    }
    
    @Override
    public DenseRealVector correlate(Vector v){
        return convolute(((DenseRealVector) v).involute());
    }

    @Override
    public DenseRealVector permute(Permutation p) {
        DenseRealVector r = new DenseRealVector(getDimensionality());
        
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
    public DenseRealVector reversePermute(Permutation p) {
        DenseRealVector r = new DenseRealVector(getDimensionality());
        
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
    
    public double[] toDoubleArray(){
        return Arrays.stream(weights).mapToDouble(w -> w.value()).toArray();
    }
    
    public double similarity(double[] vector){
        double sim = 0, norm1 = 0, norm2 = 0;
        
        for(int d=0; d<getDimensionality(); d++){
            float value1 = getWeight(d).value();
            double value2 = vector[d];
            sim += value1 * value2;
            norm1 += value1 * value1;
            norm2 += value2 * value2;
        }
        sim = sim / Math.sqrt(norm1) / Math.sqrt(norm2);
        
        return sim;
    }
    
    @Override
    public Iterator<Entry<Integer, RealWeight>> iterator() {
        return new Iterator<Entry<Integer, RealWeight>>() {
            
            int d = 0;
            
            @Override
            public boolean hasNext() {
                return d < getDimensionality();
            }

            @Override
            public Entry<Integer, RealWeight> next() {
                if(hasNext()){
                    Entry<Integer, RealWeight> r = new SimpleEntry<>(d, getWeight(d));
                    d++;
                    return r;
                }else{
                    return null;
                }
            }
        };
    }
    
    public static DenseRealVector createZeroVector(int dimensionality){
        DenseRealVector v = new DenseRealVector(dimensionality);
        for(int d=0; d<dimensionality; d++){
            v.setWeight(d, RealWeight.ZERO);
        }
        
        return v;
    }
    
    public static DenseRealVector createRandomDiscreteVector(int dimensionality){
        DenseRealVector r = new DenseRealVector(dimensionality);
        
        float oneOverDimensionalitySqrt = 1 / (float) Math.sqrt(dimensionality);
        RealWeight w1 = new RealWeight(oneOverDimensionalitySqrt);
        RealWeight w2 = new RealWeight(-oneOverDimensionalitySqrt);
        for(int d=0; d<dimensionality; d++){
            r.setWeight(d, Meta.RAND.nextBoolean() ? w1 : w2);
        }
        
        return r;
    }
    
    public static DenseRealVector createRandomGaussianVector(int dimensionality){
        DenseRealVector r = new DenseRealVector(dimensionality);
        
        float oneOverDimensionalitySqrt = 1 / (float) Math.sqrt(dimensionality);
        RealWeight w = new RealWeight(oneOverDimensionalitySqrt);
        for(int d=0; d<dimensionality; d++){
            r.setWeight(d, RealWeight.createRandomGaussian().times(w));
        }
        
        return r;
    }
    
    public static DenseRealVector createDeterministicGaussianVector(int dimensionality, String s){
        DenseRealVector r = new DenseRealVector(dimensionality);
        
        Random rand = new Random(Bobcat.asLong(s));
        
        double dimensionalitySqrt = Math.sqrt(dimensionality);
        RealWeight w;
        for(int d=0; d<dimensionality; d++){
            w = new RealWeight((float) (rand.nextGaussian() / dimensionalitySqrt));
            r.setWeight(d, w);
        }
        
        return r;
    }
    
    public static Demarcators<DenseRealVector> createFiniteDemarcators(int dimensionality, int amount){
        return new Demarcators<DenseRealVector>() {
            
            private final DenseRealVector[] vectors = new DenseRealVector[amount];
            private boolean areVectorsPrepared = false;
            
            private void prepare(){
                DenseRealVector rvAlpha = createRandomGaussianVector(dimensionality).normalise();
                DenseRealVector rvOmega = createRandomGaussianVector(dimensionality).normalise();
                rvOmega = (DenseRealVector) rvOmega.orthogonalise(rvAlpha, false);
                
                vectors[0] = rvAlpha;
                vectors[amount - 1] = rvOmega;
                double phi;
                for(int i=1; i<amount-1; i++){
                    phi = Math.PI * i / (2 * (amount-1));
                    vectors[i] = rvAlpha.times((float) Math.cos(phi)).plus(rvOmega.times((float) Math.sin(phi)));
                }
            }
            
            @Override
            public DenseRealVector get(int i) {
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
    
    public static Demarcators<DenseRealVector> createInfiniteDemarcators(int dimensionality, float fractionBase){
        return new Demarcators<DenseRealVector>() {
            
            private DenseRealVector rvAlpha, rvOmega;
            private final TreeMap<Integer, DenseRealVector> vectors = new TreeMap<>();
            
            private void prepare(){
                rvAlpha = createRandomGaussianVector(dimensionality).normalise();
                rvOmega = createRandomGaussianVector(dimensionality).normalise();
                rvOmega = (DenseRealVector) rvOmega.orthogonalise(rvAlpha, false);
            }
            
            private DenseRealVector createDemarcator(int i){
                if(rvAlpha == null || rvOmega == null) prepare();

                float fraction = (float) Math.pow(fractionBase, i);
                double phi = Math.PI * fraction / 2;
                //System.out.println("i = " + i + ", fraction = " + fraction + ", phi = " + (float) phi + ", cos = " + (float) Math.cos(phi) + ", sin = " + (float) Math.sin(phi)); //DEBUG
                
                return rvAlpha.times((float) Math.sin(phi)).plus(rvOmega.times((float) Math.cos(phi)));
            }

            @Override
            public DenseRealVector get(int i) {
                DenseRealVector vector = vectors.get(i);
                if(vector == null){
                    vector = createDemarcator(i);
                    vectors.put(i, vector);
                }
                
                return vector;
            }
            
        };
    }

}