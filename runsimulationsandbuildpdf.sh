cd figs 
mpost -interaction=nonstopmode circnomeansym.mp 
mpost -interaction=nonstopmode rotatedpic.mp 
mpost -interaction=nonstopmode wrappeduniform_var0p08.mp 
mpost -interaction=nonstopmode VonMisesSumUnif_0p3.mp 
cd .. 

cd code
CP=""
for f in lib/*.jar
do
CP=$CP:${f}
done
scala -cp PubSim.jar:Jama-1.0.2.jar:flanagan.jar:colt.jar:RngPack.jar bearingsim.scala 
scala -cp PubSim.jar:Jama-1.0.2.jar:flanagan.jar:colt.jar:RngPack.jar clt.scala 
cd data
mpost -interaction=nonstopmode directionestplot.mp 
mpost -interaction=nonstopmode delayestplot.mp 
mpost -interaction=nonstopmode sumdistplot.mp 
cd ..
cd ..

latex papersm.tex 
bibtex papersm 
latex papersm.tex
latex papersm.tex
dvips papersm.ps
ps2pdf papersm.ps
