<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:fn="http://www.w3.org/2005/xpath-functions"
		xmlns:m="http://www.loc.gov/METS/" 
		xmlns:xlink="http://www.w3.org/1999/xlink" 
		xmlns:md="http://www.loc.gov/mods/v3" 
		version="3.0">

  <xsl:output indent="yes" method="text"/>

  <xsl:template match="/">
    <xsl:apply-templates select="//m:structMap[@type='physical']/m:div[@DMDID='md-root']"/>
  </xsl:template>

  <xsl:template match="m:div">
    <xsl:for-each select="m:div">
      <xsl:variable name="div_id" select="@ID"/>
      <xsl:for-each select="m:fptr">
	<xsl:variable name="id" select="@FILEID"/>

	<xsl:variable name="fid">
	  <xsl:choose>
	    <xsl:when test="//m:smLink[@xlink:title='XL' and @xlink:from=$div_id]/@xlink:to">
	      <xsl:variable name="variant">
		<xsl:value-of select="//m:smLink[@xlink:title='XL' and @xlink:from=$div_id][1]/@xlink:to"/>
	      </xsl:variable>
	      <xsl:value-of select="//m:div[@ID=$variant]/m:fptr/@FILEID"/>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:choose>
		<xsl:when test="//m:smLink[(@xlink:title='Larger' or 
				@xlink:title='Større') and @xlink:from=$div_id][1]/@xlink:to">
		  <xsl:variable name="variant">
		    <xsl:value-of select="//m:smLink[(@xlink:title='Larger' or @xlink:title='Større') 
					  and @xlink:from=$div_id][1]/@xlink:to"/>
		  </xsl:variable>
		  <xsl:value-of select="//m:div[@ID=$variant]/m:fptr/@FILEID"/>
		</xsl:when>
		<xsl:otherwise>
		  <xsl:value-of select="$id"/>
		</xsl:otherwise>
	      </xsl:choose>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:variable>

<xsl:variable name="file"><xsl:value-of select="substring-after(//m:file[@ID=$fid]/m:FLocat/@xlink:href,'manus/')"/></xsl:variable>
<xsl:text># </xsl:text><xsl:value-of select="$id"/><xsl:text>
GET </xsl:text><xsl:value-of select="//m:file[@ID=$fid]/m:FLocat/@xlink:href"/><xsl:text> &gt; </xsl:text><xsl:value-of select="$file"/><xsl:text>
</xsl:text>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>


</xsl:stylesheet>