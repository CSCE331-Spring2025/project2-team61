#!/bin/bash

if [ $# -eq 0 ]; then
    echo "$0: Usage: $0 file1.sql file2.sql ..."
    exit 1
fi

psql_cmd="psql -h csce-315-db.engr.tamu.edu -U team_61 -d team_61_db"

for arg in "$@"; do
    sql_script_path=$(find . -name $(basename "${arg}"))

    if [ -z "${sql_script_path}" ]; then
        echo "$0: error: $1 file not found"
        exit 1
    fi

    psql_cmd+=" -f ${sql_script_path}"
done

eval "$psql_cmd"
