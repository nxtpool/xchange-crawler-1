FROM maven

MAINTAINER Andrey Lobarev <nxtpool@gmail.com>

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

COPY . /usr/src/app

RUN git submodule init
RUN git submodule update
RUN mvn install
WORKDIR crawler

CMD [ "mvn", "exec:java" ]
