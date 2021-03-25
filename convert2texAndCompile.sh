#!/bin/zsh

echo "Conmpiling the latex with pdflatex:"
cd static
pdflatex -interaction=nonstopmode out.tex
echo "...Finished converting to latex"