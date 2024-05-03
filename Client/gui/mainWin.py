from tkinter import *
from tkinter import ttk as tk1
import importlib
import requests


import login as loginWin

# tkinter 객체 생성
window = Tk()

app_width=300
app_height=100
win_width=window.winfo_screenwidth()
win_height=window.winfo_screenheight()
center_width=(win_width/2)-(app_width/2)
center_height=(win_height/2)-(app_height/2)

#window.geometry("300x100+355+400")
window.geometry(f"{app_width}x{app_height}+{int(center_width)}+{int(center_height)}")
#window2 = Tk()

# 사용자 id와 password를 저장하는 변수 생성
#new_user_id, new_password, check_password = StringVar(), StringVar(), StringVar()
#url = "http://3.39.187.161:8000/login/"


#joinUser.py에서 사용하는 변수
new_user_id= StringVar()
new_password= StringVar()
check_password= StringVar()
entry_name= StringVar()
entry_email= StringVar()


#login.py에서 사용하는 변수
user_id= StringVar()
password= StringVar()


# 사용자 id와 password를 비교하는 함수
def login():
    try:
        loginWin.login_run(user_id,password)
    except:
        print("mainWin.py의 login() Error")
    
def face_rec():
    print("회원가입")

    #import teset
    #import joinUser
# id와 password, 그리고 확인 버튼의 UI를 만드는 부분

tk1.Button(window, text = "Face", command = face_rec).grid(row = 1, column = 0, padx = 30, pady = 30)
tk1.Button(window, text = "Login", command = login).grid(row = 1, column = 1, padx = 30, pady = 30)

window.title("Test");
window.mainloop()
