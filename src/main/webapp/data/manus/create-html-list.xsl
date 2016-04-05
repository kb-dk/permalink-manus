<?xml version="1.0" encoding="UTF-8" ?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	       version="1.0">

  <xsl:output method="html"
	      encoding="UTF-8"/>

  <xsl:param name="openid" select="'This is a test'"/>

  <xsl:template match="/">
    <xsl:apply-templates select="list"/>
  </xsl:template>

  <xsl:template match="/list">
    <ul>
      <xsl:apply-templates select="item"/>
    </ul>
  </xsl:template>

 <xsl:template match="item/list">
   <xsl:param name="listid"><xsl:value-of select="$listid"/></xsl:param>
    <xsl:element name="ul">
      <xsl:attribute name="id"><xsl:value-of select="$listid"/></xsl:attribute>
      <xsl:attribute name="style">
	<xsl:text>display:none; list-style-type:none</xsl:text>
      </xsl:attribute>
      <xsl:apply-templates select="item"/>
    </xsl:element>
  </xsl:template>


  <xsl:template match="item">
    <xsl:param name="listid" select="generate-id()"/>
    <li>
      <xsl:attribute name="id">
	<xsl:value-of select="@id"/>
      </xsl:attribute>
      <xsl:if test="list">
	<xsl:element name="a">
	  <xsl:attribute name="title">Klicka för att öppna sektion</xsl:attribute>
	  <xsl:attribute name="onClick">
	    <xsl:text>openDisplay('</xsl:text><xsl:value-of select="$listid"/><xsl:text>')</xsl:text>
	  </xsl:attribute>
	  +
	</xsl:element>
      </xsl:if>

      <xsl:element name="a">
	<xsl:attribute name="href">
	  <xsl:value-of select="
	concat('http://udvikling.kb.dk:7777/hsk-mets-test/navigate-simple.jsp?id=',
	@id,
	'&amp;doc=41&amp;lang=dan')"/>
	</xsl:attribute>
	<xsl:choose>
	  <xsl:when test="title/@label">
	    <xsl:value-of select="title/@label"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:value-of select="title/@orderlabel"/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:element>
      <xsl:apply-templates select="list">
	<xsl:with-param name="listid" select="$listid"/>
      </xsl:apply-templates>
    </li>

  </xsl:template>




</xsl:transform>