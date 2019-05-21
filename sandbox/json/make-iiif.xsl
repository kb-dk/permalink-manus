<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:fn="http://www.w3.org/2005/xpath-functions"
		xmlns:m="http://www.loc.gov/METS/" 
		xmlns:xlink="http://www.w3.org/1999/xlink" 
		xmlns:md="http://www.loc.gov/mods/v3" 
		version="3.0">

  <xsl:output indent="yes" method="text"/>

  <xsl:template match="/">
    <xsl:variable name="iiif_document">
    <fn:map>
      <fn:string key="@context">http://iiif.io/api/presentation/2/context.json</fn:string>
      <fn:string key="@id">https://labs.kb.dk/fake/consolations/info.json</fn:string>
      <fn:string key="@type">sc:Manifest</fn:string>
      <fn:array  key="metadata">
	  <xsl:for-each select="//m:dmdSec[@ID='md-root']/m:mdWrap[@MDTYPE='MODS']/m:xmlData/md:mods">
	    <fn:map>
	      <xsl:for-each select="md:name[1]">
		<fn:string key="label">Shelfmark</fn:string>
		<fn:string key="value"><xsl:value-of select="md:partName"/></fn:string>
	      </xsl:for-each>
	    </fn:map>
	  </xsl:for-each>
      </fn:array>
      <!-- xsl:apply-templates select="//m:structMap[@type='physical']/m:div[@DMDID='md-root']"/ -->
    </fn:map>
    </xsl:variable>
    <xsl:apply-templates mode="serialize" select="$iiif_document"/>
  </xsl:template>

  <xsl:template mode="serialize" match="/">
    <xsl:value-of select="fn:xml-to-json(.)"/>
  </xsl:template>

</xsl:stylesheet>