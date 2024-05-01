import os
import getpass

root_path="/home/"+getpass.getuser()+"/driverLog/"

def makeUsrdirs(path):
    if not os.path.exists(root_path):
        os.makedirs(root_path)
    else:
        print(root_path+path)
        if(not os.path.exists(root_path+path)):
            os.makedirs(root_path+path)

