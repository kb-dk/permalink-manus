#!/usr/bin/perl -w

#
# Client to the manus-api. It performs the following steps:
# 1. it retrieves a list of manus-identifiers using the scripts
#    in the get-<some application>-identifiers.jsp family
# 2. for each identifier it then retrieves mets metadata using
#    get-mets-metadata.jsp. The metadata is run through an XML-parser
#    both to be really sure that it works (paranoic reason), and
#    also for formatting the records with an XML beautifier.
#
# author: Sigfrid Lundberg (slu@kb.dk)
# 
# Last $Revision: 1.6 $ $Date: 2007/01/19 14:51:09 $ by $Author: slu $.
# Current checkout was made as build $Name:  $
#

use strict;
require LWP::UserAgent;
use XML::LibXML;
use File::Path;
use FileHandle;
use Getopt::Long;

my $application;
my $targetdir;
my $baseuri;

# my @applications = ('manus','mus','musman','lum');
my @applications = ('manus','mus');

my $result = GetOptions ("application=s"     => \$application,
                         "targetdir=s"       => \$targetdir,
			 "baseuri=s"         => \$baseuri);

if( !($application && $targetdir) ) {
    &error();
    exit(-1);
} else {
    if(join(',',@applications) !~ m/$application/) {
	&error();
	exit(-1);
    }
}


my $parser = new XML::LibXML;
my $ua  = LWP::UserAgent->new;

my $apiurlbase    = $baseuri ? $baseuri : 'http://localhost:8080/mets-api/api/';
my $repository    = $targetdir;
my $iduri         = join '/',$apiurlbase,'get-'.$application.'-identifiers.jsp';
my $metsscript    = join '/',$apiurlbase,'get-mets-metadata.jsp?app='.$application.'&doc=';

my $response = $ua->get($iduri);

open(LOG,">mets-retrieve-".$application.".log");
if($response->is_success) {
    my @identifiers = split /[\r\n]+/, $response->content();
    foreach my $id (@identifiers) {
	my $metsuri      = join '',$metsscript,$id;
	my $metsresponse = $ua->get($metsuri);

	if($metsresponse->is_success) {
	    print LOG "Successfully retrieved  $metsuri\n";
	    mkpath(join('/',($repository,$id)),0,0711);
	    my $filename = join '/',($repository,$id,'metsfile.xml');
	    my $menufile = join '/',($repository,$id,'menu.xml');
	    my $doc      = $parser->parse_string($metsresponse->content());
	    if(open(XML,">$filename")) {
		print LOG "Saving $filename\n";
		print XML $doc->toString(1);
		close XML;
#		sleep 2;
	    } else {
		print LOG "FATAL: Cannot open $filename: $!\n";
		die "cannot open $filename: $!\n";
	    }
	} else {
	    print LOG "failed $metsuri: " . $metsresponse->status_line . "\n";
	}
    }
} else {
    print LOG "failed $iduri: " . $response->status_line . "\n";
}

close LOG;

sub error {
    my $apps = join(',',@applications);
 print STDERR <<"MSG";

$0 --application <application abbreviation> --targetdir <directory>
where the options have the following meaning
\t--application\tshould take one of of the applications 
\t\t\t$apps as argument
\t--baseuri\tshould take the URI of a mets-api service as an argument
\t\t\tsuch as http://disdev-01.kb.dk:8081/mets-api/
\t--targetdir\tthe directory where to write the files

MSG

}
