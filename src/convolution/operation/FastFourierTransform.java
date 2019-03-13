/*package convolution.operation;

import edu.princeton.cs.algs4.Complex;
import edu.princeton.cs.algs4.FFT;

public class FastFourierTransform {

    private static final double ALMOST_ZERO = Math.pow(2, -10);
    
    private static Complex[] toComplexArray(RealVector v){
        Complex[] vArray = new Complex[v.getDimensionality()];
        for(int d=0; d<v.getDimensionality(); d++){
            vArray[d] = new Complex(v.getValue(d), 0);
        }
        
        return vArray;
    }

    public static ComplexVector transform(RealVector v){
        return new ComplexVector(FFT.fft(toComplexArray(v)));
    }
    
    public static RealVector inverseTransform(ComplexVector v){
        Complex[] cs = FFT.ifft(v.getValues());
        RealVector rv = new RealVector(cs.length);
        Complex c;
        for(int d=0; d<cs.length; d++){
            c = cs[d];
            if(Math.abs(c.im()) < ALMOST_ZERO){
                rv.setValue(d, (float) c.re());
            }else{
                return null;
            }
        }
        
        return rv;
    }
    
    public static void main(String[] args){
        RealVector rv1 = new RealVector(new float[]{ 1, 2, 3, 4 });
        ComplexVector cv1 = FastFourierTransform.transform(rv1);
        RealVector rv2 = FastFourierTransform.inverseTransform(cv1);
        
        System.out.println("real vector 1: " + rv1);
        System.out.println("complex vector 1: " + cv1);
        System.out.println("real vector 2: " + rv2);
        
    }
    
}
*/