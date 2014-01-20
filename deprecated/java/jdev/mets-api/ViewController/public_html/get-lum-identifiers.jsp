<%@ page contentType="text/html;charset=UTF-8"%><% response.setContentType("text/plain; charset=UTF-8");request.setCharacterEncoding("UTF-8");

dk.kb.dup.metsApi.LumIdentifiers lum = new dk.kb.dup.metsApi.LumIdentifiers();

while(lum.hasNext()) {
  out.println(lum.getNextItem());
}

%>