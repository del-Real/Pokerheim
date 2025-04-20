package io.github.G16.Controller;

import io.github.G16.Model.PlayerTable;

public interface FirestoreListener {
    void listenForTableUpdates(PlayerTable playerTable);
    void listenForPlayerUpdates(PlayerTable playerTable);
    void stopListening();
}
