package vector.real;

import java.util.Random;
import misc.Meta;
import vector.Weight;

public class RealWeight implements Weight{
    
    private static final Random RAND = new Random();
    public static final RealWeight ONE = new RealWeight(1);
    public static final RealWeight ZERO = new RealWeight(0);
    public static final RealWeight MINUS_ONE = new RealWeight(-1);
    
    private final float value;
    
    public RealWeight(float value){
        this.value = value;
    }
    
    public float value(){
        return value;
    }
    
    public boolean isAlmostZero(){
        return Math.abs(value) <= Meta.ALMOST_ZERO;
    }
    
    public RealWeight negate(){
        return new RealWeight(-value);
    }

    @Override
    public RealWeight plus(Weight w) {
        if(w == null){
            return this;
        }else{
            return new RealWeight(value + ((RealWeight) w).value());
        }
    }
    
    @Override
    public RealWeight minus(Weight w) {
        if(w == null){
            return this;
        }else{
            return new RealWeight(value - ((RealWeight) w).value());
        }
    }
    
    public RealWeight reciprocal(){
        return new RealWeight(1 / value);
    }

    @Override
    public RealWeight times(Weight w) {
        if(w == null){
            return null;
        }else{
            return new RealWeight(value * ((RealWeight) w).value());
        }
    }
    
    public RealWeight divideBy(Weight w){
        if(w == null){
            return null;
        }else{
            return new RealWeight(value / ((RealWeight) w).value());
        }
    }
    
    public RealWeight sqrt(){
        return new RealWeight((float) Math.sqrt(value));
    }
    
    public RealWeight abs(){
        return new RealWeight(Math.abs(value));
    }
    
    public static RealWeight createRandomUniform(){
        return new RealWeight((float) Math.random());
        //return new RealWeight(RAND.nextInt(32) / 32f);
    }
    
    public static RealWeight createRandomGaussian(){
        return new RealWeight((float) RAND.nextGaussian());
    }
    
    @Override
    public String toString(){
        return Float.toString(value);
    }
    
}
