#-*-coding:utf-8-*-

# tkinter를 사용하기 위한 import
from tkinter import *
from tkinter import ttk as tk1
import importlib
import notifDef
import joinUser as ju
import tkinter.font as tkFont
import infoTransfer

#폰트 위치
"""
sudo apt install fontconfig
curl -o nanumfont.zip http://cdn.naver.com/naver/NanumFont/fontfiles/NanumFont_TTF_ALL.zip
sudo unzip -d /usr/share/fonts/truetype/nanum nanumfont.zip
sudo fc-cache -f -v
"""
path='/usr/share/fonts/truetype/nanum/NanumPen.ttf'
#url = "http://3.39.187.161:8000/login/"


# 사용자 id와 password를 비교하는 함수
def check_data(user_id_var,password_var,window):
    user_id_data = user_id_var.get()
    user_pw_data = password_var.get()

    notif = Label(window, text="", font=(path, 11))
    notif1 = Label(window, text="", font=(path, 11))
    notif_login_fail = Label(window, text="", font=(path, 11))

    notifDef.empty_check(notif, user_id_data, 1)
    notifDef.empty_check(notif1, user_pw_data, 3)

    #empty_check1(notif1, user_id_data, 3)
    # notif1 = Label(window, text="", font=(path, 11)

    #data = {"username": user_id_data, "password": user_pw_data}
    #login_acess= requests.post(url,data=data)
    
    success= infoTransfer.login_data(user_id_data,user_pw_data)

    if(success):
        window.destroy()
        window.update()
        return True
    
    else:
        notifDef.stateAram(notif_login_fail,5)

def sign_up():
    #print("회원가입")
    ju.runJoin()


def login_run(user_id_var, password_var):
    window = Toplevel()

    # id와 password, 그리고 확인 버튼의 UI를 만드는 부분
    tk1.Label(window, text = "아이디 : ",font=(path,11)).grid(row = 0, column = 0, pady = 5)
    tk1.Label(window, text = "비밀번호 : ",font=(path,11)).grid(row = 2, column = 0, pady = 5)
    tk1.Entry(window, textvariable = user_id_var).grid(row = 0, column = 1, pady = 5)
    tk1.Entry(window, show="*", textvariable = password_var).grid(row = 2, column = 1, pady = 5)

    tk1.Button(window, text = "회원가입", command = sign_up,width=20).grid(row = 4, column = 0)
    tk1.Button(window, text = "로그인", command = lambda : check_data(user_id_var,password_var,window),width=20).grid(row = 4, column = 1)
#, padx = 5, pady = 10

    window.title("Test");
    window.mainloop()
