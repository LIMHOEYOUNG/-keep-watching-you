import face_recognition
import cv2
import pickle
import numpy as np
import time

with open('dataset_faces.dat', 'rb') as f:
	all_face_encodings = pickle.load(f)

known_face_names = list(all_face_encodings.keys())
known_face_encodings = np.array(list(all_face_encodings.values()))

def gstreamer_pipeline(
    capture_width=320,   # 1920 - > 320
    capture_height=248,  # 1080 - > 248
    display_width=320,   # 960  - > 320
    display_height=248,  # 520  - > 248
    framerate=30,        #
    flip_method=2,       # 0    - > 2
):
    return (
        "nvarguscamerasrc ! "
        "video/x-raw(memory:NVMM), "
        "width=(int)%d, height=(int)%d, framerate=(fraction)%d/1 ! "
        "nvvidconv flip-method=%d ! "
        "video/x-raw, width=(int)%d, height=(int)%d, format=(string)BGRx ! "
        "videoconvert ! "
        "video/x-raw, format=(string)BGR ! appsink drop=True"
        % (
            capture_width,
            capture_height,
            framerate,
            flip_method,
            display_width,
            display_height,
        )
    )

cap= cv2.VideoCapture(gstreamer_pipeline(), cv2.CAP_GSTREAMER)

#video_capture = cv2.VideoCapture(0)

while True:
    ret, frame = video_capture.read()

    rgb_frame = np.ascontiguousarray(frame[:, :, ::-1])

    face_locations = face_recognition.face_locations(rgb_frame)
    face_encodings = face_recognition.face_encodings(rgb_frame, face_locations)

    for (top, right, bottom, left), face_encoding in zip(face_locations, face_encodings):
        matches = face_recognition.compare_faces(known_face_encodings, face_encoding)

        name = "Unknown"

        face_distances = face_recognition.face_distance(known_face_encodings, face_encoding)
        best_match_index = np.argmin(face_distances)
        if matches[best_match_index]:
            name = known_face_names[best_match_index]
        font = cv2.FONT_HERSHEY_DUPLEX
        if matches[best_match_index]:
            cv2.putText(frame, "success", (50,50), font, 1.0, (255, 255, 255), 1)
        else:
            for i in range(20):
                if matches[best_match_index]:
                    name = known_face_names[best_match_index]
                    cv2.putText(frame, "success", (50,50), font, 1.0, (255, 255, 255), 1)
                    break
                else:
                    time.sleep(0.1)
            cv2.putText(frame, "failed", (50,50), font, 1.0, (255, 255, 255), 1)
        cv2.rectangle(frame, (left, top), (right, bottom), (0, 0, 255), 2)

        cv2.rectangle(frame, (left, bottom - 35), (right, bottom), (0, 0, 255), cv2.FILLED)
        font = cv2.FONT_HERSHEY_DUPLEX
        cv2.putText(frame, name, (left + 6, bottom - 6), font, 1.0, (255, 255, 255), 1)

    cv2.imshow('Video', frame)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

video_capture.release()
cv2.destroyAllWindows()