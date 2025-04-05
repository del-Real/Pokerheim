package io.github.G16.android;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.G16.Controller.FirestoreTableListener;
import io.github.G16.Model.Card;
import io.github.G16.Model.PlayerTable;
import io.github.G16.Model.Rank;
import io.github.G16.Model.Suit;

public class FirestoreListener implements FirestoreTableListener {

    private final FirebaseFirestore firestore;
    private ListenerRegistration registration;

    public FirestoreListener() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void listenForTableUpdates(PlayerTable playerTable){
        registration = firestore.collection("tables")
                .document("table"+playerTable.getTableId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            System.out.println("Listen filed: " + error);
                            return;
                        }
                        if (value != null && value.exists()){
                            System.out.println("Document updated: " + value.getId());
                            System.out.println("Content: "+value.getData());

                            Long pot = value.getLong("pot");
                            String currentTurn = value.getString("currentTurn");
                            ArrayList<Card> communityCards = new ArrayList<>();
                            List<Map<String, Object>> rawCards = (List<Map<String, Object>>) value.get("community_cards");
                            if (rawCards != null) {
                                for (Map<String, Object> cardData : rawCards) {
                                    String suitStr = (String) cardData.get("suit");
                                    String rankStr = (String) cardData.get("rank");

                                    // Convert suit and rank to enum
                                    Suit suit = Suit.fromString(suitStr);
                                    Rank rank = Rank.fromString(rankStr);

                                    // Add the card to communityCards list
                                    communityCards.add(new Card(rank, suit));
                                }
                            }


                            // Get only the player's hand based on playerId
                            Map<String, Object> playersData = (Map<String, Object>) value.get("players");
                            if (playersData != null && playersData.containsKey(playerTable.getPlayerId())) {
                                Map<String, Object> playerData = (Map<String, Object>) playersData.get(playerTable.getPlayerId());

                                List<Map<String, String>> rawCardsList = (List<Map<String, String>>) playerData.get("cards");
                                ArrayList<Card> playerCards = new ArrayList<>();
                                if (rawCardsList != null) {
                                    for (Map<String, String> cardData : rawCardsList) {

                                        String suitStr = cardData.get("suit");
                                        String rankStr = cardData.get("rank");

                                        if (suitStr != null && rankStr != null) {
                                            try {
                                                Suit suit = Suit.fromString(suitStr);
                                                Rank rank = Rank.fromString(rankStr);
                                                playerCards.add(new Card(rank,suit));
                                            } catch (IllegalArgumentException e) {
                                                System.out.println("Error while converting card: " + e.getMessage());
                                            }
                                        } else {
                                            System.out.println("Error: Missing rank or suit for card");
                                        }
                                    }

                                }


                                // Update playerTable
                                if (pot != null && currentTurn != null) {
                                    playerTable.updateTable(pot.intValue(), currentTurn, communityCards, playerCards);
                                    System.out.println("PlayerTable updated!");
                                } else {
                                    System.out.println("Missing data to update PlayerTable.");
                                }
                            }
                        }
                    }
                });
    }
    public void stopListening() {
        if (registration != null) {
            registration.remove();
            System.out.println("Listener removed");
        }
    }
}
