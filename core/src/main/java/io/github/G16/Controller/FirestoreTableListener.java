package io.github.G16.Controller;

import io.github.G16.Model.PlayerTable;

public interface FirestoreTableListener {
    void listenForTableUpdates(PlayerTable playerTable);
}
