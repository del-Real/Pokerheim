package io.github.G16.Model;

import io.github.G16.View.Observer;

public interface Subject {

    // Observer pattern
    void notifyObservers();
    void addObserver(Observer observer);
}
