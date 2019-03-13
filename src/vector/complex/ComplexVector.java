package vector.complex;

import edu.princeton.cs.algs4.Complex;
import java.util.Iterator;
import java.util.Map.Entry;
import permutation.Permutation;
import vector.Vector;

public abstract class ComplexVector extends Vector<ComplexWeight> implements Iterable<Entry<Integer, ComplexWeight>>{
    
    public float euclideanNorm(){
        ComplexWeight r = ComplexWeight.ZERO;
        
        ComplexWeight w;
        for(Entry<Integer, ComplexWeight> entry : this){
            w = entry.getValue();
            r = r.plus(w.conjugate().times(w));
        }
        
        return (float) Math.sqrt(r.real());
    }
    
    public abstract ComplexVector normalise();
    @Override
    public abstract ComplexVector plus(Vector v);
    @Override
    public abstract ComplexVector minus(Vector v);
    public abstract ComplexVector times(float s);
    public abstract ComplexVector times(Complex s);
    public abstract ComplexVector times(ComplexWeight w);
    
    public ComplexWeight innerProduct(ComplexVector v){
        ComplexWeight ip = ComplexWeight.ZERO;
        
        for(Entry<Integer, ComplexWeight> entry : this){
            ComplexWeight value1 = entry.getValue();
            ComplexWeight value2 = v.getWeight(entry.getKey());
            if(value2 != null) ip = ip.plus(value1.conjugate().times(value2));
        }
        
        return ip;
    }
    
    @Override
    public float similarity(Vector v, boolean isNormalisationNecessary){
        ComplexVector cv = (ComplexVector) v;
        if(!isNormalisationNecessary) return innerProduct(cv).real();
            
        ComplexWeight sim = ComplexWeight.ZERO, norm1 = ComplexWeight.ZERO, norm2 = ComplexWeight.ZERO;
        for(Entry<Integer, ComplexWeight> entry : this){
            ComplexWeight value1 = entry.getValue();
            ComplexWeight value2 = cv.getWeight(entry.getKey());
            norm1 = norm1.plus(value1.conjugate().times(value1));
            if(value2 != null){
                sim = sim.plus(value1.conjugate().times(value2));
                norm2 = norm2.plus(value2.conjugate().times(value2));
            }
        }
        sim = sim.divideBy(norm1.sqrt()).divideBy(norm2.sqrt());
        
        return sim.real();
    }
    
    @Override
    public ComplexVector orthogonalise(Vector v, boolean isNormalisationNecessary){
        ComplexVector cv = (ComplexVector) v;
        if(isNormalisationNecessary) cv = cv.normalise();
        ComplexWeight ip = innerProduct(cv);
        if(ip.isAlmostZero()){
            return this;
        }else{
            ComplexVector r = plus(cv.times(ip.negate()));
            
            //normalise vector
            ComplexWeight rNorm = ComplexWeight.ONE.plus(ip.times(ip).negate()).sqrt();
            r = r.times(rNorm.reciprocal());
            
            return r;
        }
    }
    
    @Override
    public abstract ComplexVector permute(Permutation p);
    
    @Override
    public String toString(){
        Iterator<Entry<Integer, ComplexWeight>> it = iterator();
        
        int a = 0;
        String s = "[";
        while(it.hasNext()){
            Entry<Integer, ComplexWeight> entry = it.next();
            s += entry.getKey() + ":" + entry.getValue();
            if(it.hasNext()) s += ", ";
            if(++a >= 20){
                s += "...";
                break;
            }
        }
        
        return s + "]";
    }
    
    
    @Override
    public abstract ComplexVector convolute(Vector v);
    
    @Override
    public abstract ComplexVector correlate(Vector v);
    
}
