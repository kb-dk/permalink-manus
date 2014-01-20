<%@ page contentType="text/html;charset=UTF-8"%><% response.setContentType("text/plain; charset=UTF-8");request.setCharacterEncoding("UTF-8");

dk.kb.dup.metsApi.MusIdentifiers mus = new dk.kb.dup.metsApi.MusIdentifiers();

while(mus.hasNext()) {
  out.println(mus.getNextItem());
}
while(mus.hasNextProj()) {
  out.println(mus.getNextProj());
}
%>