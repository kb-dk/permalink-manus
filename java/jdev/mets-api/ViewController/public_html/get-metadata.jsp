<%@ page contentType="text/xml;charset=UTF-8"%><% response.setContentType("text/xml");request.setCharacterEncoding("UTF-8");

String     docId   = "253";
if(request.getParameter("doc") != null) {
  docId = request.getParameter("doc");
}

dk.kb.dup.metsApi.Manus manus = new dk.kb.dup.metsApi.Manus(docId);

//org.dom4j.Document metadata  = manus.mods();
org.dom4j.Document structure = manus.getMets();
out.println(structure.asXML());

%>