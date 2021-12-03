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
  
  <xsl:param name="language1"
             select="//m:dmdSec[@ID='md-root']/m:mdWrap[@MDTYPE='MODS']/m:xmlData/md:mods/md:titleInfo[1]/@xml:lang"/>
  <xsl:param name="manus_number" select="substring-after(/m:mets/@OBJID,'manus:')"/>
  
  <xsl:template match="/m:mets">
    <xsl:for-each select="m:dmdSec[@ID='md-root']/m:mdWrap[@MDTYPE='MODS']/m:xmlData/md:mods">
    "<xsl:value-of select="$file" />","<xsl:apply-templates  select="md:titleInfo"/>","<xsl:apply-templates select="md:name"/>","<xsl:call-template name="permalink"/>"<xsl:text>
</xsl:text></xsl:for-each>

  </xsl:template>


  <xsl:template match="md:titleInfo"><xsl:value-of select="concat(@xml:lang,'=')"/><xsl:value-of select="normalize-space(md:title)"/><xsl:text> </xsl:text></xsl:template>

  <xsl:template match="md:name"><xsl:value-of select="concat(@xml:lang,'=')"/><xsl:value-of select="normalize-space(md:partName)"/><xsl:text> </xsl:text></xsl:template>

  <xsl:template name="permalink"><xsl:text>http://www5.kb.dk/permalink/2006/manus/</xsl:text><xsl:value-of select="$manus_number"/><xsl:text>/</xsl:text><xsl:value-of select="$language1"/>/</xsl:template>
  
</xsl:transform>
  

