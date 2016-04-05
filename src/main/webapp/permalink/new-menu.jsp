<%@ page contentType="text/html;charset=UTF-8"%>
<%

//String metsPath ="http://img.kb.dk/mets/";
String metsPath ="/kb/data/mets/";
String xmlRoot = "http://"+request.getServerName() + ":" + request.getServerPort()+request.getContextPath()+"/xsl/";

response.setContentType("text/html");
request.setCharacterEncoding("UTF-8");

String appPar   = "manus";
if(request.getParameter("app") != null) {
  appPar= request.getParameter("app");
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


metsPath = metsPath + appPar + "/" +  docPar + "/metsfile.xml";
java.io.File metsFile = new java.io.File(metsPath);
org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
if(!metsFile.exists()){
  response.sendError(response.SC_NOT_FOUND);
  return;	
}
doc = reader.read(metsFile);



//metsPath = metsPath + appPar + "/" +  docPar + "/metsfile.xml";
//org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
//doc = reader.read(metsPath);

mElem = new dk.kb.mets.MetsElements(doc, "", true);

String langPar = mElem.getLang().get(0)+"";

if(request.getParameter("lang") != null) {
 if(!request.getParameter("lang").equals("")){
  langPar = request.getParameter("lang");
 }
}

dk.kb.mets.Term t = new dk.kb.mets.Term(langPar);
java.util.HashMap h = t.getLangTerm();

mElem.setLang(langPar);

String divid="";

java.util.List navigation = mElem.getSubNavigation(); 
            
java.util.Iterator iter = navigation.iterator();
java.util.Iterator iter2 = navigation.iterator();
String prevId= "";
String nowId = "";
String currId = mElem.getDivFromPage(pagePar);

while(iter2.hasNext()){
 String[] nav = (String[])iter2.next();
 if(!nav[2].equals("")){
   nowId =  nav[0];
 }
 if(currId==nav[0]){
   prevId = nowId;
 }
}


String cookieName = "dk.kb.www.mets.menu";
  String c = "class='contentNav'";
  Cookie cookies [] = request.getCookies ();
  Cookie myCookie = null;
  if (cookies != null){
    for (int i = 0; i < cookies.length; i++) {
      if (cookies [i].getName().equals (cookieName)){
        myCookie = cookies[i];
        if(myCookie.getValue().equals("none")){
          c = "class='contentNavOff'";
        }
      }
    }
  }


boolean first = false;
int id = 0;
out.println("<td id=\"contentNav\" "+c+"> <h1>"+ mElem.getManusTitle() +"</h1><div><ul>");

if(navigation.size()!=0){
  out.println("<li><a href=\"/permalink/2006/"+appPar+"/"+docPar+"/"+langPar+"/\">"+h.get("Intro")+"</a></li> ");
}
while(iter.hasNext()){
  id++;
  String onoff ="off";
  String sel ="";
  String display ="none;";

  String[] nav = (String[])iter.next();
  //if main nav item
  if(!nav[2].equals("")){
    if(first){
      out.println("</ul></li>");
    }else{
      first =true;
    }
    if(nav[0].equals(prevId)){
      onoff="on";
      display="block;";
      out.println("<li><a href=\"#\" id='a"+id+"' onclick=\"toggleMenu('a"+id+"','u"+id+"');\" class='"+onoff+"'>"+nav[2]+"</a>");
      out.println("<ul id='u"+id+"' style='display:"+display+"'>");
    }else{
      out.println("<li><a href='/permalink/2006/"+appPar+"/"+docPar+"/"+langPar+"/"+java.net.URLEncoder.encode(nav[1],"UTF-8")+"/' class='"+onoff+"'>"+nav[2]+"</a>");
      out.println("<ul id='u"+id+"' style='display:"+display+"'>");
    }
  }  
  
  if(currId.equals(nav[0])){
    sel="class='sel'";
  }
  
out.println("<li><a href='/permalink/2006/"+appPar+"/"+docPar+"/"+langPar+"/"+java.net.URLEncoder.encode(nav[1],"UTF-8")+"/' "+sel+">"+nav[1]+"</a></li>");
}
out.println("</ul>");

out.println("  </div> </td>");



%>

			