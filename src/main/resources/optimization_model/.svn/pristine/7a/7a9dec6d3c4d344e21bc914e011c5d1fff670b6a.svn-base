#!/bin/sh
# Nicola Calcavecchia
# This script removes data created by previous experiments and 
# creates the new dot file and the gif associated with it
#
echo "Removing old files"
rm -f graph.dot
rm -f graph.gif

sed '/^$/d' model_output > tt 
mv tt model_output

echo "Generating dot file"
python scripts/generate_dot_file.py

echo "Creating graph.png"
dot -Tgif graph.dot > graph.gif

echo "Opening graph.png"
eog graph.gif &

#open graph.png

