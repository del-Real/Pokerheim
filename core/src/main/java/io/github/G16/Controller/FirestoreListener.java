package io.github.G16.Controller;

import io.github.G16.Model.PlayerTable;

public interface FirestoreListener {

    // Listener interfaced used to interact with AndroidFirestoreListener in the android folder
    void listenForTableUpdates(PlayerTable playerTable);
    void listenForPlayerUpdates(PlayerTable playerTable);
    void stopListening();
}
