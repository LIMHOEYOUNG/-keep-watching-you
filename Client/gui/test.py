import tkinter as tk


def click():
    button.configure(text=f"{name.get()}님 안녕하세요?")


win = tk.Tk()

name = tk.StringVar()

tk.Label(win, text="이름을 입력한 후 버튼을 클릭하세요.").grid(column=0, row=0)

entry = tk.Entry(win, textvariable=name)
entry.grid(column=0, row=1)

button = tk.Button(text="클릭", command=click)
button.grid(column=0, row=2)

win.mainloop()
