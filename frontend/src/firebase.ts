// src/firebase.ts

import { initializeApp } from 'firebase/app';
import { getStorage } from 'firebase/storage';

// Your web app's Firebase configuration using environment variables
const firebaseConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY,
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID,
  storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET,
  messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID,
  appId: import.meta.env.VITE_FIREBASE_APP_ID,
};

// Log the Firebase config to verify correctness (Remove this in production)
console.log('Firebase Config:', firebaseConfig);

// Initialize Firebase
const app = initializeApp(firebaseConfig);

// Log Firebase app instance (Verify if initialized correctly)
console.log('Firebase App Initialized:', app);

// Get and export the Firebase Storage instance
export const storage = getStorage(app);

// Log storage bucket reference (Verify if storage is initialized)
console.log('Firebase Storage Initialized:', storage);