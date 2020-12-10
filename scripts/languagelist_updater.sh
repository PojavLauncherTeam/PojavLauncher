#!/bin/bash

THISDIR=`dirname $0`
LANGFILE=$THISDIR/../app/src/main/assets/language_list.txt

rm -f $LANGFILE
echo $THISDIR/../app/src/main/res/values-* | xargs -- basename -a > $LANGFILE

