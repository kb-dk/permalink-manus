<%@ page contentType="text/html;charset=UTF-8"%><%     

// String metsPath ="http://img.kb.dk/mets/";
// String metsPath ="/kb/data/mets/";
 
String scheme   = "http";
String userInfo = "";
String apihost  = "localhost";
int    port     = 8080;
String metsPath = "/mets-api";
String userinfo = "";
String fragment = "";


response.setContentType("text/html");
request.setCharacterEncoding("UTF-8");

String appPar   = "manus";
if(request.getParameter("app") != null) {
  appPar= request.getParameter("app");
}

String varPar   = "";
if(request.getParameter("var") != null) {
  varPar= request.getParameter("var");
}

String docPar   = "42";
if(request.getParameter("doc") != null) {
  docPar = request.getParameter("doc");
}

//getDivFromPage
String pagePar = "";
if(request.getParameter("page") != null) {
  pagePar = request.getParameter("page");
}

dk.kb.mets.MetsElements mElem = null;
org.dom4j.Document doc = null;

java.net.URI uri = null; 
if(appPar.equals("manus")) {
    metsPath = metsPath + "/api/get-mets-metadata.jsp";
    String query = "app=" +   appPar + "&doc=" +  docPar + "";
    uri = new java.net.URI(scheme,
			   userinfo,
			   apihost,
			   port,
			   metsPath,
			   query,
			   fragment);
} else {
    metsPath = metsPath + "/data/" +   appPar + "/" +  docPar + "/metsfile.xml";
    String query = "";
    uri = new java.net.URI(scheme,
			   userinfo,
			   apihost,
			   port,
			   metsPath,
			   query,
			   fragment);
}

org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();


doc = reader.read(uri.toURL());

mElem = new dk.kb.mets.MetsElements(doc, "", false);


java.util.List multiLang = mElem.getLang();
String langPar = multiLang.get(0)+"";

if(request.getParameter("lang") != null) {
 if(!request.getParameter("lang").equals("")){ 
   langPar = request.getParameter("lang");
  }
}
String secondLang = "";
try{
 secondLang = multiLang.get(1)+"";
 if(secondLang.equals(langPar)){
   secondLang = multiLang.get(0)+""; 
 }
}catch(Exception e){ 
  //Do nothing
}



mElem.setLang(langPar);
String divid = mElem.getDivFromPage(pagePar);
if(!varPar.equals("")){
  divid = mElem.getVarFromDivid(divid, varPar);
}
String pageid = divid;

boolean var = false;
if(divid.indexOf("variant")>0){
  mElem.setVar(true);
}


String img = mElem.getCurrImg(divid);
if(img.equals("")){
  mElem.setVar(false);
  varPar="";
  img = mElem.getCurrImg(pageid);
  
}
String  dash = " - ";
String orderLabel = mElem.getOrderLabel(pageid);
if(orderLabel==""){
  dash="";
}
String manusTitle = mElem.getManusTitle();
String titleString = manusTitle + dash + orderLabel;        

String cookieName = "dk.kb.www.mets.menu";
String cookieName2 = "dk.kb.www.mets.fontSize";
String c = "class=''";
String size = "";
Cookie cookies [] = request.getCookies ();
Cookie myCookie = null;
if (cookies != null){
  for (int i = 0; i < cookies.length; i++) {
    if (cookies [i].getName().equals (cookieName)){
      myCookie = cookies[i];
      if(myCookie.getValue().equals("none")){ 
        c = "class='out'";
      }
    }
    if (cookies [i].getName().equals (cookieName2)){
      myCookie = cookies[i];
      if(!(myCookie.getValue()).equals("100%")){
	  size = "style='font-size:"+myCookie.getValue()+"'";	
      }
    }
  }
}

String pdf = mElem.getPdf(); 
   
dk.kb.mets.Term t = new dk.kb.mets.Term(langPar);
String secondLangString ="";
java.util.HashMap h = t.getLangTerm();
if(!secondLang.equals("")){
  secondLangString = t.getLang(secondLang);
}

