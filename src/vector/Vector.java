package vector;

import permutation.Permutation;
import vector.binary.DenseBinaryVector;
import vector.complex.DenseComplexVector;
import vector.real.DenseRealVector;

public abstract class Vector<W extends Weight> {
    
    public abstract W getWeight(int dimension);
    public abstract void setWeight(int dimension, W w);
    public abstract int getDimensionality();
    public abstract float similarity(Vector v, boolean isNormalisationNecessary);
    public float similarity(Vector v){
        return similarity(v, true);
    }
    public abstract Vector orthogonalise(Vector v, boolean isNormalisationNecessary);
    public Vector orthogonalise(Vector v){
        return orthogonalise(v, true);
    }
    public abstract Vector permute(Permutation p);
    public abstract Vector reversePermute(Permutation p);
    
    public abstract Vector plus(Vector v);
    public abstract Vector minus(Vector v);
    public abstract Vector convolute(Vector v);
    public abstract Vector correlate(Vector v);
    
    public static Vector createDenseRandomVector(VectorType type, int dimensionality){
        switch(type){
            case BINARY:
                return DenseBinaryVector.createRandomVector(dimensionality);
            case REAL:
                return DenseRealVector.createRandomGaussianVector(dimensionality);
            case COMPLEX:
                return DenseComplexVector.createRandomGaussianVector(dimensionality);
            default:
                return null;
        }
    }
    
    public static Vector createZeroVector(VectorType type, int dimensionality){
        switch(type){
            case BINARY:
                return DenseBinaryVector.createZeroVector(dimensionality);
            case REAL:
                return DenseRealVector.createZeroVector(dimensionality);
            case COMPLEX:
                return DenseComplexVector.createZeroVector(dimensionality);
            default:
                return null;
        }
    }
    
}
