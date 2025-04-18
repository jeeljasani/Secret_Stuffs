// src/services/files/firebaseService.ts

import { storage } from "@/firebase";
import { ref, uploadBytesResumable, getDownloadURL } from "firebase/storage";

interface UploadFile {
  name: string;
  originFileObj: File;
}

export const uploadFiles = async (fileList: UploadFile[], folder: string) => {
  const uploadPromises = fileList.map((file) => {
    const storageRef = ref(storage, `${folder}/${file.name}`);

    return new Promise<string>((resolve, reject) => {
      const uploadTask = uploadBytesResumable(storageRef, file.originFileObj);

      uploadTask.on(
        "state_changed",
        (snapshot) => {
          const progress = (snapshot.bytesTransferred / snapshot.totalBytes) * 100;
          console.log(`Upload is ${progress}% done`);
        },
        (error) => reject(error),
        async () => {
          try {
            const downloadURL = await getDownloadURL(uploadTask.snapshot.ref);
            resolve(downloadURL);
          } catch (error) {
            reject(error);
          }
        }
      );
    });
  });

  return Promise.all(uploadPromises);
};