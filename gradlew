#!/usr/bin/env bash

##############################################################################
##
##  Gradle startup script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
while [ -h "$PRG" ]; do
    ls=$(ls -ld "$PRG")
    link=$(expr "$ls" : '.*-> \(.*\)$')
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=$(dirname "$PRG")"/$link"
    fi
done
SAVED="$(cd "$(dirname "$PRG")" && pwd)"
cd "$SAVED" || exit 1
APP_HOME=$(pwd -P)
cd "$OLDPWD" || exit 1

APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")

# Add default JVM options
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Use the maximum available, or set to 1 if that fails.
MAX_FD=$(ulimit -H -n) ||
    MAX_FD=$(getconf LIM_NOFILE) ||
    MAX_FD=1048576

if ! expr $MAX_FD : '-\?[0-9]\+$' > /dev/null; then
    MAX_FD=1048576
fi

ulimit -n $MAX_FD || die "Could not set maximum file descriptor limit to $MAX_FD"

# Execute Gradle
exec "$JAVACMD" $DEFAULT_JVM_OPTS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
