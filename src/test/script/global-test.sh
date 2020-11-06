#! /bin/sh

cd "$(dirname "$0")"/../../.. || exit 1

PATH1=./src/test/script/

"$PATH1"basic-test.sh
"$PATH1"common-tests.sh
