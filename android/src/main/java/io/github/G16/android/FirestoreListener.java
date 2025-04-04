package io.github.G16.android;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import io.github.G16.Controller.FirestoreUpdateListener;

public class FirestoreListener implements FirestoreUpdateListener {

    private final FirebaseFirestore firestore;
    private ListenerRegistration registration;

    public FirestoreListener() {
        firestore = FirebaseFirestore.getInstance();
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
