FROM java

ADD build/install/gradle-frontend-boilerplate /usr/local/src

ENTRYPOINT ["sh", "-c", "/usr/local/src/bin/gradle-frontend-boilerplate"]
