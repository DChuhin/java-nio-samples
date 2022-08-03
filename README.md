# Java Blocking server and NIO server examples:
Repository contains 3 apps: 
* InternalServerApp: Regular web server, imitating fixed delay
* BlockingProxyApp: proxy server, written with blocking IO
* NioProxyApp: proxy server, written with NIO

All can be run as regular Java Apps. 

# Env vars
JAVA_THREADS - number of threads

INTERNAL_SERVER_HOST, INTERNAL_SERVER_PORT - where to proxy request

# How to access service
Services can be accessed as regular http server: curl, apache ab, postman, open in browser
