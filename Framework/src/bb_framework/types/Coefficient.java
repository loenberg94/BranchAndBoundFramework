package bb_framework.types;

public abstract class Coefficient<T> {
    T val;

    public boolean isIndex(){
        return this instanceof Index;
    }

    public boolean isValue(){
        return this instanceof Value;
    }

    public boolean isNone(){
        return this instanceof Empty;
    }

    public T getVal() {
        return val;
    }

    public Coefficient(T value){
        val = value;
    }

    public abstract String toString();
}
