package vector.complex;

import edu.princeton.cs.algs4.Complex;
import misc.Meta;
import vector.Weight;

public class ComplexWeight implements Weight{
    
    public static final ComplexWeight ZERO = new ComplexWeight(0, 0);
    public static final ComplexWeight ONE = new ComplexWeight(1, 0);
    
    private final Complex value;
    
    public ComplexWeight(double real, double imaginary){
        value = new Complex(real, imaginary);
    }
    
    public ComplexWeight(Complex value){
        this.value = value;
    }
    
    public Complex value(){
        return value;
    }
    
    public boolean isAlmostZero(){
        return Math.abs(abs()) <= Meta.ALMOST_ZERO;
    }
    
    public ComplexWeight negate(){
        return times(-1);
    }
    
    public ComplexWeight times(double s){
        return new ComplexWeight(value.scale(s));
    }
    
    public ComplexWeight times(Complex s){
        return new ComplexWeight(value.times(s));
    }
    
    @Override
    public ComplexWeight times(Weight w) {
        ComplexWeight c = (ComplexWeight) w;
        return new ComplexWeight(value.times(c.value()));
    }
    
    public float real(){
        return (float) value.re();
    }
    
    public float imaginary(){
        return (float) value.im();
    }
    
    public double abs(){
        return Math.sqrt(real() * real() + imaginary() * imaginary());
    }
    
    public ComplexWeight sqrt(){
        double abs = abs();
        
        double re = Math.sqrt((abs + real()) / 2);
        if(Double.isNaN(re)) re = 0;
        double im = Math.signum(imaginary()) * Math.sqrt((abs - real()) / 2);
        if(Double.isNaN(im)) im = 0;
        
        return new ComplexWeight(new Complex(re, im));
    }
    
    public ComplexWeight conjugate(){
        return new ComplexWeight(value.conjugate());
    }

    @Override
    public ComplexWeight plus(Weight w) {
        ComplexWeight c = (ComplexWeight) w;
        return new ComplexWeight(value.plus(c.value()));
    }
    
    @Override
    public ComplexWeight minus(Weight w) {
        ComplexWeight c = (ComplexWeight) w;
        return new ComplexWeight(value.minus(c.value()));
    }
    
    public ComplexWeight reciprocal(){
        return new ComplexWeight(value.reciprocal());
    }
    
    public ComplexWeight divideBy(ComplexWeight w){
        return new ComplexWeight(value.times(w.value().reciprocal()));
    }
    
    @Override
    public String toString(){
        return value.toString();
    }
    
}