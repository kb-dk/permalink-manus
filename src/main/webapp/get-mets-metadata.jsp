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

if(app.equalsIgnoreCase("manus")){
  dk.kb.dup.metsApi.Manus manus = new dk.kb.dup.metsApi.Manus(docId);
  structure = manus.getMets();
}else if(app.equalsIgnoreCase("lum")){
  dk.kb.dup.metsApi.Lum lum = new dk.kb.dup.metsApi.Lum(docId);
  structure = lum.getMets();
}else if(app.equalsIgnoreCase("lum-proj")){
  dk.kb.dup.metsApi.Lum lum = new dk.kb.dup.metsApi.Lum();
  structure = lum.getProjectMets();
}else if(app.equalsIgnoreCase("musman")){
  dk.kb.dup.metsApi.MusMan musman = new dk.kb.dup.metsApi.MusMan(docId);
  structure = musman.getMets();
}else if(app.equalsIgnoreCase("musman-proj")){
  dk.kb.dup.metsApi.MusMan musman = new dk.kb.dup.metsApi.MusMan();
  structure = musman.getProjectMets();
}else if(app.equalsIgnoreCase("mus")){
 try{
    Integer.parseInt(docId);
    dk.kb.dup.metsApi.Mus mus = new dk.kb.dup.metsApi.Mus(docId,"");
    structure = mus.getNodeMets();
  }catch(Exception e){
    dk.kb.dup.metsApi.Mus mus = new dk.kb.dup.metsApi.Mus("",docId);
    structure = mus.getProjectMets();
  }
}else if(app.equalsIgnoreCase("musik")){
  dk.kb.dup.metsApi.Musik musik = new dk.kb.dup.metsApi.Musik();
  structure = musik.getProjectsMets();
}

out.println(structure.asXML());

%>
