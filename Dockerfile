FROM ubuntu:16.04
RUN apt-get update
RUN apt-get -y install software-properties-common python-software-properties debconf-utils
RUN add-apt-repository -y ppa:hugin/hugin-builds
RUN add-apt-repository -y ppa:webupd8team/java
RUN apt-get update
RUN apt-get -y install hugin python-pip
RUN pip install Pillow
RUN echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | debconf-set-selections
RUN apt-get -y install  oracle-java8-installer
#RUN apt-get -y install maven
#RUN mvn clean package

VOLUME /tmp

ADD ./target/pannellum-tour-maker-0.0.1-SNAPSHOT.jar tour-editor.jar
ADD ./generate.py /home/generate.py
RUN sh -c 'touch /tour-editor.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/tour-editor.jar"]
