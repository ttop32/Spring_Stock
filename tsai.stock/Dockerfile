FROM python:3.8-slim AS compile-image
COPY requirements.txt .
RUN pip install --user -r requirements.txt



FROM python:3.8-slim AS build-image
COPY --from=compile-image /root/.local /root/.local
ENV PATH=/root/.local/bin:$PATH

RUN mkdir -p /app/
ENV TZ="Asia/Seoul"
COPY ./run.py .
COPY ./stock_util.py .
COPY ./web_util.py .
COPY ./models/regression2.pkl . 
# ENTRYPOINT [ "python", "run.py"  ]


# # cron tab
RUN apt-get update && apt-get -y install cron
COPY cron /etc/cron.d/cron
RUN chmod 0755 /etc/cron.d/cron
RUN crontab /etc/cron.d/cron
ENTRYPOINT ["cron", "-f"]

