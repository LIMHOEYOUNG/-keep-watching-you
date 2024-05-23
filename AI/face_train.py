import math
from sklearn import neighbors
import os
import os.path
from PIL import Image, ImageDraw
from face_recognition.face_recognition_cli import image_files_in_folder
import face_recognition
import cv2
import pickle
import numpy as np

def train(train_dir, model_save_path=None, n_neighbors=None, knn_algo='ball_tree', verbose=False):
    X, y = [], []

    for class_dir in os.listdir(train_dir):
        class_path = os.path.join(train_dir, class_dir)
        if not os.path.isdir(class_path):
            continue

        for img_path in image_files_in_folder(class_path):
            image = face_recognition.load_image_file(img_path)
            face_bounding_boxes = face_recognition.face_locations(image)

            if len(face_bounding_boxes) != 1:
                if verbose:
                    reason = "Didn't find a face" if len(face_bounding_boxes) < 1 else "Found more than one face"
                    print(f"Image {img_path} not suitable for training: {reason}")
            else:
                X.append(face_recognition.face_encodings(image, known_face_locations=face_bounding_boxes)[0])
                y.append(class_dir)

    if n_neighbors is None:
        n_neighbors = int(round(math.sqrt(len(X))))
        if verbose:
            print(f"Chose n_neighbors automatically: {n_neighbors}")

    knn_clf = neighbors.KNeighborsClassifier(n_neighbors=n_neighbors, algorithm=knn_algo, weights='distance')
    knn_clf.fit(X, y)

    if model_save_path:
        with open(model_save_path, 'wb') as f:
            pickle.dump(knn_clf, f)

    return knn_clf

if __name__ == "__main__":
    print("Training KNN classifier...")
    classifier = train("knn_examples/train", model_save_path="trained_knn_model.clf", n_neighbors=2)
    print("Training complete!")
