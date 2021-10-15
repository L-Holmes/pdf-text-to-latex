#!/bin/zsh

#use  chmod +x <fileName> to get this script ready
#use sudo ./<fileName> to run this script

#for non-horizontal wording, rotate in finder, then try to quit, and choose 'save'.

echo "Compiling the latex with pdflatex:"
cd static
#cd out
cd test
pdflatex -interaction=nonstopmode SCC361-Wk1-L1-TEST-out.tex
echo "...Finished converting to latex to a pdf"