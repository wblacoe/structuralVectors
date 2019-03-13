package vector.binary;

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

public class DenseBinaryVector extends Vector<BinaryWeight> implements Iterable<Entry<Integer, BinaryWeight>> {
    
    private final BinaryWeight[] weights;
    
    public DenseBinaryVector(int dimensionality){
        weights = new BinaryWeight[dimensionality];
    }
    
    public DenseBinaryVector(BinaryWeight[] weights){
        this.weights = weights;
    }
    
    @Override
    public BinaryWeight getWeight(int dimension) {
        return weights[dimension];
    }
    
    @Override
    public void setWeight(int dimension, BinaryWeight w) {
        weights[dimension] = w;
    }

    @Override
    public int getDimensionality() {
        return weights.length;
    }
    
    @Override
    public DenseBinaryVector plus(Vector v) {
        if(!(v instanceof DenseBinaryVector)) return null;
        if(getDimensionality() != v.getDimensionality()) return null;
        
        DenseBinaryVector r = new DenseBinaryVector(getDimensionality());

        for(int d=0; d<getDimensionality(); d++){
            r.setWeight(d, getWeight(d).plus(v.getWeight(d)));
        }

        return r;
    }
    
    @Override
    public DenseBinaryVector minus(Vector v) {
        return null;
    }
    
    public DenseBinaryVector xor(DenseBinaryVector v){
        DenseBinaryVector r = new DenseBinaryVector(getDimensionality());
        
        for(int d=0; d<getDimensionality(); d++){
            r.setWeight(d, getWeight(d).xor(v.getWeight(d)));
        }
        
        return r;
    }
    
    @Override
    public DenseBinaryVector convolute(Vector v){
        return xor((DenseBinaryVector) v);
    }
    
    @Override
    public DenseBinaryVector correlate(Vector v){
        return xor((DenseBinaryVector) v);
    }

    @Override
    public DenseBinaryVector permute(Permutation p) {
        DenseBinaryVector r = new DenseBinaryVector(getDimensionality());
        
        int fromDimension, toDimension;
        BinaryWeight w;
        for(Entry<Integer, BinaryWeight> entry : this){
            fromDimension = entry.getKey();
            toDimension = p.getMappingFrom(fromDimension);
            w = entry.getValue();
            r.setWeight(toDimension, w);
        }
        
        return r;
    }
    
    @Override
    public DenseBinaryVector reversePermute(Permutation p) {
        DenseBinaryVector r = new DenseBinaryVector(getDimensionality());
        
        int fromDimension, toDimension;
        BinaryWeight w;
        for(Entry<Integer, BinaryWeight> entry : this){
            toDimension = entry.getKey();
            fromDimension = p.getMappingTo(toDimension);
            w = entry.getValue();
            r.setWeight(fromDimension, w);
        }
        
        return r;
    }
    
    public int hammingDistance(DenseBinaryVector v){
        int r = 0;
        
        for(int d=0; d<getDimensionality(); d++){
            if(!getWeight(d).equals(v.getWeight(d))){
                r++;
            }
        }
        
        return r;
    }
    
    public float normalisedHammingDistance(DenseBinaryVector v){
        return 1f * hammingDistance(v) / getDimensionality();
    }
    
    @Override
    public float similarity(Vector v, boolean dumymy) {
        return 2 * (0.5f - normalisedHammingDistance((DenseBinaryVector) v));
    }
    
    public DenseBinaryVector copy(){
        return new DenseBinaryVector(Arrays.copyOf(weights, getDimensionality()));
    }
    
    @Override
    public DenseBinaryVector orthogonalise(Vector v, boolean dummy){
        DenseBinaryVector bv = (DenseBinaryVector) v;
        DenseBinaryVector r = copy();
        
        int hd = hammingDistance(bv);
        int dimensionality = getDimensionality();
        int halfDimensionality = dimensionality / 2;
        
        int d;
        while(true){
            if(hd < halfDimensionality){
                d = Meta.RAND.nextInt(dimensionality);
                if(getWeight(d).equals(bv.getWeight(d))){
                    r.setWeight(d, getWeight(d).not());
                    hd++;
                }

            }else if(hd > halfDimensionality){
                d = Meta.RAND.nextInt(dimensionality);
                if(!getWeight(d).equals(bv.getWeight(d))){
                    r.setWeight(d, getWeight(d).not());
                    hd--;
                }

            }else{
                break;
            }
        }
        
        return r;
    }

    @Override
    public Iterator<Entry<Integer, BinaryWeight>> iterator() {
        return new Iterator<Entry<Integer, BinaryWeight>>() {
            
            int d = 0;
            
            @Override
            public boolean hasNext() {
                return d < getDimensionality();
            }

            @Override
            public Entry<Integer, BinaryWeight> next() {
                if(hasNext()){
                    Entry<Integer, BinaryWeight> r = new SimpleEntry<>(d, getWeight(d));
                    d++;
                    return r;
                }else{
                    return null;
                }
            }
        };
    }
    
