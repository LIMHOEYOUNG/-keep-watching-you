import json

def parsing(data):
    exportData= json.loads(data)
    with open('/home/jetson/usr/jwtValue.txt','wb') as file:
        file.write(data["logintoken"])
        file.close()

def user_pass(data):
    exportData = json.loads(data)
    return (data['success'])

