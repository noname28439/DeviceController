from win10toast import ToastNotifier
import win32console
import win32gui
import time
from win32con import SW_SHOWNORMAL
import socket
from threading import Thread
import time

app = Flask(__name__)
toaster = ToastNotifier()

minimized = []

close = False

def trigger(header="Warning!", text="Der Sender wurde getriggert!"):
    print("Triggered!!!")
    global close
    if not close:

        toaster.show_toast(header,
                           text,
                           icon_path=None,
                           duration=10,
                           threaded=True)
    else:
        win = win32gui.GetForegroundWindow()
        minimized.append(win)
        win32gui.ShowWindow(win, 0)



def restore():
    for cw in minimized:
        win32gui.ShowWindow(cw, SW_SHOWNORMAL)
        minimized.remove(cw)


connection = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

def client_connect():
    global connection
    device_password = "SYHVsangj8kyQRTSfABA"
    device_name = "WarnServer"
    data_handeling_mode = -1

    connection.connect(("192.168.178.45", 13579))
    connection.send(("login:"+device_password+":"+device_name+":"+str(data_handeling_mode)+"\n").encode())

    lisetenerTarget = "Bewegungssensor2"

    time.sleep(0.2)
    connection.send(("trigger:"+lisetenerTarget+"\n").encode())
    connection.send(("trigger:"+"Bewegungssensor1"+"\n").encode())

    while True:
        data = str(connection.recv(1024).decode().replace("\n", ""))
        print("RECV: \"" +data +"\"")
        args = data.split(':')
        if args[0] == "triggered":
            name = args[1]
            triggerstate = int(args[2])
            if name == lisetenerTarget or True:
                if triggerstate == 1:
                    trigger(header=f"{name} was triggered!", text=f"{name}'s state: {triggerstate}")
                elif triggerstate==0:
                    print("Trigger released...")

if __name__ == '__main__':
    t = Thread(target=client_connect)
    t.daemon = True
    t.start()
    while True:
        rcv = input("-->")
        if rcv == "get":
            print("sending...")
            connection.send(("get:Bewegungssensor1\n").encode())
