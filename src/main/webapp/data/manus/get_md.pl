#!/usr/bin/perl -w

use strict;

my $saxon="java -jar /home/slu/saxon/saxon9he.jar --suppressXsltNamespaceCheck:on  ";


if(open(my $metsfiles,"find . -name 'metsfile.xml' -print |")) {

      while(my $file = <$metsfiles>) {
	       chomp $file;
	       my $data = `$saxon -xsl:extract.xsl -s:$file file=$file`;
	       chomp $data;
	       print "$data\n";
	   }

  }



