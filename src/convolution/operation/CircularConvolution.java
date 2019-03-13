/*package convolution.operation;

import java.util.ArrayList;

public class CircularConvolution {
    
    private static int mod(int value, int circleSize){
        if(value >= circleSize){
            return value % circleSize;
        }else if(value < 0){
            return circleSize - ((-value) % circleSize);
        }else{
            return value;
        }
    }

    public static RealVector convolve(RealVector v1, RealVector v2){
        if(v1.getDimensionality() != v2.getDimensionality()){
            return null;
        }
        
        RealVector r = new RealVector(v1.getDimensionality());
        int dd = v1.getDimensionality();
        for(int i=0; i<dd; i++){
            float sum = 0f;
            for(int j=0; j<dd; j++){
                //System.out.println("i = " + i + ", j = " + j + ", i - j = " + (i  - j) + ", (i - j) % dd = " + mod(i - j, dd)); //DEBUG
                sum += v1.getValue(j) * v2.getValue(mod(i - j, dd));
            }
            r.setValue(i, sum);
        }
        
        return r;
    }
    
    public static RealVector inverseConvolve(RealVector v1, RealVector v2){
        return convolve(v1.involve(), v2);
    }
    
    public static RealVector correlate(RealVector v1, RealVector v2){
        return inverseConvolve(v1, v2);
    }
    
    @Override
    public String toString(){
        return "{circular convolution}";
    }
    
    public static void main(String[] args) {
        ArrayList<RealVector> vs = new ArrayList<>();
        vs.add(new RealVector(new float[]{ 1, 2, 3, 4}));
        vs.add(new RealVector(new float[]{ 1.1f, 2.2f, 3.3f, 4.4f}));
        vs.add(CircularConvolution.convolve(vs.get(0), vs.get(1)));
        
        vs.add(RealVector.createGaussianElementaryVector(1024));
        vs.add(RealVector.createGaussianElementaryVector(1024));
        vs.add(CircularConvolution.convolve(vs.get(3), vs.get(4)));
        vs.add(CircularConvolution.inverseConvolve(vs.get(3), vs.get(5)));
        vs.add(CircularConvolution.inverseConvolve(vs.get(4), vs.get(5)));
        
        vs.add(new RealVector(new float[]{ vs.get(3).getSimilarity(vs.get(4)) }));
        vs.add(new RealVector(new float[]{ vs.get(3).getSimilarity(vs.get(5)) }));
        vs.add(new RealVector(new float[]{ vs.get(4).getSimilarity(vs.get(5)) }));
        vs.add(new RealVector(new float[]{ vs.get(3).getSimilarity(vs.get(6)) }));
        vs.add(new RealVector(new float[]{ vs.get(4).getSimilarity(vs.get(6)) }));
        vs.add(new RealVector(new float[]{ vs.get(3).getSimilarity(vs.get(7)) }));
        vs.add(new RealVector(new float[]{ vs.get(4).getSimilarity(vs.get(7)) }));
                
        for(RealVector v : vs){
            System.out.println(v);
        }
        
    }
    
}
*/