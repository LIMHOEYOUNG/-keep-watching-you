# -- coding: utf-8 --
# tkinter를 사용하기 위한 import
from tkinter import *
from tkinter import ttk
import notifDef
import importlib
import tkinter.messagebox as msgbox
import folderMake
import infoTransfer
import requests

import json

import traceback

#print("joinUser run")
# tkinter 객체 생성

#window2 = Tk()

# 사용자 정보를 저장하는 변수 생성
"""
new_user_id= StringVar()
new_password= StringVar()
check_password= StringVar()
entry_name= StringVar()
entry_email= StringVar()
"""

#폰트 위치
"""
sudo apt install fontconfig
curl -o nanumfont.zip http://cdn.naver.com/naver/NanumFont/fontfiles/NanumFont_TTF_ALL.zip
sudo unzip -d /usr/share/fonts/truetype/nanum nanumfont.zip
sudo fc-cache -f -v
"""
path='/usr/share/fonts/truetype/nanum/NanumPen.ttf'
url= "http://3.39.187.161:8000/join_member/"

def id_check(notif, username):
    error_id = 1
    error_other =8
    try:
        #print("아이디 체크 진행")
        check_id= username

        response=infoTransfer.id_double_check(check_id)
        print("response = ",response)

        #received_data=json.loads(response)
        received_data= response.json()
        """
        print(received_data)

        print("응답 코드:", response.status_code)
        print("응답 내용:", response.text)

        """
        success= received_data.get('success')

        #print("success = ",success)

        if (success):
            return success
        else:
            notif.config(text="ID is exist",fg='red')
            notifDef.show_grid(notif,error_id)
            return success
        
        #print("아이디 체크 종료")
    except Exception as e:
        print("error = ",e)
        traceback.print_exc()
        notif.config(text='Error check ID', fg='red')
        notifDef.show_grid(notif,error_other)

def send(window,new_user_id,new_password,check_password,entry_name,entry_email,emailComboBox):
    notif1 = Label(window, text="", font=("Calibri", 11))
    notif4 = Label(window, text="", font=("Calibri", 11))
    notif8 = Label(window, text="", font=("Calibri", 11))

    error_id = 1
    error_pw = 4
    error_other = 8

    try:
        username = new_user_id.get()
        password = new_password.get()
        checkPassword = check_password.get()
        name = entry_name.get()
        email = entry_email.get()+"@"+emailComboBox.get()

        if username == "" or password == "" or checkPassword=="" or name == "" or entry_email.get() == "":
            notif4.config(text=' ' * 40)
            notifDef.show_grid(notif4, error_other)
            notif8.config(text='All fields Required!!', fg='red')
            notifDef.show_grid(notif8,error_other)
            # return
        elif password != checkPassword:
            print("not correct password")
            notif4.config(text='check password again', fg='red')
            notifDef.show_grid(notif4,error_pw)
            notif8.config(text=' ' * 40)
            notifDef.show_grid(notif8, error_other)
            # return
        else:
            notif4.config(text=' '*40)
            notif8.config(text=' '*40)
            notifDef.show_grid(notif4,error_pw)
            notifDef.show_grid(notif8,error_other)

            #id 중복 체크             
            if(id_check(notif8, username)):
                folderMake.makeUsrdirs(username)

                infoTransfer.join_data(username,password,name,email)

                window.destroy()    #창 닫기


    except Exception as e :
        print(e)
        traceback.print_exc()
        notif8.config(text='Error sending info', fg='red')
        notifDef.show_grid(notif8,error_other)

def runJoin():
    window = Toplevel()
    
    
    new_user_id = StringVar()
    new_password = StringVar()
    check_password = StringVar()
    entry_name = StringVar()
    entry_email = StringVar()

    email_addr = ["naver.com", "gmail.com", "nate.com", "hanmail.net", "daum.net", "kakao.com"]
    emailComboBox = ttk.Combobox(window, heigh=len(email_addr), values=email_addr, state='readonly')
    emailComboBox.set("select")

    #라벨
    # 1은 아이디 중복 알림, 4는 비밀번호 일치안됨
    ttk.Label(window, text = "아이디 : ",font=(path,11)).grid(row = 0, column = 0, padx = 10, pady = 10)
    ttk.Label(window, text = "비밀번호 : ",font=(path,11)).grid(row = 2, column = 0, padx = 10, pady = 10)
    ttk.Label(window, text = "비밀번호 확인 : ",font=(path,11)).grid(row = 3, column = 0, padx = 10, pady = 10)
    ttk.Label(window, text = "이름 : ",font=(path,11)).grid(row = 5, column = 0, padx = 10, pady = 10)
    ttk.Label(window, text = "E-mail : ",font=(path,11)).grid(row = 6, column = 0, padx = 10, pady = 10)
    ttk.Label(window, text="@").grid(row = 6, column = 2, padx=2, pady= 10)
    emailComboBox.grid(row = 6, column = 3, padx=2, pady= 10)

    #입력
    ttk.Entry(window, textvariable = new_user_id).grid(row = 0, column = 1, padx = 10, pady = 10)
    ttk.Entry(window, show="*", textvariable = new_password).grid(row = 2, column = 1, padx = 10, pady = 10)
    ttk.Entry(window, show="*", textvariable = check_password).grid(row = 3, column = 1, padx = 10, pady = 10)
    ttk.Entry(window, textvariable = entry_name,font=("Calibri", 11)).grid(row = 5, column = 1, padx = 10, pady = 10)
    ttk.Entry(window, textvariable = entry_email).grid(row = 6, column = 1, padx = 10, pady = 10)


    #ttk.Button(window, text= "back").grid(row = 5, column = 0, padx = 10, pady = 10)
    ttk.Button(window, text = "가입", command = lambda : send(window,new_user_id,new_password,check_password,entry_name,entry_email,emailComboBox)).grid(row = 7, column = 1, padx = 10, pady = 10)

    window.mainloop()
