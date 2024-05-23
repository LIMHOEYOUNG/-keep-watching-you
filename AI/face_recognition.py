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

ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}

def capture_image_on_face_detection(output_path='check/image.jpg'):
    video_capture = cv2.VideoCapture(-1)
    face_detected = False

    while not face_detected:
        ret, frame = video_capture.read()
        if not ret:
            continue

        rgb_frame = frame[:, :, ::-1]
        face_locations = face_recognition.face_locations(rgb_frame)

        if face_locations:
            cv2.imwrite(output_path, frame)
            face_detected = True

    video_capture.release()
    cv2.destroyAllWindows()
    return output_path

def predict(X_img_path, knn_clf=None, model_path=None, distance_threshold=0.4):
    if not os.path.isfile(X_img_path) or os.path.splitext(X_img_path)[1][1:] not in ALLOWED_EXTENSIONS:
        raise Exception(f"Invalid image path: {X_img_path}")

    if knn_clf is None and model_path is None:
        raise Exception("Must supply knn classifier either through knn_clf or model_path")

    if knn_clf is None:
        with open(model_path, 'rb') as f:
            knn_clf = pickle.load(f)

    X_img = face_recognition.load_image_file(X_img_path)
    X_face_locations = face_recognition.face_locations(X_img)

    if not X_face_locations:
        return []

    faces_encodings = face_recognition.face_encodings(X_img, known_face_locations=X_face_locations)
    closest_distances = knn_clf.kneighbors(faces_encodings, n_neighbors=1)
    are_matches = [closest_distances[0][i][0] <= distance_threshold for i in range(len(X_face_locations))]

    return [(pred, loc) if rec else ("Unknown", loc) for pred, loc, rec in zip(knn_clf.predict(faces_encodings), X_face_locations, are_matches)]

def show_prediction_labels_on_image(img_path, predictions):
    pil_image = Image.open(img_path).convert("RGB")
    draw = ImageDraw.Draw(pil_image)

    for name, (top, right, bottom, left) in predictions:
        draw.rectangle(((left, top), (right, bottom)), outline=(0, 0, 255))
        name = name.encode("UTF-8")
        text_width, text_height = draw.textsize(name)
        draw.rectangle(((left, bottom - text_height - 10), (right, bottom)), fill=(0, 0, 255), outline=(0, 0, 255))
        draw.text((left + 6, bottom - text_height - 5), name, fill=(255, 255, 255, 255))

    del draw
    pil_image.show()

if __name__ == "__main__":

    captured_image_path = capture_image_on_face_detection()
    if captured_image_path:
        print(f"Looking for faces in {captured_image_path}")
        predictions = predict(captured_image_path, model_path="trained_knn_model.clf")
        for name, (top, right, bottom, left) in predictions:
            print(f"- Found {name} at ({left}, {top})")
        show_prediction_labels_on_image(captured_image_path, predictions)

