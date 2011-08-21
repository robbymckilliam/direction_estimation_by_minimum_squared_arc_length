cd figs
mpost -interaction=nonstopmode circnomeansym.mp
mpost -interaction=nonstopmode rotatedpic.mp
mpost -interaction=nonstopmode wrappeduniform_var0p08.mp
mpost -interaction=nonstopmode VonMisesSumUnif_0p3.mp
cd ..

cd plots
scala -nocompdaemon -cp PubSim.jar:Jama-1.0.2.jar:flanagan.jar:colt.jar:RngPack.jar bearingsim.scala
scala -nocompdaemon -cp PubSim.jar:Jama-1.0.2.jar:flanagan.jar:colt.jar:RngPack.jar clt.scala
mpost -interaction=nonstopmode directionestplot.mp
mpost -interaction=nonstopmode delayestplot.mp
mpost -interaction=nonstopmode sumdistplot.mp
cd ..

latex papersm.tex
bibtex papersm
latex papersm.tex
latex papersm.tex
ps2pdf papersm.tex