prologues := 2;
filenametemplate "%j-%c.mps";
verbatimtex
%&latex
\documentclass{minimal}
\begin{document}
etex

input graph;
input sarith;
picture ORIGINAL_PICTURE;
ORIGINAL_PICTURE := currentpicture;

picture dd; dd :=
dashpattern(on 4bp off 2bp on 1bp off 2bp);

picture diamond;
draw unitsquare shifted(-0.5,-0.5) scaled 0.17cm rotated 45;
diamond := currentpicture; currentpicture := ORIGINAL_PICTURE;

picture square;
draw unitsquare shifted(-0.5,-0.5) scaled 0.17cm;
square := currentpicture; currentpicture := ORIGINAL_PICTURE;

picture circle; 
draw fullcircle scaled 0.17cm;
circle := currentpicture; currentpicture := ORIGINAL_PICTURE;

picture cross; 
draw (-0.09cm,0.0)--(0.09cm,0.0);
draw (0,-0.09cm)--(0,0.09cm);
cross := currentpicture; currentpicture := ORIGINAL_PICTURE;

picture dot; 
fill fullcircle scaled 0.12cm;
dot := currentpicture; currentpicture := ORIGINAL_PICTURE;

picture times; times := cross rotated 45;

picture legend;
sx = 0.2cm;
sh = 0.2cm;
ups = 0.35cm;
label.rt(btex Angular least squares (theor.) etex, (1cm,sh));
draw (sx,sh)--(1cm,sh) dashed evenly;

label.rt(btex Angular least squares etex, (1cm,sh + ups));
draw circle shifted 1/2[(sx,sh + ups),(1cm,sh + ups)];

label.rt(btex Sample circular mean (theor.) etex, (1cm,sh + 2*ups));
draw (sx,sh + 2*ups)--(1cm,sh + 2*ups);

label.rt(btex Sample circular mean etex, (1cm,sh + 3*ups));
draw dot shifted 1/2[(sx,sh + 3*ups),(1cm,sh + 3*ups)];

legend := currentpicture; currentpicture := ORIGINAL_PICTURE;

plotheight = 12cm;
plotwidth = 9cm;

legendx = 3cm;

beginfig(1);
draw begingraph(plotwidth, plotheight);
 setcoords(log,log);
 glabel.bot(btex $\sigma^2$ etex, OUT);
 glabel.lft(btex MSE etex, OUT) rotated 90;
 
  setrange(whatever,"3.7e-7", whatever,0.05);

gdraw "AngularLeastSquaresEstimator_WrappedGaussian_4"  plot(circle);
gdraw "SampleCircularMean_WrappedGaussian_4"  plot(dot);

gdraw "AngularLeastSquaresEstimator_WrappedGaussian_64"  plot(circle);
gdraw "SampleCircularMean_WrappedGaussian_64"  plot(dot);

gdraw "AngularLeastSquaresEstimator_WrappedGaussian_1024"  plot(circle);
gdraw "SampleCircularMean_WrappedGaussian_1024"  plot(dot);

gdraw "clt_AngularLeastSquaresEstimator_WrappedGaussian_4"  dashed evenly; glabel.lrt(btex $N=4$ etex, 0);
 gdraw "clt_AngularLeastSquaresEstimator_WrappedGaussian_64"  dashed evenly;glabel.lrt(btex $N=64$ etex, 0);
gdraw "clt_AngularLeastSquaresEstimator_WrappedGaussian_1024"  dashed evenly;glabel.lrt(btex $N=1024$ etex, 0);

gdraw "clt_SampleCircularMean_WrappedGaussian_4" ;
gdraw "clt_SampleCircularMean_WrappedGaussian_64" ;
gdraw "clt_SampleCircularMean_WrappedGaussian_1024" ;

draw legend shifted(legendx,0.1cm);


endgraph ;

clip currentpicture to (-10cm,-10cm)--(-10cm,plotheight)--(plotwidth+5cm,plotheight)--(plotwidth+5cm,-10cm)--cycle

endfig;

end;
