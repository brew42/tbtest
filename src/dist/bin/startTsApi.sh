#!/bin/bash

#
# Author  : Tom Brewster
# Desc    : Launch TimeSeries API
#
SCRIPT=$0
SCRIPT_NAME=$(basename $SCRIPT)
SCRIPT_DIR=$(dirname $SCRIPT)
TIMESERIES_HOME=$(cd ${SCRIPT_DIR}/..; pwd)
CONFIG_DIR=${TIMESERIES_HOME}/conf
BIN_DIR=${TIMESERIES_HOME}/bin
LOG_DIR=${TIMESERIES_HOME}/logs
LIB_DIR=${TIMESERIES_HOME}/lib
PROFILE="$1"
PORT="$2"
LOG_LEVEL="$3"
#######################
# Available log levels
# FATAL
# ERROR
# WARN
# INFO
# DEBUG
# TRACE
#######################

####################################################################################
# Main function
####################################################################################
main() {
    cd $TIMESERIES_HOME
    
    # Run the script to set up the environment variables for the application
    createLogDirectory
    
    echo "${0##*/} START : $(date +%Y/%m/%d-%T)" | tee -a ${STDOUT_NAME}
    
    processPid=
    closeDownProcess() {
        if [ "$processPid" != "" ]; then
            kill $processPid
        fi
    }
    trap 'closeDownProcess' SIGTERM SIGINT
    
    
    exec java -jar -Dspring.profiles.active="$PROFILE" -Dserver.port="$PORT" -Xmx1024m -Xms256m $LIB_DIR/timeseriesapi*.jar --"$LOG_LEVEL"

	processPid=$!
    updatePidFile $processPid

    wait $pid

    echo "${0##*/} STOP  : $(date +%Y/%m/%d-%T)" | tee -a ${STDOUT_NAME}
    echo " "

}

####################################################################################
# Update pid file if directory exists
####################################################################################
updatePidFile() {
    pid=$1

    if [ -d /var/run ]; then
        echo $pid > /var/run/timeSeriesAPI.pid
    fi
}

####################################################################################
# Create Log Directory and simlink
####################################################################################
createLogDirectory() {
    if [ ! -e $LOG_DIR ]; then
        if [ -d /var/log ]; then
            mkdir -p /var/log/anaeko/timeseriesapi$PROFILE
            ln -s /var/log/anaeko/timeseriesapi$PROFILE $LOG_DIR
        else
            mkdir -p $LOG_DIR
        fi
    fi
}

main $*