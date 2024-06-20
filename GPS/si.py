import subprocess

def run():

    password = 'asd6218'  # 실제 비밀번호로 교체해야 합니다

    # su 명령어 실행을 위한 명령어 리스트
    su_command = ['su', '-c', 'chmod 666 /dev/ttyACM0']
    try:
        # subprocess.Popen을 사용하여 su 명령어 실행
        process = subprocess.Popen(su_command, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        output, error = process.communicate(input=password.encode())  # su 명령어 실행 중 비밀번호 입력

        if process.returncode == 0:
            print("/dev/ttyACM0 권한 변경 성공.")
        else:
            print(f"/dev/ttyACM0 권한 병경 실패. Error: {error.decode()}")
    except Exception as e:
        print(f"Error executing su command: {e}")
    

    #device_path='/dev/ttyACM0'
    #command=['sudo','chmod','666',device_path]
    #subprocess.run(command, check=True)
    
    # gps.c를 컴파일하는 명령어
    #compile_command = "gcc -o a severSend.c -L/usr/lib/x86_64-linux-gnu -lcurl"
    #compile_command = "gcc -o test0620 main.c -L/usr/lib/x86_64-linux-gnu -lcurl"
    compile_command = "gcc -o demo main.c cJSON.c -lm -lcurl"

    # 컴파일 명령어 실행
    subprocess.run(compile_command, shell=True, check=True)

    # 컴파일된 프로그램 실행
    subprocess.run(["./demo"], check=True)


