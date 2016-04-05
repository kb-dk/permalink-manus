<?xml version="1.0" encoding="UTF-8" ?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	       xmlns:m="http://www.loc.gov/METS/"
	       xmlns:mds="http://www.loc.gov/mods/v3" 
	       xmlns:xlink="http://www.w3.org/1999/xlink" 
	       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	       xmlns:exsl="http://exslt.org/common"
	       version="1.0">

  <xsl:output method="xml"
	      encoding="UTF-8"
	      indent="yes"/>

  <xsl:template match="/">
    <xsl:apply-templates select="m:mets/m:structMap[@type='logical']"/>
  </xsl:template>

  <xsl:template match="m:structMap">
    <xsl:apply-templates select="m:div"/>
  </xsl:template>

  <xsl:template match="m:div">
    <xsl:element name="list">
      <xsl:for-each select="m:div[@LABEL]">
	<xsl:element name="item">
	  <xsl:attribute name="id">
	    <xsl:value-of select="@ID"/>
	  </xsl:attribute>
	  <xsl:call-template name="get-title"/>
	  <xsl:variable name="myid" select="@ID"/>
	  <xsl:if test="following-sibling::m:div[1][not(@LABEL)]">
	    <xsl:element name="list">
	      <xsl:for-each select="following-sibling::m:div[not(@LABEL)][preceding-sibling::m:div[@LABEL][1]/@ID=$myid]">
		<xsl:element name="item">
		  <xsl:attribute name="id">
		    <xsl:value-of select="@ID"/>
		  </xsl:attribute>
		  <xsl:call-template name="get-title"/>
		</xsl:element>
	      </xsl:for-each>
	    </xsl:element>
	  </xsl:if>
	</xsl:element>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <xsl:template name="get-title">
    <xsl:element name="title">
      <xsl:attribute name="lang">
	<xsl:value-of select="@xml:lang"/>
      </xsl:attribute>
      <xsl:attribute name="orderlabel">
	<xsl:value-of select="@ORDERLABEL"/>
      </xsl:attribute>
      <xsl:if test="@LABEL">
	<xsl:attribute name="label">
	  <xsl:value-of select="@LABEL"/>
	</xsl:attribute>
      </xsl:if>
    </xsl:element>
  </xsl:template>

</xsl:transform>