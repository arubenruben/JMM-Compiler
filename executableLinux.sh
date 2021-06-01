#!/usr/bin/env bash

# Absolute path to this script, e.g. /home/user/bin/foo.sh
SCRIPT=$(readlink -f "$0")

# Absolute path this script is in, thus /home/user/bin
SCRIPTPATH=$(dirname "$SCRIPT")

java -cp "$SCRIPTPATH/comp2021-2a.jar:$SCRIPTPATH/libs/utils.jar:$SCRIPTPATH/libs/gson-2.8.2.jar:$SCRIPTPATH/libs/ollir.jar:$SCRIPTPATH/libs/jasmin.jar" Main "$@"
