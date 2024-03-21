import torch
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
eyecount = 0
mouthcount = 0

while cap.isOpened():
    ret, frame = cap.read()
    
    font = cv2.FONT_HERSHEY_DUPLEX
    results_list = model(frame)
    
    for results in results_list:
        confs = results.boxes.cls
        confs = confs.type(torch.int32)
        for check in confs:
            if check == 0:
                eyecount = eyecount + 1
                print("eyecount1:" , eyecount)
            elif check == 3:
                mouthcount = mouthcount + 1
                print("mouthcount2:", mouthcount)
            elif check == 1:
                if(eyecount > 0):
                    eyecount = eyecount - 2
                print("eyecount3:" , eyecount)
            elif check == 2:
                if(mouthcount > 0):
                    mouthcount = mouthcount - 1
                print("mouthcount4:", mouthcount)
        if eyecount >= 20:
            cv2.putText(frame, "drowsy dectection eyecount: %d" %eyecount, (50,50), font, 1.0, (0, 0, 0), 1)
        if mouthcount >= 25:
            cv2.putText(frame, "drowsy dectection mouthcount: %d" %mouthcount, (50,50), font, 1.0, (0, 0, 0), 1)
        annotated_image = results.plot()
        cv2.imshow('YOLO', annotated_image)
    
    if cv2.waitKey(10) & 0xFF == ord("q"):
        break
        
cap.release()
cv2.destroyAllWindows()

