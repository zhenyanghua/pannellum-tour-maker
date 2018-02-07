FROM ubuntu:16.04
RUN apt-get update
RUN apt-get -y install software-properties-common python-software-properties default-jre python-pip
RUN apt-get -y install netcat
RUN add-apt-repository -y ppa:hugin/hugin-builds
RUN apt-get update
RUN apt-get -y install hugin
RUN pip install Pillow

VOLUME /tmp

ADD ./target/pannellum-tour-maker-0.0.7-SNAPSHOT.jar tour-editor.jar
ADD ./generate.py /home/generate.py
RUN sh -c 'touch /tour-editor.jar'

ADD run.sh run.sh
RUN chmod +x run.sh
CMD ./run.sh
