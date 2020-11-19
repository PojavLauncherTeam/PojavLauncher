#!/bin/bash

THISDIR=`dirname $0`
LANGFILE=$THISDIR/../app/src/main/assets/language_list.txt

rm $LANGFILE
ls $THISDIR/../app/src/main/res/values-* >> $LANGFILE

