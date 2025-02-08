// firebase.js
import { initializeApp } from "firebase/app";
import { getStorage } from "firebase/storage";
import { getAuth } from "firebase/auth";

const firebaseConfig = {
  apiKey: "AIzaSyACNOcwB0QaYTO8dnwyjJcQexnxSebTn3k",
  authDomain: "kh-react-firebase.firebaseapp.com",
  projectId: "kh-react-firebase",
  storageBucket: "kh-react-firebase.firebasestorage.app",
  messagingSenderId: "37264675266",
  appId: "1:37264675266:web:a78b694aa1ad09802e065b",
  measurementId: "G-JV6DW5BYZB",
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

// Initialize Firebase services
const storage = getStorage(app); // For file uploads
const auth = getAuth(app); // For user authentication (if needed)

export { storage, auth };

// // Import the functions you need from the SDKs you need
// import { initializeApp } from "firebase/app";
// import { getAnalytics } from "firebase/analytics";
// // TODO: Add SDKs for Firebase products that you want to use
// // https://firebase.google.com/docs/web/setup#available-libraries

// // Your web app's Firebase configuration
// // For Firebase JS SDK v7.20.0 and later, measurementId is optional
// const firebaseConfig = {
//   apiKey: "AIzaSyACNOcwB0QaYTO8dnwyjJcQexnxSebTn3k",
//   authDomain: "kh-react-firebase.firebaseapp.com",
//   projectId: "kh-react-firebase",
//   storageBucket: "kh-react-firebase.firebasestorage.app",
//   messagingSenderId: "37264675266",
//   appId: "1:37264675266:web:a78b694aa1ad09802e065b",
//   measurementId: "G-JV6DW5BYZB"
// };

// // Initialize Firebase
// const app = initializeApp(firebaseConfig);
// const analytics = getAnalytics(app);
