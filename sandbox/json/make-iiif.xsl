<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:fn="http://www.w3.org/2005/xpath-functions"
		xmlns:m="http://www.loc.gov/METS/" 
		xmlns:xlink="http://www.w3.org/1999/xlink" 
		xmlns:md="http://www.loc.gov/mods/v3" 
		version="3.0">

  <xsl:output indent="yes" method="text"/>

  <!-- These hare hard-coded here, need to be passed or calculated in the real world -->

  <xsl:param name="manus_number" select="'642'"/>
  <xsl:param name="manus_base_uri" select="'http://www.kb.dk'"/>
  <!-- xsl:param name="iiif_host">http://localhost/iiif/Manus</xsl:param -->
  <xsl:param name="iiif_host">http://kb-images.kb.dk/public/Manus/</xsl:param>

  <xsl:param name="manus_lang" select="'dan'"/>

  <xsl:template match="/">
    <xsl:variable name="iiif_document">
      <xsl:variable name="mets"><xsl:copy-of select="."/></xsl:variable>
      <fn:map>

	<fn:string key="@context">http://iiif.io/api/presentation/2/context.json</fn:string>
	<fn:string key="@id">https://raw.githubusercontent.com/Det-Kongelige-Bibliotek/permalink-manus/iiif_presentation/sandbox/specimen.json</fn:string>
	<fn:string key="@type">sc:Manifest</fn:string>

	<xsl:for-each select="//m:dmdSec[@ID='md-root']/m:mdWrap[@MDTYPE='MODS']/m:xmlData/md:mods">
	  <xsl:for-each select="md:name[md:role/md:roleTerm='author'][1]">
	    <fn:string key="label"><xsl:value-of select="md:partName"/></fn:string>
	  </xsl:for-each>
	</xsl:for-each>

	<fn:map key="logo">
	  <fn:string key="@id">http://www.kb.dk/export/system/modules/dk.kb.responsive.local/resources/img/kb_logo_smallX2.png</fn:string>
	  <fn:map key="service">
	    <fn:string key="@id">http://www.kb.dk/export/system/modules/dk.kb.responsive.local/resources/img/kb_logo_small.png</fn:string>
	  </fn:map>
	</fn:map>

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
		    <xsl:call-template name="string_lang"/>
		  </fn:map>
		</xsl:for-each>
	      </fn:array>
	    </fn:map>
	    <fn:map>
	      <fn:string key="label">Source</fn:string>
	      <fn:string key="value">http://www.kb.dk/da/nb/materialer/haandskrifter/HA/e-mss/clh.html</fn:string>
	    </fn:map>
	  </fn:array>


	  <fn:array key="description">
	    <xsl:for-each select="md:note[@type='presentation']">
	      <xsl:for-each select="div/p">
		<fn:map>
		  <fn:string key="value"><xsl:value-of select="."/></fn:string>
		  <xsl:call-template name="string_lang"/>
		</fn:map>
	      </xsl:for-each>

	    </xsl:for-each>
	  </fn:array>
	</xsl:for-each>
	<!-- obviously, we should select these in a more clever ways. -->
	<fn:map key="thumbnail">
	  <fn:string key="@id"><xsl:value-of select="$iiif_host"/>/gks1911/gks1911_006x/full/full/0/default.jpg</fn:string>
	  <fn:string key="@type">dctypes:Image</fn:string>
	  <fn:string key="format">image/jpeg</fn:string>
	  <fn:number key="width">1400</fn:number>
	  <fn:number key="height">2047</fn:number>
	  <fn:map key="service">
	    <fn:string key="@context">http://iiif.io/api/image/2/context.json</fn:string>
	    <fn:string key="@id"><xsl:value-of select="$iiif_host"/>/gks1911/gks1911_006x/info.json</fn:string>
	    <fn:string key="profile">http://iiif.io/api/image/2/level2.json</fn:string>
	  </fn:map>
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
      <fn:string key="@id"><xsl:call-template name="mk_manus_uri"/></fn:string>
      <fn:string key="@type">sc:Sequence</fn:string>
      <fn:string key="label">Default</fn:string>
      <fn:array key="canvases">
	<xsl:for-each select="m:div[not(@ID = preceding-sibling::m:div/@ID)]">
	  <xsl:variable name="id" select="@ID"/>
	  <xsl:call-template name="mdiv">
	    <xsl:with-param name="mets" select="$mets"/>
	  </xsl:call-template>
	</xsl:for-each>
      </fn:array>
    </fn:map>

  </xsl:template>


  <xsl:template name="mdiv">
    <xsl:param name="mets"/>
    <xsl:variable name="id" select="@ID"/>
    <fn:map>
      <fn:string key="@id">
	<xsl:call-template name="get_uri">
	  <xsl:with-param name="div_id" select="$id"/>
	  <xsl:with-param name="suffix">/info.json</xsl:with-param>
	  <xsl:with-param name="mets" select="$mets"/>		
	</xsl:call-template>
      </fn:string>
      <fn:string key="@type">sc:Canvas</fn:string>
      <fn:map key="thumbnail">	  
	<fn:string key="@id">
	  <xsl:call-template name="get_uri">
	    <xsl:with-param name="div_id" select="$id"/>
	    <xsl:with-param name="suffix">/full/full/0/default.jpg</xsl:with-param>
	    <xsl:with-param name="mets" select="$mets"/>		
	  </xsl:call-template>
	</fn:string>
      </fn:map>
      <fn:array key="label">
	<xsl:for-each select="$mets//m:div[@ID=$id]">
	  <fn:map>
	    <fn:string key="value"><xsl:value-of select="(@LABEL|@ORDERLABEL)[1]"/></fn:string>
	    <xsl:call-template name="string_lang"/>
	  </fn:map>
	</xsl:for-each>
      </fn:array>
      <fn:array key="images">
	<fn:map>
	  <xsl:variable name="image_id" select="$id"/>
	  <fn:string key="@id">
	    <xsl:call-template name="get_uri">
	      <xsl:with-param name="div_id" select="$image_id"/>
	      <xsl:with-param name="suffix">/info.json</xsl:with-param>
	      <xsl:with-param name="mets" select="$mets"/>		
	    </xsl:call-template>
	  </fn:string>
	  <fn:array key="label">
	    <xsl:for-each select="$mets//m:div[@ID=$id]">
	      <fn:map>
		<fn:string key="value"><xsl:value-of select="(@LABEL|@ORDERLABEL)[1]"/></fn:string>
		<xsl:call-template name="string_lang"/>
	      </fn:map>
	    </xsl:for-each>
	  </fn:array>
	 
	  <fn:map key="resource">
	    <xsl:variable name="uri">
	      <xsl:call-template name="get_uri">
		<xsl:with-param name="div_id" select="$image_id"/>
		<xsl:with-param name="mets" select="$mets"/>
		<xsl:with-param name="suffix"></xsl:with-param>
	      </xsl:call-template>
	    </xsl:variable>
	    <fn:string key="@label">
	      <xsl:call-template name="get_lbl">
		<xsl:with-param name="mets" select="$mets"/>
		<xsl:with-param name="div_id" select="$image_id"/>
	      </xsl:call-template>
	    </fn:string>
	    <fn:string key="@id">
	      <xsl:value-of select="concat($uri,'/full/full/0/default.jpg')"/>
	    </fn:string>
	    <fn:string key="@type">dctypes:Image</fn:string>
	    <fn:string key="format">image/jpeg</fn:string>
	    <xsl:call-template name="get_resolution">
	      <xsl:with-param name="uri" select="$uri"/>		      
	    </xsl:call-template>
	    <fn:map key="service">
	      <fn:string key="@context">http://iiif.io/api/image/2/context.json</fn:string>
	      <fn:string key="@id"><xsl:value-of select="$uri"/>/info.json</fn:string>
	      <fn:string key="profile">http://iiif.io/api/image/2/level2.json</fn:string>
	    </fn:map>
	  </fn:map>
	</fn:map>

      </fn:array>
    </fn:map>
    <xsl:message><xsl:value-of select="$mets/local-name(.)"/></xsl:message>
  </xsl:template>

  <xsl:template name="get_lbl">
    <xsl:param name="div_id"/>
    <xsl:param name="mets"/>
    <xsl:value-of select="$mets//m:div[@ID=$div_id][1]/@ORDERLABEL"/>
  </xsl:template>


  <xsl:template name="get_uri">
    <xsl:param name="div_id"/>
    <xsl:param name="mets"/>
    <xsl:param name="suffix" />

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

    <xsl:value-of select="concat($iiif_host,$path,$suffix)"/>

  </xsl:template>

  <xsl:template name="get_resolution">
    <xsl:param name="uri"/>
    <xsl:choose>
      <xsl:when test="matches($uri,'x$')">
	<fn:number key="width">1400</fn:number>
	<fn:number key="height">2049</fn:number>
      </xsl:when>
      <xsl:otherwise>
	<fn:number key="width">830</fn:number>
	<fn:number key="height">1225</fn:number>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="mk_manus_uri">
    <xsl:value-of select="concat($manus_base_uri,'/permalink/2006/manus/',$manus_number,'/',$manus_lang,'/')"/>
  </xsl:template>

  <xsl:template name="string_lang">
    <fn:string key="@language">
      <xsl:choose>
	<xsl:when test="ancestor-or-self::node()/@xml:lang='eng'">en</xsl:when>
	<xsl:otherwise>da</xsl:otherwise>
      </xsl:choose>
    </fn:string>
  </xsl:template>


</xsl:stylesheet>


