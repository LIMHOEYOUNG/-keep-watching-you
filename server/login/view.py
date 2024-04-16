from django.shortcuts import render

# Create your views here.
from django.http import JsonResponse
import jwt_app
import json
#from restapp.models import *
from login_user.models import *
from django.views.decorators.csrf import csrf_exempt
from rest_framework import viewsets
from login_user.serializers import MyModelSerializer


class MyModelViewSet(viewsets.ModelViewSet):
    queryset = Driveuser.objects.all()
    serializer_class = MyModelSerializer

@csrf_exempt
def main(request):
    if request.method == "GET":
        return render(request, 'users/main.html')

    elif request.method == 'POST':
        # POST 요청에서의 데이터는 request.POST 딕셔너리에 저장됩니다.
        received_data = json.loads(request.body)
        username = received_data.get('username')
        password = received_data.get('password')
        userjwt = None

        userjwt = received_data.get('jwt')

        # 데이터 처리 로직을 여기에 작성합니다.
        # 예를 들어, 받은 데이터를 출력하고 응답을 반환할 수 있습니다.
        #print("Received data:", received_data)
        if(userjwt != None):
            x=jwt_app.validate_token(userjwt, "tkpo")
            print(x["email"])
            resp_data={
                'success': 1,
                'logintoken': None
            }
            return JsonResponse(resp_data)
        else:
            user0=Driveuser.objects.filter(id=username)
            if user0.exists():
                if(username == user0[0].id and password == user0[0].passwd):
                    token= jwt_app.create_token("tkpo", username)
                    resp_data={
                        'success': 1,
                        'logintoken': token
                    }
                    return JsonResponse(resp_data)
                else:
                    resp_data={
                        'success': 0,
                        'logintoken': None
                    }
                    return JsonResponse(resp_data)
            else:
                resp_data={
                    'success': 0,
                    'logintoken': None
                }
                return JsonResponse(resp_data)
    else:
        # POST 요청이 아닌 경우에는 오류 응답을 반환합니다.
        return JsonResponse({'error': 'POST 요청이 필요합니다.'}, status=400)

