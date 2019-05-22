<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:fn="http://www.w3.org/2005/xpath-functions"
		xmlns:m="http://www.loc.gov/METS/" 
		xmlns:xlink="http://www.w3.org/1999/xlink" 
		xmlns:md="http://www.loc.gov/mods/v3" 
		version="3.0">

  <xsl:output indent="yes" method="text"/>

  <xsl:template match="/">
    <xsl:variable name="iiif_document">
    <xsl:variable name="mets"><xsl:copy-of select="."/></xsl:variable>
    <fn:map>

      <fn:string key="@context">http://iiif.io/api/presentation/2/context.json</fn:string>
      <fn:string key="@id">https://raw.githubusercontent.com/Det-Kongelige-Bibliotek/permalink-manus/iiif_presentation/sandbox/specimen.json</fn:string>
      <fn:string key="@type">sc:Manifest</fn:string>

      <xsl:for-each select="//m:dmdSec[@ID='md-root']/m:mdWrap[@MDTYPE='MODS']/m:xmlData/md:mods">
	<fn:array  key="metadata">
	  <xsl:for-each select="md:name[md:role/md:roleTerm='author'][1]">
	    <fn:map>
		<fn:string key="label">Shelfmark</fn:string>
		<fn:string key="value"><xsl:value-of select="md:partName"/></fn:string>
	    </fn:map>
	  </xsl:for-each>
	  <fn:map>
	    <fn:string key="label">title</fn:string>
	    <fn:array key="value">
	      <xsl:for-each select="md:titleInfo">
		<fn:map>
		  <fn:string key="@value"><xsl:value-of select="md:title"/></fn:string>
		  <fn:string key="@language"><xsl:value-of select="@xml:lang"/></fn:string>
		</fn:map>
	      </xsl:for-each>
	    </fn:array>
	  </fn:map>
	</fn:array>

      <xsl:for-each select="md:name[md:role/md:roleTerm='author'][1]">
	<fn:string key="label"><xsl:value-of select="md:partName"/></fn:string>
      </xsl:for-each>

      <fn:array key="description">
	<xsl:for-each select="md:note[@type='presentation']">
	  <xsl:variable name="lang" select="@xml:lang"/>
	    <xsl:for-each select="div/p">
	    <fn:map>
	      <fn:string key="value"><xsl:value-of select="."/></fn:string>
	      <fn:string key="language"><xsl:value-of select="$lang"/></fn:string>
	    </fn:map>
	    </xsl:for-each>

	</xsl:for-each>
	  </fn:array>
      </xsl:for-each>
      <!-- obviously, we should select these in a more clever ways. -->
      <fn:map key="thumbnail">
	<fn:string key="@id">http://kb-images.kb.dk/public/Manus/gks1911/gks1911_006x/full/!225,/0/native.jpg</fn:string>
	<fn:string key="@type">dctypes:Image</fn:string>
	<fn:string key="format">image/jpeg</fn:string>
      </fn:map>

      <fn:string key="viewingDirection">left-to-right</fn:string>
      <fn:string key="viewingHint">paged</fn:string>
      <fn:string key="attribution">Det Kgl. Bibliotek</fn:string>
      <fn:array key="sequences">
	<xsl:apply-templates select="//m:structMap[@type='physical']">
	  <xsl:with-param name="mets" select="$mets"/>
	</xsl:apply-templates>
      </fn:array>
      <!-- xsl:apply-templates select="//m:structMap[@type='physical']/m:div[@DMDID='md-root']"/ -->
    </fn:map>



    </xsl:variable>
    <xsl:apply-templates mode="serialize" select="$iiif_document"/>
  </xsl:template>

  <xsl:template mode="serialize" match="/">
    <xsl:value-of select="fn:xml-to-json(.)"/>
  </xsl:template>

  <xsl:template match="m:structMap[@type='physical']">
    <xsl:param name="mets"/>
    <xsl:apply-templates select="m:div[@DMDID='md-root']">
      <xsl:with-param name="mets" select="$mets"/>
    </xsl:apply-templates>

  </xsl:template>

  <xsl:template match="m:div[@DMDID='md-root']">
    <xsl:param name="mets"/>
    <fn:map>
      <fn:string key="@type">sc:Sequence</fn:string>
      <fn:array key="canvases">
	<xsl:for-each select="distinct-values(m:div[@LABEL]/@ID)">
	  <xsl:variable name="id" select="."/>
	  <fn:map>
	    <fn:string key="@id">
	      <xsl:call-template name="get_uri">
		<xsl:with-param name="div_id" select="$id"/>
		<xsl:with-param name="mets" select="$mets"/>
	      </xsl:call-template>
	    </fn:string>
	    <fn:string key="@type">sc:Canvas</fn:string>
	    <fn:array key="label">
	      <xsl:for-each select="$mets//m:div[@ID=$id]">
		<fn:map>
		  <fn:string key="value"><xsl:value-of select="@LABEL"/></fn:string>
		  <fn:string key="language"><xsl:value-of select="@xml:lang"/></fn:string>
		</fn:map>
	      </xsl:for-each>
	    </fn:array>
	    <fn:array key="images">
	      <xsl:for-each select="distinct-values($mets//m:div[preceding-sibling::m:div[@LABEL]/@ID=$id]/@ID)">
		<fn:map>
		  <xsl:variable name="image_id" select="."/>
		  <fn:string key="@id"><xsl:value-of select="$image_id"/></fn:string>
		  <fn:array key="label">
		    <xsl:for-each select="$mets//m:div[@ID=$id]">
		      <fn:map>
			<fn:string key="value"><xsl:value-of select="@ORDERLABEL"/></fn:string>
			<fn:string key="language"><xsl:value-of select="@xml:lang"/></fn:string>
		      </fn:map>
		    </xsl:for-each>
		  </fn:array>
		  <fn:map key="resource">
		    <fn:string key="@id">
		      <xsl:call-template name="get_uri">
			<xsl:with-param name="div_id" select="$image_id"/>
			<xsl:with-param name="mets" select="$mets"/>
		      </xsl:call-template>
		    </fn:string>
		  </fn:map>
		</fn:map>
	      </xsl:for-each>
	    </fn:array>
	  </fn:map>
	</xsl:for-each>
      </fn:array>
    </fn:map>
    <xsl:message><xsl:value-of select="$mets/local-name(.)"/></xsl:message>
  </xsl:template>

  <xsl:template name="get_uri">
    <xsl:param name="div_id"/>
    <xsl:param name="mets"/>

    <xsl:variable name="id"> 
      <xsl:value-of select="$mets//m:div[@ID=$div_id][1]/m:fptr/@FILEID"/>
    </xsl:variable>

    <xsl:variable name="fid">
      <xsl:choose>
	<xsl:when test="$mets//m:smLink[@xlink:title='XL' and @xlink:from=$div_id]/@xlink:to">
	  <xsl:variable name="variant">
	    <xsl:value-of select="$mets//m:smLink[@xlink:title='XL' and @xlink:from=$div_id][1]/@xlink:to"/>
	  </xsl:variable>
	  <xsl:value-of select="$mets//m:div[@ID=$variant]/m:fptr/@FILEID"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:choose>
	    <xsl:when test="$mets//m:smLink[(@xlink:title='Larger' or 
			    @xlink:title='Større') and @xlink:from=$div_id][1]/@xlink:to">
	      <xsl:variable name="variant">
		<xsl:value-of select="$mets//m:smLink[(@xlink:title='Larger' or @xlink:title='Større') 
				      and @xlink:from=$div_id][1]/@xlink:to"/>
	      </xsl:variable>
	      <xsl:value-of select="$mets//m:div[@ID=$variant]/m:fptr/@FILEID"/>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:value-of select="$id"/>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>



    <xsl:variable name="path">
      <xsl:value-of select="substring-before(substring-after($mets//m:file[@ID=$fid]/m:FLocat/@xlink:href,'anus/'),'.jpg')"/>
    </xsl:variable>

    <xsl:value-of select="concat('http://kb-images.kb.dk/public/Manus/',$path,'/info.json')"/>

  </xsl:template>


</xsl:stylesheet>


