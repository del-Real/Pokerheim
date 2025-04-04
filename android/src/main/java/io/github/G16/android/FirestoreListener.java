package io.github.G16.android;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class FirestoreListener {

    private final FirebaseFirestore firestore;
    private ListenerRegistration registration;

    public FirestoreListener() {
        firestore = FirebaseFirestore.getInstance();
    }
    public void listenForCollectionUpdates() {
        registration = firestore.collection("players")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot snapshot, FirebaseFirestoreException e) {
                        if (e != null) {
                            System.out.println("Listen failed: " + e);
                            return;
                        }

                        if (snapshot != null && !snapshot.isEmpty()) {
                            for (DocumentSnapshot document : snapshot.getDocuments()) {
                                System.out.println("Document updated: " + document.getId());
                                System.out.println("Content: " + document.getData());
                            }
                        } else {
                            System.out.println("No document found");
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
