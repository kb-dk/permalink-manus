<%@ page contentType="text/xml;charset=UTF-8"%><% response.setContentType("text/xml");request.setCharacterEncoding("UTF-8");

org.dom4j.Document structure = null;

String     docId   = "41";
if(request.getParameter("doc") != null) {
  docId = request.getParameter("doc");
}

String app ="manus";
if(request.getParameter("app") != null) {
  app = request.getParameter("app");
}

dk.kb.dup.metsApi.Structure struct = dk.kb.dup.metsApi.Structure.getInstance();

out.println(struct.mets(app,docId));

%>
