import numpy as np
import cv2
from matplotlib import pyplot as plt
import uuid
import os 
import time
import ultralytics
from ultralytics import YOLO

model = YOLO('best.pt')
cap = cv2.VideoCapture(0)
count = 0

while cap.isOpened():
    ret, frame = cap.read()

    font = cv2.FONT_HERSHEY_DUPLEX
    results_list = model(frame)
    
    for results in results_list:
        confs = results.boxes.cls
        for check in confs:
            if check == 0:
                count = count + 1
        if count >= 20:
            cv2.putText(frame, "drowsy dectection", (50,50), font, 1.0, (0, 0, 0), 1)
            count = 0
        annotated_image = results.plot()
        cv2.imshow('YOLO', annotated_image)
    
    if cv2.waitKey(10) & 0xFF == ord("q"):
        break
        
cap.release()
cv2.destroyAllWindows()
