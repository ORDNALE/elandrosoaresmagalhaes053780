FROM ubuntu:latest
LABEL authors="ordnale"

ENTRYPOINT ["top", "-b"]