package vector;

public interface Weight {
    public Weight plus(Weight w);
    public Weight minus(Weight w);
    public Weight times(Weight w);
}
