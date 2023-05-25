package com.netease.nemo.function;

@FunctionalInterface
public interface Action<T> {
    T doAction();
}
