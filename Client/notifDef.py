from tkinter import *
from tkinter import ttk as tk1
import importlib

path='/usr/share/fonts/truetype/nanum/NanumPen.ttf'

def empty_check(notif,blank_data,row):
    if(blank_data==""):
        print("write row ", row)
        notif.config(text='Write Your Info', fg='red')
        show_grid(notif, row)
    else:
        print("clear row ",row)
        notif.config(text=" "*30)
        show_grid(notif,row)

def stateAram(notif,row):
    notif.config(text="아이디/비밀번호를 다시 입력하세요",fg='red')
    show_grid(notif, row)

def failure_server_login(notif,row):
    notif.config(text="Server connection failure", fg= 'red')
    show_grid(notif,row)

def error_login_module(notif, row):
    notif.config(text="login error", fg='red')
    show_grid(notif, row)

def line_clear(notif,row):
    notif.config(text=" "*50)
    show_grid(notif, row)

def show_grid(notif,mg_loc):
    print("show_grid",mg_loc)
    notif.grid(row=mg_loc,sticky=S)
