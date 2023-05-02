import BaseHTTPServer
import SimpleHTTPServer
import ssl

httpd = BaseHTTPServer.HTTPServer(('localhost', 1312), SimpleHTTPServer.SimpleHTTPRequestHandler)
httpd.socket = ssl.wrap_socket(httpd.socket, certfile='public.pem', keyfile='private.pem', server_side=True)
httpd.serve_forever()
