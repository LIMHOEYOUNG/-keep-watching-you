import requests
import json

url = "http://3.39.187.161:8000/user_model/model_get"
#url = "http://127.0.0.1:8000/user_unknown/unknown_get"
data = {
    'jwt': 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MjExNDExOTEsImVtYWlsIjoidGVzdCJ9._uYZdTNvoXtVfBivE9wB3aMeoYS4BWp8wXWCxxmV09Y'
}
json_data = json.dumps(data)
#headers = {"Authorization": "token"}
response = requests.post(url, data=json_data)
try:
    if response.status_code == 200:
        # 응답이 성공적이면 이미지를 파일로 저장합니다.
        with open('downloaded_image.jpg', 'wb') as f:
            f.write(response.content)
            print("성공적으로 저장되었습니다.")
    else:
        print("응답 코드:", response.status_code)
        print("응답 내용:", response.text)

except requests.exceptions.RequestException as e:
    # 요청이 실패한 경우 예외를 처리합니다.
    print("요청 실패:", e)