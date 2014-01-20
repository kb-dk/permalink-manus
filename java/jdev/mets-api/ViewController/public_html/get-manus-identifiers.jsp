<%@ page contentType="text/html;charset=UTF-8"%><% response.setContentType("text/plain; charset=UTF-8");request.setCharacterEncoding("UTF-8");

dk.kb.dup.metsApi.ManusIdentifiers manus = new dk.kb.dup.metsApi.ManusIdentifiers();

while(manus.hasNext()) {
  out.println(manus.getNextItem());
}

%>
