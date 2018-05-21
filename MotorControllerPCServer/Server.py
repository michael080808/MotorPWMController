# -*- coding:utf-8 -*-

import sys
import socket
import threading


def fun(sock, addr):
    try:
        while True:
            data = sock.recv(1024)
            if data == "quit" or data == "exit":
                print("Client %s exit." % addr[0])
                sock.close()
                break
            if data:
                print("Message from %s: %s" % (addr[0], data))
                sock.send(bytes("OK\r\n" % data, encoding='utf-8'))
    except socket.errno as e:
        print("Socket error: %s" % str(e))
    except Exception as e:
        print("Other exception: %s" % str(e))
    finally:
        sock.close()


def test_server(port):
    # 创建TCP套接字
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # 启用地址重用
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

    # 绑定地址和端口号
    srv_addr = ("0.0.0.0", port)
    sock.bind(srv_addr)

    # 侦听客户端
    sock.listen(5)

    try:
        while True:
            # 接受客户端连接
            conn, addr = sock.accept()
            t = threading.Thread(target=fun, args=(conn, addr))
            t.start()
    except KeyboardInterrupt as e:
        exit(0)


if __name__ == "__main__":
    if len(sys.argv) == 2:
        test_server(int(sys.argv[1]))
    else:
        print("Input error. Format: %s <Port>" % sys.argv[1])