    public static DenseBinaryVector superpose(DenseBinaryVector... vs){
        if(vs == null || vs.length == 0) return null;
        
        int amountOfVectors = vs.length;
        int dimensionality = vs[0].getDimensionality();
        DenseBinaryVector r = new DenseBinaryVector(dimensionality);
        
        int[] amountOfOnes = new int[dimensionality];
        for(DenseBinaryVector v : vs){
            for(int d=0; d<dimensionality; d++){
                if(v.getWeight(d).value()) amountOfOnes[d]++;
            }
        }
        
        for(int d=0; d<dimensionality; d++){
            int a = amountOfOnes[d];
            if(a < amountOfVectors - a){
                r.setWeight(d, new BinaryWeight(false));
            }else if(a > amountOfVectors - a){
                r.setWeight(d, new BinaryWeight(true));
            }else{
                r.setWeight(d, new BinaryWeight(Meta.RAND.nextBoolean()));
            }
        }
        
        return r;
    }
    
    @Override
    public String toString(){
        Iterator<Entry<Integer, BinaryWeight>> it = iterator();
        
        int a = 0;
        String s = "[";
        while(it.hasNext()){
            Entry<Integer, BinaryWeight> entry = it.next();
            s += entry.getKey() + ":" + entry.getValue().toInt();
            if(it.hasNext()) s += ", ";
            if(++a >= 20){
                s += "...";
                break;
            }
        }
        
        return s + "]";
    }
    
    public static DenseBinaryVector createZeroVector(int dimensionality){
        return new DenseBinaryVector(dimensionality);
    }
    
    public static DenseBinaryVector createRandomVector(int dimensionality){
        DenseBinaryVector r = new DenseBinaryVector(dimensionality);
        
        for(int d=0; d<dimensionality; d++){
            r.setWeight(d, new BinaryWeight(Meta.RAND.nextBoolean()));
        }
        
        return r;
    }
    
    public static DenseBinaryVector createDeterministicVector(int dimensionality, String s){
        DenseBinaryVector r = new DenseBinaryVector(dimensionality);
        
        Random rand = new Random(Bobcat.asLong(s));
        
        for(int d=0; d<dimensionality; d++){
            r.setWeight(d, new BinaryWeight(rand.nextBoolean()));
        }
        
        return r;
    }
    
    public static Demarcators<DenseBinaryVector> createFiniteDemarcators(int dimensionality, int amount){
        return new Demarcators<DenseBinaryVector>() {
            
            private final long RNG_SEED = 123L;
            private final DenseBinaryVector[] vectors = new DenseBinaryVector[amount];
            private boolean areVectorsPrepared = false;
            
            private void prepare(){
                DenseBinaryVector bvAlpha = createRandomVector(dimensionality);
                DenseBinaryVector bvOmega = createRandomVector(dimensionality);
                bvOmega = bvOmega.orthogonalise(bvAlpha, false);

                Random rand = new Random();

                vectors[0] = bvAlpha;
                vectors[amount - 1] = bvOmega;
                int dimensionality = bvAlpha.getDimensionality();
                DenseBinaryVector bv;
                float fraction, threshold, randomFloat;
                for(int i=1; i<amount-1; i++){
                    rand.setSeed(RNG_SEED);

                    bv = new DenseBinaryVector(dimensionality);
                    for(int d=0; d<dimensionality; d++){

                        BinaryWeight wAlpha = bvAlpha.getWeight(d);
                        BinaryWeight wOmega = bvOmega.getWeight(d);

                        if(wAlpha.equals(wOmega)){
                            bv.setWeight(d, wAlpha);

                        }else{
                            fraction = 1f * i / (amount-1);
                            threshold = (wAlpha.value() ? 1-fraction : fraction);
                            randomFloat = rand.nextFloat();
                            bv.setWeight(d, new BinaryWeight(randomFloat <= threshold));
                        }

                    }
                    vectors[i] = bv;
                }
            }
            
            @Override
            public DenseBinaryVector get(int i) {
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
    
    public static Demarcators<DenseBinaryVector> createInfiniteDemarcators(int dimensionality, float fractionBase){
        return new Demarcators<DenseBinaryVector>() {
            
            private final long RNG_SEED = 123L;
            private final Random rand = new Random();
            
            private DenseBinaryVector bvAlpha, bvOmega;
            private final TreeMap<Integer, DenseBinaryVector> vectors = new TreeMap<>();
            
            private void prepare(){
                bvAlpha = createRandomVector(dimensionality);
                bvOmega = createRandomVector(dimensionality);
                bvOmega = bvOmega.orthogonalise(bvAlpha, false);
            }
            
            private DenseBinaryVector createDemarcator(int i){
                if(bvAlpha == null || bvOmega == null) prepare();
                rand.setSeed(RNG_SEED);

                DenseBinaryVector bv = new DenseBinaryVector(dimensionality);
                float fraction, threshold, randomFloat;
                for(int d=0; d<dimensionality; d++){

                    BinaryWeight wAlpha = bvAlpha.getWeight(d);
                    BinaryWeight wOmega = bvOmega.getWeight(d);

                    if(wAlpha.equals(wOmega)){
                        bv.setWeight(d, wAlpha);

                    }else{
                        fraction = (float) Math.pow(fractionBase, i);
                        threshold = (wAlpha.value() ? 1-fraction : fraction);
                        randomFloat = rand.nextFloat();
                        bv.setWeight(d, new BinaryWeight(randomFloat <= threshold));
                    }
                }
                
                return bv;
            }

            @Override
            public DenseBinaryVector get(int i) {
                DenseBinaryVector vector = vectors.get(i);
                if(vector == null){
                    vector = createDemarcator(i);
                    vectors.put(i, vector);
                }
                
                return vector;
            }
            
        };
    }
    
}