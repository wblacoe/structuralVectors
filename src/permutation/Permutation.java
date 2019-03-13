package permutation;

import java.util.Arrays;
import java.util.Random;

public class Permutation {

    //expected to be strictly one cycle
    private int[] mappingFromTo, mappingToFrom;
    private final String name;
    
    public Permutation(int dimensionality, String name){
        mappingFromTo = new int[dimensionality];
        Arrays.fill(mappingFromTo, -1);
        mappingToFrom = new int[dimensionality];
        Arrays.fill(mappingToFrom, -1);
        this.name = name;
    }
    
    public String name(){
        return name;
    }
    
    public int getDimensionality(){
        return mappingFromTo.length;
    }
    
    public void setMappingFromTo(int fromDimension, int toDimension){
        mappingFromTo[fromDimension] = toDimension;
        mappingToFrom[toDimension] = fromDimension;
    }
    
    public int getMappingFrom(int fromDimension){
        return mappingFromTo[fromDimension];
    }
    
    public int getMappingTo(int toDimension){
        return mappingToFrom[toDimension];
    }
    
    public boolean isMappingSetFrom(int fromDimension){
        return mappingFromTo[fromDimension] > -1;
    }
    
    public boolean isMappingSetTo(int toDimension){
        return mappingToFrom[toDimension] > -1;
    }
    
    public boolean isBijective(){
        int[] counts = new int[getDimensionality()];
        
        int toDimension;
        for(int d=0; d<getDimensionality(); d++){
            toDimension = getMappingFrom(d);
            if(counts[toDimension] > 0){
                return false;
            }else{
                counts[toDimension]++;
            }
        }
        
        return true;
    }
    
    /*public RealVector permute(RealVector v){
        if(getDimensionality() != v.getDimensionality()){
            return null;
        }
        
        RealVector r = new RealVector(v.getDimensionality());
        
        int toDimension;
        for(int d=0; d<v.getDimensionality(); d++){
            toDimension = getMappingFrom(d);
            r.setValue(toDimension, v.getValue(d));
        }
        
        return r;
    }
    
    public RealVector reversePermute(RealVector v){
        if(getDimensionality() != v.getDimensionality()){
            return null;
        }
        
        RealVector r = new RealVector(v.getDimensionality());
        
        int fromDimension;
        for(int d=0; d<v.getDimensionality(); d++){
            fromDimension = getMappingTo(d);
            r.setValue(fromDimension, v.getValue(d));
        }
        
        return r;
    }*/
    
    //returns a new permutation created from this one
    //by repeating it [depth] many times
    //[depth] can be negative, i.e. for reverse mapping
    public Permutation toThePowerOf(int power){
        Permutation p = new Permutation(getDimensionality(), name + "^" + power);
        
        if(power == 0){
            //do nothing, it is an empty permutation
            
        }else if(power == 1){
            //use this permutation unchanged
            p.mappingFromTo = mappingFromTo;
            p.mappingToFrom = mappingToFrom;
            
        }else if(power > 1){
            int toDimension;
            for(int i=0; i<getDimensionality(); i++){
                toDimension = i;
                for(int j=0; j<power; j++){
                    toDimension = getMappingFrom(toDimension);
                }
                p.setMappingFromTo(i, toDimension);
            }
            
        }else{ //if power < 0
            int fromDimension;
            for(int i=0; i<p.getDimensionality(); i++){
                fromDimension = i;
                for(int j=0; j>power; j--){
                    fromDimension = getMappingTo(fromDimension);
                }
                p.setMappingFromTo(i, fromDimension);
            }
        }
        
        return p;
    }
    
    public Permutation reverse(){
        return toThePowerOf(-1);
    }
    
    //returns a permutation composed of this and given permutation
    //which if applied to a vector v
    //would output this(given(v))
    public Permutation composeWith(Permutation p){
        if(getDimensionality() != p.getDimensionality()) return null;
        
        Permutation cp = new Permutation(getDimensionality(), name + " o " + p.name());
        for(int d=0; d<getDimensionality(); d++){
            cp.setMappingFromTo(d, getMappingFrom(p.getMappingFrom(d)));
        }
        
        return cp;
    }
    
    public static Permutation createIdentityPermutation(int dimensionality){
        Permutation p = new Permutation(dimensionality, "id");
        
        for(int d=0; d<dimensionality; d++){
            p.setMappingFromTo(d, d);
        }
        
        return p;
    }
    
    public static Permutation createShiftPermutation(int dimensionality, int shiftRightBy){
        if(shiftRightBy == 0 || Math.abs(shiftRightBy) >= dimensionality){
            return null;
        }
        
        Permutation p = new Permutation(dimensionality, "shiftRightBy" + shiftRightBy);
        
        for(int d=0; d<dimensionality; d++){
            p.setMappingFromTo(d, (d + shiftRightBy) % dimensionality);
        }
        
        return p;
    }
    
    public static Permutation createRandomPermutation(int dimensionality, String name){
        Permutation p = new Permutation(dimensionality, name);
        
        Random rand = new Random();
        int fromDimension = 0, toDimension;
        for(int i=0; i<dimensionality-1; i++){
            toDimension = rand.nextInt(dimensionality);
            if(fromDimension == toDimension || p.isMappingSetFrom(toDimension)){
                i--;
            }else{
                p.setMappingFromTo(fromDimension, toDimension);
                fromDimension = toDimension;
            }
        }
        if(fromDimension != 0){
            p.setMappingFromTo(fromDimension, 0);
        }
        
        return p;
    }

    @Override
    public String toString(){
        String s = "{";
        for(int d=0; d<getDimensionality(); d++){
            s += "" + d + "->" + getMappingFrom(d) + ", ";
        }
        
        return s += "}";
    }
    
    public static void main(String[] args){
        int dimensionality = 10;
        
        Permutation p = Permutation.createRandomPermutation(dimensionality, "random");
        Permutation p1 = p.toThePowerOf(1);
        Permutation p2 = p.toThePowerOf(2);
        Permutation p3 = p.toThePowerOf(3);
        Permutation p4 = p2.toThePowerOf(2);
        Permutation p5 = p.toThePowerOf(-1);
        Permutation p6 = p.toThePowerOf(-2);
        
        System.out.println("P: " + p.toString() + ", bijective: " + p.isBijective());
        System.out.println("P^1: " + p1.toString() + ", bijective: " + p1.isBijective());
        System.out.println("P^2: " + p2.toString() + ", bijective: " + p2.isBijective());
        System.out.println("P^3: " + p3.toString() + ", bijective: " + p3.isBijective());
        System.out.println("(P^2)^2: " + p4.toString() + ", bijective: " + p4.isBijective());
        System.out.println("P^-1: " + p5.toString() + ", bijective: " + p5.isBijective());
        System.out.println("P^-2: " + p6.toString() + ", bijective: " + p6.isBijective());
        
        Permutation q = Permutation.createRandomPermutation(dimensionality, "random");
        Permutation pq = p.composeWith(q);
        
        System.out.println("P: " + p.toString() + ", bijective: " + p.isBijective());
        System.out.println("Q: " + q.toString() + ", bijective: " + q.isBijective());
        System.out.println("P o Q: " + pq.toString() + ", bijective: " + pq.isBijective());
        
    }
    
}
