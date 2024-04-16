import requests
import json

url = "http://3.39.187.161:8000/join_member/"
#url = "http://127.0.0.1:8000/join_member/"
data = {
    'username': 'test1',
    'password': 'paa',
    'email':'ema'
   }
json_data = json.dumps(data)
#headers = {"Authorization": "token"}
#response = requests.post(url, data=data)
try:
    # POST 요청을 보냅니다.
    response = requests.post(url, data=json_data)
    data = response.json()
    print(data)
    # 서버로부터 받은 응답을 출력합니다.
    print("응답 코드:", response.status_code)
    print("응답 내용:", response.text)
except requests.exceptions.RequestException as e:
    # 요청이 실패한 경우 예외를 처리합니다.
    print("요청 실패:", e)


