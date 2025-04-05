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
                            // Parse fields
                            Long pot = value.getLong("pot");
                            String currentTurn = value.getString("currentTurn");

                            ArrayList<Card> communityCards = new ArrayList<>();
                            List<Map<String, Object>> rawCards = (List<Map<String, Object>>) value.get("communityCards");
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


                            // Update playerTable
                            if (pot != null && currentTurn != null) {
                                playerTable.updateTable(pot.intValue(), currentTurn, communityCards);
                                System.out.println("PlayerTable updated!");
                            } else {
                                System.out.println("Missing data to update PlayerTable.");
                            }
                        }
                    }
                });
    }
    public void listenForUpdates(String collection, String document) {
        registration = firestore.collection(collection)
                .document(document)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
                        if (e != null) {
                            System.out.println("Listen failed: " + e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            System.out.println("Document updated: " + snapshot.getId());
                            System.out.println("Content: " + snapshot.getData());
                        } else {
                            System.out.println("Document does not exist");
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
