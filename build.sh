cd figs 
mpost -interaction=nonstopmode circnomeansym.mp 
mpost -interaction=nonstopmode rotatedpic.mp 
mpost -interaction=nonstopmode wrappeduniform_var0p08.mp 
mpost -interaction=nonstopmode VonMisesSumUnif_0p3.mp 
cd .. 

cd code/data 
mpost -interaction=nonstopmode directionestplot.mp 
mpost -interaction=nonstopmode delayestplot.mp 
mpost -interaction=nonstopmode sumdistplot.mp 
cd ../..

pdflatex papersm.tex 
bibtex papersm 
pdflatex papersm.tex 
pdflatex papersm.tex 
