<%@ page contentType="text/html;charset=UTF-8"%><% response.setContentType("text/plain; charset=UTF-8");request.setCharacterEncoding("UTF-8");

dk.kb.dup.metsApi.MusManIdentifiers musman = new dk.kb.dup.metsApi.MusManIdentifiers();

while(musman.hasNext()) {
  out.println(musman.getNextItem());
}

%>