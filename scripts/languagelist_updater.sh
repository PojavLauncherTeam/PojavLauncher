#!/bin/bash

THISDIR = `dirname $0`
LANGFILE = $THISDIR/../app/src/main/assets/language_list.txt

rm $LANGFILE
cd $THISDIR/../app/src/main/res
ls | grep values-* >> $LANGFILE

