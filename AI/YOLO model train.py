import torch
import numpy as np
import cv2
from matplotlib import pyplot as plt
import uuid 
import os 
import time
import tensorflow as tf
from tensorflow.python.client import device_lib
import ultralytics
from ultralytics import YOLO
import glob

#체크용
#print(device_lib.list_local_devices())
#print(torch.cuda.get_device_name(0))
#print(torch.cuda.is_available())

model = YOLO('best.pt')
model.train(data='./drowsy/data.yaml', epochs=30, patience=30, batch=32, imgsz=416)
results = model.predict(source='./drowsy/test/images/', save=True)
detetced_image_list = glob.glob(('./runs/detect/predict2/*'))

detected_image_nums = len(detetced_image_list)

print(detected_image_nums)

print(detetced_image_list)