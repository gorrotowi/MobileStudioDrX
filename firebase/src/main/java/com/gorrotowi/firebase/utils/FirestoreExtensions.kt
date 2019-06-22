package com.gorrotowi.firebase.utils

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query

inline fun <R> Query.observe(
    crossinline action: (
        documents: List<DocumentSnapshot?>?,
        exception: Exception?
    ) -> R
): R? {
    addSnapshotListener { querySnapshot, firebaseFirestoreException ->
        firebaseFirestoreException?.let {
            action(null, it)
        } ?: querySnapshot?.let { snapshot ->
            action(snapshot.documents, null)
        }
    }
    return action(null, null)
}