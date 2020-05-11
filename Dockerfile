FROM clojure

LABEL "com.github.actions.name"="Repo Scanner"
LABEL "com.github.actions.description"="Runs validations against a GitHub Repository."
LABEL "com.github.actions.repository"="https://github.com/maoo/repo-scanner"
LABEL "com.github.actions.maintainer"="Maurizio Pillitu <maoo@finos.org>"
LABEL "com.github.actions.icon"="message-square"
LABEL "com.github.actions.color"="blue"

COPY . /opt/gos
WORKDIR /opt/gos

ENTRYPOINT ["/opt/gos/entrypoint.sh"]
