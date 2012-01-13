cd figs
mpost -interaction=nonstopmode circnomeansym.mp
mpost -interaction=nonstopmode rotatedpic.mp
mpost -interaction=nonstopmode wrappeduniform_var0p08.mp
mpost -interaction=nonstopmode VonMisesSumUnif_0p3.mp
cd ..

cd plots
mpost -interaction=nonstopmode directionestplot.mp
mpost -interaction=nonstopmode delayestplot.mp
mpost -interaction=nonstopmode sumdistplot.mp
cd ..

latex papersm.tex
bibtex papersm
latex papersm.tex
latex papersm.tex
dvips papersm.dvi
ps2pdf papersm.ps
