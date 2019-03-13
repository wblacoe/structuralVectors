package vector.binary;

import misc.Meta;
import vector.Weight;

public class BinaryWeight implements Weight{
    
    private final boolean value;
    
    public BinaryWeight(boolean value){
        this.value = value;
    }
    
    public boolean value(){
        return value;
    }
    
    public BinaryWeight xor(BinaryWeight w){
        return new BinaryWeight(value ^ w.value());
    }
    
    public BinaryWeight not(){
        return new BinaryWeight(!value);
    }

    @Override
    public BinaryWeight plus(Weight w) {
        if(value == ((BinaryWeight) w).value()){
            return this;
        }else{
            return new BinaryWeight(Meta.RAND.nextBoolean());
        }
    }
    
    @Override
    public BinaryWeight minus(Weight w){
        return null;
    }

    @Override
    public BinaryWeight times(Weight w) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String toString(){
        return (value ? "1" : "0");
    }
    
    @Override
    public boolean equals(Object o){
        return value == ((BinaryWeight) o).value();
    }
    
    public int toInt(){
        return (value ? 1 : 0);
    }

}
