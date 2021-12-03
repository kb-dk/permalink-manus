<?xml version="1.0"  encoding="UTF-8" ?>
<xsl:transform
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:m="http://www.loc.gov/METS/"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:md="http://www.loc.gov/mods/v3"
    version="3">

  <xsl:output method="text" encoding="UTF-8"/>

  <xsl:param name="file" select="''"/>

  <xsl:template match="/m:mets">
    <xsl:for-each select="m:dmdSec[@ID='md-root']/m:mdWrap[@MDTYPE='MODS']/m:xmlData/md:mods">
    "<xsl:value-of select="$file" />","<xsl:apply-templates  select="md:titleInfo"/>","<xsl:apply-templates select="md:name"/>"<xsl:text>
</xsl:text></xsl:for-each>

  </xsl:template>


  <xsl:template match="md:titleInfo"><xsl:value-of select="concat(@xml:lang,'=')"/><xsl:value-of select="normalize-space(md:title)"/><xsl:text> </xsl:text></xsl:template>

  <xsl:template match="md:name"><xsl:value-of select="concat(@xml:lang,'=')"/><xsl:value-of select="normalize-space(md:partName)"/><xsl:text> </xsl:text></xsl:template>

  
</xsl:transform>
  

