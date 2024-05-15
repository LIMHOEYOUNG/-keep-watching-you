from tkinter import *
from tkinter import ttk as tk1
import login as loginWin

"""
app_width=300
app_height=100
win_width=window.winfo_screenwidth()
win_height=window.winfo_screenheight()
center_width=(win_width/2)-(app_width/2)
center_height=(win_height/2)-(app_height/2)

#window.geometry("300x100+355+400")
window.geometry(f"{app_width}x{app_height}+{int(center_width)}+{int(center_height)}")
"""
class Application(Frame):
    def __init__(self, master=None):
        super().__init__(master)
        self.master = master
        self.create_widgets()
        self.initialize_variables()

    def create_widgets(self):
        self.master.geometry("300x100")
        self.master.title("Test")

        tk1.Button(self.master, text="Face", command=self.face_rec).grid(row=1, column=0, padx=30, pady=15)
        tk1.Button(self.master, text="Login", command=self.login).grid(row=1, column=1, padx=30, pady=15)
        tk1.Label(self.master, text="test").grid(row=2, column=0, columnspan=2)

    def initialize_variables(self):
        # joinUser.py에서 사용하는 변수
        self.new_user_id = StringVar()
        self.new_password = StringVar()
        self.check_password = StringVar()
        self.entry_name = StringVar()
        self.entry_email = StringVar()

        # login.py에서 사용하는 변수
        self.user_id = StringVar()
        self.password = StringVar()

    def login(self):
        try:
            status_check = loginWin.login_run(self.user_id, self.password)
            # if(status_check):
        except Exception as e:
            print("Error in login():", e)

    def face_rec(self):
        print("회원가입")
        # Implement face recognition functionality here

if __name__ == "__main__":
    root = Tk()
    app = Application(master=root)
    app.mainloop()