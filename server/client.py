import requests

url = "http://127.0.0.1:8000/login/"
data = {"username": "test", "password": "passwd","jwt":
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MjExNDExOTEsImVtYWlsIjoidGVzdCJ9._uYZdTNvoXtVfBivE9wB3aMeoYS4BWp8wXWCxxmV09Y"}
headers = {"Authorization": "token"}
response = requests.post(url, data=data)

print(response.text)