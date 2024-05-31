import os
import os.path
import face_recognition
import cv2
import pickle
import subprocess

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

def predict(X_img_path, knn_clf=None, model_path=None, distance_threshold=0.3):
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

if __name__ == "__main__":
    unknown_count = 0
    max_attempts = 3

    while unknown_count < max_attempts:
        captured_image_path = capture_image_on_face_detection()
        if captured_image_path:
            print(f"Looking for faces in {captured_image_path}")
            predictions = predict(captured_image_path, model_path="trained_knn_model.clf")
            for name, (top, right, bottom, left) in predictions:
                if name == "Unknown":
                    print(f"- Found {name} at ({left}, {top})")
                    unknown_count += 1
                    if unknown_count >= max_attempts:
                        # 서버에 사진과 비인가자 알림 전송
                        print("Unauthorized person detected multiple times. Sending alert to server.")
                        #서버로 사진과 알림을 전송하는 코드
                else:
                    subprocess.run(['python', 'drowsy_detection_cam.py'])
                    unknown_count = 0  # 인증된 사용자가 발견되면 시도 횟수 초기화
                    break  # 인증된 사용자를 찾으면 더 이상의 검사를 중단

    if unknown_count >= max_attempts:
        # 서버에 사진과 비인가자 알림 전송
        print("Unauthorized person detected multiple times. Sending alert to server.")
        #서버로 사진과 알림을 전송하는 코드