String depCol = "e-manuskripter";
//"h.get("e-manuskripter");
String depUrl = "/da/kb/nb/ha/";
if(appPar.equals("musman") || appPar.equals("mus") || appPar.equals("lum") || appPar.equals("musik")){
  depCol = "e-noder";
  depUrl = "/da/nb/samling/ma/digmus/musem.html";
}
String trykString = " 217 268 23 370 665 673 733 728 668 366 696 731 729 695 732 708 727 586 9 541 664 526 528 530 655 750 751 ";
String victorMadsen = " 734 735 736 ";
if(appPar.equals("manus")){
  String docString = " "+docPar+" ";    
  if(trykString.indexOf(docString)>-1){
    depCol = "Sjældne tryk";
    depUrl = "/da/kb/nb/ha/boghistorie/sjaeldne_tryk.html";
  }
  if(victorMadsen.indexOf(docString)>-1){
    depCol = "Victor Madsen-kataloget";
    depUrl = "/da/kb/nb/ha/boghistorie/victormadsen/";
  }

}



String[] host = mElem.getHost();
java.util.List menuItem = mElem.getMenuItem();

%>
<html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <title><%= titleString %></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <style type="text/css" media="screen">@import "css/global.css";</style>
    <style type="text/css" media="print">@import "css/print.css";</style>

    <script src="js/manus.js" language="javascript"  type="text/javascript"></script>

  </head>
  <body <%= size %>>
  <table cellspacing="0" cellpadding="0" border="0">
    <tr><!--HEADER-->
      <td id="header">
	<a href="http://www.kb.dk"><img src="img/logo.gif" alt="Det Kongelige Bibliotek" border="0"/></a>
	<a href="javascript:increase(15);" title="" class="zoomOut"></a>
	<a href="javascript:decrease(15);" title="" class="zoomIn"></a>
	<div class="clear"></div>
      </td>
    </tr>
    <tr><!--NAVIGATION-->
      <td id="nav">
	<ul>
	  <li><a href="http://www.kb.dk">www.kb.dk</a></li>
	  <% if(appPar.equals("manus") ||appPar.equals("mus")){  %>
	  <li><a href="<%= host[1] %>" title="<%= host[1] %>"><%= host[0] %></a></li>
	  <% } else { %>
	  <li><a href="/da/kb/nb/mta/">Musik- og Teaterafdelingen</a></li>
	  <% } %>
	  <% if(menuItem!=null){
	  java.util.Iterator linkIterator = menuItem.iterator();
	  while(linkIterator.hasNext()){
	  String[] arr = (String[])linkIterator.next();
	  if(!arr[1].equals("")){
	  out.println("<li><a href='"+arr[1]+"' title='"+arr[1]+"'>"+arr[0]+"</a></li>");
	  }
	  }
	  if(!pdf.equals("")){
	  out.println("<li><a href='"+pdf+"'>Vis Noden (pdf)</a></li>");
	  }
	  }%>
	  <li class="lang"><a href="/permalink/2006/<%= appPar %>/<%= docPar %>/<%= secondLang %>/<%= java.net.URLEncoder.encode(pagePar,"UTF-8") %>/" title=""><%= secondLangString %></a></li>								 
	</ul>
	<div class="clear"></div>
      </td>
    </tr>

    <tr><!--CONTENT-->
      <td id="content">
	
	<table>
	  <tr>
	    <!--CONTENTNAVIGATION-->
	    <jsp:include page="new-menu.jsp"/>
	    <!--SLIDER-->
	    <td id="slider"><a href="javascript:slide();" id="slideA" <%= c %> title="slide ind"></a></td>
	    <!--CONTENTDATA-->
	    <td>
	      
	      <%
	      
	      double orderSize = orderLabel.length()*0.75;
	      if(orderSize<=2.0){orderSize=2.0;}
	      java.util.List links = mElem.getVarLink(pageid); 
	      
	      %>     
	      <% if(!mElem.getFirst().equals("")){  %>
	      <!--  BEGIN NAVIGATION     -->
	      <div id="function">
		<div class="pageFunction">
		  <div class="borderTopLeft">
		    <div class="borderTopRight">
		      <div class="borderBottomRight">
			<div class="borderBottomLeft">
			  <p><a href="<%= "/permalink/2006/"+appPar+"/"+docPar+"/"+langPar+"/"+java.net.URLEncoder.encode(mElem.getFirst(),"UTF-8")+"/?var="+varPar %>">|&lt;</a></p>
			</div>
		      </div>
		    </div>
		  </div>
		  <div class="borderTopLeft">
		    <div class="borderTopRight">
		      <div class="borderBottomRight">
			<div class="borderBottomLeft">
			  <p><a href="<%= "/permalink/2006/"+appPar+"/"+docPar+"/"+langPar+"/"+java.net.URLEncoder.encode(mElem.getPrev(pageid),"UTF-8")+"/?var="+varPar %>">&lt;</a></p>
			</div>
		      </div>
		    </div>
		  </div>
		  <div class="borderTopLeft">
		    <div class="borderTopRight">
		      <div class="borderBottomRight">
			<div class="borderBottomLeft">
			  <form method="get" action="javascript:location='/permalink/2006/manus/<%= docPar %>/<%= langPar %>/'+encodeURI(document.getElementById('goto').value.replace(' ', '+'))+'/';">
			  <p><input id="goto" value="<%= orderLabel %>" type="text" style="width:<%= orderSize %>em;" class="text" onFocus="this.value='';"/></p>
			</form>
		      </div>
		    </div>
		  </div>
		</div>
		<div class="borderTopLeft">
		  <div class="borderTopRight">
		    <div class="borderBottomRight">
		      <div class="borderBottomLeft">
			<%if(img.equals("")){%>
			<p><a href="<%= "/permalink/2006/"+appPar+"/"+docPar+"/"+langPar+"/"+java.net.URLEncoder.encode(mElem.getFirst(),"UTF-8")+"/?var="+varPar %>">&gt;</a></p>
			<%}else{%>
			<p><a href="<%= "/permalink/2006/"+appPar+"/"+docPar+"/"+langPar+"/"+java.net.URLEncoder.encode(mElem.getNext(pageid),"UTF-8")+"/?var="+varPar %>">&gt;</a></p>
			<%}%>				                  
			
		      </div>
		    </div>
		  </div>
		</div>
		<div class="borderTopLeft">
		  <div class="borderTopRight">
		    <div class="borderBottomRight">
		      <div class="borderBottomLeft">
			<p><a href="<%= "/permalink/2006/"+appPar+"/"+docPar+"/"+langPar+"/"+java.net.URLEncoder.encode(mElem.getLast(),"UTF-8")+"/?var="+varPar %>">&gt;|</a></p>
		      </div>
		    </div>
		  </div>
		</div>
	      </div><!-- END pageFunction -->
	      <div class="buttonFunction">                             
		<% if(!divid.equals(pageid)){%>
		<div class="borderTopLeft">
		  <div class="borderTopRight">
		    <div class="borderBottomRight">
		      <div class="borderBottomLeft">
			<% out.println("<p><a href=\"/permalink/2006/"+appPar+"/"+docPar+"/"+langPar+"/"+java.net.URLEncoder.encode(mElem.getOrderLabel(pageid),"UTF-8")+"/\" class=\"button\">"+ h.get("StandardImg") +"</a></p>");  %>
		      </div>
		    </div>
		  </div>
		</div>
		<%}
		for(java.util.Iterator linkIter = links.iterator(); linkIter.hasNext();){
		String[] tmp = (String[])linkIter.next();
		if(!tmp[2].equals(varPar)){%>
		<div class="borderTopLeft">
		  <div class="borderTopRight">
		    <div class="borderBottomRight">
		      <div class="borderBottomLeft">
			<% out.println("<p><a href=\"/permalink/2006/"+appPar+"/"+docPar+"/"+langPar+"/"+java.net.URLEncoder.encode(mElem.getOrderLabel(pageid),"UTF-8")+"/?var="+tmp[2]+"\" class=\"button\">"+ tmp[1]+"</a></p>");  %>
		      </div>
		    </div>
		  </div>
		</div>
		<%}
		}%>
	      </div><!-- END buttonFunction -->
	      <div class="clear"></div>
	    </div><!--  END function     -->					

	    <%}%>

	    
	    <div id="breadCrumb">
	      <dl>
		<dd><p><a href="<%= depUrl %>"><%=depCol%></a></p>
		<dl>
		  <dd><p>: <a href="/permalink/2006/<%= appPar %>/<%= docPar %>/"> <%= manusTitle %></a></p> 
		  <% if(!orderLabel.equals("")){%><dd><p>: <%= orderLabel %><% } %></p></dd>
		</dd>
	      </dl>
	    </dd>
	  </dl>
	  <div class="clear"></div>
	</div>
	<% if(img.equals("")){%>
	<div id="contentHeader">
	  <h1><%= titleString %></h1>
	  <a href="javascript:window.print();" title="print"></a>
	  <div class="clear"></div>
	</div>
	<%} %>
	<div id="contentPage">

	  <%
	  String thumb = mElem.getThumb();
	  
	  if(img.equals("")){
	  try{
	  if(!thumb.equals("") && pdf.equals("")){ 
	  out.println("<img src='"+thumb+"'/>");
	  }else if(!thumb.equals("") && !pdf.equals("")){ 
	  out.println("<a href='"+pdf+"'><img src='"+thumb+"'/></a>");
	  }
	  out.println("<blockquote><p>"+mElem.getManusDescription().replaceAll("<div>","").replaceAll("</div>","")+"</p></blockquote>");
	  }catch(Exception e){
	  }
	  }
	  
	  
	  if(img.endsWith("djvu")){
	  out.println("<embed name='djvu'  src='"+img+"' type='image/x.djvu' width='700' height='500' zoom='10%' frame='yes' mode='color'/>");              
	  }else if(!img.equals("")){          
	  out.println("<img src='"+img+"' alt='"+orderLabel+"'/>");
	  }
	  if(!mElem.getDescription(pageid).equals("")){  
	  out.println("<blockquote><p>"+mElem.getDescription(pageid)+"</p></blockquote>");
	  }
	  try{
	  out.println(mElem.getList(appPar));
	  }catch(Exception e) {
	  
	  }  
	  %>
	</div>

	<div class="clear"></div>
	<% if(!img.equals("")){  %>

	<% if(!mElem.getFirst().equals("")){  %>
	<!--  BEGIN NAVIGATION     -->
	<div id="function">
	  <div class="pageFunction">
	    <div class="borderTopLeft">
	      <div class="borderTopRight">
		<div class="borderBottomRight">
		  <div class="borderBottomLeft">
		    <p><a href="<%= "/permalink/2006/"+appPar+"/"+docPar+"/"+langPar+"/"+java.net.URLEncoder.encode(mElem.getFirst(),"UTF-8")+"/?var="+varPar %>">|&lt;</a></p>
		  </div>
		</div>
	      </div>
	    </div>
	    <div class="borderTopLeft">
	      <div class="borderTopRight">
		<div class="borderBottomRight">
		  <div class="borderBottomLeft">
		    <p><a href="<%= "/permalink/2006/"+appPar+"/"+docPar+"/"+langPar+"/"+java.net.URLEncoder.encode(mElem.getPrev(pageid),"UTF-8")+"/?var="+varPar %>">&lt;</a></p>
		  </div>
		</div>
	      </div>
	    </div>
	    <div class="borderTopLeft">
	      <div class="borderTopRight">
		<div class="borderBottomRight">
		  <div class="borderBottomLeft">
		    <form method="get" action="javascript:location='/permalink/2006/manus/<%= docPar %>/<%= langPar %>/'+encodeURI(document.getElementById('goto2').value.replace(' ', '+'));+'/';">
		    <p><input id="goto2" value="<%= orderLabel %>" type="text" style="width:<%= orderSize %>em;" class="text" onFocus="this.value='';"/></p>
		  </form>
		</div>
	      </div>
	    </div>
	  </div>
	  <div class="borderTopLeft">
	    <div class="borderTopRight">
	      <div class="borderBottomRight">
		<div class="borderBottomLeft">
		  <%if(img.equals("")){%>
		  <p><a href="<%= "/permalink/2006/"+appPar+"/"+docPar+"/"+langPar+"/"+java.net.URLEncoder.encode(mElem.getFirst(),"UTF-8")+"/?var="+varPar %>">&gt;</a></p>
		  <%}else{%>
		  <p><a href="<%= "/permalink/2006/"+appPar+"/"+docPar+"/"+langPar+"/"+java.net.URLEncoder.encode(mElem.getNext(pageid),"UTF-8")+"/?var="+varPar %>">&gt;</a></p>
		  <%}%>				                  
		</div>
	      </div>
	    </div>
	  </div>
	  <div class="borderTopLeft">
	    <div class="borderTopRight">
	      <div class="borderBottomRight">
		<div class="borderBottomLeft">
		  <p><a href="<%= "/permalink/2006/"+appPar+"/"+docPar+"/"+langPar+"/"+java.net.URLEncoder.encode(mElem.getLast(),"UTF-8")+"/?var="+varPar %>">&gt;|</a></p>
		</div>
	      </div>
	    </div>
	  </div>
	</div><!-- END pageFunction -->
	<div class="buttonFunction">                             
	  <% if(!divid.equals(pageid)){%>
	  <div class="borderTopLeft">
	    <div class="borderTopRight">
	      <div class="borderBottomRight">
		<div class="borderBottomLeft">
		  <% out.println("<p><a href=\"/permalink/2006/"+appPar+"/"+docPar+"/"+langPar+"/"+java.net.URLEncoder.encode(mElem.getOrderLabel(pageid),"UTF-8")+"/\" class=\"button\">"+ h.get("StandardImg") +"</a></p>");  %>
		</div>
	      </div>
	    </div>
	  </div>
	  <%}
	  for(java.util.Iterator linkIter = links.iterator(); linkIter.hasNext();){
	  String[] tmp = (String[])linkIter.next();
	  if(!tmp[2].equals(varPar)){%>
	  <div class="borderTopLeft">
	    <div class="borderTopRight">
	      <div class="borderBottomRight">
		<div class="borderBottomLeft">
		  <% out.println("<p><a href=\"/permalink/2006/"+appPar+"/"+docPar+"/"+langPar+"/"+java.net.URLEncoder.encode(mElem.getOrderLabel(pageid),"UTF-8")+"/?var="+tmp[2]+"\" class=\"button\">"+ tmp[1]+"</a></p>");  %>
		</div>
	      </div>
	    </div>
	  </div>
	  <%}
	  }%>
	</div><!-- END buttonFunction -->
	<div class="clear"></div>
      </div><!--  END function     -->					

      <%}}%>
      

    </td>
  </tr>
</table>

</td>
</tr>
<tr><!--FOOTER-->
  <td id="footer">
    <p>
      Det Kongelige Bibliotek, Postbox 2149, DK-1016 København K (+45) 33 47 47 47, kb@kb.dk EAN lokations nr: 5798 000795297
    </p>
  </td>
</tr>
</table>

<!-- Google Analytics www.kb.dk -->
<script src="http://www.google-analytics.com/urchin.js" type="text/javascript"></script>
<script type="text/javascript">_uacct ="UA-1269676-1";_udn="www.kb.dk";urchinTracker();</script>

</body>
</html>





