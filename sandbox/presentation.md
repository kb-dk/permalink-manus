# What I actuallly did

## Worked with one manuscript

I wanted return to [CODICES LATINI HAUNIENSES](http://www.kb.dk/en/nb/materialer/haandskrifter/HA/e-mss/clh.html), both for lookiing at the data I worked with for more than ten years ago, and hopefully demonstrate that they can still be useful.

I opted for the only book I've actually read, namely Boethius [De Consolatione Philosophiae](https://en.wikipedia.org/wiki/The_Consolation_of_Philosophy). Boethius wrote wrote it AD 523 while awaiting trial and execution. The manus version oof it is on http://www.kb.dk/permalink/2006/manus/642/eng/

## Made a quick and dirty migration 


[Wrote a transform METS to Bash](https://github.com/Det-Kongelige-Bibliotek/permalink-manus/blob/iiif_presentation/sandbox/images/extract_img_info.xsl) creating a shell script that retrieves all the images in a 
manus document, transforms them to tiff and from that to jp2000.

The jp2000 files were installed on IIP Image

