<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:fn="http://www.w3.org/2005/xpath-functions"
		version="3.0">

  <xsl:output indent="yes" method="text"/>

  <xsl:template match="/test">
    <xsl:variable name="parse_tree">
      <fn:array key="test">
	<fn:map>
	  <xsl:for-each select="/test/*">
	    <fn:string key="{local-name(.)}"><xsl:apply-templates /></fn:string>
	  </xsl:for-each>
	</fn:map>
      </fn:array>
    </xsl:variable>
    <xsl:apply-templates mode="serialize" select="$parse_tree"/>
  </xsl:template>

  <xsl:template mode="serialize" match="/">
    <xsl:value-of select="fn:xml-to-json(.)"/>
  </xsl:template>

</xsl:stylesheet>