FROM clojure
WORKDIR /usr/src/app
COPY project.clj /usr/src/app/
RUN lein deps
COPY . /usr/src/app

RUN lein cljsbuild once

ENTRYPOINT ["lein"]
CMD ["cljsbuild", "auto"]