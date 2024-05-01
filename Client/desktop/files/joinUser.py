# tkinter를 사용하기 위한 import
from tkinter import *
from tkinter import ttk
import notifDef
import jsonParsing
import importlib
import tkinter.messagebox as msgbox

# tkinter 객체 생성

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

def id_check(notif,check_id,error_other):
    try:
        data={"username": check_id}
        json_data= json.dumps(data)
        response = requests.post(url, data=json_data)
        
        if(jsonParsing.user_pass(response)):
            notif.config(text="ID is exist")
            notifDef.show_grid(notif,error_other)
        else:
            return True
    except:
        notif.config(text='Error check ID', fg='red')
        notifDef.show_grid(notif,error_other)

def send(window,new_user_id,new_password,check_password,entry_name,entry_email,emailComboBox):
    notif1 = Label(window, text="", font=("Calibri", 11))
    notif4 = Label(window, text="", font=("Calibri", 11))
    notif8 = Label(window, text="", font=("Calibri", 11))
    try:
        error_id = 1
        error_pw = 4
        error_other = 8

        username = new_user_id.get()
        password = new_password.get()
        checkPassword = check_password.get()
        name = entry_name.get()
        email = entry_email.get()+"@"+emailComboBox.get()

        if username == "" or password == "" or checkPassword=="" or name == "" or entry_email.get() == "":
            notif4.config(text=' ' * 40)
            notifDef.show_grid(notif4, error_pw)
            notif8.config(text='All fields Required!!', fg='red')
            notifDef.show_grid(notif8,error_other)
            # return
        elif password != checkPassword:
            print("not correct password")
            notif4.config(text='check password again', fg='red')
            notifDef.show_grid(notif4,error_pw)
            notifDef.line_clear(notif8,error_other)
            # return
        else:
            notifDef.line_clear(notif4, error_pw)
            notifDef.line_clear(notif8, error_other)

            if id_check(notif8,username,error_other):
                data={"username":username,"password":password,"name":name,"email":email}
                json_data= json.dumps(data)
                response = requests.post(url, data=json_data)

    except:
        notif8.config(text='Error sending info', fg='red')
        notifDef.show_grid(notif8,error_other)

def makeAlram():
    if new_password.get() == check_password.get():
        print(new_password.get())
        print(check_password.get())
        msgbox.showinfo("Alram", "Your membership registration has been processed successfully.")
        window.destroy()
        window.update()
    else:
        msgbox.showwarning("Alram","check your password")

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
    ttk.Entry(window, textvariable = entry_name).grid(row = 5, column = 1, padx = 10, pady = 10)
    ttk.Entry(window, textvariable = entry_email).grid(row = 6, column = 1, padx = 10, pady = 10)


    #ttk.Button(window, text= "back").grid(row = 5, column = 0, padx = 10, pady = 10)
    ttk.Button(window, text = "가입", command = lambda : send(window,new_user_id,new_password,check_password,entry_name,entry_email,emailComboBox)).grid(row = 7, column = 1, padx = 10, pady = 10)

    window.mainloop()
