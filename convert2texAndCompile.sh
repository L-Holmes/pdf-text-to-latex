#!/bin/zsh

#use  chmod +x <fileName> to get this script ready
#use sudo ./<fileName> to run this script

#for non-horizontal wording, rotate in finder, then try to quit, and choose 'save'.

echo "Conmpiling the latex with pdflatex:"
cd static
#cd out
cd year3_lecture_slides
cd SCC361
cd out
pdflatex -interaction=nonstopmode SCC361-Wk1-L1-out.tex
echo "...Finished converting to latex to a pdf"