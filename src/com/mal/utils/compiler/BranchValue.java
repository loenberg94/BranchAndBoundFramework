package com.mal.utils.compiler;

public class BranchValue<T> {
    final T _value;

    public BranchValue(T val){
        this._value = val;
    }

    public T get_value() {
        return _value;
    }
}
