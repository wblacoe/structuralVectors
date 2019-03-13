package vector.real;

import java.util.Iterator;
import java.util.Map.Entry;
import permutation.Permutation;
import vector.Vector;

public abstract class RealVector extends Vector<RealWeight> implements Iterable<Entry<Integer, RealWeight>>{
    
    public float manhattanNorm(){
        float r = 0;
        
        for(Entry<Integer, RealWeight> entry : this){
            r += Math.abs(entry.getValue().value());
        }
        
        return r;
    }
    
    public float euclideanNorm(){
        float r = 0;
        
        float w;
        for(Entry<Integer, RealWeight> entry : this){
            w = entry.getValue().value();
            r += w * w;
        }
        r = (float) Math.sqrt(r);
        
        return r;
    }
    
    public abstract RealVector normalise();
    
    @Override
    public abstract RealVector plus(Vector v);
    public abstract RealVector times(float s);
    public abstract RealVector times(RealWeight w);
    
    public RealWeight innerProduct(RealVector v){
        RealWeight ip = RealWeight.ZERO;
        
        for(Entry<Integer, RealWeight> entry : this){
            RealWeight value1 = entry.getValue();
            RealWeight value2 = v.getWeight(entry.getKey());
            if(value2 != null) ip = ip.plus(value1.times(value2));
        }
        
        return ip;
    }
    
    @Override
    public float similarity(Vector v, boolean isNormalisationNecessary){
        RealVector rv = (RealVector) v;
        if(!isNormalisationNecessary) return innerProduct(rv).value();
        
        RealWeight sim = RealWeight.ZERO, norm1 = RealWeight.ZERO, norm2 = RealWeight.ZERO;
        for(Entry<Integer, RealWeight> entry : this){
            RealWeight value1 = entry.getValue();
            RealWeight value2 = rv.getWeight(entry.getKey());
            sim = sim.plus(value1.times(value2));
            norm1 = norm1.plus(value1.times(value1));
            if(value2 != null) norm2 = norm2.plus(value2.times(value2));
        }
        sim = sim.divideBy(norm1.sqrt()).divideBy(norm2.sqrt());
        
        return sim.value();
    }
    
    @Override
    public RealVector orthogonalise(Vector v, boolean isNormalisationNecessary){
        RealVector rv = (RealVector) v;
        if(isNormalisationNecessary) rv = rv.normalise();
        RealWeight ip = innerProduct(rv);
        if(ip.isAlmostZero()){
            return this;
        }else{
            RealVector r = plus(rv.times(ip.negate()));
            
            //normalise vector
            RealWeight rNorm = RealWeight.ONE.plus(ip.times(ip).negate()).sqrt();
            r = r.times(rNorm.reciprocal());

            return r;
        }
    }
    
    @Override
    public abstract RealVector permute(Permutation p);
    
    @Override
    public String toString(){
        Iterator<Entry<Integer, RealWeight>> it = iterator();
        
        int a = 0;
        String s = "[";
        while(it.hasNext()){
            Entry<Integer, RealWeight> entry = it.next();
            s += entry.getKey() + ":" + entry.getValue();
            if(it.hasNext()) s += ", ";
            if(++a >= 20){
                s += "...";
                break;
            }
        }
        
        return s + "]";
    }
    
}