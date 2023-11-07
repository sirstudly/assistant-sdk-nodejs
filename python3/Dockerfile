ARG PYTHON_VERSION
FROM python:${PYTHON_VERSION}

RUN apt-get update && apt-get -y --allow-unauthenticated upgrade
RUN wget https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.deb && apt -y install ./jdk-17_linux-x64_bin.deb

COPY jdk.sh /etc/profile.d/
RUN echo ". /etc/profile.d/jdk.sh\n" >> /root/.bashrc

# install google-oauth tool
RUN apt-get update \
    && apt-get install -y python3-pip npm \
    && pip3 install --upgrade pip \
    && pip3 install google-auth-oauthlib click

# install nodejs
RUN curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.33.11/install.sh | bash

RUN . ~/.nvm/nvm.sh \
    && nvm install 16.13.0

# TODO: can't build using stuff from a mapped drive so we'll need to run these commands manually inside the container
# docker exec -it google-assistant /bin/bash
# cd ~/google-assistant-grpc
# npm install

# TODO: missing DISPLAY; enable GUI access from host
# https://thespecguy.medium.com/launching-google-chrome-on-docker-container-a7dc2ba2d5
# https://medium.com/dot-debug/running-chrome-in-a-docker-container-a55e7f4da4a8
# https://leimao.github.io/blog/Docker-Container-GUI-Display/
# https://jayeshkumarjangir49.medium.com/launching-a-gui-software-on-docker-container-a3388796369e
# RUN google-oauthlib-tool --client-secrets credentials.json --credentials devicecredentials.json --scope https://www.googleapis.com/auth/assistant-sdk-prototype --scope https://www.googleapis.com/auth/gcm --save

# keep container running
ENTRYPOINT ["tail", "-f", "/dev/null"]