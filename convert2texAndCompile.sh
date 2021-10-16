#!/bin/zsh

#use  chmod +x <fileName> to get this script ready
#use sudo ./<fileName> to run this script

#for non-horizontal wording, rotate in finder, then try to quit, and choose 'save'.

echo "Compiling the latex with pdflatex:"
cd $1
pdflatex -interaction=nonstopmode $2
echo "...Finished converting to latex to a pdf"