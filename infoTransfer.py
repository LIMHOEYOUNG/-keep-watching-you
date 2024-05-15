# -- coding: utf-8 --
import requests
import json
import getpass

login_url="http://3.39.187.161:8000/login/"
join_url="http://3.39.187.161:8000/join_member/"
#root_user= getpass.getuser()
#file = open('/home/'+root_user+'/driverLog/shareData.txt', 'w')
file_path='/home/'+getpass.getuser()+'/driverLog/shareData.json'
folder_path='/home/'+getpass.getuser()+'/driverLog/'

def login_data(id,password):
    data = {"username": id, "password": password}
    response = requests.post(login_url, data=json.dumps(data))

    #received_data = json.loads(response)
    received_data = response.json()
    
    success= received_data.get('success')
    jwt_data = received_data.get('logintoken')

    if(success):
        file = open(file_path, 'w')
        data = {"username":id, "jwt": jwt_data}
        print(data)
        file.write(str(json.dumps(data)))
        #file.write(jwt_data+"\n")
        file.close()
        return success
    else:
        return success
    
def join_data(id,password,name,email):
    data={"username":id,"password":password,"name":name,"email":email}
    response = requests.post(join_url, data=json.dumps(data))

    """
    #received_data = json.loads(response)
    received_data=response.json()

    print(received_data)

    jwt_data = received_data.get('jwt')
                
    #jwt 토큰 작성 txt파일 생성
    file = open(file_path, 'w')
    file.write(jwt_data+"\n")
    file.close()
    print("종료\n")
    """
def id_double_check(id):
    data={"username": id}
    response = requests.post(join_url, data=json.dumps(data))

    return response

                

