FROM ubuntu:16.04
RUN apt-get update
RUN apt-get -y install software-properties-common python-software-properties default-jre python-pip
RUN add-apt-repository -y ppa:hugin/hugin-builds
RUN apt-get update
RUN apt-get -y install hugin
RUN pip install Pillow

VOLUME /tmp

ADD ./target/pannellum-tour-maker-0.0.1-SNAPSHOT.jar tour-editor.jar
ADD ./generate.py /home/generate.py
RUN sh -c 'touch /tour-editor.jar'

ENTRYPOINT ["java", "-Dspring.profiles.active=default,production", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/tour-editor.jar"]
