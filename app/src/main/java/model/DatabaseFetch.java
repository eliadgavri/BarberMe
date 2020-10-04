package model;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import userData.BarberShop;

public class DatabaseFetch {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void fetchAllBarberShops(Consumer<List<BarberShop>> consumer) {
        Query query = db.collection("shops").orderBy("updateDate", Query.Direction.DESCENDING);
        query.get()
                .addOnCompleteListener(task -> {
                    List<BarberShop> data = null;
                    if (task.isSuccessful()) {
                        data = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            data.add(document.toObject(BarberShop.class));
                        }
                        consumer.apply(data);
                    } else {
                        task.getException().printStackTrace();
                    }
                });
    }

    public void fetchUserBarberShops(Consumer<List<BarberShop>> consumer, String uid) {
        Query query = db.collection("shops").whereEqualTo("userId", uid)/*.orderBy("updateDate", Query.Direction.DESCENDING)*/;
        query
                .get()
                .addOnCompleteListener(task -> {
                    List<BarberShop> data = null;
                    if (task.isSuccessful()) {
                        data = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            data.add(document.toObject(BarberShop.class));
                        }
                        consumer.apply(data);
                    } else {
                        task.getException().printStackTrace();
                    }
                });
    }
}
