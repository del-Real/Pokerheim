package io.github.G16.android;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.G16.Model.Card;
import io.github.G16.Model.PlayerTable;
import io.github.G16.Model.Rank;
import io.github.G16.Model.Suit;

public class AndroidFirestoreListener implements io.github.G16.Controller.FirestoreListener {

    // This class listens for updates on firestore and updates PlayerTable
    private final FirebaseFirestore firestore;
    private ListenerRegistration tableRegistration;
    private ListenerRegistration playerRegistration;

    public AndroidFirestoreListener() {
        firestore = FirebaseFirestore.getInstance();
    }

    // This is for listening table updates
    public void listenForTableUpdates(PlayerTable playerTable) {
        tableRegistration = firestore.collection("tables")
                .document("table" + playerTable.getTableId())
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        System.out.println("Table listen failed: " + error);
                        return;
                    }

                    if (value != null && value.exists()) {
                        System.out.println("Table updated: " + value.getId());

                        Long pot = value.getLong("pot");
                        String currentTurn = value.getString("currentTurn");
                        String lastAction = value.getString("last_action");

                        ArrayList<Card> communityCards = new ArrayList<>();
                        List<Map<String, Object>> rawCards = (List<Map<String, Object>>) value.get("community_cards");
                        if (rawCards != null) {
                            for (Map<String, Object> cardData : rawCards) {
                                Suit suit = Suit.fromString((String) cardData.get("suit"));
                                Rank rank = Rank.fromString((String) cardData.get("rank"));
                                communityCards.add(new Card(rank, suit));
                            }
                        }

                        if (pot != null && currentTurn != null) {
                            firestore.collection("tables")
                                    .document("table" + playerTable.getTableId())
                                    .collection("players")
                                    .document(currentTurn)
                                    .get()
                                    .addOnSuccessListener(snapshot -> {
                                        String currentPlayerName = null;
                                        if (snapshot.exists()) {
                                            currentPlayerName = snapshot.getString("name");
                                        }

                                        playerTable.updateTable(
                                                pot.intValue(),
                                                currentTurn,
                                                communityCards,
                                                currentPlayerName,
                                                lastAction
                                        );
                                    })
                                    .addOnFailureListener(e -> {
                                        System.out.println("Failed to fetch current player name: " + e.getMessage());
                                    });
                        }
                    }
                });
    }

    // This is for listening player updates

    public void listenForPlayerUpdates(PlayerTable playerTable) {
        playerRegistration = firestore.collection("tables")
                .document("table" + playerTable.getTableId())
                .collection("players")
                .document(playerTable.getPlayerId())
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        System.out.println("Player listen failed: " + error);
                        return;
                    }

                    if (value != null && value.exists()) {
                        System.out.println("Player updated: " + value.getId());

                        Long stack = null;
                        Object stackObj = value.get("stack");
                        if (stackObj instanceof Long) {
                            stack = (Long) stackObj;
                        } else if (stackObj instanceof Integer) {
                            stack = ((Integer) stackObj).longValue();
                        }

                        List<Map<String, String>> rawCardsList = (List<Map<String, String>>) value.get("cards");
                        ArrayList<Card> playerCards = new ArrayList<>();
                        if (rawCardsList != null) {
                            for (Map<String, String> cardData : rawCardsList) {
                                String suitStr = cardData.get("suit");
                                String rankStr = cardData.get("rank");

                                if (suitStr != null && rankStr != null) {
                                    try {
                                        Suit suit = Suit.fromString(suitStr);
                                        Rank rank = Rank.fromString(rankStr);
                                        playerCards.add(new Card(rank, suit));
                                    } catch (IllegalArgumentException e) {
                                        System.out.println("Error converting card: " + e.getMessage());
                                    }
                                }
                            }
                        }

                        if (stack != null) {
                            playerTable.updatePlayer(playerCards, stack.intValue());
                        }
                    }
                });
    }

    // This is to stop listening

    public void stopListening() {
        if (tableRegistration != null) {
            tableRegistration.remove();
            System.out.println("Table listener removed");
        }
        if (playerRegistration != null) {
            playerRegistration.remove();
            System.out.println("Player listener removed");
        }
    }
}
