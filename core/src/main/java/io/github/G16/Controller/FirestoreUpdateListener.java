package io.github.G16.Controller;

public interface FirestoreUpdateListener {
    void listenForUpdates(String collection, String document);
}
